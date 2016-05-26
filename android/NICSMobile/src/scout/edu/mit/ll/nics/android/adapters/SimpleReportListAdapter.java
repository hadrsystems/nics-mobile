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
package scout.edu.mit.ll.nics.android.adapters;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportData;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;


public class SimpleReportListAdapter extends ArrayAdapter<SimpleReportPayload> {
	private List<SimpleReportPayload> mItems;
	private Resources mResources;
	private DataManager mDataManager;
	private Context mContext;
	
	public SimpleReportListAdapter(Context context, int resource, int textViewResourceId, List<SimpleReportPayload> list) {
		
		super(context, resource, textViewResourceId, list);
		
		mContext = context;
		mDataManager = DataManager.getInstance(context);
		mResources = context.getResources();
		mItems = list;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(mItems.size() == 0) {
			return null;
		}
		
		SimpleReportPayload payload = mItems.get(position);
		SimpleReportData data = payload.getMessageData();
		View row = super.getView(position, convertView, parent);

		TextView name = (TextView)row.findViewById(R.id.simpleReportTitle);
		
		TextView size = (TextView)row.findViewById(R.id.simpleReportTime);
		size.setText(new Date(payload.getSeqTime()).toString());
		
		TextView assign = (TextView)row.findViewById(R.id.simpleReportAssignment);
		
		if(data.getAssign() != null && !data.getAssign().isEmpty()) {
			assign.setText(mContext.getString(R.string.assigned_to, data.getAssign()));
			assign.setVisibility(View.VISIBLE);
		} else {
			assign.setVisibility(View.GONE);
		}
		
		ImageView blueDot = (ImageView)row.findViewById(R.id.srBlueDotImage);
		if(payload.isNew()){
			blueDot.setVisibility(View.VISIBLE);
		}else{
			blueDot.setVisibility(View.INVISIBLE);
		}
		
		if(payload.isDraft()) {
			name.setText(mContext.getString(R.string.draft) + String.valueOf(data.getUser()));
		} else if(payload.getSendStatus() == ReportSendStatus.WAITING_TO_SEND && payload.getProgress() != 100.0) {
			if(!payload.isFailedToSend()) {
				if(payload.getProgress() > 0){
					name.setText(mContext.getString(R.string.sending_progress, payload.getProgress()) + String.valueOf(data.getUser()));
				}else{
					name.setText(mContext.getString(R.string.sending) + String.valueOf(data.getUser()));
				}
			} else {
				name.setText(R.string.sending_failed + String.valueOf(data.getUser()));
			}
			
			if(!mDataManager.isOnline()) {
				size.setText(R.string.device_not_connected_to_network);
			}
		} else {
			name.setText(String.valueOf(data.getUser()));
		}
		

		ImageView image = (ImageView) row.findViewById(R.id.simpleReportThumbnail);

		int out = -1;
		if(data.getCategory() != null) {
			
			switch(data.getCategory()) {
				case OTHR:
					out = R.drawable.message;
					break;
				case IC:
					out = R.drawable.incident_commander;
					break;
				case PIO:
					out = R.drawable.public_information_officer;
					break;
				case LO:
					out = R.drawable.liaison_officer;
					break;
				case AR:
					out = R.drawable.agency_representative;
					break;
				case SO:
					out = R.drawable.safety_officer;
					break;
				case OS:
					out = R.drawable.operations;
					break;
				case PS:
					out = R.drawable.planning;
					break;
				case SR:
					out = R.drawable.suppression_repair;
					break;
				case FS:
					out = R.drawable.finance_section;
					break;
				case COMP:
					out = R.drawable.damage_advisory;
					break;
				case LS:
					out = R.drawable.logistics_section;
					break;
				case GSR:
					out = R.drawable.ground_support;
					break;
				default:
					out = R.drawable.none;
					break;
			}
		} else {
			out = R.drawable.none;
		}
		
		image.setImageDrawable(mResources.getDrawable(out));
		return(row);
		
	}
	
	public List<SimpleReportPayload> getItems() {
		return mItems;
	}
	
	@Override
	public void addAll(Collection<? extends SimpleReportPayload> collection) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(collection);
		} else {
			for(SimpleReportPayload payload : collection) {
				add(payload);
			}
		}
	}
}
