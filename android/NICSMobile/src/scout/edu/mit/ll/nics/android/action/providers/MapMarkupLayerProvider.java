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

public class MapMarkupLayerProvider extends ActionProvider implements OnMenuItemClickListener {
	
//	private Context mContext;
	
	public MapMarkupLayerProvider(Context context) {
		super(context);
//		mContext = context;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
//		boolean isChecked = item.isChecked();
//		item.setChecked(!item.isChecked());
//		
//		String selectedLayer = null;
//		
//		Resources resources = mContext.getResources();
//		
//		switch(item.getItemId()) {
//			case R.id.wfs_ca_xfr_avl:
//				selectedLayer = resources.getString(R.string.wfslayer_ca_xfr_avl_layername);
//				break;
//				
//			case R.id.wfs_cdf_aff:
//				selectedLayer = resources.getString(R.string.wfslayer_cdf_aff_layername);
//				break;
//				
//			case R.id.wfs_ca_xmy_xsl_avl:
//				selectedLayer = resources.getString(R.string.wfslayer_ca_xmy_xsl_avl_layername);
//				break;
//				
//			case R.id.wfs_ca_beu_xmy_avl:
//				selectedLayer = resources.getString(R.string.wfslayer_ca_beu_xmy_avl_layername);
//				break;
//				
//			case R.id.wfs_ca_xri_avl:
//				selectedLayer = resources.getString(R.string.wfslayer_ca_xri_avl_layername);
//				break;
//				
//			case R.id.wfs_delorme_pli:
//				selectedLayer = resources.getString(R.string.wfslayer_delorme_pli_layername);
//				break;
//				
//			case R.id.wfs_ca_xsd_rcip_avl:
//				selectedLayer = resources.getString(R.string.wfslayer_ca_xsd_rcip_avl_layername);
//				break;
//				
//			case R.id.wfs_sarapp_pli:
//				selectedLayer = resources.getString(R.string.wfslayer_sarapp_pli_layername);
//				break;
//				
//			case R.id.wfs_nics_mobile:
//				selectedLayer = resources.getString(R.string.wfslayer_nics_mobile_layername);
//				break;
//				
//			case R.id.wfs_nics_sr:
//				String layer = resources.getString(R.string.wfslayer_nics_simple_report_layername);
//				if(!isChecked) {
//					MapMarkupFragment.addSimpleReportLayer(layer);
//				} else {
//					MapMarkupFragment.removeMapLayer(layer);
//				}
//				break;
//		}
		
//		if(selectedLayer != null) {
//			if(!isChecked) {
//				MapMarkupFragment.addMapLayer(selectedLayer);
//			} else {
//				MapMarkupFragment.removeMapLayer(selectedLayer);
//			}
//			return true;
//		}

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
