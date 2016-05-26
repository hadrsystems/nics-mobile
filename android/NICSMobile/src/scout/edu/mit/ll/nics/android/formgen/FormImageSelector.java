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
package scout.edu.mit.ll.nics.android.formgen;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;


public class FormImageSelector extends FormWidget {
	protected static final int CAMERA_REQUEST = 100;
	protected static final int SELECT_PHOTO = 200;
	
	private DataManager mDataManager;
	
	private LinearLayout mButtonLayout;
	private Button mBrowseImageButton;
	private Button mCaptureImageButton;
	
	private ImageView mImageView;
	private ProgressBar mImageProgressBar;
	
	private AlertDialog mAlertDialog;
	
	private Bitmap mSelectedImage;
	
	private DownloadImageTask mImageDownloadTask;
	private Header[] mBase64Auth;
	private Options mBitmapOptions;
	
	private Uri mImageUri;
	
	public FormImageSelector(FragmentActivity context, String name, String displayText, boolean enabled, OnFocusChangeListener listener,Fragment fragment) {
		super(context, name, displayText,fragment);

		mEnabled = enabled;
		mLayoutInflater.inflate(R.layout.form_imageselector, mLayout);

		mDataManager = DataManager.getInstance(context);
	        
		mBase64Auth = mDataManager.getAuthData();
		mBitmapOptions = new Options();
		mBitmapOptions.inJustDecodeBounds = true;
	
		mImageView = (ImageView) mLayout.findViewById(R.id.form_image_selector_preview);
		
		mImageProgressBar = (ProgressBar) mLayout.findViewById(R.id.form_image_selector_progress);
		mImageProgressBar.setVisibility(View.GONE);
		
		mButtonLayout = (LinearLayout) mLayout.findViewById(R.id.form_image_selector_button_layout);
		
		mBrowseImageButton = (Button) mLayout.findViewById(R.id.form_image_selector_browse_image_button);
		mCaptureImageButton = (Button) mLayout.findViewById(R.id.form_image_selector_capture_image_button);
		
		mBrowseImageButton.setOnClickListener(onBrowseButtonClick);
		mCaptureImageButton.setOnClickListener(onCaptureButtonClick);
	}

	public void setValue(String value) {
		setValue(value, true);
	}
	
	public void setValue(String value, boolean download) {
		if(mImageDownloadTask != null) {
			mImageDownloadTask.cancel(true);
			mImageDownloadTask = null;
		}
		
		if(value != null && !value.isEmpty()) {
			mImageUri = Uri.parse(value);
			
			if(download) {
				mImageProgressBar.setVisibility(View.VISIBLE);
				mImageDownloadTask = (DownloadImageTask) new DownloadImageTask(mImageView).execute(value);
			}
		} else {
			mImageUri = null;
			mImageProgressBar.setVisibility(View.GONE);
		}
		
		if(download) {
			mImageView.setImageBitmap(null);
			mImageView.destroyDrawingCache();
		}
	}
	
	@Override
	public String getValue() {
		if(mImageUri != null) {
			return getRealPathFromURI(mImageUri);
		}
		
		return "";
	}

	@Override
	public void setEditable(boolean editable) {
		if(editable) {
			mButtonLayout.setVisibility(View.VISIBLE);
		} else {
			mButtonLayout.setVisibility(View.GONE);
		}
	}
	
	private OnClickListener onBrowseButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

		//getActivity returns null when phone is rotated while formview is open
//			mFragment.getActivity().startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			Activity activity = (Activity)mDataManager.getActiveActivity();
			activity.startActivityForResult(photoPickerIntent, SELECT_PHOTO);
		}
	
	};
	
	
	
	private OnClickListener onCaptureButtonClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		
		//getActivity returns null when phone is rotated while formview is open
