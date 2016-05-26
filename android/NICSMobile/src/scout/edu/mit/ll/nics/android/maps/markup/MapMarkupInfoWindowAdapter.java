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
package scout.edu.mit.ll.nics.android.maps.markup;

import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayout.LayoutParams;
import android.support.v7.widget.GridLayout.Spec;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.utils.Constants;

public class MapMarkupInfoWindowAdapter implements InfoWindowAdapter {

	private Context mContext;
	private View mRootView;
	private GridLayout mGridLayout;

	public MapMarkupInfoWindowAdapter(Context context) {
		mContext = context;
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.map_info_window, null);
		mGridLayout = (GridLayout) mRootView.findViewById(R.id.mapInfoWindowGridLayout);
		mGridLayout.setColumnCount(2);
	}
	
	@Override
	public View getInfoContents(Marker marker) {
		try {
			mGridLayout.removeAllViews();
			
			JsonObject data = new Gson().fromJson(marker.getTitle(), JsonObject.class);
			ImageView icon = (ImageView) mRootView.findViewById(R.id.mapInfoWindowImage);
	
			try {
				int iconId = data.remove("icon").getAsInt();
				icon.setImageResource(iconId);
				icon.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				icon.setVisibility(View.GONE);
			}
			
			try {
				String title = data.remove("title").getAsString();
			
				if(data.get("type").getAsString().equals("sr") || data.get("type").getAsString().equals("dmgrpt") || data.get("type").getAsString().equals("ur")) {
					TextView clickView = new TextView(mContext);
					clickView.setTextAppearance(mContext, android.R.attr.textAppearanceMedium);
					clickView.setTextColor(mContext.getResources().getColor(R.color.holo_blue_dark));
					clickView.setTypeface(null, Typeface.BOLD);
					clickView.setPadding(0, 5, 0, 0);
					clickView.setText(title);
					Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.TOP);
					Spec colSpec = GridLayout.spec(0,2, GridLayout.CENTER);
					mGridLayout.addView(clickView, new LayoutParams(rowSpec, colSpec));
				}
			} catch(Exception e) {
			}
			
			Set<Entry<String, JsonElement>> dataSet = data.entrySet();
			mGridLayout.setRowCount(dataSet.size() - 1);
			
			for(Entry<String, JsonElement> entry : dataSet) {
				String key = entry.getKey();
				String value = null;
				
				if(key.equals(mContext.getResources().getString(R.string.markup_timestamp))) {
					value = DateFormat.format(Constants.nics_TIME_FORMAT, new Date(entry.getValue().getAsLong())).toString();
				} else {
					if(entry.getValue() != null && entry.getValue().isJsonNull() == false ){
						value = entry.getValue().getAsString();
					}
				}
				
				if(value != null && !value.isEmpty() && !key.equals("reportId") && !key.equals("type") && !key.equals("payload")) {
					TextView titleView = new TextView(mContext);
					titleView.setTypeface(null, Typeface.BOLD);
					titleView.setTextColor(Color.BLACK);
					titleView.setText(key);
					titleView.setPadding(0, 0, 5, 0);
					
					Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.TOP);
					Spec colSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.RIGHT);
					mGridLayout.addView(titleView, new LayoutParams(rowSpec, colSpec));
					
					TextView valueView = new TextView(mContext);
					valueView.setTextAppearance(mContext, android.R.attr.textAppearanceSmall);
					valueView.setTextColor(Color.BLACK);
					valueView.setText(Html.fromHtml(value));
					valueView.setMaxWidth(350);
					mGridLayout.addView(valueView);
				}
			}
			
			if(data.get("type").getAsString().equals("sr") || data.get("type").getAsString().equals("dmgrpt") || data.get("type").getAsString().equals("ur")) {
				TextView clickView = new TextView(mContext);
				clickView.setTextAppearance(mContext, android.R.attr.textAppearanceSmall);
				clickView.setTextSize(10);
				clickView.setTextColor(mContext.getResources().getColor(R.color.holo_blue_dark));
				clickView.setTypeface(null, Typeface.ITALIC);
				clickView.setPadding(0, 5, 0, 0);
				clickView.setText(R.string.markup_press_to_view);
				Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, GridLayout.TOP);
				Spec colSpec = GridLayout.spec(0,2, GridLayout.CENTER);
				mGridLayout.addView(clickView, new LayoutParams(rowSpec, colSpec));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mRootView;
	}

	@Override
	public View getInfoWindow(Marker marker) {
		return null;
	}
}
