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

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import scout.edu.mit.ll.nics.android.auth.AuthProvider;

public class BasicAuthProvider extends AuthProvider {

	private Header[] mAuthHeader;


	public BasicAuthProvider() {
	super();
		mClient.setURLEncodingEnabled(false);
		mClient.setMaxRetriesAndTimeout(2, 1000);
	}

	@Override
	public String getType() {
		return "Basic";
	}

	@Override
	public void setupAuth(String username, String password) {
		mClient.setBasicAuth(username, password);
		mDataManager.setUsername(username);
		
    	UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
		mAuthHeader = new Header[] { BasicScheme.authenticate(credentials, "UTF-8", false) };
	}
	
    
    public void get(String url, final AsyncHttpResponseHandler responseHandler) {
	    mClient.get(null, getAbsoluteUrl(url), mAuthHeader, null, responseHandler);
    }
    
    public void getWithoutCredentials(Context context, String url, final AsyncHttpResponseHandler responseHandler) {
	    mClient.get(context, getAbsoluteUrl(url),null,null, responseHandler);
    }

    public void post(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
		entity.setContentType("application/json");
    	mClient.post(null, getAbsoluteUrl(url), mAuthHeader, entity, "application/json", responseHandler);
    }
    
    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	mClient.post(null, getAbsoluteUrl(url), mAuthHeader, params, "multipart/form-data", responseHandler);
    }
    
    
    public void delete(String url, AsyncHttpResponseHandler responseHandler) {
    	mClient.delete(null, getAbsoluteUrl(url), mAuthHeader, responseHandler);
    }
}
