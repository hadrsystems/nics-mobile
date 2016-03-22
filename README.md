#  NICS Mobile (Android / iOS) 

## Synopsis

Contains the projects files for the Android and iOS version of NICS Mobile

##Dependencies

###Android

NICS Mobile is only compatible with Eclipse

When creating your Eclipse workspace, import all the packages in the "nics-mobile/android" directory
  <br>- Right click the project explorer window
  <br>- Select Import then in the next menu select import again
  <br>- In the import open the Android dropdown and select Existing Android Code into Workspace
  <br>- Browse to the android directory of the repository and press the open button with the android folder selected
  <br>- You should see the following 8 packages in the import window. Make sure all are checked and press the finish button

<ul>
  <li>NICSMobile</li>
  <li>nicsAndroidAPI</li>
  <li>android-async-http</li>
  <li>google-play-services_lib</li>
  <li>google-support-v7-appcompat</li>
  <li>gridlayout_v7</li>
  <li>nasa-worldwind-coordinate-converter</li>
  <li>nmea-handler_lib</li>
</ul>

Now that everything is imported you need to configure the dependencies of each package.

The NicsMobile and NicsAndroidAPI package dependencies should be preconfigured in each package's properties menu when imported to match the structure below. You can check these by right clicking each NICS project and selecting Properties then Android.

NICSMobile
<ul>
  <li>nicsAndroidAPI</li>
  <li>nmea-handler_lib</li>
  <li>gridlayout_v7</li>
  <li>google-support-v7-appcompat</li>
</ul>

NICSAndroidAPI  (Is Library)
<ul>
  <li>android-async-http</li>
  <li>google-play-services_lib</li>
  <li>gridlayout_v7</li>
  <li>nasa-worldwind-coordinate-converter</li>
</ul>

You can deploy the app by building the "NICSMobile" project as an Android Application in Eclipse.

## Configuration

###Android

You will also need to enter your Google Maps API key here which you can register for on Googles developer console: https://developers.google.com/maps/documentation/android-api/signup. That key needs to be placeing in the config_strigns.xml file mentioned below.

You can manually enter your server information into the app after it is build from the settings menu within the app if you would like to change the server at runtime. Or you can edit the config file to store your server info into the config_strings.xml file.

The configuration file can be found at this path: nics_mobile/android/NICSAndroidAPI/res/values/config_strings.xml

This is where you will enter all of your NICS web configuration information.

The App is setup to allow you to easily toggle between multiple NICS instances from the settings menu within the app. If you only have one instance then you can disregard the second item in each string array or remove them. 

NICS Mobile is setup to use Application Crash Reports for Android (ACRA-https://github.com/ACRA/acra) to auto send crash reports to a Gmail account of your choice and you can configure this at the bottom of the config_strings.xml


###iOS

NICS Mobile uses Cocoa Pods which requires you to open it using the "NICS Mobile.xcworkspace" file instead of the typical ".xcodeproj" file.

The configuration file can be found at this path: nics_mobile/ios/NICS Mobile/Localized/Settings.bundle/Root.plist

The bottom half of the file is what needs to be configured. Starting at the "Select NICS Server" field. The fields get configured with the same info that is used in the Android config.

Your Google Maps API key needs to be entered in the AppDelegate.m file at this path nics_mobile/ios/NICS Mobile/AppDelegate.m)
