/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.payload.forms.SimpleReportPayload;

public class UnreadMessageManager {

	/*
	 IncidentList holds incidentMessageContainers for each incident you have read a message in.
	 When you read a message this script will find the container for the current active incident + user.
	 incidentname + username is used to lookup read message list to support multiple users on one device.
	 once the current active incident is found it takes the message id and uses that for the index of the viewedMessages list.
	 It marks the corresponding index to true once it has been opened.
	 */
	
	class IncidentMessageContainer{
		String userIncidentName;
		List<Boolean> viewedMessages;
		
		IncidentMessageContainer(String _userIncidentName, boolean FromFile)
		{
			userIncidentName = _userIncidentName;
			viewedMessages = new ArrayList<Boolean>();
			
			if(FromFile == false){
				for(int i = 0; i < 50;i++){
					viewedMessages.add(false);
				}
			}
		}
	}
	
	private List<IncidentMessageContainer> incidentList = null;

	private DataManager mDataManager;
	private String filename = "readMessageCache";
	
	public UnreadMessageManager(DataManager _dataManager){
		incidentList = new ArrayList<IncidentMessageContainer>();
		mDataManager = _dataManager;
	}
	
	public void AddMessageToList(long id){
		
		if(incidentList.size() <= 0){		
			incidentList.add(new IncidentMessageContainer(getCurrentUserIncidentName(),false));
			AddMessageToList(id);
		}else{
			boolean incidentFound = false;
			for(int i = 0; i < incidentList.size(); i++){				
				if(incidentList.get(i).userIncidentName.equals(getCurrentUserIncidentName()))	//if incident already exists then add a message
				{
					incidentFound = true;
					if((int)id >= incidentList.get(i).viewedMessages.size()){
						
						int originalViewedMessagesSize = incidentList.get(i).viewedMessages.size();
						for(int j = 0; j < (int)id - originalViewedMessagesSize +2; j++){
							incidentList.get(i).viewedMessages.add(false);
						}
						incidentList.get(i).viewedMessages.set((int) id, true);
					}else{
						incidentList.get(i).viewedMessages.set((int) id, true);
					}
					i += incidentList.size(); //break loop
				}
			}
			
			if(incidentFound == false)	//if incident name doesn't exist yet, then add it
			{
				incidentList.add(new IncidentMessageContainer(getCurrentUserIncidentName(),false));
				AddMessageToList(id);
			}
			
		}
	}
	public boolean GetValueAtLocation(long id)
	{
		for(int i = 0; i < incidentList.size(); i++){
			if(incidentList.get(i).userIncidentName.equals(getCurrentUserIncidentName())){
				if((int)id >= incidentList.get(i).viewedMessages.size()){
					return false;
				}
				return incidentList.get(i).viewedMessages.get((int) id);
			}
		}
		return false;
	}
	public void clearList(){
		incidentList.clear();
	}
	public int getIncidentSize(){
		return incidentList.size();
	}
	public int getMessageListSize()
	{
		for(int i = 0; i < incidentList.size(); i++){
			if(incidentList.get(i).userIncidentName.equals(getCurrentUserIncidentName())){
				return incidentList.get(i).viewedMessages.size();
			}
		}
		return 0;
	}
	public void MarkAllMessagesAsRead()
	{
		ArrayList<SimpleReportPayload> payload = mDataManager.getSimpleReportHistoryForIncident(mDataManager.getActiveIncidentId());
		
		for(int payloadCount = 0; payloadCount < payload.size(); payloadCount++){
			AddMessageToList(payload.get(payloadCount).getId());
		}
		
		payload = mDataManager.getAllSimpleReportStoreAndForwardReadyToSend(mDataManager.getActiveIncidentId());
		
		for(int payloadCount = 0; payloadCount < payload.size(); payloadCount++){
			AddMessageToList( payload.get(payloadCount).getId());
		}
	}
	private int getActiveIncidentID()
	{
		int activeIncidentId = -1;
		//find active incident index
		for(int i = 0; i < incidentList.size();i++)
		{
			if(incidentList.get(i).userIncidentName.equals(getCurrentUserIncidentName()))
			{
				activeIncidentId = i;
				i += incidentList.size();
			}
		}
		return activeIncidentId;
	}
	public void SaveToFile()
	{
		File file = new File(filename);
		Context context = mDataManager.getContext();
		
		try{
		OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, context.MODE_PRIVATE));

			 for(int incidentCount = 0; incidentCount < incidentList.size(); incidentCount++){
			 outputStreamWriter.write(incidentList.get(incidentCount).userIncidentName + "\n");
			 
				 for(int messageCount = 0; messageCount< incidentList.get(0).viewedMessages.size(); messageCount++){
					 outputStreamWriter.write(incidentList.get(incidentCount).viewedMessages.get(messageCount).toString() + "\n");
				 }
			 }
			  outputStreamWriter.close();
			
		}catch (Exception e){
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}
	public void LoadFromFile()
	{		
		Context context = mDataManager.getContext();
		try {
			
			File file = new File(filename);
			if(!file.exists()){
		    	SaveToFile();
			}
			
			InputStream inputStream = context.openFileInput(filename);
			if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				try {
					int incidentCount = -1;
					receiveString = bufferedReader.readLine();
		            while ( receiveString != null ) {
		            	if(receiveString.equals("true")){
		            		incidentList.get(incidentCount).viewedMessages.add(true);
		            	}else if(receiveString.equals("false")){
		            		incidentList.get(incidentCount).viewedMessages.add(false);
		            	}else{
		            		incidentList.add(new IncidentMessageContainer(receiveString,true));
		            		incidentCount++;
		            	}
		            	receiveString = bufferedReader.readLine();
		            }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    catch (FileNotFoundException e) {
	    	Log.e("login activity", "File not found: " + e.toString());
		}
	}
	private String getCurrentUserIncidentName(){
		return (mDataManager.getActiveIncidentName() + mDataManager.getUsername());
	}
}
