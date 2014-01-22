package org.hw.parlance;

/*
 ParlanceActivity.java
 Copyright (C) 2012  Belledonne Communications, Grenoble, France

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */




import static android.content.Intent.ACTION_MAIN;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.hw.parlance.ui.ItemAdapter;
import org.hw.parlance.ui.ItemHandler;
import org.hw.parlance.ui.JSONParser;
import org.json.JSONException;
import org.linphone.AboutFragment;
import org.linphone.AccountPreferencesFragment;
import org.linphone.ChatFragment;
import org.linphone.ChatListFragment;
import org.linphone.ChatMessage;
import org.linphone.ChatStorage;
import org.linphone.Contact;
//import org.linphone.ContactFragment;
import org.linphone.ContactsFragment;
import org.linphone.DialerFragment;
//import org.linphone.EditContactFragment;
import org.linphone.FragmentsAvailable;
import org.linphone.InCallActivity;
import org.linphone.IncomingCallActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.LinphoneSimpleListener;
import org.linphone.LinphoneUtils;
import org.linphone.R;
import org.linphone.SettingsFragment;
import org.linphone.StatusFragment;
import org.linphone.LinphoneManager.AddressType;
import org.linphone.LinphoneSimpleListener.LinphoneOnCallStateChangedListener;
import org.linphone.LinphoneSimpleListener.LinphoneOnMessageReceivedListener;
import org.linphone.LinphoneSimpleListener.LinphoneOnRegistrationStateChangedListener;
import org.linphone.R.anim;
import org.linphone.R.bool;
import org.linphone.R.id;
import org.linphone.R.layout;
import org.linphone.R.string;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.OnlineStatus;
import org.linphone.mediastream.Log;
import org.linphone.ui.AddressText;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * @author Sylvain Berfini
 */
