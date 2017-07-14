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
/**
 *
 */
package scout.edu.mit.ll.nics.android.api.handlers;

import org.apache.http.Header;
import android.annotation.SuppressLint;
import android.os.Looper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.auth.AuthManager;

public class OpenAMAuthResponseHandler extends AsyncHttpResponseHandler {
	
	private AsyncHttpResponseHandler mPassthroughResponseHandler;
	private DataManager mDataManager;
	private Looper mLooper;
	private AuthManager mAuthManager;
	private Looper looper;
	
	public OpenAMAuthResponseHandler(AsyncHttpResponseHandler passthroughHandler, Looper looper) {
		mPassthroughResponseHandler = passthroughHandler;
		mAuthManager = AuthManager.getInstance();
		mDataManager = DataManager.getInstance();
		mLooper = looper;
	}
	
	
	@Override
	@SuppressLint("NewApi")
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		boolean sessionTimeout = false;
		for(Header header : headers) {
			if(header.getName().equals("X-AuthErrorCode")) {
				sessionTimeout = true;
			}
		}
		
		if(sessionTimeout) {
			if(mDataManager.isLoggedIn() && !AuthManager.isRequestingAuth()) {
				mDataManager.stopPollingAlarms();
				new Thread(new Runnable() {

					@Override
					public void run() {
						Looper.prepare();
				    	mAuthManager.requestAuth();
				    	looper = Looper.myLooper();
				    	Looper.loop();
					}
				}).start();
				
				try {
					mAuthManager.getClient().getLatch().await();
					if(looper != null) {
							looper.quitSafely();
							looper = null;
						 
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			mPassthroughResponseHandler.onFailure(statusCode, headers, responseBody, new Throwable("OpenAM Authentication Token expired."));
			
		} else {
			mPassthroughResponseHandler.onSuccess(statusCode, headers, responseBody);
		}
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
		
		mPassthroughResponseHandler.onFailure(statusCode, headers, responseBody, error);
	}
	
	@Override
	public void onProgress(int bytesWritten, int totalSize) {
		
		mPassthroughResponseHandler.onProgress(bytesWritten,totalSize);
	}
	
	@SuppressLint("NewApi")
	public void onFinish() {
			 mLooper.quitSafely();
	}

}
