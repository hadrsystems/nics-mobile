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
package scout.edu.mit.ll.nics.android.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.res.Resources;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.Vector2;
import scout.edu.mit.ll.nics.android.api.payload.TrackingLayerPayload;
import scout.edu.mit.ll.nics.android.maps.markup.MapMarkupInfoWindowAdapter;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupBaseShape;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupCircle;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupFireLine;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupRectangle;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupSegment;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupSymbol;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupText;
import scout.edu.mit.ll.nics.android.maps.markup.MarkupType;
import scout.edu.mit.ll.nics.android.maps.markup.Symbols;
import scout.edu.mit.ll.nics.android.maps.markup.layers.DamageReportLayer;
import scout.edu.mit.ll.nics.android.maps.markup.layers.MarkupLayer;
import scout.edu.mit.ll.nics.android.maps.markup.layers.SimpleReportLayer;
import scout.edu.mit.ll.nics.android.maps.markup.tileprovider.MarkupFeatureTileProvider;
import scout.edu.mit.ll.nics.android.maps.markup.tileprovider.MarkupWMSTileProvider;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.LocationHandler;
import scout.edu.mit.ll.nics.android.utils.MarkupCoordinateManager;

public class MapMarkupFragment extends Fragment implements OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener, OnLocationChangedListener, OnTouchListener {
	protected Activity mContext;
	private MarkupCoordinateManager mCoordinateManager;
	private SupportMapFragment mMapFragment;
	private LatLng mLastPressLocation;
	protected static GoogleMap mMap;
	private static int mMapType = GoogleMap.MAP_TYPE_NORMAL;

	private View mRootView;
	private View mMapClickView;

	private Button mCoordinateOKButton;
	private Button mCoordinateCancelButton;

	private Menu mMenu;

	private MarkupBaseShape mCurrentShape = null;
	private String mCurrentShapeId = null;
	private Stack<MarkupBaseShape> mUndoStack;
	private HashMap<String, MarkupBaseShape> mTempShapes;
	private HashMap<String, MarkupBaseShape> mMarkupShapes;
	private DataManager mDataManager;
	private ArrayList<MarkupFeature> mFeatures;
	private IntentFilter mMarkupReceiverFilter;
	private IntentFilter mCollabRoomSwitchedFilter;
	private IntentFilter mIncidentSwitchedFilter;
	private IntentFilter mLocalMapDataClearedFilter;
	private IntentFilter mMarkupFailedToPostFilter;
	private ImageButton mIncidentFocusButton;
	protected Button mPickerCompleteButton;	
	private boolean markerReceiverRegistered;
	private int mCurrentSymbolResourceId = -1;
	private MarkerOptions mJunctionBitmapOptions;
	private ArrayList<MarkupFireLine> mFirelineFeatures;
	private GsonBuilder mBuilder;

	private ProgressBar mShapesListProgress;

	private ListView mShapesListView;
	private ArrayAdapter<MarkupFeature> mShapesAdapter;
	private int[] mColor = new int[] { 255, 255, 255, 255 };
	private boolean mIgnoreUpdate;

	private boolean mFirstLoad = true;
	private FragmentManager mFragmentManager;
	private LocationHandler handler;

	private boolean addingMarkupEnabled = true;

	private static HashMap<String, MarkupLayer> mMarkupLayers;

	private static AsyncTask<ArrayList<MarkupFeature>, Object, Integer> mRenderMarkupFeaturesTask;
	private MarkupFeatureTileProvider markupTileProvider;
	private static MarkupWMSTileProvider wmsTileProvider;
	private TileOverlay tileOverlay;
	private static TileOverlay wmsTileOverlay;
	private InfoWindowAdapter mInfoWindowAdapter;

	private MarkerOptions markerOptionsTabletReport = null;
	private Marker markerTabletReport = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();

		mInfoWindowAdapter = new MapMarkupInfoWindowAdapter(mContext);
		mFragmentManager = getFragmentManager();
		mBuilder = new GsonBuilder();

		mMarkupShapes = new HashMap<String, MarkupBaseShape>();
		mTempShapes = new HashMap<String, MarkupBaseShape>();

		mUndoStack = new Stack<MarkupBaseShape>();

		mFeatures = new ArrayList<MarkupFeature>();
		mFirelineFeatures = new ArrayList<MarkupFireLine>();

		mDataManager = DataManager.getInstance(this.mContext);

		mMarkupLayers = new HashMap<String, MarkupLayer>();

