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
package scout.edu.mit.ll.nics.android.action.providers;

import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.fragments.MapMarkupFragment;

public class MapMarkupActionProvider extends ActionProvider implements OnMenuItemClickListener {
	
	public MapMarkupActionProvider(Context context) {
		super(context);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		item.setChecked(!item.isChecked());
		
		switch(item.getItemId()) {
			case R.id.normalMapOption:
				MapMarkupFragment.changeMapType(GoogleMap.MAP_TYPE_NORMAL);
				return true;
				
			case R.id.satelliteMapOption:
				MapMarkupFragment.changeMapType(GoogleMap.MAP_TYPE_SATELLITE);
				return true;
				
			case R.id.hybridMapOption:
				MapMarkupFragment.changeMapType(GoogleMap.MAP_TYPE_HYBRID);
				return true;
			
			case R.id.terrainMapOption:
				MapMarkupFragment.changeMapType(GoogleMap.MAP_TYPE_TERRAIN);
				return true;
				
			case R.id.offlineMapOption:
				MapMarkupFragment.changeMapType(GoogleMap.MAP_TYPE_NONE);
				return true;
				
			case R.id.trafficMapOption:
				MapMarkupFragment.enableTraffic(item.isChecked());
				return true;
				
			case R.id.indoorMapOption:
				MapMarkupFragment.enableIndoor(item.isChecked());
				return true;
				
		}

		return false;
	}
	
	@Override
	public void onPrepareSubMenu(SubMenu subMenu) {
		super.onPrepareSubMenu(subMenu);
		
		for(int i = 0; i < subMenu.size(); i++) {
			subMenu.getItem(i).setOnMenuItemClickListener(this);
		}
	}
	
	@Override
	public View onCreateActionView(MenuItem item) {

		SubMenu subMenu = item.getSubMenu();
		for(int i = 0; i < subMenu.size(); i++) {
			subMenu.getItem(i).setOnMenuItemClickListener(this);
		}
		
		return super.onCreateActionView(item);
	}

	@Override
	public View onCreateActionView() {
		return null;
	}
}
