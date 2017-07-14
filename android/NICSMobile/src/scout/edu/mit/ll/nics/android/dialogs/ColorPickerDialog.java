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
package scout.edu.mit.ll.nics.android.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.utils.Constants;

public class ColorPickerDialog extends DialogFragment implements OnTouchListener {

	private ImageView mColorGridView;
	private Dialog mDialog;
	private int mColor;
	
	public ColorPickerDialog() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_color_picker, container);
		
        mColorGridView = (ImageView) view.findViewById(R.id.color_picker_image_view);
        mColorGridView.setOnTouchListener(this);
        
        mDialog = this.getDialog();
        mDialog.setTitle(R.string.color_picker_title);
        return view;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(v.getId() == R.id.color_picker_image_view) {
			int action = event.getAction();
            switch(action) {
	            case(MotionEvent.ACTION_DOWN):
	                int x = (int)event.getX();
	                int y = (int)event.getY();
	
	                try {
	                	mColor = findColor(v, x, y);
	                	
	    				Intent intent = new Intent();
	    				intent.putExtra("pickedColor", mColor);
	    				getTargetFragment().onActivityResult(200, 0, intent);
	    				dismiss();
	                } catch (NullPointerException e) {
	                        return false;
	                }
	                break;
	              
	            case(MotionEvent.ACTION_UP):
	            	v.performClick();
	                break;
            }
		}
		
		return true;
	}
		
	public static int findColor(View view, int x, int y) throws NullPointerException {
	    int red = 0;
		int green = 0;
		int blue = 0;
		int color = 0;
		
		int offset = 1; // 3x3 Matrix
		int pixelsNumber = 0;
		
		int xImage = 0;
		int yImage = 0;
		
		// Get the bitmap from the view.
		ImageView imageView = (ImageView)view;
		BitmapDrawable bitmapDrawable = (BitmapDrawable)imageView.getDrawable();
		Bitmap imageBitmap = bitmapDrawable.getBitmap();
		
		// Calculate the target in the bitmap.
		xImage = (int)(x * ((double)imageBitmap.getWidth() / (double)imageView.getWidth()));
		yImage = (int)(y * ((double)imageBitmap.getHeight() / (double)imageView.getHeight()));
		
		// Average of pixels color around the center of the touch.
		for (int i = xImage - offset; i <= xImage + offset; i++) {
		        for (int j = yImage - offset; j <= yImage + offset; j++) {
		                try {
		                        color = imageBitmap.getPixel(i, j);
		                        red += Color.red(color);
		                        green += Color.green(color);
		                        blue += Color.blue(color);
		                        pixelsNumber += 1;
		                } catch(Exception e) {
		                        //Log.w(TAG, "Error picking color!");
		                }       
		        }
		}

		if(pixelsNumber != 0) {
			red = red / pixelsNumber;
			green = green / pixelsNumber;
			blue = blue / pixelsNumber;
		}

    	Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Color: " + red + "," + green + "," + blue);
    	
		return Color.rgb(red, green, blue); 
	}
	
	public int getColor() {
		return mColor;
	}
}