		if (savedInstanceState != null) {
			mMapType = savedInstanceState.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL);
		} else {
//			SharedPreferences settings = this.mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0);
			EncryptedPreferences settings = new EncryptedPreferences( this.mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));
			mMapType = settings.getPreferenceLong("mapType", Integer.toString(GoogleMap.MAP_TYPE_NORMAL)).intValue();
		}

		mMarkupReceiverFilter = new IntentFilter(Intents.nics_NEW_MARKUP_RECEIVED);
		mCollabRoomSwitchedFilter = new IntentFilter(Intents.nics_COLLABROOM_SWITCHED);
		mIncidentSwitchedFilter = new IntentFilter(Intents.nics_INCIDENT_SWITCHED);
		mLocalMapDataClearedFilter = new IntentFilter(Intents.nics_LOCAL_MAP_FEATURES_CLEARED);
		mMarkupFailedToPostFilter = new IntentFilter(Intents.nics_FAILED_TO_POST_MARKUP);
		
		if (!markerReceiverRegistered) {
			Log.d("MarkupFragment", "receivers registereed onCreate");
			mContext.registerReceiver(markupReceiver, mMarkupReceiverFilter);
			mContext.registerReceiver(collabRoomSwitchedReceiver,mCollabRoomSwitchedFilter);
			mContext.registerReceiver(incidentSwitchedReceiver,mIncidentSwitchedFilter);
			mContext.registerReceiver(localMapDataClearedReceiver,mLocalMapDataClearedFilter);
			mContext.registerReceiver(mapFeatureFailedToPostReceiver,mMarkupFailedToPostFilter);
			markerReceiverRegistered = true;
		}
		addingMarkupEnabled = mDataManager.getSelectedCollabRoom().doIHaveMarkupPermission(mDataManager.getUserId());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		mMapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.markupMapFragment);

		if (mMapFragment == null) {
			int id = -1;
			if (savedInstanceState != null) {
				id = savedInstanceState.getInt("mapId", -1);
				mMapType = savedInstanceState.getInt("mapType", GoogleMap.MAP_TYPE_NORMAL);
			}

			if (id == -1) {
				if(mDataManager.getTabletLayoutOn()){
					mRootView = inflater.inflate(R.layout.fragment_mapmarkup_tablet, container, false);
				}else{
					mRootView = inflater.inflate(R.layout.fragment_mapmarkup, container, false);
				}
			} else {
				mMapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(id);

				if (mMapFragment != null) {
					mMap = mMapFragment.getMap();
					if (mMap != null && mMapType != -1) {
						mMap.setMapType(mMapType);
						if(wmsTileOverlay != null) {
							wmsTileOverlay.remove();
							wmsTileOverlay = null;
							wmsTileProvider = null;
						}
						
						if(mMapType == GoogleMap.MAP_TYPE_NONE) {
							wmsTileProvider = new MarkupWMSTileProvider(256, 256);
							TileOverlayOptions wmsOverlayOptions = new TileOverlayOptions();
							wmsOverlayOptions.tileProvider(wmsTileProvider);
							wmsTileOverlay = mMap.addTileOverlay(wmsOverlayOptions);
						}
					}
					mRootView = container.findViewById(R.layout.fragment_mapmarkup);
				}
			}

		}

		setHasOptionsMenu(true);

		return mRootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.mapmarkup, menu);
		mMenu = menu;

		if (mMap != null) {
			MenuItem trafficMenuItem = mMenu.findItem(R.id.trafficMapOption);
			trafficMenuItem.setChecked(mMap.isTrafficEnabled());

			MenuItem indoorMenuItem = mMenu.findItem(R.id.indoorMapOption);
			indoorMenuItem.setChecked(mMap.isIndoorEnabled());

			MenuItem mapItem = null;
			switch (mMap.getMapType()) {
			case GoogleMap.MAP_TYPE_NORMAL:
				mapItem = mMenu.findItem(R.id.normalMapOption);
				break;
			case GoogleMap.MAP_TYPE_SATELLITE:
				mapItem = mMenu.findItem(R.id.satelliteMapOption);
				break;
			case GoogleMap.MAP_TYPE_HYBRID:
				mapItem = mMenu.findItem(R.id.hybridMapOption);
				break;
			case GoogleMap.MAP_TYPE_TERRAIN:
				mapItem = mMenu.findItem(R.id.terrainMapOption);
				break;
			case GoogleMap.MAP_TYPE_NONE:
				mapItem = mMenu.findItem(R.id.offlineMapOption);
				break;
			default:
				mapItem = mMenu.findItem(R.id.normalMapOption);
			}

			if (mapItem != null) {
				mapItem.setChecked(true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addMarkupFromServer() {

		if (mCurrentShape == null && mCoordinateManager.getCurrentShapeType() == -1 && !mIgnoreUpdate && mRenderMarkupFeaturesTask == null) {
			try {
				// mShapesListView.setVisibility(View.GONE);
				// mShapesListProgress.setVisibility(View.VISIBLE);

				if (mRenderMarkupFeaturesTask == null) {
					mRenderMarkupFeaturesTask = new RenderMarkupFeaturesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mFeatures);
				}
			} catch (Exception e) {
				Log.e(Constants.nics_DEBUG_ANDROID_TAG, e.toString());
			}
		}
	}

	public Bitmap generateRotatedBitmap(String symbolPath) {
		Integer bitmapId = Symbols.ALL.get(symbolPath);
		if (bitmapId == null) {
			bitmapId = R.drawable.symbol;
		}

		return generateRotatedBitmap(bitmapId, 0);
	}

	public Bitmap generateRotatedBitmap(int resourceId, float rotationDegrees) {
		Bitmap bitmap = null;
		Options opts = new BitmapFactory.Options();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			opts.inMutable = true;
			bitmap = BitmapFactory.decodeResource(getResources(), resourceId, opts);
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(), resourceId, opts).copy(Config.ARGB_8888, true);
		}

		if (!this.isDetached()) {
			bitmap = BitmapFactory.decodeResource(getResources(), resourceId, opts);

			try {
				Matrix rotator = new Matrix();
				rotator.postRotate(rotationDegrees, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);

				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotator, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	public Bitmap generateTintedBitmap(int resourceId, int[] colorArray) {

		Bitmap bitmap = null;
		Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			opts.inMutable = true;
			bitmap = BitmapFactory.decodeResource(getResources(), resourceId, opts);
		} else {
			bitmap = BitmapFactory.decodeResource(getResources(), resourceId, opts).copy(Config.ARGB_8888, true);
		}

		if (bitmap != null) {
			Canvas test = new Canvas(bitmap);

			Paint paint = new Paint();

			int color = Color.argb(255, colorArray[1], colorArray[2], colorArray[3]);

			if (color != -1) {
				paint.setColorFilter(new PorterDuffColorFilter(color, Mode.SRC_IN));
			} else {
				paint.setColorFilter(new PorterDuffColorFilter(Color.WHITE, Mode.SRC_IN));
			}
			test.drawBitmap(bitmap, 0, 0, paint);
		}

		return bitmap;
	}

	@Override
	public void onResume() {
		super.onResume();
		mDataManager.setNewMapAvailable(false);
		mContext.registerReceiver(markupReceiver, mMarkupReceiverFilter);
		setUpMapIfNeeded();

		if (!markerReceiverRegistered) {
			Log.d("MarkupFragment", "receivers registereed onResume");
			mContext.registerReceiver(markupReceiver, mMarkupReceiverFilter);
			mContext.registerReceiver(collabRoomSwitchedReceiver,mCollabRoomSwitchedFilter);
			mContext.registerReceiver(incidentSwitchedReceiver,mIncidentSwitchedFilter);
			mContext.registerReceiver(localMapDataClearedReceiver,mLocalMapDataClearedFilter);
			mContext.registerReceiver(mapFeatureFailedToPostReceiver,mMarkupFailedToPostFilter);
			markerReceiverRegistered = true;
		}
		
		mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), true);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (handler != null) {
			handler.setUpdateRate(mDataManager.getMDTDataRate());
		}

		if (mRenderMarkupFeaturesTask != null) {
			mRenderMarkupFeaturesTask.cancel(true);
			mRenderMarkupFeaturesTask = null;
		}

		
		if (markerReceiverRegistered) {
			Log.d("MarkupFragment", "receivers unregistereed onPause");
			mContext.unregisterReceiver(markupReceiver);
			mContext.unregisterReceiver(collabRoomSwitchedReceiver);
			mContext.unregisterReceiver(incidentSwitchedReceiver);
			mContext.unregisterReceiver(localMapDataClearedReceiver);
			mContext.unregisterReceiver(mapFeatureFailedToPostReceiver);
			mCoordinateManager.unregisterReceivers();
			markerReceiverRegistered = false;
		}

		for (MarkupFireLine fireline : mFirelineFeatures) {
			try {
				fireline.removeFromMap();
				fireline.getFeature().setRendered(false);
			} catch (Exception e) {
			}
		}

		mFirelineFeatures.clear();
		markupTileProvider.setFirelineFeatures(mFirelineFeatures);
		tileOverlay.clearTileCache();

		try {
			for (MarkupBaseShape shape : mMarkupShapes.values()) {
				shape.removeFromMap();
				shape.getFeature().setRendered(false);
			}
		} catch (Exception e) {
		}
		mMarkupShapes.clear();

		mDataManager.stopPollingMarkup();

		saveMapState();

		for (MarkupLayer layer : mMarkupLayers.values()) {
			layer.clearFromMap();
			layer.unregister();
		}
		mMarkupLayers.clear();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// Save the ID of the existing MapFragment so it can properly be
		// restored when app resumes
		if (mMapFragment != null) {
			outState.putInt("mapId", mMapFragment.getId());
		}

		if (mMap != null) {
			outState.putInt("mapType", mMap.getMapType());
		}
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		if (mCoordinateManager.getCurrentShapeType() != -1) {
			onMapClick(mLastPressLocation);
			return true;
		}
		return false;
	}

	@Override
	public void onMapLongClick(LatLng arg0) {

	}

	@Override
	public void onMapClick(LatLng coordinate) {
		String newId = UUID.randomUUID().toString();
		switch (mCoordinateManager.getCurrentShapeType()) {

		case R.id.MarkupButtonSymbol:
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Adding symbol point @ " + coordinate.toString());
			if (mCurrentShape == null) {
				Bitmap symbolBitmap = generateRotatedBitmap(mCurrentSymbolResourceId, 0);
				String symbolPath = Symbols.ALL.getKey(mCurrentSymbolResourceId);
				MarkupSymbol symbol = new MarkupSymbol(mDataManager, mMap, "Marker", coordinate, symbolBitmap, symbolPath, new int[] { 255, 255, 255, 255 });
				symbol.setMarker(mMap.addMarker(symbol.getOptions()));
				symbol.setType(MarkupType.marker);
				
				mCurrentShape = symbol;
				mCurrentShapeId = newId;
				mMarkupShapes.put(newId, symbol);
				mUndoStack.push(mCurrentShape);

				mCoordinateManager.setCoordinates(0, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));

			} else {
				showConfirmDialog(getString(R.string.markup_finalize, getString(R.string.markup_symbol)), getString(R.string.markup_confirm_completion, getString(R.string.markup_symbol_lowercase)), "");
			}
			break;

		case R.id.MarkupButtonLine:
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Adding line point @ " + coordinate.toString());

			mJunctionBitmapOptions.position(coordinate);

			if (mCurrentShape != null) {
				if (mCurrentShape.getPoints().size() < 2) {
					mCurrentShape.addPoint(coordinate);
					mCoordinateManager.setCoordinates(mCurrentShape.getPoints().size() - 1, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
					mCurrentShape.addMarker(mMap.addMarker(mJunctionBitmapOptions));
				} else {
					showConfirmDialog(getString(R.string.markup_finalize, getString(R.string.markup_segment)), getString(R.string.markup_confirm_completion, getString(R.string.markup_segment_lowercase)), "");
				}
			} else {
				MarkupSegment segment = new MarkupSegment(mDataManager, "", coordinate, mColor);
				segment.setPolyline(mMap.addPolyline(segment.getOptions()));
				segment.addMarker(mMap.addMarker(mJunctionBitmapOptions));
				segment.setType(MarkupType.sketch);
				mCurrentShape = segment;
				mCurrentShapeId = newId;
				mMarkupShapes.put(newId, segment);
				mUndoStack.push(mCurrentShape);
				mCoordinateManager.setCoordinates(mCurrentShape.getPoints().size() - 1, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
			}
			break;

		case R.id.MarkupButtonRectangle:
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Adding rectangle point @ " + coordinate.toString());

			mJunctionBitmapOptions.position(coordinate);
			if (mCurrentShape != null) {
				if (mCurrentShape.getPoints().size() < 2) {
					LatLng origin = mCurrentShape.getPoints().get(0);

					mCurrentShape.addPoint(new LatLng(origin.latitude, coordinate.longitude));
					mCurrentShape.addPoint(coordinate);
					mCurrentShape.addPoint(new LatLng(coordinate.latitude, origin.longitude));

					mCoordinateManager.setCoordinates(1, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
					mCurrentShape.addMarker(mMap.addMarker(mJunctionBitmapOptions));
				} else {
					showConfirmDialog(getString(R.string.markup_finalize, getString(R.string.markup_rectangle)), getString(R.string.markup_confirm_completion, getString(R.string.markup_rectangle_lowercase)), "");
				}
			} else {
				MarkupRectangle rectangle = new MarkupRectangle(mDataManager, "", coordinate, mColor);
				rectangle.setPolygon(mMap.addPolygon(rectangle.getOptions()));
				rectangle.addMarker(mMap.addMarker(mJunctionBitmapOptions));
				rectangle.setType(MarkupType.square);
				
				mCurrentShape = rectangle;
				mCurrentShapeId = newId;
				mMarkupShapes.put(newId, rectangle);
				mUndoStack.push(mCurrentShape);
				mCoordinateManager.setCoordinates(0, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
			}
			break;

		case R.id.MarkupButtonTrapezoid:
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Adding trapezoid point @ " + coordinate.toString());

			mJunctionBitmapOptions.position(coordinate);

			if (mCurrentShape != null) {
				if (mCurrentShape.getPoints().size() < 4) {
					mCurrentShape.addPoint(coordinate);
					mCoordinateManager.setCoordinates(mCurrentShape.getPoints().size() - 1, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
					mCurrentShape.addMarker(mMap.addMarker(mJunctionBitmapOptions));
				} else {
					showConfirmDialog(getString(R.string.markup_finalize, getString(R.string.markup_trapezoid)), getString(R.string.markup_confirm_completion, getString(R.string.markup_trapezoid_lowercase)), "");
				}
			} else {
				MarkupRectangle trap = new MarkupRectangle(mDataManager, "", coordinate, mColor);
				trap.setType(MarkupType.polygon);
				trap.setPolygon(mMap.addPolygon(trap.getOptions()));
				trap.addMarker(mMap.addMarker(mJunctionBitmapOptions));

				mCurrentShape = trap;
				mCurrentShapeId = newId;
				mMarkupShapes.put(newId, trap);
				mUndoStack.push(mCurrentShape);

				mCoordinateManager.setCoordinates(mCurrentShape.getPoints().size() - 1, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
			}
			break;

		case R.id.MarkupButtonCircle:
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Adding circle point @ " + coordinate.toString());
			mJunctionBitmapOptions.position(coordinate);

			if (mCurrentShape != null) {
				if (mCurrentShape.getPoints().size() < 2) {
					MarkupCircle circle = (MarkupCircle) mCurrentShape;
					mCurrentShape.addPoint(coordinate);
					LatLng startCoord = mCurrentShape.getPoints().remove(0);

					float[] results = new float[3];
					Location.distanceBetween(startCoord.latitude, startCoord.longitude, coordinate.latitude, coordinate.longitude, results);

					circle.setRadius(results[0]);
					mCoordinateManager.setRadius(results[0]);
					circle.addMarker(mMap.addMarker(mJunctionBitmapOptions));
					circle.setCircle(mMap.addCircle((CircleOptions) mCurrentShape.getOptions()));
					circle.setType(MarkupType.circle);
				} else {
					showConfirmDialog(getString(R.string.markup_finalize, getString(R.string.markup_circle)), getString(R.string.markup_confirm_completion, getString(R.string.markup_circle_lowercase)), "");
				}
			} else {
				MarkupCircle circle = new MarkupCircle(mDataManager, "", coordinate, 0, mColor);
				circle.addMarker(mMap.addMarker(mJunctionBitmapOptions));

				mCurrentShape = circle;
				mCurrentShapeId = newId;
				mMarkupShapes.put(newId, circle);
				mUndoStack.push(mCurrentShape);
				mCoordinateManager.setCoordinates(mCurrentShape.getPoints().size() - 1, String.valueOf(coordinate.latitude), String.valueOf(coordinate.longitude));
			}
			break;

		default:
			
			if(mDataManager.getTabletLayoutOn()){
				if(markerOptionsTabletReport == null){
					markerOptionsTabletReport = new MarkerOptions();
					markerOptionsTabletReport.position(new LatLng(coordinate.latitude, coordinate.longitude));
					markerOptionsTabletReport.draggable(true);
					
					markerTabletReport = mMap.addMarker(markerOptionsTabletReport);
					markerTabletReport.setPosition(new LatLng(coordinate.latitude, coordinate.longitude));
					
				}else{
					markerOptionsTabletReport.position(new LatLng(coordinate.latitude, coordinate.longitude));
					markerTabletReport.setPosition(new LatLng(coordinate.latitude, coordinate.longitude));
				}
			}
			
			break;
		}

		Collections.sort(mFeatures, markupComparator);
		mShapesAdapter.notifyDataSetChanged();

		if (!mIgnoreUpdate && mCoordinateManager.getCurrentShapeType() == -1) {
			mShapesListView.setVisibility(View.VISIBLE);
			mShapesListProgress.setVisibility(View.GONE);
		}
	}

	public LatLng getReportMarkerCoordinates(){
		if(markerTabletReport != null){
			return markerTabletReport.getPosition();
		}else{
			return new LatLng(0,0);
		}
	}
	
	@Override
	public void onLocationChanged(Location arg0) {
		Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Cur Location: " + arg0.toString());
	}

	private void setUpMapIfNeeded() {

		mShapesListView = (ListView) mRootView.findViewById(R.id.markupShapesListView);
		mShapesListView.setVisibility(View.GONE);

		mShapesListProgress = (ProgressBar) mRootView.findViewById(R.id.markupShapesProgress);
		mShapesListProgress.setVisibility(View.VISIBLE);

		if (mMapFragment == null || mMap == null) {
			mMapFragment = (SupportMapFragment) mFragmentManager.findFragmentById(R.id.markupMapFragment);
			mMap = mMapFragment.getMap();

			if (mMap != null) {
				if (mMapType != -1) {
					mMap.setMapType(mMapType);
					if(wmsTileOverlay != null) {
						wmsTileOverlay.remove();
						wmsTileOverlay = null;
						wmsTileProvider = null;
					}
					
					if(mMapType == GoogleMap.MAP_TYPE_NONE) {
						wmsTileProvider = new MarkupWMSTileProvider(256, 256);
						TileOverlayOptions wmsOverlayOptions = new TileOverlayOptions();
						wmsOverlayOptions.tileProvider(wmsTileProvider);
						wmsTileOverlay = mMap.addTileOverlay(wmsOverlayOptions);
					}
				}

				mMap.setOnMapClickListener(this);
				mMap.setOnMarkerClickListener(this);

				if (mDataManager.isMDTEnabled()) {
					handler = mDataManager.getLocationSource();
					handler.setUpdateRate(0);
					mMap.setLocationSource(handler);
					mMap.setMyLocationEnabled(true);
				}
				mDataManager.forceLocationUpdate();
				mIgnoreUpdate = false;
			}
		} else {
			addMarkupFromServer();
		}

		mCoordinateManager = new MarkupCoordinateManager(this, mRootView, mDataManager);

		mCoordinateOKButton = (Button) mRootView.findViewById(R.id.coordinateOkButton);
		mCoordinateCancelButton = (Button) mRootView.findViewById(R.id.coordinateCancelButton);

		mCoordinateOKButton.setOnClickListener(confirmListener);
		mCoordinateCancelButton.setOnClickListener(confirmListener);

		mShapesAdapter = new ArrayAdapter<MarkupFeature>(mContext, android.R.layout.simple_list_item_1, mFeatures);
		mShapesListView.setAdapter(mShapesAdapter);

		if (mMap != null) {

			if (mInfoWindowAdapter != null) {
				mMap.setInfoWindowAdapter(mInfoWindowAdapter);
				mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {
						try {
							JSONObject data = new JSONObject(marker.getTitle());
							String type = data.getString("type");
							if (type != null) {
								MainActivity main = (MainActivity) mContext;
								if( type.equals("sr")) {
									main.mViewSimpleReport = true;
									main.mOpenedSimpleReportId = data.getLong("reportId");
									main.mOpenedSimpleReportPayload = data.getString("payload");
									
									//all three navigation calls from map to report are not working on phone ui right now.
									//locks up in MainActivity around line 1958 : mFragmentManager.executePendingTransactions();
									
//									main.onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), -1);
								} else if(type.equals("dmgrpt")) {
									main.mViewDamageReport = true;
									main.mOpenedDamageReportId = data.getLong("reportId");
									main.mOpenedDamageReportPayload = data.getString("payload");
//									main.onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -1);
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}

			TileOverlayOptions tileOverlayOptions = new TileOverlayOptions();
			markupTileProvider = new MarkupFeatureTileProvider();
			tileOverlayOptions.tileProvider(markupTileProvider);
			tileOverlay = mMap.addTileOverlay(tileOverlayOptions);

			// TODO: Re-enable delete markup item
			// mShapesListView.setOnItemLongClickListener(new
			// AdapterView.OnItemLongClickListener() {
			//
			// @Override
			// public boolean onItemLongClick(AdapterView<?> parent, final View
			// view, int position, long id) {
			// MarkupBaseShape shape = (MarkupBaseShape)
			// parent.getItemAtPosition(position);
			//
			// if (shape.getFeatureId() != null) {
			// setIgnoreUpdate(true);
			// showDeleteDialog(shape);
			// }
			//
			// return false;
			// }
			// });

			mShapesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
					MarkupFeature feature = (MarkupFeature) parent.getItemAtPosition(position);

					MarkupBaseShape shape = mMarkupShapes.get(feature.getFeatureId());

					if (shape != null) {
						if (shape.getType().equals(MarkupType.marker) || shape.getType().equals(MarkupType.label)) {
							mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(shape.getPoints().get(0), 13));
						} else {
							LatLngBounds.Builder t = LatLngBounds.builder();
							for (LatLng point : shape.getPoints()) {
								t.include(point);
							}
							mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(t.build(), 200));
						}
					}
				}
			});

			mMapClickView = mRootView.findViewById(R.id.markupMapClickView);
			mMapClickView.setOnTouchListener(this);

			mIncidentFocusButton = (ImageButton) mRootView.findViewById(R.id.markupIncidentButton);
			mPickerCompleteButton = (Button) mRootView.findViewById(R.id.markupPickerCompleteButton);

			mPickerCompleteButton.setVisibility(View.INVISIBLE);
			
			if(mDataManager.getSelectedCollabRoom().getName().equals(getString(R.string.no_selection))){
				mIncidentFocusButton.setVisibility(View.INVISIBLE);
			}else{
				mIncidentFocusButton.setVisibility(View.VISIBLE);
			}
			
			mIncidentFocusButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (extentBuilder != null) {
						try {
							LatLngBounds bounds = extentBuilder.build();
							mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
						} catch (Exception e) {
							if(mDataManager.getIncidentPositionLatitude() == 0 && mDataManager.getIncidentPositionLongitude() == 0){
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDataManager.getMDTLatitude(),mDataManager.getMDTLongitude()),12));
							}else{
								mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDataManager.getIncidentPositionLatitude(), mDataManager.getIncidentPositionLongitude()), 12));
							}
						}
					} else {
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mDataManager.getIncidentPositionLatitude(), mDataManager.getIncidentPositionLongitude()), 12));
					}
				}
			});
		
			try {
				mJunctionBitmapOptions = new MarkerOptions();
				mJunctionBitmapOptions.anchor(0.5f, 0.5f);
				mJunctionBitmapOptions.icon(BitmapDescriptorFactory.fromBitmap(generateRotatedBitmap(R.drawable.dot, 0)));
			} catch (Exception e) {

			}

			restoreMapState();
		} else {
			if (addingMarkupEnabled) {
				mCoordinateManager.setButtonsVisibility(View.VISIBLE);
			} else {
				mCoordinateManager.setButtonsVisibility(View.GONE);
			}
		}

		mIgnoreUpdate = false;
	}

	private void restoreMapState() {
		EncryptedPreferences settings = new EncryptedPreferences(this.mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));	
		changeMapType(settings.getPreferenceLong(Constants.nics_MAP_TYPE, Integer.toString(GoogleMap.MAP_TYPE_NORMAL)).intValue());

		boolean isTrafficEnabled = settings.getPreferenceBoolean(Constants.nics_MAP_TRAFFIC_ENABLED, String.valueOf(mMap.isTrafficEnabled()));
		boolean isIndoorEnabled = settings.getPreferenceBoolean(Constants.nics_MAP_INDOOR_ENABLED,String.valueOf( mMap.isIndoorEnabled()));

		mMap.setTrafficEnabled(isTrafficEnabled);
		mMap.setIndoorEnabled(isIndoorEnabled);

		if (mMenu != null) {
			MenuItem trafficMenuItem = mMenu.findItem(R.id.trafficMapOption);
			if (trafficMenuItem != null) {
				trafficMenuItem.setChecked(isTrafficEnabled);
			}

			MenuItem indoorMenuItem = mMenu.findItem(R.id.indoorMapOption);
			if (indoorMenuItem != null) {
				indoorMenuItem.setChecked(isIndoorEnabled);
			}

			MenuItem mapItem = null;
			switch (mMap.getMapType()) {
			case GoogleMap.MAP_TYPE_NORMAL:
				mapItem = mMenu.findItem(R.id.normalMapOption);
				break;
			case GoogleMap.MAP_TYPE_SATELLITE:
				mapItem = mMenu.findItem(R.id.satelliteMapOption);
				break;
			case GoogleMap.MAP_TYPE_HYBRID:
				mapItem = mMenu.findItem(R.id.hybridMapOption);
				break;
			case GoogleMap.MAP_TYPE_TERRAIN:
				mapItem = mMenu.findItem(R.id.terrainMapOption);
				break;
			case GoogleMap.MAP_TYPE_NONE:
				mapItem = mMenu.findItem(R.id.offlineMapOption);
				break;
			default:
				mapItem = mMenu.findItem(R.id.normalMapOption);
			}

			if (mapItem != null) {
				mapItem.setChecked(true);
			}
		}

		mCoordinateManager.setCoordinates(settings.getPreferenceString((Constants.nics_MAP_MARKUP_COORDINATES)));

		int shapeType = settings.getPreferenceLong(Constants.nics_MAP_CURRENT_SHAPE_TYPE).intValue();

		mCoordinateManager.setCurrentShapeType(shapeType);
		if (shapeType != -1 && addingMarkupEnabled) {
			mCoordinateManager.show(shapeType);
			mCoordinateManager.setButtonsVisibility(View.GONE);
			mShapesListView.setVisibility(View.GONE);
			mShapesListProgress.setVisibility(View.GONE);
			mIgnoreUpdate = true;

			if (mRenderMarkupFeaturesTask != null) {
				mRenderMarkupFeaturesTask.cancel(true);
				mRenderMarkupFeaturesTask = null;
			}
		} else {
			if (addingMarkupEnabled) {
				mCoordinateManager.setButtonsVisibility(View.VISIBLE);
			} else {
				mCoordinateManager.setButtonsVisibility(View.GONE);
			}
			mShapesListView.setVisibility(View.GONE);
			mShapesListProgress.setVisibility(View.VISIBLE);

			mIgnoreUpdate = false;
		}

		String pos = settings.getPreferenceString(Constants.nics_MAP_PREVIOUS_CAMERA);
	
		if (pos != null) {
			String[] posDetails = pos.split(",");
			CameraPosition camPos = new CameraPosition(new LatLng(Double.valueOf(posDetails[0]), Double.valueOf(posDetails[1])), Float.valueOf(posDetails[2]), Float.valueOf(posDetails[3]), Float.valueOf(posDetails[4]));
			mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
		}else {
			
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
				mIncidentFocusButton.callOnClick();
			} else {
				mIncidentFocusButton.performClick();
			}
		}
		
		mColor = new int[] { 255, settings.getPreferenceLong(Constants.nics_MAP_COORDINATES_COLOR_RED, "255").intValue(), settings.getPreferenceLong(Constants.nics_MAP_COORDINATES_COLOR_GREEN, "255").intValue(), settings.getPreferenceLong(Constants.nics_MAP_COORDINATES_COLOR_BLUE, "255").intValue() };
		mCoordinateManager.setColor(Color.argb(255, mColor[1], mColor[2], mColor[3]));

		mCurrentSymbolResourceId = settings.getPreferenceLong(Constants.nics_MAP_CURRENT_SYMBOL_RESOURCE_ID, "-1").intValue();
		if (mCurrentSymbolResourceId == -1) {
			mCurrentSymbolResourceId = R.drawable.small_x;
		}

		mCoordinateManager.setSymbol(mCurrentSymbolResourceId);

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			activeWFSLayers = settings.getStringSet(Constants.nics_MAP_ACTIVE_WFS_LAYERS, null);
//		} else {
//			activeWFSLayers = new HashSet<String>(Arrays.asList(settings.getPreferenceString(Constants.nics_MAP_ACTIVE_WFS_LAYERS, "").split(";")));
//		}

		Handler handler = new Handler();
		handler.post(postRestoreHandler);
	}

	private Runnable postRestoreHandler = new Runnable() {

		@Override
		public void run() {
			addMarkupFromServer();

			Handler handler = new Handler();
			handler.post(postInitialMarkupLoad);
		}
	};

	private Runnable postInitialMarkupLoad = new Runnable() {
		@Override
		public void run() {
			for (TrackingLayerPayload layer : mDataManager.getTrackingLayers()) {
				try {
					if(layer.isActive()){
						if (layer.getDisplayname().equals(getResources().getString(R.string.wfslayer_nics_simple_report_title))) {
							addSimpleReportLayer(layer);
						} else if (layer.getDisplayname().equals(getResources().getString(R.string.wfslayer_nics_damage_report_title))) {
							addDamageReportLayer(layer);
						} else {
							addMapLayer(layer);
						}
					}
				} catch (Exception e) {
					mDataManager.addPersonalHistory("Failed to add WFS Layer " + layer.getLayername() + " to the map.");
					Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Failed to add WFS Layer " + layer.getLayername() + " to the map.");
				}
			}
		}
	};

	public void saveMapState() {
		
		EncryptedPreferences settings = new EncryptedPreferences(this.mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));

		settings.savePreferenceLong(Constants.nics_MAP_TYPE,(long) mMap.getMapType());
		settings.savePreferenceBoolean(Constants.nics_MAP_TRAFFIC_ENABLED, mMap.isTrafficEnabled());

		settings.savePreferenceLong(Constants.nics_MAP_CURRENT_SYMBOL_RESOURCE_ID,(long) mCurrentSymbolResourceId);
		settings.savePreferenceBoolean(Constants.nics_MAP_INDOOR_ENABLED, mMap.isIndoorEnabled());
		settings.savePreferenceString(Constants.nics_MAP_MARKUP_COORDINATES, mCoordinateManager.getCoordinates());
		settings.savePreferenceLong(Constants.nics_MAP_CURRENT_SHAPE_TYPE,(long) mCoordinateManager.getCurrentShapeType());

		CameraPosition pos = mMap.getCameraPosition();
		String cameraPos = pos.target.latitude + "," + pos.target.longitude + "," + pos.zoom + "," + pos.tilt + "," + pos.bearing;
		settings.savePreferenceString(Constants.nics_MAP_PREVIOUS_CAMERA, cameraPos);

		if (mColor != null) {
			settings.savePreferenceLong(Constants.nics_MAP_COORDINATES_COLOR_RED, (long)mColor[1]);
			settings.savePreferenceLong(Constants.nics_MAP_COORDINATES_COLOR_GREEN,(long) mColor[2]);
			settings.savePreferenceLong(Constants.nics_MAP_COORDINATES_COLOR_BLUE,(long) mColor[3]);
		}

