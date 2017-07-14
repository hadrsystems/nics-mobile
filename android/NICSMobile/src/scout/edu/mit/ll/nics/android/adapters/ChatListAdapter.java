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
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;

public class ChatListAdapter extends ArrayAdapter<ChatPayload> {
	private List<ChatPayload> mItems;
	private Context mContext;
	
	public ChatListAdapter(Context context, int resource, int textViewResourceId, List<ChatPayload> list) {
		
		super(context, resource, textViewResourceId, list);
		
		mContext = context;
		mItems = list;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ChatPayload payload = mItems.get(position);
		
		View row = super.getView(position, convertView, parent);

		TextView name = (TextView)row.findViewById(R.id.chatUser);
		name.setText(String.valueOf(payload.getNickname()));
		
		
		TextView message = (TextView)row.findViewById(R.id.chatMessage);
		message.setText(String.valueOf(payload.getmessage()));
		
		TextView size = (TextView)row.findViewById(R.id.chatTimeStamp);
		if(payload.getseqnum() <= 0){
			size.setText("");
		}else{
			size.setText(new Date(payload.getseqnum()).toString());
		}
		
		return(row);
		
	}
	
	@Override
	public void addAll(Collection<? extends ChatPayload> collection) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(collection);
		} else {
			for(ChatPayload payload : collection) {
				add(payload);
			}
		}
	}
}
