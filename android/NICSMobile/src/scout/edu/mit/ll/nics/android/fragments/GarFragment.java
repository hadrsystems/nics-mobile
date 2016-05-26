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


import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;

public class GarFragment extends Fragment {
	
	private Context mContext;
	
	private static final String nics_GAR_STATE = "nics_GAR_STATE";
	// from the UI
	private int m_total = 0;
	private String m_sendString;
	private Button m_totalBtn;
	private Button m_sendBtn;
	
	final int numItems = 6;
	private SeekBar m_SB[] = new SeekBar[numItems];
	private TextView m_ET[] = new TextView[numItems];
		
	private TextView m_totalTxt;
	private View mRootView;
	private DataManager mDataManager;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_gar);
        
        mContext = getActivity();
        mDataManager = DataManager.getInstance(mContext);
        // Hide keyboard
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
    	mRootView = inflater.inflate(R.layout.fragment_gar, container, false);
    	
		
		// Rig up seek bars
		setSeekBars();	

		// Edit text boxes
		setTextBoxes();
		
		// Set listeners and buttons
		setListeners();
		
		SharedPreferences settings = this.mContext.getSharedPreferences(nics_GAR_STATE, 0);
		if(settings != null) {
			for(int i = 0; i < m_SB.length; i++) {
				m_SB[i].setProgress(settings.getInt("seekbar_" + i, 0));
			}
			
			for(int i = 0; i < m_ET.length; i++) {
				m_ET[i].setText(settings.getString("edittext_" + i, "0"));
			}
			
			doTotal();
		}
    	return mRootView;
    }
   
    public void doTotal(){
   			
   			m_total = 0;
   			for (int i = 0; i < numItems; i++)
   				m_total += Integer.parseInt(m_ET[i].getText().toString());
    			
   			// set total 
   			m_totalTxt.setText(m_total+"");
   			m_totalTxt.setTextColor(Color.BLACK);
   			
   			String serious = getString(R.string.high_risk);
   			if(m_total > 44){    								
				m_totalTxt.setBackgroundColor(Color.RED);
   			}
			else if (m_total > 23){
				m_totalTxt.setBackgroundColor(Color.YELLOW);
				serious = getString(R.string.caution);
			}
			else{
				m_totalTxt.setBackgroundColor(Color.GREEN);
				serious = getString(R.string.low_risk);
			}
   			   			
   			// create send string
   			m_sendString = getString(R.string.gar_condition_update, serious, m_total, new Date());
   			((TextView) mRootView.findViewById(R.id.sendTxt)).setText(m_sendString);
    }
    
    public void doSend(){
    	// post the time message was sent to the view
    	((TextView) mRootView.findViewById(R.id.sendStatusTxt)).setText(getString(R.string.gar_sent, new Date()));
    	
    	mDataManager.addChatMsgToStoreAndForward(m_sendString, mDataManager.getSelectedCollabRoom().getName());
    	mDataManager.sendChatMessages();
    }
    
    public void setSeekBars(){

		String[] barNames = {"superBar","planBar","crewSelBar", "crewFitBar", "envBar", "complexBar"};
		
		for (int i = 0; i < numItems; i++){
			int resID = this.getResources().getIdentifier(barNames[i], "id", mContext.getPackageName());
			m_SB[i] = (SeekBar) mRootView.findViewById(resID);
		}
		       
        
    }
    
    public void setTextBoxes(){ 	
		
    	String[] editNames = {"superTxt","planTxt","crewSelTxt","crewFitTxt","envTxt","complexTxt"};		
    	
    	for (int i = 0; i < numItems; i++){
			int resID = this.getResources().getIdentifier(editNames[i], "id", mContext.getPackageName());
			m_ET[i] = (TextView) mRootView.findViewById(resID);
			m_ET[i].setEnabled(false);	// disable manual entry
		}
		
		m_totalTxt = (TextView) mRootView.findViewById(R.id.totalTxt);
		m_totalTxt.setEnabled(false);
    }
    
    public void setListeners(){
    	for(int i = 0; i < numItems; i++){
    		m_SB[i].setOnSeekBarChangeListener( new OnSeekBarChangeListener() {	  
    			public void onProgressChanged(SeekBar seekBar, int val, boolean fromUser)
    			{
    				for(int j = 0; j < numItems; j++)
    					if (m_SB[j].getId() == seekBar.getId())
    					{
    							m_ET[j].setText(val+"");
    							m_ET[j].setTextColor(Color.BLACK);
    							if(val > 6)    								
    								m_ET[j].setBackgroundColor(Color.RED);
    							else if (val > 3)
    								m_ET[j].setBackgroundColor(Color.YELLOW);
    							else
    								m_ET[j].setBackgroundColor(Color.GREEN);
    					}
    			
              	}
                public void onStartTrackingTouch(SeekBar seekBar) {}
                public void onStopTrackingTouch(SeekBar seekBar) {} 
    		  });
    	}
    	
        // total Button
        m_totalBtn = (Button) mRootView.findViewById(R.id.buttonTotal);
        m_totalBtn.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	doTotal();	        	
	        }
	    });
        
        // send Button
        m_sendBtn = (Button) mRootView.findViewById(R.id.buttonSend);
        m_sendBtn.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	doSend();	        	
	        }
	    });
    }
    
    @Override
    public void onPause() {
		super.onPause();
		SharedPreferences settings = mContext.getSharedPreferences(nics_GAR_STATE, 0);
		SharedPreferences.Editor editor = settings.edit();

		for(int i = 0; i < m_SB.length; i++) {
			editor.putInt("seekbar_" + i, m_SB[i].getProgress());
		}
		
		for(int i = 0; i < m_ET.length; i++) {
			editor.putString("edittext_" + i, m_ET[i].getText().toString());
		}
		
		editor.commit();

	}
}