//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			editor.putStringSet(Constants.nics_MAP_ACTIVE_WFS_LAYERS, mMarkupLayers.keySet());
//		} else {
			String layers = "";
			for (String layerName : mMarkupLayers.keySet()) {
				layers += layerName + ";";
			}
			settings.savePreferenceString(Constants.nics_MAP_ACTIVE_WFS_LAYERS, layers);
//		}
	}

	public static void addMapLayer(TrackingLayerPayload layerPayload) {
		MarkupLayer layer = mMarkupLayers.remove(layerPayload.getDisplayname());
		if(layer != null) {
			layer.unregister();
		}
		mMarkupLayers.put(layerPayload.getDisplayname(), new MarkupLayer(MainActivity.getAppContext(), layerPayload, mMap));
	}

	public static void addSimpleReportLayer(TrackingLayerPayload layerPayload) {
		mMarkupLayers.put(layerPayload.getDisplayname(), new SimpleReportLayer(MainActivity.getAppContext(), layerPayload, mMap));
	}
	
	public static void addDamageReportLayer(TrackingLayerPayload layerPayload) {
		mMarkupLayers.put(layerPayload.getDisplayname(), new DamageReportLayer(MainActivity.getAppContext(), layerPayload, mMap));
	}

	public static void removeMapLayer(String string) {
		MarkupLayer removedLayer = mMarkupLayers.remove(string);
		if (removedLayer != null) {
			removedLayer.clearFromMap();
			removedLayer.unregister();
		}
	}

	public static void changeMapType(int mapType) {
		mMap.setMapType(mapType);
		
		if(wmsTileOverlay != null) {
			wmsTileOverlay.remove();
			wmsTileOverlay = null;
			wmsTileProvider = null;
		}
		 
		if(mapType == GoogleMap.MAP_TYPE_NONE) {
			wmsTileProvider = new MarkupWMSTileProvider(256, 256);
			TileOverlayOptions wmsOverlayOptions = new TileOverlayOptions();
			wmsOverlayOptions.tileProvider(wmsTileProvider);
			wmsTileOverlay = mMap.addTileOverlay(wmsOverlayOptions);
		}
	}

	public static void enableTraffic(boolean isTrafficEnabled) {
		mMap.setTrafficEnabled(isTrafficEnabled);
	}

	public static void enableIndoor(boolean isIndoorEnabled) {
		mMap.setIndoorEnabled(isIndoorEnabled);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mLastPressLocation = mMap.getProjection().fromScreenLocation(new Point(Math.round(event.getX()), Math.round(event.getY())));

		if (MotionEvent.ACTION_UP == event.getAction()) {
			v.performClick();
		}

		return false;
	}

	public void removeMapFragment() {
		if (mMapFragment != null && mFragmentManager != null) {
			mFragmentManager.beginTransaction().remove(mMapFragment).commit();
			mMapFragment = null;
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		markerOptionsTabletReport = null;
		markerTabletReport = null;
		
//		if (markerReceiverRegistered) {
//			mContext.unregisterReceiver(markupReceiver);
//			mContext.unregisterReceiver(collabRoomSwitchedReceiver);
//			mContext.unregisterReceiver(incidentSwitchedReceiver);
//			mContext.unregisterReceiver(localMapDataClearedReceiver);
//			mContext.unregisterReceiver(mapFeatureFailedToPostReceiver);
//			mCoordinateManager.unregisterReceivers();
//			markerReceiverRegistered = false;
//		}

		mDataManager.stopPollingMarkup();
		if (mRenderMarkupFeaturesTask != null) {
			mRenderMarkupFeaturesTask.cancel(true);
			mRenderMarkupFeaturesTask = null;
		}
		
		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}

	private BroadcastReceiver markupReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			try {
								
				Log.i(Constants.nics_DEBUG_ANDROID_TAG, "Rendering shapes");
				if (intent.getLongExtra("collabroomId", -99) == mDataManager.getSelectedCollabRoom().getCollabRoomId()) {
					String[] addMarkup = intent.getStringArrayExtra("featuresToAdd");
					String[] removeMarkup = intent.getStringArrayExtra("featuresToRemove");
					if (addMarkup != null) {
						for (String markupPayloadString : addMarkup) {
							MarkupFeature feature = mBuilder.create().fromJson(markupPayloadString, MarkupFeature.class);

							MarkupBaseShape shape = mMarkupShapes.get(feature.getFeatureId());
							if (shape != null) {
								shape.removeFromMap();
								mFeatures.remove(shape.getFeature());
								mMarkupShapes.remove(shape);
							}
							mFeatures.add(feature);
						}
					}

					if (removeMarkup != null) {
						for (String featureId : removeMarkup) {
							MarkupBaseShape shape = mMarkupShapes.get(featureId);
							if (shape != null) {
								shape.removeFromMap();
								mFeatures.remove(shape.getFeature());
								mMarkupShapes.remove(shape);
							}
						}
					}

					mFirelineFeatures.clear();

					if (markupTileProvider != null) {
						markupTileProvider.setFirelineFeatures(mFirelineFeatures);
					}

					if (tileOverlay != null) {
						tileOverlay.clearTileCache();
					}
					
					if(wmsTileOverlay != null) {
						wmsTileOverlay.clearTileCache();
					}

					addMarkupFromServer();
				} else if (!mFirstLoad && !mIgnoreUpdate && mCoordinateManager.getCurrentShapeType() == -1) {
					mShapesListView.setVisibility(View.VISIBLE);
					mShapesListProgress.setVisibility(View.GONE);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 100) {
			mCurrentSymbolResourceId = data.getExtras().getInt("resourceId");
			mCoordinateManager.setSymbol(mCurrentSymbolResourceId);
		}

		if (requestCode == 200) {
			int temp = data.getExtras().getInt("pickedColor");
			mColor = new int[] { 255, Color.red(temp), Color.green(temp), Color.blue(temp) };
			mCoordinateManager.setColor(temp);

			if (mCurrentShape != null) {
				mCurrentShape.setStrokeColor(mColor);

			}
		}
	}

	public void showDeleteDialog(final MarkupBaseShape shape) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setMessage(R.string.markup_confrim_delete);

		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				RestClient.deleteMarkup(shape.getFeatureId());
				shape.removeFromMap();
				mFeatures.remove(shape.getFeature());
				mMarkupShapes.remove(shape);

				mShapesAdapter.notifyDataSetChanged();
				setIgnoreUpdate(false);
				dialog.dismiss();
			}

		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				setIgnoreUpdate(false);
				dialog.dismiss();
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public void showConfirmDialog(final String title, final String message, final String activeModeName) {

		if (mCurrentShape != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

			builder.setMessage(message);
			builder.setTitle(title);

			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mDataManager.addMarkupFeatureToStoreAndForward(mCurrentShape.toFeature());

					mDataManager.sendMarkupFeatures();

					mCurrentShape = null;
					mCurrentShapeId = null;
					dialog.dismiss();
					mCoordinateManager.setCurrentShapeType(-1);
					mCoordinateManager.clearCoordinates();
					mCoordinateManager.hide();
					mShapesListView.setVisibility(View.VISIBLE);
					mIgnoreUpdate = false;
				}
			});
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
		} else {

		}
	}
	
	OnClickListener confirmListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean success = false;

			switch (v.getId()) {
			case R.id.coordinateOkButton:

				MarkupType type = null;
				if (mCurrentShape != null) {
					type = mCurrentShape.getType();
				}
				
				ArrayList<LatLng> points = mCoordinateManager.getCoordinatesArray();
				//enclosed shapes must end with their starting point
//				if(type == MarkupType.circle || type == MarkupType.hexagon || type == MarkupType.polygon || type == MarkupType.square || type == MarkupType.triangle){
//					points.add(points.get(0));
//				}
				int size = points.size();

				if (mCurrentShape == null && size > 0) {
					onMapClick(points.get(0));
				}

				if (mCurrentShape == null || type == MarkupType.marker && size != 1 || type == MarkupType.sketch && size != 2 || type == MarkupType.polygon && size < 3 || type == MarkupType.square && size != 5) {
					final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
					alertDialog.setTitle(getString(R.string.markup_shape_incomplete));
					alertDialog.setMessage(getString(R.string.markup_confirm_fields));
					alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							alertDialog.dismiss();
						}
					});
					alertDialog.show();
				} else {
					
					mCurrentShape.setPoints(mCoordinateManager.getCoordinatesArray());
					mCurrentShape.setStrokeColor(mColor);
					mCurrentShape.setTime(System.currentTimeMillis());
					mCurrentShape.setFeatureId(mCurrentShapeId);

					mDataManager.addMarkupFeatureToStoreAndForward(mCurrentShape.toFeature());
					mDataManager.sendMarkupFeatures();

					if (type == MarkupType.marker) {
						JsonObject attr = new JsonObject();
						Resources resources = mContext.getResources();
						try {
							attr.addProperty("icon", mCurrentSymbolResourceId);
							attr.addProperty(resources.getString(R.string.markup_user), mDataManager.getUsername());
							attr.addProperty(resources.getString(R.string.markup_timestamp), mCurrentShape.getTime());
						} catch (Exception e) {
						}
						
						MarkupSymbol temp = ((MarkupSymbol) mCurrentShape);

						Bitmap symbolBitmap = generateRotatedBitmap(mCurrentSymbolResourceId, 0);
						String symbolPath = Symbols.ALL.getKey(mCurrentSymbolResourceId);
						temp.setTitle(attr.toString());
						temp.setIcon(symbolBitmap, new int[] { 255, 255, 255, 255 });
						temp.setSymbolPath(symbolPath);

					}
					
					mTempShapes.put(mCurrentShapeId, mCurrentShape);
					mCurrentShape = null;
					mCurrentShapeId = null;
					success = true;
				}
				break;
			case R.id.coordinateCancelButton:

				if (mCurrentShape != null) {
					mCurrentShape.removeFromMap();
					mMarkupShapes.remove(mCurrentShapeId);

					mCurrentShape = null;
					mCurrentShapeId = null;
				}
				success = true;
			}
			if (success) {
				mCoordinateManager.setCurrentShapeType(-1);
				mCoordinateManager.clearCoordinates();
				mCoordinateManager.hide();
				mShapesListView.setVisibility(View.VISIBLE);
			}

			mIgnoreUpdate = false;

			for (MarkupFireLine fireline : mFirelineFeatures) {
				fireline.removeFromMap();
			}
			mFirelineFeatures.clear();
			markupTileProvider.setFirelineFeatures(mFirelineFeatures);
			tileOverlay.clearTileCache();
			addMarkupFromServer();
		}
	};

	public void hideListView() {
		mShapesListView.setVisibility(View.GONE);
	}

	private Comparator<? super MarkupFeature> markupComparator = new Comparator<MarkupFeature>() {

		@Override
		public int compare(MarkupFeature lhs, MarkupFeature rhs) {
			return rhs.getSeqTime().compareTo(lhs.getSeqTime());
		}
	};

	private Comparator<? super MarkupFeature> markupComparatorOlder = new Comparator<MarkupFeature>() {

		@Override
		public int compare(MarkupFeature lhs, MarkupFeature rhs) {
			return lhs.getSeqTime().compareTo(rhs.getSeqTime());
		}
	};
	public Builder extentBuilder;

	private class RenderMarkupFeaturesTask extends AsyncTask<ArrayList<MarkupFeature>, Object, Integer> {

		private boolean isCancelled;
		protected float zoom;
		private boolean mClearTiles;
		private boolean mCenterMap;

		public RenderMarkupFeaturesTask() {
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();

			isCancelled = true;

			if (mIgnoreUpdate && mCoordinateManager.getCurrentShapeType() != -1) {
				mShapesListView.setVisibility(View.GONE);
				mShapesListProgress.setVisibility(View.GONE);
			}
		}

		@Override
		protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<MarkupFeature>... markupFeatures) {
			Integer numParsed = 0;

			if (!isCancelled) {
				if (mFeatures.size() == 0) {
					mFirstLoad = true;
				}

				if (mFirstLoad) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					mFeatures.addAll(mDataManager.getAllMarkupFeaturesStoreAndForwardReadyToSend());
					
					//Flippeding lat and lon values of draft features.
					//currently the web api accepts these coordinates backwards. so they don't show up properly on mobile when they are a draft
					//this can be removed once the lat lon web bug is fixed
					for(int i = 0; i < mFeatures.size(); i++){
						ArrayList<Vector2> latLonList = mFeatures.get(i).getGeometryVector2();
						
						for(int latlonIndex =0; latlonIndex < latLonList.size(); latlonIndex++){
							double x = latLonList.get(latlonIndex).x;
							double y = latLonList.get(latlonIndex).y;
							
							latLonList.get(latlonIndex).x = y;
							latLonList.get(latlonIndex).y = x;
						}
						mFeatures.get(i).setGeometryVector2(latLonList);
					}
					
					mFeatures.addAll(mDataManager.getMarkupHistoryForCollabroom(mDataManager.getSelectedCollabRoom().getCollabRoomId()));

					if (mFeatures.size() > 0) {
						mFirstLoad = false;
						mCenterMap = true;
						mClearTiles = true;
					} else if (RestClient.isParsingMarkup() || RestClient.isFetchingMarkup()) {
						return -1;
					}
				}

				mIgnoreUpdate = true;

				int[] lastFillColor;
				int[] serverColor;
				int color;
				int fillColor;
				Double alpha;

				Collections.sort(mFeatures, markupComparatorOlder);

				try {
					extentBuilder = LatLngBounds.builder();
					for (final MarkupFeature feature : mFeatures) {
						for (Vector2 point : feature.getGeometryVector2()) {
							extentBuilder.include(new LatLng(point.x, point.y));
						}

						if (!feature.isRendered()) {
							try {
								if(feature.getStrokeColor() != null){
										color = Color.parseColor(feature.getStrokeColor());
									}else{
										color = Color.WHITE;
									}
							} catch (IllegalArgumentException e) {
								color = Color.WHITE;
							}
							serverColor = new int[] { Color.alpha(color), Color.red(color), Color.green(color), Color.blue(color) };

							if(feature.getFillColor() != null){
								fillColor = Color.parseColor(feature.getFillColor());
							}else{
								fillColor = Color.WHITE;
							}
							alpha = 61.2; // default opacity for fill?

							if (feature.getOpacity() != -1) {
								alpha = (feature.getOpacity() * 255);
							}

							lastFillColor = new int[] { alpha.intValue(), Color.red(fillColor), Color.green(fillColor), Color.blue(fillColor) };

							if (feature.getType().equals(MarkupType.marker.toString()) || feature.getType().equals("marker")) {
								String symbolPath = feature.getGraphic();
								symbolPath = symbolPath.substring(symbolPath.lastIndexOf("images"));
								final MarkupSymbol symbol = new MarkupSymbol(mDataManager, feature, generateRotatedBitmap(symbolPath), symbolPath, serverColor);
								mMarkupShapes.put(feature.getFeatureId(), symbol);

								mContext.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										symbol.setMarker(mMap.addMarker(symbol.getOptions()));
										feature.setRendered(true);
									}
								});

							} else if (feature.getType().equals(MarkupType.marker.toString()) || feature.getType().equals("sketch") || feature.getType().equals("line")) {

//								if (feature.getDashStyle() == null || feature.getDashStyle().equals("solid") || feature.getDashStyle().equals("completedLine")) {
//									final MarkupSegment segment = new MarkupSegment(mDataManager, feature, serverColor);
//									mMarkupShapes.put(feature.getFeatureId(), segment);
//
//									mContext.runOnUiThread(new Runnable() {
//
//										@Override
//										public void run() {
//											segment.setPolyline(mMap.addPolyline(segment.getOptions()));
//											feature.setRendered(true);
//										}
//									});
//								} else {
////									final MarkupFireLine fireline = new MarkupFireLine(mDataManager, feature, serverColor, zoom);
////									mMarkupShapes.put(feature.getFeatureId(), fireline);
////									mFirelineFeatures.add(fireline);
//								}
							} else if (feature.getType().equals(MarkupType.square.toString())) {
								final MarkupRectangle rectangle = new MarkupRectangle(mDataManager, feature, serverColor, lastFillColor);
								mMarkupShapes.put(feature.getFeatureId(), rectangle);

								mContext.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										rectangle.setPolygon(mMap.addPolygon(rectangle.getOptions()));
										feature.setRendered(true);
									}
								});

							} else if (feature.getType().equals(MarkupType.polygon.toString()) || feature.getType().equals("box") || feature.getType().equals(MarkupType.circle.toString())
									|| (feature.getType().equals("circle") && feature.getGeometryVector2().size() > 1) || feature.getType().equals("triangle") || feature.getType().equals("polygon") || feature.getType().equals("hexagon")) {
								final MarkupRectangle trapezoid = new MarkupRectangle(mDataManager, feature, serverColor, lastFillColor);
								mMarkupShapes.put(feature.getFeatureId(), trapezoid);

								mContext.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										trapezoid.setPolygon(mMap.addPolygon(trapezoid.getOptions()));
										feature.setRendered(true);
									}
								});

							} else if (feature.getType().equals("label")) {
								final MarkupText text = new MarkupText(mDataManager, feature, serverColor);
								mMarkupShapes.put(feature.getFeatureId(), text);

								mContext.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										text.setMarker(mMap.addMarker(text.getOptions()));
										feature.setRendered(true);
									}
								});

							} else if (feature.getType().equals(MarkupType.circle.toString()) || feature.getType().equals("circle")) {
								final MarkupCircle circle = new MarkupCircle(mDataManager, feature, serverColor, lastFillColor);
								mMarkupShapes.put(feature.getFeatureId(), circle);

								mContext.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										circle.setCircle(mMap.addCircle(circle.getOptions()));
										feature.setRendered(true);
									}
								});
							}
							numParsed++;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (mShapesListView != null) {
					final int index = mShapesListView.getFirstVisiblePosition();
					View v = mShapesListView.getChildAt(0);
					final int top = (v == null) ? 0 : v.getTop();

					Collections.sort(mFeatures, markupComparator);

					mContext.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							mShapesListView.setSelectionFromTop(index, top);
							mShapesAdapter.notifyDataSetChanged();
							if (mCurrentShape == null && mCoordinateManager.getCurrentShapeType() == -1 && !mIgnoreUpdate) {
								mShapesListView.setVisibility(View.VISIBLE);
								mShapesListProgress.setVisibility(View.GONE);
							}

							markupTileProvider.setFirelineFeatures(mFirelineFeatures);
							tileOverlay.clearTileCache();
						}
					});
				}
			}

			mIgnoreUpdate = false;

			if (numParsed > 0) {
				// TODO: notifications for markup?
				// mNotificationHandler.createChatNotification(chatPayloads[0],
				// mDataManager.getActiveIncidentId());
			}

			return numParsed;
		}

		@Override
		protected void onPostExecute(Integer numParsed) {
			super.onPostExecute(numParsed);
			if (numParsed == 0 && mFirstLoad) {
				Log.i(Constants.nics_DEBUG_ANDROID_TAG, "No features found.");
				mShapesListView.setVisibility(View.VISIBLE);
				mShapesListProgress.setVisibility(View.GONE);
				mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), false);
				mFirstLoad = false;
			} else {
				Log.i(Constants.nics_DEBUG_ANDROID_TAG, "Successfully rendered " + numParsed + " markup features.");

				if (RestClient.isParsingMarkup() || RestClient.isFetchingMarkup()) {
					mShapesListView.setVisibility(View.GONE);
					mShapesListProgress.setVisibility(View.VISIBLE);
				} else {
					mShapesListView.setVisibility(View.VISIBLE);
					mShapesListProgress.setVisibility(View.GONE);
				}
			}

			if (mRenderMarkupFeaturesTask != null) {
				mRenderMarkupFeaturesTask.cancel(true);
				mRenderMarkupFeaturesTask = null;
			}

			if (mClearTiles) {
				markupTileProvider.setFirelineFeatures(mFirelineFeatures);
				tileOverlay.clearTileCache();
				mClearTiles = false;
			}

