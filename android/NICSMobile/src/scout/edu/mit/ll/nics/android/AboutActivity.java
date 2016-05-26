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
package scout.edu.mit.ll.nics.android;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpResponseHandler;

import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.fragments.AboutFragment;

public class AboutActivity extends ActionBarActivity {
	
	AboutFragment mAboutFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		mAboutFragment = new AboutFragment();

		getSupportFragmentManager().beginTransaction().replace(R.id.aboutContainer, mAboutFragment).commit();
		getSupportFragmentManager().executePendingTransactions();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		JSONObject object = mAboutFragment.toJson();
		try {			
			object.put("funding", getResources().getString(R.string.funding));
			object.put("icons", getResources().getString(R.string.nounproject));
			
			String googleLegal = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);
			if(googleLegal != null){
				object.put("google", googleLegal);
			}else{
				RestClient.getGoogleMapsLegalInfo(new AsyncHttpResponseHandler() {
					
						@Override
						public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
							String content = (responseBody != null) ? new String(responseBody) : "error";
							JSONObject object = mAboutFragment.toJson();
							try {			
								object.put("funding", getResources().getString(R.string.funding));
							    String cleanString = content.replaceAll("\\<.*?>","");
							    cleanString = cleanString.replace("\n", "");
								object.put("google", cleanString);
								object.put("icons", getResources().getString(R.string.nounproject));
							} catch (JSONException e) {
								e.printStackTrace();
							}
							mAboutFragment.populate(object.toString(), 0, false);
						}
						
						@Override
						public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
							String content = (responseBody != null) ? new String(responseBody) : "error";
					}
				});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mAboutFragment.populate(object.toString(), 0, false);
	}
}
