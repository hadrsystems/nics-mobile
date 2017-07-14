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
package scout.edu.mit.ll.nics.android;

import android.hardware.GeomagneticField;

public class LRF_HV {
	private double horizontalDistanceValue;
	private String horizontalDistanceUnits;
	
	private double azimuthValue;
	private String azimuthUnits;
	
	private double inclinationValue;
	private String inclinationUnits;
	
	private double slopeDistanceValue;
	private String slopeDistanceUnits;
	private double reckonedLatitude;
	private double reckonedLongitude;
	
	public double getHorizontalDistanceValue() {
		return horizontalDistanceValue;
	}
	
	public void setHorizontalDistanceValue(double horizontalDistanceValue) {
		this.horizontalDistanceValue = horizontalDistanceValue;
	}
	
	public String getHorizontalDistanceUnits() {
		return horizontalDistanceUnits;
	}
	
	public void setHorizontalDistanceUnits(String horizontalDistanceUnits) {
		this.horizontalDistanceUnits = horizontalDistanceUnits;
	}
	
	public double getAzimuthValue() {
		return azimuthValue;
	}
	
	public void setAzimuthValue(double azimuthValue) {
		this.azimuthValue = azimuthValue;
	}
	
	public String getAzimuthUnits() {
		return azimuthUnits;
	}
	
	public void setAzimuthUnits(String azimuthUnits) {
		this.azimuthUnits = azimuthUnits;
	}
	
	public double getInclinationValue() {
		return inclinationValue;
	}
	
	public void setInclinationValue(double inclinationValue) {
		this.inclinationValue = inclinationValue;
	}
	
	public String getInclinationUnits() {
		return inclinationUnits;
	}
	
	public void setInclinationUnits(String inclinationUnits) {
		this.inclinationUnits = inclinationUnits;
	}
	
	public double getSlopeDistanceValue() {
		return slopeDistanceValue;
	}
	
	public void setSlopeDistanceValue(double slopeDistanceValue) {
		this.slopeDistanceValue = slopeDistanceValue;
	}
	
	public String getSlopeDistanceUnits() {
		return slopeDistanceUnits;
	}
	
	public void setSlopeDistanceUnits(String slopeDistanceUnits) {
		this.slopeDistanceUnits = slopeDistanceUnits;
	}
	
	public double[] getReckonedLocation(float latitudeOrigin, float longitudeOrigin, float altitudeOrigin){
		//
		double dtMili = System.currentTimeMillis();
		GeomagneticField geoField = new GeomagneticField(latitudeOrigin, longitudeOrigin, altitudeOrigin,(long)dtMili);
		float azimuthOffset = geoField.getDeclination();
		// check units, get to meters and degrees
		
		// take azimuth and eliminate offset
		setAzimuthValue(getAzimuthValue() + azimuthOffset);
		// take azimuth and hvDistance along with latitude origin and longitude origin -- get new lat/lon pair (option for height as well?)
		return vreckon(latitudeOrigin, longitudeOrigin, altitudeOrigin);
	}
	
	private double[] vreckon(float latitudeOrigin, float longitudeOrigin, float altitudeOrigin){
		double rng = getHorizontalDistanceValue(); 
		
		//double m = 6;
	    double a = 6378137.0;
	    double f = 1/298.257223563;
	    double b = a*(1-f);
	    double lat0 = Math.toRadians(latitudeOrigin);
	    double lon0 = Math.toRadians(longitudeOrigin);
	    double az = Math.toRadians(getAzimuthValue());
	    
	    double axa = a*a;
	    double bxb = b*b;
	    
	    double tan_U1 = (1-f)*Math.sin(lat0)/Math.cos(lat0);
	    
	    double U1 = Math.atan(tan_U1);
	    double cos_alfa1 = Math.cos(az);
	    double sig1 = Math.atan2(tan_U1, cos_alfa1);
	    
	    double cos_U1 = Math.cos(U1);
	    double sin_alfa1 = Math.sin(az);
	    
	    double sin_alfa = cos_U1*sin_alfa1;
	    
	    double cos2_alfa = (1-sin_alfa)*(1+sin_alfa);
	    double uxu = cos2_alfa*(axa-bxb)/bxb;
	    
	    double A = 1+uxu/16384*(4096+uxu*(-768+uxu*(320-175*uxu)));
	    double B = uxu/1024*(256+uxu*(-128+uxu*(74-47*uxu)));
	    
	    double sig = rng/(b*A);
	    
	    double change = 1;
	    
	    double twosig_m;
	    double cos_twosig_m;
	    double cos2_twosig_m;
	    double dsig;
	    double sigold;
	    
	    while(Math.abs(change) > 1e-9){
	    	twosig_m = 2*sig1+sig;
	    	cos_twosig_m = Math.cos(twosig_m);
	    	cos2_twosig_m = cos_twosig_m*cos_twosig_m;
	    	dsig = B*Math.sin(sig)*(cos_twosig_m+1.0/4.0*B*(Math.cos(sig)*(-1+2.*cos2_twosig_m)-1.0/6.0*B*cos_twosig_m*(-3+4*(Math.sin(sig)*Math.sin(sig)))*(-3+4*cos2_twosig_m)));
	    	sigold = sig;
	    	sig = rng/(b*A)+dsig;
	    	change = sig-sigold;
	    }
	    
	    twosig_m = 2*sig1+sig;
	    
	    cos_twosig_m = Math.cos(twosig_m);
	    cos2_twosig_m = cos_twosig_m*cos_twosig_m;
	    double sin_U1 = Math.sin(U1);
	    
	    double cos_sig = Math.cos(sig);
	    double sin_sig = Math.sin(sig);
	    double sin2_alfa = sin_alfa*sin_alfa;
	    
	    double latOut = Math.atan2(sin_U1*cos_sig+cos_U1*sin_sig*cos_alfa1,(1-f)*Math.sqrt(sin2_alfa+(sin_U1*sin_sig-cos_U1*cos_sig*cos_alfa1)*(sin_U1*sin_sig-cos_U1*cos_sig*cos_alfa1)));
		
	    double lambda = Math.atan2(sin_sig*sin_alfa1,cos_U1*cos_sig-sin_U1*sin_sig*cos_alfa1);
	    double C = f/16*cos2_alfa*(4+f*(4-3*cos2_alfa));
	    double L = lambda-(1-C)*f*sin_alfa*(sig+C*sin_sig*(cos_twosig_m+C*cos_sig*(-1+2*cos2_twosig_m)));
	    
	    double lonOut = L+lon0;
	    
	    latOut = Math.toDegrees(latOut);
	    lonOut = Math.toDegrees(lonOut);
	    
	    reckonedLatitude = latOut;
	    reckonedLongitude = lonOut;
		return new double[] { reckonedLatitude, reckonedLongitude};
	}
	
}