//			if (mCenterMap) {
//				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
//					mIncidentFocusButton.callOnClick();
//				} else {
//					mIncidentFocusButton.performClick();
//				}
//				mCenterMap = false;
//			}
		}

	}

	public void setIgnoreUpdate(boolean ignoreUpdate) {
		mIgnoreUpdate = ignoreUpdate;

		if (mRenderMarkupFeaturesTask != null) {
			mRenderMarkupFeaturesTask.cancel(true);
			mRenderMarkupFeaturesTask = null;
		}

		if (ignoreUpdate) {
			mShapesListView.setVisibility(View.VISIBLE);
			mShapesListProgress.setVisibility(View.GONE);
		}
	}

	public boolean isAddingMarkupEnabled() {
		return addingMarkupEnabled;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mapmarkup_layer_provider) {
//			String[] wfsNameArray = getResources().getStringArray(R.array.wfslayer_title_array);
//			final String[] wfsLayer = getResources().getStringArray(R.array.wfslayer_array);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			
			ArrayList<TrackingLayerPayload> layerPayloads = mDataManager.getTrackingLayers();
			
			String[] wfsNameArray = new String[layerPayloads.size()];
			boolean[] wfsActiveArray = new boolean[layerPayloads.size()];
			
			for(int i = 0; i < layerPayloads.size(); i++){
				wfsNameArray[i] = layerPayloads.get(i).getDisplayname();
				wfsActiveArray[i] = layerPayloads.get(i).isActive();
			}
			

//			if (activeWFSLayers != null) {
//				for (String layer : activeWFSLayers) {
//					int index = ArrayUtils.indexOf(wfsLayer, layer);
//					if (index > ArrayUtils.INDEX_NOT_FOUND) {
//						wfsActiveArray[index] = true;
//					}
//				}
//			} else {
//				for (String layer : mMarkupLayers.keySet()) {
//					int index = ArrayUtils.indexOf(wfsLayer, layer);
//					if (index > ArrayUtils.INDEX_NOT_FOUND) {
//						wfsActiveArray[index] = true;
//					}
//				}
//			}

			builder.setMultiChoiceItems(wfsNameArray, wfsActiveArray, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					TrackingLayerPayload selectedLayer = mDataManager.getTrackingLayers().get(which);
					if (isChecked) {
						if (selectedLayer.getDisplayname().equals(getResources().getString(R.string.wfslayer_nics_simple_report_title))) {
							addSimpleReportLayer(selectedLayer);
						} else if (selectedLayer.getDisplayname().equals(getResources().getString(R.string.wfslayer_nics_damage_report_title))) {
							addDamageReportLayer(selectedLayer);
						} else {
							addMapLayer(selectedLayer);
						}
					} else {
						removeMapLayer(selectedLayer.getDisplayname());
					}
					
					TrackingLayerPayload payload = mDataManager.getTrackingLayers().get(which);
					payload.setActive(isChecked);
					mDataManager.UpdateTrackingLayerData(payload);
					
//					EncryptedPreferences settings = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_MAP_MARKUP_STATE, 0));
//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//						editor.putStringSet(Constants.nics_MAP_ACTIVE_WFS_LAYERS, mMarkupLayers.keySet());
//					} else {
//						String layers = "";
//						for (String layerName : mMarkupLayers.keySet()) {
//							layers += layerName + ";";
//						}
//						settings.savePreferenceString(Constants.nics_MAP_ACTIVE_WFS_LAYERS, layers);
//					}
				}
			});
			
			builder.setPositiveButton(R.string.ok, new Dialog.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});

			builder.create().show();
		}

		return super.onOptionsItemSelected(item);
	}
	
	
	
	private BroadcastReceiver mapFeatureFailedToPostReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			saveMapState();
			
			mMap.clear();
			mFeatures.clear();
			mMarkupShapes.clear();	
			setUpMapIfNeeded();
			
			mShapesAdapter.notifyDataSetChanged();
			
			final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
			alertDialog.setTitle("Feature Failed To Post");
			alertDialog.setMessage(intent.getExtras().getString("message"));
			alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
				}
			});
			alertDialog.show();
			
		}
	};
	
	private BroadcastReceiver collabRoomSwitchedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mMap.clear();
			mFeatures.clear();
			mMarkupShapes.clear();
			if(mDataManager.getActiveCollabroomId() != -1){
				setUpMapIfNeeded();
			}
			mShapesAdapter.notifyDataSetChanged();
		}
	};
	
	private BroadcastReceiver incidentSwitchedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mMap.clear();
			mFeatures.clear();
			mMarkupShapes.clear();
			if(mDataManager.getActiveCollabroomId() != -1){
				setUpMapIfNeeded();
			}
			mShapesAdapter.notifyDataSetChanged();
		}
	};
	
	private BroadcastReceiver localMapDataClearedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			mMap.clear();
			mFeatures.clear();
			mMarkupShapes.clear();
			if(mDataManager.getActiveCollabroomId() != -1){
				setUpMapIfNeeded();
			}
			mShapesAdapter.notifyDataSetChanged();
		}
	};
}
