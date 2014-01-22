package org.hw.parlance;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
/**
 * Project Name: Parlance App
 * @Author: Yanchao Yu
 * @Version: 1.1
 * Class: ParlanceActivity.class
 */
public class GPSTracker extends Service implements LocationListener{
	private final Context m_context;
	private boolean isEnable_GPS = false; // flag for GPS status
	private boolean isConnected = false; // flag for Internet
	private boolean isGPS = false; // flag for GPS
	private Location location;
	private double latitude, longitude; // value of latitude and longitude
	
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // minimum distance of change updates in meters
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // minimum time between updates in milliseconds
	protected LocationManager location_manager;
	
	/**
	 * Constructor to checking GPS and initialize the location data. 
	 * @param m_context
	 */
	public GPSTracker(Context m_context){ 
		this.m_context = m_context;
		getLocation();
	}
	
	/**
	 * Method to check GPS and Internet status and get Location
	 * @return Location the location with latitude and longitude
	 */
	private Location getLocation(){
		try{
			this.location_manager = (LocationManager) m_context.getSystemService(LOCATION_SERVICE);
			this.isEnable_GPS = location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER); // checking GPS status 
			this.isConnected = location_manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); // checking Internet
			if(!isEnable_GPS && !isConnected){}
			else{
				this.isGPS = true;
				if (isConnected){ // if Internet is avaliable
					this.location_manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Checked");
                    if(this.location_manager != null){
                    	this.location = this.location_manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    	if(this.location != null){
                    		this.latitude = this.location.getLatitude(); // get Latitude
                    		this.longitude = this.location.getLongitude(); // get Longitude
                    	}
                    }
				}
				if(isGPS){ // if get latitude and longitude
					if(this.location == null){
						this.location_manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS", "Enabled");
                        if(this.location_manager != null){
                        	this.location = this.location_manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        	if(this.location != null){
                        		this.latitude = this.location.getLatitude(); // get Latitude
                        		this.longitude = this.location.getLongitude(); // get Longitude
                        	}
                        }
					}
				}
			}
		}catch(Exception e){}
		return location;		
	}	
	/**
	 * Method to stop GPS interface
	 */
	public void stopGPS(){
		if(this.location_manager != null){
			this.location_manager.removeUpdates(GPSTracker.this);
		}
	}	
	/**
	 * Method to get latitude
	 * @return double value of latitude
	 */
	public double getLatitude(){
		if(this.location != null)
			this.latitude = this.location.getLatitude();
		return this.latitude;
	}    
	/**
	 * Method to get longitude
	 * @return double value of longitude
	 */
	public double getLongitude(){
		if(this.location != null)
			this.longitude = this.location.getLongitude();
		return this.longitude;
	}	

	
	public void setLatitude(double latitude){
			this.latitude = latitude;
	}   
	

	public void setLongitude(double longitude){
			this.longitude = longitude;
	}    
	
	/**
	 * Method to check the GPS state
	 * @return boolean flag for whether GPS could get latitude and longitude
	 */
	public boolean isGetLocation(){
		return this.isGPS;
	}	
	/**
	 * Method to show Settings Dialog
	 */
	public void alteringSettings(){
		AlertDialog.Builder alert_Dialog = new AlertDialog.Builder(this.m_context);
		alert_Dialog.setTitle("GPS is settings"); // set Dialog title
		alert_Dialog.setMessage("GPS isn't enabled. Do you want to go to settings menu?"); // set Dialog message
		alert_Dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() { // press "Settings" button
			public void onClick(DialogInterface dialog,int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				m_context.startActivity(intent);
			}
        });
		alert_Dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // press "Cancel" button
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
		alert_Dialog.show();
	} 

/********************************************* Do not use in App *************************************************/
	@Override
	public void onLocationChanged(Location arg0) {
		this.setLongitude(arg0.getLongitude());
		this.setLatitude(arg0.getLongitude());
		
	}
	@Override
	public void onProviderDisabled(String arg0) {}
	@Override
	public void onProviderEnabled(String arg0) {}
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}
	@Override
	public IBinder onBind(Intent arg0) {return null;}
}