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
package edu.mit.ll.phinics.android.bugreporting;

import android.app.Application;

import org.acra.*;
import org.acra.annotation.*;

import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.R.string;

//@ReportsCrashes(
//        formKey = "", // This is required for backward compatibility but not used
//        mailTo = "phinics.mit.ll@gmail.com",
//        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
//        mode = ReportingInteractionMode.DIALOG,
////        resToastText = R.string.crash_toast_text
//        
////        resDialogText = "Dialog Text",	// R.string.crash_dialog_text,
////        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
////        resDialogTitle = "Dialog Title", // optional. default is your application name
//        resDialogCommentPrompt = R.string.crash_toast_text, // optional. When defined, adds a user text field input with this text resource as a label
//        resDialogEmailPrompt = R.string.crash_toast_text, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
//        resDialogOkToast = R.string.crash_toast_text // optional. displays a Toast message when the user accepts to send a report.
//        
		
//        formKey="dGVacG0ydVHnaNHjRjVTUTEtb3FPWGc6MQ",
//        		mailTo = "phinics.mit.ll@gmail.com",
//        mode = ReportingInteractionMode.DIALOG,
//        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
//        resDialogText = R.string.crash_dialog_text,
//        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
//        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
//        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
//        resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
//        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
       
        
//		)
    

@ReportsCrashes(
		formKey = ""
//			customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }
)

public class AcraApplication extends Application{
	
    @Override
    public void onCreate() {
        super.onCreate();

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        
        // instantiate the report sender with the email credentials.
        // these will be used to send the crash report
        AcraEmailSender reportSender = new AcraEmailSender("NicsMobileLogs.mit.ll@gmail.com", "Cra$hLog1!");
        
        // register it with ACRA.
        ACRA.getErrorReporter().setReportSender(reportSender);
    }
}
