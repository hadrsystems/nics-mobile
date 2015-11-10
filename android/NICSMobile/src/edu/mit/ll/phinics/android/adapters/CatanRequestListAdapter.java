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
/**
 *
 */
package edu.mit.ll.phinics.android.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import edu.mit.ll.phinics.android.MainActivity;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.data.CatanRequestData;
import edu.mit.ll.phinics.android.api.payload.forms.CatanRequestPayload;
import edu.mit.ll.phinics.android.fragments.CatanRequestListFragment;


public class CatanRequestListAdapter extends ArrayAdapter<CatanRequestPayload> {
	private List<CatanRequestPayload> mItems;
	private Resources mResources;
	private DataManager mDataManager;
	private Context mContext;
	private CatanRequestListFragment parentFragment;
	
	public CatanRequestListAdapter(Context context, int resource, int textViewResourceId, List<CatanRequestPayload> list) {
		
		super(context, resource, textViewResourceId, list);
		
		mContext = context;
		mDataManager = DataManager.getInstance(context);
		mResources = context.getResources();
		mItems = list;
		MainActivity main = (MainActivity) mDataManager.getActiveActivity();
		parentFragment = main.mCatanRequestListFragment;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mItems.size() == 0) {
			return null;
		}
		
		CatanRequestPayload payload = mItems.get(position);
		CatanRequestData data = payload.getMessageData();		
		
		View row = super.getView(position, convertView, parent);

		TextView name = (TextView)row.findViewById(R.id.catanRequestTitle);
		TextView gps = (TextView)row.findViewById(R.id.catanRequestGPS);
		TextView size = (TextView)row.findViewById(R.id.catanRequestTime);
		size.setText(new Date(payload.getSeqTime()).toString());
		
		TextView assign = (TextView)row.findViewById(R.id.catanRequestAssignment);
		
		if(data.getAssign() != null && !data.getAssign().isEmpty()) {
			assign.setText(data.getCatan_service()[0].getAidComment());
			assign.setVisibility(View.VISIBLE);
		} else {
			assign.setVisibility(View.GONE);
		}
		
//		LinearLayout blueBackground = (LinearLayout)row.findViewById(R.id.catanRequestIconBorder);
		
		name.setText(data.getCatan_service()[0].getName_given() + " " +  data.getCatan_service()[0].getName_family());
		gps.setText(data.getLat() +  " , " + data.getLon());
		
		ImageView image = (ImageView) row.findViewById(R.id.catanRequestThumbnail);

		int out = -1;
		
		int serviceType = (int)data.getCatan_service()[0].getService_type();
		int serviceSubType = (int)data.getCatan_service()[0].getService_subtype();
		
		if(data!= null){
			if(serviceType == 0){	//aid
		
				switch(serviceSubType) {
					case 0:
						out = R.drawable.none;
						break;
						
					case 1:
						out = R.drawable.catan_aid_water;
						break;
						
					case 2:
						out = R.drawable.catan_aid_food;
						break;
						
					case 3:
						out = R.drawable.catan_aid_shelter;
						break;

					case 4:
						out = R.drawable.catan_aid_cleanup;
						break;
						
					case 5:
						out = R.drawable.catan_aid_fuel;
						break;
						
					default:
						out = R.drawable.none;
						break;
				}
				
			}else if(serviceType == 1){	//volunteer
				
				switch(serviceSubType) {
					case 0:
						out = R.drawable.catan_volunteer_translator;
						break;
						
					case 1:
						out = R.drawable.catan_volunteer_guide;
						break;
						
					case 2:
						out = R.drawable.catan_volunteer_laborer;
						break;
						
					case 3:
						out = R.drawable.catan_volunteer_counselor;
						break;
	
					case 4:
						out = R.drawable.catan_volunteer_rescuer;
						break;
						
					case 5:
						out = R.drawable.catan_volunteer_transportation;
						break;
						
					default:
						out = R.drawable.none;
						break;
			}
				
			}
		}

		
		image.setImageDrawable(mResources.getDrawable(out));
		return(row);
		
	}
	
	public List<CatanRequestPayload> getItems() {
		return mItems;
	}
	
	@Override
	public void addAll(Collection<? extends CatanRequestPayload> collection) {
//		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			super.addAll(collection);
//		} else {
		ArrayList<Integer> countOfEachReport = new ArrayList<Integer>();
		countOfEachReport.add( 0);
		countOfEachReport.add( 0);
		countOfEachReport.add( 0);
		countOfEachReport.add( 0);
		countOfEachReport.add( 0);
		countOfEachReport.add( 0);
			for(CatanRequestPayload payload : collection) {
				
				CatanRequestData data = payload.getMessageData();
				double serviceType = data.getCatan_service()[0].getService_type();
				if(parentFragment.getSelectedTab()== serviceType){
					countOfEachReport.set( (int)data.getCatan_service()[0].getService_subtype(),countOfEachReport.get( (int)data.getCatan_service()[0].getService_subtype()) +1);
					add(payload);
				}
			}
			parentFragment.setCountOfEachReport(countOfEachReport);
//		}
	}
}