//			mFragment.getActivity().startActivityForResult(cameraIntent, CAMERA_REQUEST);
			Activity activity = (Activity)mDataManager.getActiveActivity();
			activity.startActivityForResult(cameraIntent, CAMERA_REQUEST);
		}
		
	};

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
		 	case CAMERA_REQUEST:
		 		if(intent != null) {
			 		mImageUri = intent.getData();
			 		
			 		if(intent.getExtras() == null){
			 			return;
			 		}
			 		
			 		mSelectedImage = (Bitmap) intent.getExtras().get("data");
			 		
			 		Uri alternate = getImageUri(mContext, mSelectedImage);
			 		
			 		if(mImageUri == null && mSelectedImage != null) {
			 			mDataManager.addPersonalHistory(mContext.getString(R.string.using_alt_image_path_for_general_message));
			 			mImageUri = alternate;
			 		}
		 		}
		        break;
		    case SELECT_PHOTO:
		    	if(intent != null) {
			    	mImageUri = intent.getData();
					InputStream imageStream = null;
					try {
						imageStream = mContext.getContentResolver().openInputStream(mImageUri);
//						File test = new File(getRealPathFromURI(mImageUri));
//						if(!test.exists()) {
//							mImageUri = null;
//							mAlertDialog = new AlertDialog.Builder(mContext).create();
//							mAlertDialog.setTitle(mContext.getString(R.string.selected_image_not_available_title));
//							mAlertDialog.setMessage(mContext.getString(R.string.selected_image_not_available_desc));
//							mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									mAlertDialog.dismiss();
//									mAlertDialog = null;
//								}
//							});
//							mAlertDialog.show();
//						} else {
					        mSelectedImage = BitmapFactory.decodeStream(imageStream);
	
							Matrix orientationMatrix = findImageOrientation(mImageUri);
							mSelectedImage = Bitmap.createBitmap(mSelectedImage, 0, 0, mSelectedImage.getWidth(), mSelectedImage.getHeight(), orientationMatrix, true);							
//						}
					} catch (FileNotFoundException e) {
						mImageUri = null;
						mAlertDialog = new AlertDialog.Builder(mContext).create();
						mAlertDialog.setTitle(mContext.getString(R.string.selected_image_not_available_title));
						mAlertDialog.setMessage(mContext.getString(R.string.selected_image_not_available_desc));
						mAlertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mAlertDialog.dismiss();
								mAlertDialog = null;
							}
						});
						mAlertDialog.show();
						e.printStackTrace();
					}
		    	}
	    }
		
		if(mSelectedImage != null) {
//			mSelectedImage = Bitmap.createScaledBitmap(mSelectedImage, /*mSelectedImage.getWidth()/2, mSelectedImage.getHeight()/2*/ 100,100, true);
			mImageView.setImageBitmap(mSelectedImage);
			setValue(getRealPathFromURI(mImageUri), false);
		}
	}
	
	private Matrix findImageOrientation(Uri imageUri) {
		ExifInterface exifInterface;
		int orientation = -1;
		
		try {
			exifInterface = new ExifInterface(getRealPathFromURI(imageUri));
			orientation = Integer.parseInt(exifInterface.getAttribute(ExifInterface.TAG_ORIENTATION));
		} catch (IOException e) {
			orientation = ExifInterface.ORIENTATION_UNDEFINED;
		}
		
		if(orientation == ExifInterface.ORIENTATION_UNDEFINED) {
			String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
			Cursor cur = mContext.getContentResolver().query(imageUri, orientationColumn, null, null, null);
			orientation = -1;
			if (cur != null && cur.moveToFirst()) {
			    orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
			} 
		}
		
		Matrix matrix = new Matrix();
		
		switch (orientation) {
			case (ExifInterface.ORIENTATION_ROTATE_90):
				matrix.postRotate(90);
				break;
				
			case (ExifInterface.ORIENTATION_ROTATE_180):
				matrix.postRotate(180);
				break;
				
			case (ExifInterface.ORIENTATION_ROTATE_270):
				matrix.postRotate(270);
				break;
				
			default:
				break;
		}
		
		return matrix;
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
	    return Uri.parse(path);
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;
	    String url;
		private Bitmap mIcon11;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	        bmImage.setImageDrawable(null);
	    }

	    protected Bitmap doInBackground(String... urls) {
	        this.url = urls[0];
	        
			mContext.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mImageProgressBar.setVisibility(View.VISIBLE);
					
				}
			});
	        
			if(url.equals("0")){	//side step: url sometimes equals "0" instead of null. should track down at some point
				url = null;
			}
			
	        if(url != null && !url.isEmpty()) {
	        	Uri test = Uri.parse(url);
	        	
	            File file = new File(Environment.getExternalStorageDirectory(), "/nics/simple_reports/" + test.getLastPathSegment());
	            if(test.getScheme() != null && !file.exists()) {
	        		mIcon11 = downloadImageFromURL(url, file);
		        } else {
					InputStream imageStream = null;
					try {
						if(file.exists()) {
							mImageUri = Uri.fromFile(file);
						} else {
							mImageUri = Uri.fromFile(new File(url));
						}
						imageStream = mContext.getContentResolver().openInputStream(mImageUri);
						
			            BitmapFactory.decodeStream(imageStream, null, mBitmapOptions);
			            imageStream.close();
			            int width = mBitmapOptions.outWidth;
			            int height = mBitmapOptions.outHeight;
			            
			            double scale = 1;
			            if(width > height) {
			            	scale = width/1024.0d;
			            } else {
			            	scale = height/1024.0d;
			            }

						imageStream = mContext.getContentResolver().openInputStream(mImageUri);
			            Options temp = new Options();
			            temp.inSampleSize = (int)scale;
			            mIcon11 = BitmapFactory.decodeStream(imageStream, null, temp);
			            
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

		        }
	        }
		        	
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	if(result != null && mImageUri != null) {
				Matrix orientationMatrix = findImageOrientation(mImageUri);
				result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), orientationMatrix, true);
		        bmImage.setImageBitmap(result);
		        mSelectedImage = result;
		        
		        bmImage.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(mImageUri, "image/png");
						mContext.startActivity(intent);
					}
				});
		        mContext.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mImageProgressBar.setVisibility(View.GONE);
						
					}
				});
	    	}
	    }
	    
	    @Override
	    protected void onCancelled() {
	    	try {
	    	super.onCancelled();
		    	mContext.runOnUiThread(new Runnable() {
	
					@Override
					public void run() {
						if(mImageProgressBar != null) {
							mImageProgressBar.setVisibility(View.GONE);
						}
						
					}
				});
	    	} catch (Exception e) {
	    	}
	    }
	}
	
	private String getRealPathFromURI(Uri contentURI) {
		String path = "";
		try {
			if(contentURI != null) {
		        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null); 
		        if (cursor == null)
		            path = contentURI.getPath();
		        else {
		            cursor.moveToFirst(); 
		            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
		            path = cursor.getString(idx); 
			        
		        	cursor.close();
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public Bitmap downloadImageFromURL(String url, File file) {
        URLConnection connection = null;
        InputStream in = null;
        
        Bitmap icon = null;
        
        try {
        	if(url.startsWith("http://")) {
        		connection = (HttpURLConnection) new URL(url).openConnection();
        	} else if(url.startsWith("https://")) {
        		connection = (HttpsURLConnection) new URL(url).openConnection();
        	}

        	connection.setRequestProperty (mBase64Auth[0].getName(), mBase64Auth[0].getValue());
            in = connection.getInputStream();
            
            BitmapFactory.decodeStream(in, null, mBitmapOptions);
            in.close();
            int width = mBitmapOptions.outWidth;
            int height = mBitmapOptions.outHeight;
            
            double scale = 1;
            if(width > height) {
            	scale = width/1024.0d;
            } else {
            	scale = height/1024.0d;
            }
            
        	if(url.startsWith("http://")) {
        		connection = (HttpURLConnection) new URL(url).openConnection();
        	} else if(url.startsWith("https://")) {
        		connection = (HttpsURLConnection) new URL(url).openConnection();
        	}
        	connection.setRequestProperty("Cookie", "iPlanetDirectoryPro=" + mDataManager.getAuthToken() + "; AMAuthCookie=" + mDataManager.getAuthToken());
        	
        	connection.setRequestProperty (mBase64Auth[0].getName(), mBase64Auth[0].getValue());
            in = connection.getInputStream();
            Options temp = new Options();
            temp.inSampleSize = (int)scale;
            icon = BitmapFactory.decodeStream(in, null, temp);
            
            if(icon != null) {
				File f = new File(Environment.getExternalStorageDirectory() + "/nics");
				if(!f.exists()) {
					f.mkdir();
				}
				
				f = new File(Environment.getExternalStorageDirectory() + "/nics/simple_reports/");
				if(!f.exists()) {
					f.mkdir();
				}
	            FileOutputStream out = new FileOutputStream(file);
	            icon.compress(Bitmap.CompressFormat.JPEG, 100, out);
	            mImageUri = Uri.fromFile(file);
            } else {
            	
            }
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (MalformedURLException e) {
        	e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
        	try {
        		if(in != null) {
        			in.close();
        		}
        		
        		if(connection != null) {
                	if(url.startsWith("http://")) {
                		((HttpURLConnection)connection).disconnect();
                	} else if(url.startsWith("https://")) {
                		((HttpsURLConnection)connection).disconnect();
                	}
        		}
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
		return icon;
	}
}
