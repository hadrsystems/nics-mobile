package edu.mit.ll.utility.display;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;


public class DisplayResolution {


    private static final String TAG = "DisplayResolution";

    private static Context sContext;// used in Frag
    private static Window sWindow;
    private static Resources sResources;
    private View mView;


    /**
     * THIS METHOD MUST BE CALLED BEFORE setContentView()
     * 
     * @param window
     */
    public static void setWindowUseAll(Window window) {

        window.requestFeature(Window.FEATURE_NO_TITLE);

        window.setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);

        window.addFlags(LayoutParams.FLAG_DISMISS_KEYGUARD);
    }


    /* Compute the number of physical pixels that aren't owned by the OS, such
     * as the title bar, status bar, and oyster bar. */

    /**
     * Get available display dimensions, width and height. Some of the height is
     * used up in the titlebar.
     * 
     * @param activity
     * @return int[] = { width, height, density, status_bar_height }
     * 
     */
    public static int[] getAvailableScreenSize(Activity activity) {

        // get physical width, height, density
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);


        // Get status bar height
        int status_bar_height = 0;
        int resourceId = activity.getResources().getIdentifier(
                "status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            status_bar_height = activity.getResources().getDimensionPixelSize(
                    resourceId);
        }

        int[] dim = new int[] {
                metrics.widthPixels,
                metrics.heightPixels,
                metrics.densityDpi,
                status_bar_height };

        msgLogArray("GetAvailFun", dim, 1);

        return dim;
    }


    public static void msgLog(String msg, int val) {
        if (0 <= val) {
            Log.v(TAG, msg);
        }
    }


    public static void msgLogArray(String title, int[] arr, int val) {
        msgLog(title, val);
        for (int n = 0; n < arr.length; n++) {
            msgLog(String.valueOf(arr[n]) + " ", val);
        }
    }


}// ~
