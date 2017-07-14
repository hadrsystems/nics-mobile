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
package scout.edu.mit.ll.nics.android.bugreporting;

import java.util.Date;

import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import scout.edu.mit.ll.nics.android.api.DataManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class AcraEmailSender  implements ReportSender {

	 private String emailUsername ;
	 private String emailPassword ;
	
	
    public AcraEmailSender(String emailUsername, String emailPassword){
        // initialize your sender with needed parameters
    	
    	  super();
    	  this.emailUsername = emailUsername;
    	  this.emailPassword = emailPassword;
    }

	@Override
	public void send(CrashReportData report) throws ReportSenderException {
		
     // Extract the required data out of the crash report.
     String reportBody = createCrashReport(report);
     
     // instantiate the email sender
     GMailSender gMailSender = new GMailSender(emailUsername, emailPassword);
     
     try {
      // specify your recipients and send the email
      gMailSender.sendMail("CRASH REPORT", reportBody, emailUsername, emailUsername);
     } catch (Exception e) {
      Log.d("Error Sending email", e.toString());
     }
    }


    /** Extract the required data out of the crash report.*/
    private String createCrashReport(CrashReportData report) {
     
    	DataManager mDataManager = DataManager.getInstance();
    	
     // I've extracted only basic information.
     // U can add loads more data using the enum ReportField. See below.
     StringBuilder body = new StringBuilder();
     try {
		body
		 .append("Device : " + report.getProperty(ReportField.BRAND) + "-" + report.getProperty(ReportField.PHONE_MODEL))
		 .append("\n")
		 .append("Android Version :" + report.getProperty(ReportField.ANDROID_VERSION))
		 .append("\n")
		 .append("App Version : " + mDataManager.getContext().getPackageManager().getPackageInfo(mDataManager.getContext().getPackageName(), 0).versionName)
		 .append("\n")
		 .append("Date of Crash : " + new Date( System.currentTimeMillis()) )
		 .append("\n")
		 .append("Locale : " + mDataManager.getLocale())
		 .append("\n")
		 .append("Incident : " + mDataManager.getActiveIncidentName() + "(" + mDataManager.getActiveIncidentId() + ")" )
		 .append("\n")
		 .append("CollabRoom : " + mDataManager.getSelectedCollabRoom().getName() + "(" + mDataManager.getSelectedCollabRoom().getCollabRoomId() + ")" )
		 .append("\n")
		 .append("CurrentView : " + mDataManager.getCurrentNavigationView())
		 .append("\n")
		 .append("Data Server : " + mDataManager.getServer())
		 .append("\n")
		 .append("Auth Server : " + mDataManager.getAuthServerURL() )
		 .append("\n")
		 .append("Geo Server : " + mDataManager.getGeoServerURL() )
		 .append("\n")
		 .append("\n")
		 .append("\n")
		 .append("LogCat : \n" + report.getProperty(ReportField.LOGCAT))
		 .append("\n")
		 .append("\n")
		 .append("\n")
		 .append("STACK TRACE : \n" + report.getProperty(ReportField.STACK_TRACE));
		 
		Log.d("ACRA_Crash_log", ReportField.STACK_TRACE.toString());
		
	} catch (NameNotFoundException e) {
		
		e.printStackTrace();
	}
     
     
     return body.toString();
    }


   }