public class ParlanceActivity extends FragmentActivity implements
		OnClickListener, LinphoneOnCallStateChangedListener,
		LinphoneOnMessageReceivedListener,
		LinphoneOnRegistrationStateChangedListener {
	public static final String PREF_FIRST_LAUNCH = "pref_first_launch";
	private static final int SETTINGS_ACTIVITY = 123;
	private static final int FIRST_LOGIN_ACTIVITY = 101;
	private static final int callActivity = 19;
	private static final String EXTENTIONNUM = "1001@137.195.27.135";
	
	private String FINALID = null;

	private static ParlanceActivity instance;

	private StatusFragment statusFragment;
	private SavedState dialerSavedState;
	private boolean preferLinphoneContacts = false;
	private boolean isAnimationDisabled = false, isContactPresenceDisabled = false;
	private Handler mHandler = new Handler();
	private Cursor contactCursor, sipContactCursor;
	private OrientationEventListener mOrientationHelper;	

	private boolean isMessage = false;
	
	private ViewFlipper btn_viewFlipper, layouts_viewFlipper;

	private ImageButton btn_refresh;
	private ImageButton btn_dialer;
	private ImageButton btn_about;
	private ImageButton btn_hangup;
	private ListView list_view;
	private ItemAdapter _itemAdapter;

	private List<String> _reIdList = new ArrayList<String>();
	private GoogleMap mMap;
	
	private LatLng _GPS;
	private List<Marker> _markList;
	private boolean _isFinished = true;
	private String finalId = null;
	
	private int[] numMarkerIcon = { R.drawable.logo_1_icon,R.drawable.logo_2_icon,R.drawable.logo_3_icon,R.drawable.logo_4_icon,R.drawable.logo_5_icon,
			R.drawable.logo_6_icon,R.drawable.logo_7_icon,R.drawable.logo_8_icon,R.drawable.logo_9_icon,R.drawable.logo_10_icon};

	private final String YAHOOAPI = "http://parlance.research.yahoo.com:4080/LocalSearchService/search?model.ranking=unranked&rank.or:venueid=";

	private TextView venues_txtview;
	
	public static final boolean isInstanciated() {
		return instance != null;
	}

	public static final ParlanceActivity instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("LinphoneActivity not instantiated yet");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!LinphoneManager.isInstanciated()) {
			Log.e("No service running: avoid crash by starting the launcher", this.getClass().getName());
			// super.onCreate called earlier
			finish();
			startActivity(getIntent().setClass(this, ParlanceLauncherActivity.class));
			return;
		}

		boolean useFirstLoginActivity = getResources().getBoolean(R.bool.display_account_wizard_at_first_start);
		if (useFirstLoginActivity && LinphonePreferences.instance().isFirstLaunch()) {
			if (LinphonePreferences.instance().getAccountCount() > 0) {
				LinphonePreferences.instance().firstLaunchSuccessful();
			} else {
				startActivityForResult(new Intent().setClass(this, SetupActivity.class), FIRST_LOGIN_ACTIVITY);
			}
		}

		setContentView(R.layout.main);
		instance = this;
		initButtons();
		int rotation = getWindowManager().getDefaultDisplay().getRotation();
		switch (rotation) {
		case Surface.ROTATION_0:
			rotation = 0;
			break;
		case Surface.ROTATION_90:
			rotation = 90;
			break;
		case Surface.ROTATION_180:
			rotation = 180;
			break;
		case Surface.ROTATION_270:
			rotation = 270;
			break;
		}

		LinphoneManager.getLc().setDeviceRotation(rotation);
		mAlwaysChangingPhoneAngle = rotation;
		updateAnimationsState();
	}
	
	private MenuItem _speakerButton;
	private boolean _isSpeaker = false;
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        _speakerButton = (MenuItem) menu.findItem (R.id.button_speaker);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.button_speaker:
            	switchState();
            	return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
	
	private void switchState() {
		if(_isSpeaker){
			LinphoneManager.getInstance().chTypeOfAudio(false);
			_speakerButton.setIcon(R.drawable.ic_action_speak);
			_isSpeaker = false;
		}
		else{
			LinphoneManager.getInstance().chTypeOfAudio(true);
			_speakerButton.setIcon(R.drawable.ic_action_nospeak);
			_isSpeaker = true;
		}
	}

	private void initButtons() {		
		btn_refresh = (ImageButton)findViewById(R.id.button_refresh);
		btn_refresh.setOnClickListener(this);
		btn_dialer = (ImageButton) findViewById(R.id.button_call);
		btn_dialer.setOnClickListener(this);
		btn_hangup = (ImageButton) findViewById(R.id.button_hang);
		btn_hangup.setOnClickListener(this);
		btn_about = (ImageButton) findViewById(R.id.button_about);
		btn_about.setOnClickListener(this);		
		btn_viewFlipper = (ViewFlipper)findViewById(R.id.btn_viewFlipper);
		layouts_viewFlipper = (ViewFlipper)findViewById(R.id.mark);
		
		// Initial ListView of restaurant items
		list_view = (ListView) findViewById(R.id.recom_list);  
				
		_itemAdapter = new ItemAdapter(this);  
		list_view.setAdapter(_itemAdapter); 	
		list_view.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				if(_isFinished){				
					String name = ((TextView) arg1.findViewById(R.id.tv_rname)).getText().toString();
					FINALID = getFinalID(name);
					Intent mainIntent = new Intent(ParlanceActivity.this,org.hw.parlance.YahooActivity.class); 						 
					if(FINALID != null){
						mainIntent.putExtra("sID", FINALID);
						mainIntent.putExtra("isID", true);
					}
					else 
						mainIntent.putExtra("isID", false);
					 
					ParlanceActivity.this.startActivity(mainIntent); 
					ParlanceActivity.this.finish(); 
				}
			}			
		});

		if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
		
		_markList = new  ArrayList<Marker>();
		getLocation();
		
		venues_txtview = (TextView) findViewById(R.id.venues_txtview);
	}
	
	private void resetSelection() {
		btn_dialer.setSelected(false);
		btn_about.setSelected(false);
		btn_about.setEnabled(true);
		btn_hangup.setSelected(false);
	
		if(btn_viewFlipper.getCurrentView().getId() == R.id.hung_up_btn)
			btn_viewFlipper.showPrevious();
	
		if(layouts_viewFlipper.getCurrentView().getId() == R.id.viewAbout)
			layouts_viewFlipper.showNext();
		else if(layouts_viewFlipper.getCurrentView().getId() == R.id.viewList)
			layouts_viewFlipper.showPrevious();
	}
	
	private void hideStatusBar() {		
		findViewById(R.id.status).setVisibility(View.GONE);
		findViewById(R.id.fragmentContainer).setPadding(0, 0, 0, 0);
	}

	private void showStatusBar() {
		if (statusFragment != null && !statusFragment.isVisible()) {
			statusFragment.getView().setVisibility(View.VISIBLE);
		}
		findViewById(R.id.status).setVisibility(View.VISIBLE);
		findViewById(R.id.fragmentContainer).setPadding(0, LinphoneUtils.pixelsToDpi(getResources(), 40), 0, 0);
	}

	private void updateAnimationsState() {
		isAnimationDisabled = getResources().getBoolean(R.bool.disable_animations) || !LinphonePreferences.instance().areAnimationsEnabled();
		isContactPresenceDisabled = !getResources().getBoolean(R.bool.enable_linphone_friends);
	}

	public boolean isAnimationDisabled() {
		return isAnimationDisabled;
	}

	public boolean isContactPresenceDisabled() {
		return isContactPresenceDisabled;
	}

	@SuppressLint("SimpleDateFormat")
	private String secondsToDisplayableString(int secs) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		cal.set(0, 0, 0, 0, 0, secs);
		return dateFormat.format(cal.getTime());
	}
	
	@Override
	public void onClick(View v) {		
		int id = v.getId();
		if(id == R.id.button_refresh){
			resetSelection();					
		} else if (id == R.id.button_call) {
			btn_viewFlipper.showNext();	
			btn_about.setSelected(false);
			btn_about.setEnabled(false);	
			
		 	LinphoneManager.getInstance().newOutgoingCall(EXTENTIONNUM, "parlance assistant");
		 	_isFinished = false;
			
			if(layouts_viewFlipper.getCurrentView().getId() == R.id.viewLogo)
				layouts_viewFlipper.showNext();
			else if(layouts_viewFlipper.getCurrentView().getId() == R.id.viewAbout)
				layouts_viewFlipper.showPrevious();	
			
				
		} else if (id == R.id.button_hang) {
			LinphoneCore lc = LinphoneManager.getLc();
			LinphoneCall currentCall = lc.getCurrentCall();
						
			if (currentCall != null) {
				lc.terminateCall(currentCall);
			} else if (lc.isInConference()) {
				lc.terminateConference();
			} else {
				lc.terminateAllCalls();
			}
			_isFinished = true;
			//resetSelection();	
		} else if (id == R.id.button_about) {
			if(layouts_viewFlipper.getCurrentView().getId() == R.id.viewLogo)
				layouts_viewFlipper.showPrevious();
			else if(layouts_viewFlipper.getCurrentView().getId() == R.id.viewList)
				layouts_viewFlipper.showNext();
			btn_about.setSelected(true);
		}
	}

	public void updateStatusFragment(StatusFragment fragment) {
		statusFragment = fragment;

		LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
		if (lc != null && lc.getDefaultProxyConfig() != null) {
			statusFragment.registrationStateChanged(LinphoneManager.getLc().getDefaultProxyConfig().getState());
		}
	}

	public void displaySettings() {
		changeCurrentFragment(FragmentsAvailable.SETTINGS, null);
		btn_about.setSelected(true);
	}

	public void displayAccountSettings(int accountNumber) {
		Bundle bundle = new Bundle();
		bundle.putInt("Account", accountNumber);
		changeCurrentFragment(FragmentsAvailable.ACCOUNT_SETTINGS, bundle);
		btn_about.setSelected(true);
	}

	public StatusFragment getStatusFragment() {
		return statusFragment;
	}

	public List<String> getChatList() {
		return getChatStorage().getChatList();
	}

	public List<String> getDraftChatList() {
		return getChatStorage().getDrafts();
	}

	public List<ChatMessage> getChatMessages(String correspondent) {
		return getChatStorage().getMessages(correspondent);
	}

	public void removeFromChatList(String sipUri) {
		getChatStorage().removeDiscussion(sipUri);
	}

	public void removeFromDrafts(String sipUri) {
		getChatStorage().deleteDraft(sipUri);
	}

	@Override
	public void onMessageReceived(LinphoneAddress from, LinphoneChatMessage message, int id) {
		final String _sipMessage = message.getText();
		if(_sipMessage.contains("matchingVenues")){
			isMessage = true;
			runOnUiThread(new Runnable(){
				@Override
				public void run() {	
					//venues_txtview.setText(getVenues_Test(_sipMessage));
					parserMessage(_sipMessage);					
				}				
			});
		}
		/*else if(_sipMessage.contains("Name")){
			
		}*/
		
		
		//FINALID = getFinalID("Just Won Ton");
	}
	
	
	public synchronized String getVenues_Test(String message){
		String venues = "Test = ";
		_reIdList = new JSONParser().parseJSON(message);
		int num_res = 0;
		if(_reIdList.size() <= 11 && _reIdList.size() > 1){
			if(_reIdList.size() == 2)
				this.finalId = _reIdList.get(0);
			
			num_res = _reIdList.size();
		}else if(_reIdList.size() > 11){
			num_res = 11;
		}	
		
		for(int i = 0; i < num_res-1; i++){
			venues += _reIdList.get(i) + " , ";
		}
		
		return venues;
	}
	
	public int onMessageSent(String to, String message) {
		getChatStorage().deleteDraft(to);
		return getChatStorage().saveTextMessage("", to, message, System.currentTimeMillis());
	}

	public int onMessageSent(String to, Bitmap image, String imageURL) {
		getChatStorage().deleteDraft(to);
		return getChatStorage().saveImageMessage("", to, image, imageURL, System.currentTimeMillis());
	}

	public void onMessageStateChanged(String to, String message, int newState) {
		getChatStorage().updateMessageStatus(to, message, newState);
	}

	public void onImageMessageStateChanged(String to, int id, int newState) {
		getChatStorage().updateMessageStatus(to, id, newState);
	}

	@Override
	public void onRegistrationStateChanged(RegistrationState state) {
		if (statusFragment != null) {
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null && lc.getDefaultProxyConfig() != null)
				statusFragment.registrationStateChanged(lc.getDefaultProxyConfig().getState());
			else
				statusFragment.registrationStateChanged(RegistrationState.RegistrationNone);
		}
	}

	@Override
	public void onCallStateChanged(LinphoneCall call, State state, String message) {
		if (state == State.IncomingReceived) {
			startActivity(new Intent(this, IncomingCallActivity.class));
		} else if (state == State.CallEnd){
		//	String _sID = "21385205";
			 
			/* Intent mainIntent = new Intent(this,org.hw.parlance.YahooActivity.class); 						 
			 if(FINALID != null){
				 mainIntent.putExtra("sID", FINALID);
				 mainIntent.putExtra("isID", true);
			 }
			 else 
				 mainIntent.putExtra("isID", false);
				 
			 this.startActivity(mainIntent); 
			 this.finish(); 	*/
			 
			 
		} else if ( state == State.Error || state == State.CallReleased) {
			// Convert LinphoneCore message for internalization
			if (message != null && message.equals("Call declined.")) { 
				displayCustomToast(getString(R.string.error_call_declined), Toast.LENGTH_LONG);
			} else if (message != null && message.equals("User not found.")) {
				displayCustomToast(getString(R.string.error_user_not_found), Toast.LENGTH_LONG);
			} else if (message != null && message.equals("Incompatible media parameters.")) {
				displayCustomToast(getString(R.string.error_incompatible_media), Toast.LENGTH_LONG);
			}
			resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
		}
	}

	public void displayCustomToast(final String message, final int duration) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.toast, (ViewGroup) findViewById(R.id.toastRoot));

				TextView toastText = (TextView) layout.findViewById(R.id.toastMessage);
				toastText.setText(message);

				final Toast toast = new Toast(getApplicationContext());
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setDuration(duration);
				toast.setView(layout);
				toast.show();
			}
		});
	}

	public void setAddressAndGoToDialer(String number) {
		Bundle extras = new Bundle();
		extras.putString("SipUri", number);
		changeCurrentFragment(FragmentsAvailable.DIALER, extras);
	}
	public void startIncallActivity(LinphoneCall currentCall) {
		Intent intent = new Intent(this, InCallActivity.class);
		intent.putExtra("VideoEnabled", false);
		startOrientationSensor();
		startActivityForResult(intent, callActivity);
	}

	/**
	 * Register a sensor to track phoneOrientation changes
	 */
	private synchronized void startOrientationSensor() {
		if (mOrientationHelper == null) {
			mOrientationHelper = new LocalOrientationEventListener(this);
		}
		mOrientationHelper.enable();
	}

	private int mAlwaysChangingPhoneAngle = -1;
	private class LocalOrientationEventListener extends OrientationEventListener {
		public LocalOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(final int o) {
			if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
				return;
			}

			int degrees = 270;
			if (o < 45 || o > 315)
				degrees = 0;
			else if (o < 135)
				degrees = 90;
			else if (o < 225)
				degrees = 180;

			if (mAlwaysChangingPhoneAngle == degrees) {
				return;
			}
			mAlwaysChangingPhoneAngle = degrees;

			Log.d("Phone orientation changed to ", degrees);
			int rotation = (360 - degrees) % 360;
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			if (lc != null) {
				lc.setDeviceRotation(rotation);
				LinphoneCall currentCall = lc.getCurrentCall();
				if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
					lc.updateCall(currentCall, null);
				}
			}
		}
	}
	
	public Cursor getAllContactsCursor() {
		return contactCursor;
	}

	public Cursor getSIPContactsCursor() {
		return sipContactCursor;
	}

	private void refreshStatus(OnlineStatus status) {
		if (LinphoneManager.isInstanciated()) {
			LinphoneManager.getLcIfManagerNotDestroyedOrNull().setPresenceInfo(0, "", status);
		}
	}

	public void exit() {
		finish();
		stopService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_FIRST_USER && requestCode == SETTINGS_ACTIVITY) {
			if (data.getExtras().getBoolean("Exit", false)) {
				exit();
			} else {
				FragmentsAvailable newFragment = (FragmentsAvailable) data.getExtras().getSerializable("FragmentToDisplay");
				changeCurrentFragment(newFragment, null, true);
				selectMenu(newFragment);
			}
		} else if (requestCode == callActivity) {
			boolean callTransfer = data == null ? false : data.getBooleanExtra("Transfer", false);
			if (LinphoneManager.getLc().getCallsNb() > 0) {
				initInCallMenuLayout(callTransfer);
			} else {
				resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
			}
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (!LinphoneService.isReady())  {
			startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
		}

		LinphoneManager.removeListener(this);
		LinphoneManager.addListener(this);

		prepareContactsInBackground();

		if (LinphoneManager.getLc().getCalls().length > 0) {
			LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
			LinphoneCall.State callState = call.getState();
			if (callState == State.IncomingReceived) {
				startActivity(new Intent(this, IncomingCallActivity.class));
			}
		}

		refreshStatus(OnlineStatus.Online);
	}

	@Override
	protected void onPause() {		
		super.onPause();
		refreshStatus(OnlineStatus.Away);
	}

	@Override
	protected void onDestroy() {
		LinphoneManager.removeListener(this);

		if (mOrientationHelper != null) {
			mOrientationHelper.disable();
			mOrientationHelper = null;
		}

		instance = null;
		super.onDestroy();

		unbindDrawables(findViewById(R.id.topLayout));
		System.gc();
	}

	private void unbindDrawables(View view) {
		if (view != null && view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU && statusFragment != null) {
			if (event.getRepeatCount() < 1) {
				statusFragment.openOrCloseStatusBar(true);
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	/********************************************************************************************/

	private String getFinalID(String _name){
		for(int index=0; index < _itemAdapter.arr.size(); index++){	
			if(_itemAdapter.arr.get(index).getName().equals(_name)){
				return _itemAdapter.arr.get(index).getID();
			}
		}
		return null;
	}
	/**
	 * Method to get Location of Emergency
	 */
	private void getLocation(){
		GPSTracker gps_tracker = new GPSTracker(this);
		if(gps_tracker.isGetLocation()){
			double latitude = 37.7749295;//gps_tracker.getLatitude(); // get latitude
			double longitude = -122.4194155;//gps_tracker.getLongitude(); // get longitude

			this._GPS = new LatLng(latitude, longitude);
		}
		else{
			gps_tracker.alteringSettings();
		}
	}
	/**
	 * Method to parse XML response from Yahoo URL and add markers of restaurants on the map
	 */
	private synchronized void xmlParsing(String XMLResponse){		
		try {
			URLConnection conn = new URL(XMLResponse).openConnection();
			conn.setConnectTimeout(50000);
			conn.setReadTimeout(50000);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			ItemHandler itemHandler = new ItemHandler();
			xr.setContentHandler(itemHandler);
			xr.parse(new InputSource(conn.getInputStream()));
						
			_itemAdapter.arr = itemHandler.getList();
			
			for(int index=0; index < _itemAdapter.arr.size(); index++){				
				
				Marker temp_marker = mMap.addMarker(new MarkerOptions()
                					.position(new LatLng(Double.parseDouble(_itemAdapter.arr.get(index).getLat()),
                							Double.parseDouble(_itemAdapter.arr.get(index).getLon())))
                					.title(_itemAdapter.arr.get(index).getName())
                	                .icon(BitmapDescriptorFactory.fromResource(numMarkerIcon[index])));
				
			 	this._markList.add(temp_marker);			
			}
			
			_itemAdapter.notifyDataSetChanged(); 
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Method to process the sip message to get Yahoo API
	 * @param message the SIP message
	 */
	private void parserMessage(String message){

	 	if(mMap != null)
	 		mMap.clear();	 	
	 	
	 	this._markList.clear();

		Marker own_location = mMap.addMarker(new MarkerOptions()
		        .position(this._GPS)
		        .title("Your Location")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)));

	 	this._markList.add(own_location);
		
		String XMLResponse = this.YAHOOAPI;
		_reIdList = new JSONParser().parseJSON(message);
		int num_res = 0;
		if(_reIdList.size() <= 11 && _reIdList.size() > 1){
			if(_reIdList.size() == 2)
				this.finalId = _reIdList.get(0);
			
			num_res = _reIdList.size();
		}else if(_reIdList.size() > 11){
			num_res = 11;
		}	
		
		for(int i = 0; i < num_res-1; i++){
			if(i == 0){
				XMLResponse += _reIdList.get(i);					
			}
			else{
				XMLResponse += "%20" + _reIdList.get(i);
			}
		}
		
		String uri = XMLResponse;
		xmlParsing(uri);	
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for(Marker m : this._markList) {
		    builder.include(m.getPosition());
		}
		LatLngBounds bounds = builder.build();
		
		CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(bounds.getCenter(),11);
		this.mMap.moveCamera(cu);
	}
/********************************** Not Use In This APP******************************************/
	public void onNewSubscriptionRequestReceived(LinphoneFriend friend,	String sipUri) {
	}
	
	public synchronized void prepareContactsInBackground() {		
	}
	
	@SuppressWarnings("unused")
	private void changeFragmentForTablets(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
	}

	private void initInCallMenuLayout(boolean callTransfer) {
	}

	public void resetClassicMenuLayoutAndGoBackToCallIfStillRunning() {
	}

	public void getCurrentFragment() {
	}

	public ChatStorage getChatStorage() {
		return ChatStorage.getInstance();
	}

	private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras) {
	}

	public void updateMissedChatCount() {
	}

	public void displayChat(String sipUri) {
	}

	private void changeCurrentFragment(FragmentsAvailable newFragmentType, Bundle extras, boolean withoutAnimation) {
	}
	
	private void changeFragment(Fragment newFragment, FragmentsAvailable newFragmentType, boolean withoutAnimation) {
	}

	@SuppressWarnings("incomplete-switch")
	public void selectMenu(FragmentsAvailable menuToSelect) {
	}

	public void updateDialerFragment(DialerFragment fragment) {		
	}	

	public void updateChatFragment(ChatFragment fragment) {		
	}

	public void updateChatListFragment(ChatListFragment fragment) {
	}

	public void hideMenu(boolean hide) {
	}

	public void displayAbout() {
		//changeCurrentFragment(FragmentsAvailable.ABOUT, null);
	}

	public void applyConfigChangesIfNeeded() {
	}
}

