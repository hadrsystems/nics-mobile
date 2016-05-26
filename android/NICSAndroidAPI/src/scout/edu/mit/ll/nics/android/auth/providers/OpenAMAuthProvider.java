/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
package scout.edu.mit.ll.nics.android.auth.providers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

import org.apache.http.Header;
import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;

import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import scout.edu.mit.ll.nics.android.api.data.OpenAMAuthenticationData;
import scout.edu.mit.ll.nics.android.api.handlers.OpenAMAuthResponseHandler;
import scout.edu.mit.ll.nics.android.auth.AuthManager;
import scout.edu.mit.ll.nics.android.auth.AuthProvider;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class OpenAMAuthProvider extends AuthProvider {
	
	private String mToken;
	private boolean mTokenIsValid;
	private boolean mIsAuthenticating;
	private GsonBuilder mBuilder;

	private Context mContext;
	
	private boolean debugIgnoreOpenAmAuth = false;
	
	public OpenAMAuthProvider( Context context) {
		super();
		mContext = context;
		mBuilder = new GsonBuilder();
		mClient.setURLEncodingEnabled(false);
		mClient.setMaxRetriesAndTimeout(2, 1000);
	}
	
	@Override
	public String getType() {
		return "OpenAM";
	}

	@Override
	public void setupAuth(String username, String password) {
		mDataManager.setUsername(username);
		mPassword = password;

		if(debugIgnoreOpenAmAuth){
			mIsAuthenticating = false;
			AuthManager.setRequestingAuth(false);
		}else{
			mIsAuthenticating = true;
			AuthManager.setRequestingAuth(true);
		}

		mLatch = new CountDownLatch(1);
		
		if(!debugIgnoreOpenAmAuth){
			String authToken = mDataManager.getAuthToken();
			if(authToken != null) {
				validateAuthToken(authToken, mPassword);
			} else {
				requestAuthToken(username, mPassword);
			}
		}
	}
	
	public void requestAuthToken(final String username, final String password) {
		
    	try {
			mClient.post(null, mDataManager.getAuthServerURL() + "json/authenticate", new Header[]{ new BasicHeader("X-OpenAM-Username", username), new BasicHeader("X-OpenAM-Password", password), new BasicHeader("Content-Type", "application/json") }, new StringEntity("{}"), "application/json", new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";

					OpenAMAuthenticationData authData = mBuilder.create().fromJson(content, OpenAMAuthenticationData.class);
					
					if(authData.getErrorMessage() == null) {
						
						Log.d("nicsRest","" + authData.getTokenId());
						
						mToken = authData.getTokenId();
						mTokenIsValid = true;
						mDataManager.setAuthToken(mToken);
						setAuthCookies();

						mIsAuthenticating = false;
			    		AuthManager.setRequestingAuth(false);
						mLatch.countDown();
						Log.e("nics_AUTH", content);
					} else {
						onFailure(statusCode, headers, responseBody, null);
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					clearAuthCookies();

					mIsAuthenticating = false;
		    		AuthManager.setRequestingAuth(false);
					Log.e("nics_AUTH", content);
					
					Intent intent = new Intent();
				    intent.setAction(Intents.nics_FAILED_LOGIN);
				    
					if(error.getClass() == HttpResponseException.class) {
						HttpResponseException exception = (HttpResponseException)error;
						
						if(exception.getStatusCode() == 401) {
					        intent.putExtra("message", "Invalid username or password");
						} else {
					        intent.putExtra("message", exception.getMessage());
						}
					} else {
				        Log.e("nicsRest", error.getMessage());
						intent.putExtra("offlineMode", true);
						
						if(error.getClass() == UnknownHostException.class) {
							 intent.putExtra("message", "Failed to connect to server. Please check your network connection.");	
						} else {
							intent.putExtra("message", error.getMessage());
						}
						error.printStackTrace();
					}
			        mContext.sendBroadcast(intent);
			        
			        if(intent.getExtras() != null) {
			        	mDataManager.addPersonalHistory("User " + username + " login failed: " + intent.getExtras().get("message"));
			        } else {
			        	mDataManager.addPersonalHistory("User " + username + " login failed.");
			        }
			        

					mLatch.countDown();
				}
			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void validateAuthToken(final String tokenId, final String password) {
    	try {
    		mClient.get(mDataManager.getAuthServerURL() + "identity/isTokenValid?tokenid=" + URLEncoder.encode(tokenId, "UTF-8"), new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					Log.e("nics_AUTH", content);
					
					String temp = content.split("=")[1];
					temp = temp.substring(0, temp.length() - 1);
					mTokenIsValid = Boolean.parseBoolean(temp);
					
					if(!mTokenIsValid) {
						clearAuthCookies();
						requestAuthToken(mDataManager.getUsername(), password);
					} else {
						Log.d("nicsRest","" + tokenId);
						
						mToken = tokenId;
						setAuthCookies();
			    		mIsAuthenticating = false;
			    		AuthManager.setRequestingAuth(false);
			    		mLatch.countDown();
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					Log.e("nics_AUTH", content);

					clearAuthCookies();
		    		mIsAuthenticating = false;
		    		AuthManager.setRequestingAuth(false);
		    		mLatch.countDown();
				}
			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean setAuthCookies() {
    	
    	if(mTokenIsValid && mToken != null) {
			BasicCookieStore cookieStore = new BasicCookieStore();
			
			BasicClientCookie iPlanetDirectoryProCookie = new BasicClientCookie("iPlanetDirectoryPro", mToken);
			iPlanetDirectoryProCookie.setPath("/");
			iPlanetDirectoryProCookie.setDomain(mDataManager.getIplanetCookieDomain());
			
			BasicClientCookie AMAuthCookie = new BasicClientCookie("AMAuthCookie", mToken);
			AMAuthCookie.setPath("/");
			AMAuthCookie.setDomain(mDataManager.getAmAuthCookieDomain());
			
			cookieStore.addCookie(iPlanetDirectoryProCookie);
			cookieStore.addCookie(AMAuthCookie);
			
			mClient.setCookieStore(cookieStore);
			
			return true;
    	}
    	
    	mClient.setCookieStore(null);
    	
    	return false;
    }
    
    public void clearAuthCookies() {
    	mToken = null;
		mTokenIsValid = false;
		mClient.setCookieStore(null);
		mDataManager.setAuthToken(null);
    }
    
    public void get(final String url, final AsyncHttpResponseHandler responseHandler) {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
		    	try {
			    	if(mIsAuthenticating) {
			    		if(mLatch == null || mLatch.getCount() == 0) {
			    			mLatch = new CountDownLatch(1);
			    		}
						mLatch.await();
					}
			    
			    	
			    	mClient.get(null, getAbsoluteUrl(url), new Header[]{ new BasicHeader("AMAuthCookie", mToken), new BasicHeader("iPlanetDirectoryPro", mToken),  new BasicHeader("CUSTOM-uid",mDataManager.getUsername()),new BasicHeader("Content-Type", "application/json") }, null, new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
		    	} catch(InterruptedException e) {
		    		
		    	}
				Looper.loop();
			}
		}).start();
    }

    public void getWithoutCredentials(Context context,final String url, final AsyncHttpResponseHandler responseHandler) {
    
    }
    	
    public void post(final String url, final StringEntity entity, final AsyncHttpResponseHandler responseHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
		    	try {
					if(mIsAuthenticating) {
			    		if(mLatch == null || mLatch.getCount() == 0) {
			    			mLatch = new CountDownLatch(1);
			    		}
						mLatch.await();
					}
					
					if(debugIgnoreOpenAmAuth){mTokenIsValid = true;};
					
					if(mTokenIsValid) { 
						Header[] cookie = new Header[]{ new BasicHeader("AMAuthCookie", mToken), new BasicHeader("iPlanetDirectoryPro", mToken), new BasicHeader("CUSTOM-uid",mDataManager.getUsername()) ,new BasicHeader("Content-Type", "application/json") };
						mClient.setEnableRedirects(true, true, true);
						mClient.post(null, getAbsoluteUrl(url), cookie , entity, "application/json", new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
					}
		    	
		    	} catch(InterruptedException e) {
		    		
		    	}

				Looper.loop();
			}
		}).start();
    }
    
    public void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
		    	try {
			    	if(mIsAuthenticating) {
			    		if(mLatch == null || mLatch.getCount() == 0) {
			    			mLatch = new CountDownLatch(1);
			    		}
						mLatch.await();
			    	}
			    	
			    	if(debugIgnoreOpenAmAuth){mTokenIsValid = true;};
			    	
			    	if(mTokenIsValid) {
						Header[] cookie = new Header[]{ new BasicHeader("AMAuthCookie", mToken), new BasicHeader("iPlanetDirectoryPro", mToken), new BasicHeader("CUSTOM-uid",mDataManager.getUsername()) ,new BasicHeader("Content-Type", "multipart/form-data") };
						mClient.setEnableRedirects(true, true, true);
						mClient.post(null, getAbsoluteUrl(url), cookie, params, "multipart/form-data", new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
			    	}
		    	} catch(InterruptedException e) {
		    		
		    	}

				Looper.loop();
			}
		}).start();
    }
    
    public void delete(final String url, final AsyncHttpResponseHandler responseHandler) {
    	new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
		    	try {
			    	if(mIsAuthenticating) {
			    		if(mLatch == null || mLatch.getCount() == 0) {
			    			mLatch = new CountDownLatch(1);
			    		}
						mLatch.await();
			    	}
			    	if(debugIgnoreOpenAmAuth){mTokenIsValid = true;};
			    	
			    	if(mTokenIsValid) 
			    		{Header[] cookie = new Header[]{ new BasicHeader("AMAuthCookie", mToken), new BasicHeader("iPlanetDirectoryPro", mToken), new BasicHeader("CUSTOM-uid",mDataManager.getUsername()) ,new BasicHeader("Content-Type", "application/json") };
			    		mClient.delete(null, getAbsoluteUrl(url), cookie, new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
			    	}
		    	} catch(InterruptedException e) {
		    		
		    	}

				Looper.loop();
			}
		}).start();
    }




}
