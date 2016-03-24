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
package scout.edu.mit.ll.nics.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

import android.content.SharedPreferences;
import android.util.Log;

public class EncryptedPreferences {

	private SharedPreferences mPreferences;
	private String EncryptKey;

	
	public EncryptedPreferences(SharedPreferences prefs){
		mPreferences = prefs;
		InitEncryptKey();
	}	
	
	public void InitEncryptKey(){
		
		EncryptKey = mPreferences.getString(Constants.nics_USER_KEY, "");
		
		if(EncryptKey.equals("")){
			try {
			    AesCbcWithIntegrity.SecretKeys key;
		        key = AesCbcWithIntegrity.generateKey();
		        EncryptKey = AesCbcWithIntegrity.keyString(key);
				
		        mPreferences.edit().putString(Constants.nics_USER_KEY, EncryptKey).commit();
		        
				    } catch (GeneralSecurityException e) {
				        Log.e("Security exception", "GeneralSecurityException", e);
				    }
			}
	}
	
	private String encryptString(String input, String seed)
	{
		String encryptedString = "";
		
		try {
			
			AesCbcWithIntegrity.SecretKeys key;
			key = AesCbcWithIntegrity.keys(EncryptKey);

			encryptedString = AesCbcWithIntegrity.encrypt(seed, input, key).toString();
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encryptedString;
	}
	
	private String decryptString(String input)
	{
		String decryptedString = "";
		
		try {
			
			AesCbcWithIntegrity.SecretKeys key;
			key = AesCbcWithIntegrity.keys(EncryptKey);	

			decryptedString = AesCbcWithIntegrity.decryptString(new AesCbcWithIntegrity.CipherTextIvMac( input), key);	
			
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return decryptedString;
	}
	
	public void savePreferenceFloat(String prefKey, float value){
		String encrytpedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueField = encryptString(Float.toString(value), prefKey);
		mPreferences.edit().putString(encrytpedKeyField, encryptedValueField).commit();
	}
	
	public void savePreferenceLong(String prefKey, Long value){
		String encrytpedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueField = encryptString(Long.toString(value), prefKey);
		mPreferences.edit().putString(encrytpedKeyField, encryptedValueField).commit();
	}
	
	public void savePreferenceString(String prefKey, String value){
		String encrytpedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueField = encryptString(value, prefKey);
		mPreferences.edit().putString(encrytpedKeyField, encryptedValueField).commit();
	}
	
	public void savePreferenceBoolean(String prefKey, boolean value){
		String encrytpedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueField = encryptString(String.valueOf(value),prefKey);
		mPreferences.edit().putString(encrytpedKeyField, encryptedValueField).commit();
	}
	
	public String getPreferenceString(String prefKey){
		String encryptedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueStr = mPreferences.getString(encryptedKeyField, null);
		if(encryptedValueStr != null){
			return decryptString(encryptedValueStr);				
		}else{
			return null;
		}
	}
	
	public String getPreferenceString(String prefKey, String defaultReturn){
		String encryptedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueStr = mPreferences.getString(encryptedKeyField, defaultReturn);
		if(encryptedValueStr != defaultReturn){
			return decryptString(encryptedValueStr);				
		}else{
			return defaultReturn;
		}
	}
	
	public Long getPreferenceLong(String prefKey){
		String encryptedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueStr = mPreferences.getString(encryptedKeyField, "-1");
		if(!encryptedValueStr.equals("-1")){
			String decryptedValueStr = decryptString(encryptedValueStr);
			return Long.parseLong(decryptedValueStr);				
		}else{
			return (long) -1;
		}
	}
	
	public Long getPreferenceLong(String prefKey, String defaultValue){
		String encryptedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueStr = mPreferences.getString(encryptedKeyField, defaultValue);
		if(!encryptedValueStr.equals(defaultValue)){
			String decryptedValueStr = decryptString(encryptedValueStr);
			return Long.parseLong(decryptedValueStr);				
		}else{
			return Long.parseLong(defaultValue);
		}
	}
	
	public Float getPreferenceFloat(String prefKey, String defaultReturn){
		String encryptedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueStr = mPreferences.getString(encryptedKeyField, defaultReturn);
		if(!encryptedValueStr.equals(defaultReturn)){
			return Float.parseFloat(decryptString(encryptedValueStr));				
		}else{
			return Float.parseFloat(defaultReturn);
		}
	}
	
	public Boolean getPreferenceBoolean(String prefKey, String defaultReturn){
		String encryptedKeyField = encryptString(prefKey, prefKey);
		String encryptedValueStr = mPreferences.getString(encryptedKeyField, defaultReturn);
		if(!encryptedValueStr.equals(defaultReturn)){
			return Boolean.valueOf(decryptString(encryptedValueStr));				
		}else{
			return Boolean.valueOf(defaultReturn);
		}
	}
	
	public void removePreference(String prefKey){		
		mPreferences.edit().remove(encryptString(prefKey, prefKey)).commit();
	}
	
//	public void clearPreferences(){
//		mPreferences.edit().clear().commit();
//	}
}
