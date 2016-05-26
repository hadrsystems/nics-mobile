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
package scout.edu.mit.ll.nics.android.api.tasks;

import java.util.ArrayList;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.WeatherReportPayload;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;


public class MarkAllReportsAsReadTask extends AsyncTask<FormType,String,String> {

	Context mContext;
	DataManager mDataManager;
	
	public MarkAllReportsAsReadTask(Context context) {
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);
	}

	  @Override
	  protected String doInBackground(FormType... params) {
		  
		  FormType type = params[0];
		  
		  switch (type){
		    case SR: 
				ArrayList<SimpleReportPayload> simpleReports = mDataManager.getSimpleReportHistoryForIncident(mDataManager.getActiveIncidentId());
				mDataManager.deleteSimpleReportFromHistoryByIncident(mDataManager.getActiveIncidentId());
				
				for (SimpleReportPayload payload : simpleReports){
					payload.setNew(false);
					mDataManager.addSimpleReportToHistory(payload);
				}
		    	break;
			case ABC:
				break;
			case AGRRPT:
				break;
			case ASSGN:
				break;
			case DR:
				ArrayList<DamageReportPayload> damageReports= mDataManager.getDamageReportHistoryForIncident(mDataManager.getActiveIncidentId());
				mDataManager.deleteDamageReportFromHistoryByIncident(mDataManager.getActiveIncidentId());
				
				for (DamageReportPayload payload : damageReports){
					payload.setNew(false);
					mDataManager.addDamageReportToHistory(payload);
				}
				break;
			case FR:
				break;
			case NINE_110:
				break;
			case RESC:
				break;
			case RESREQ:
				break;
			case ROC:
				break;
			case SITREP:
				break;
			case SVRRPT:
				break;
			case TASK:
				break;
			case TWO_15:
				break;
			case WR:
				ArrayList<WeatherReportPayload> weatherReports = mDataManager.getWeatherReportHistoryForIncident(mDataManager.getActiveIncidentId());
				mDataManager.deleteWeatherReportFromHistoryByIncident(mDataManager.getActiveIncidentId());
				
				for (WeatherReportPayload payload : weatherReports){
					payload.setNew(false);
					mDataManager.addWeatherReportToHistory(payload);
				}
				break;
			default:
				break;
		  }
		  
	   return "";
	  }

	  @Override
	  protected void onPostExecute(String result) {
	        Intent intent = new Intent();
	        intent.setAction(Intents.nics_MARKING_ALL_REPORTS_READ_FINISHED);
	        mContext.sendBroadcast (intent);
	  }

	  @Override
	  protected void onPreExecute() {

	  }


	  @Override
	  protected void onProgressUpdate(String... text) {

	  }
	 }
