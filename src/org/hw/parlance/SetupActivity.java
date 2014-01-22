package org.hw.parlance;
/*
SetupActivity.java
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
import java.net.URL;

import org.linphone.LinphoneManager;
import org.linphone.LinphoneManager.EcCalibrationListener;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.LinphoneSimpleListener.LinphoneOnRegistrationStateChangedListener;
import org.linphone.R;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.mediastream.Log;

import de.timroes.axmlrpc.XMLRPCCallback;
import de.timroes.axmlrpc.XMLRPCClient;
import de.timroes.axmlrpc.XMLRPCException;
import de.timroes.axmlrpc.XMLRPCServerException;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
/**
 * @author Sylvain Berfini
 */
public class SetupActivity extends Activity implements OnClickListener, EcCalibrationListener {
	private static SetupActivity instance;
	private ImageButton btn_setup;
	private LinphonePreferences mPrefs;
	private boolean accountCreated = false;
	private Handler mHandler = new Handler();
	private boolean mSendEcCalibrationResult = false;
	
	private static final String _DOMAIN = "137.195.27.135";
	private static final String _USERNAME = "1007";
	private static final String _PASWD = "1234";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getResources().getBoolean(R.bool.isTablet) && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }		
		setContentView(R.layout.setup);
        mPrefs = LinphonePreferences.instance();

		btn_setup = (ImageButton) findViewById(R.id.btn_setup);
		btn_setup.setOnClickListener(this);
		
        instance = this;
	};
	
	public static SetupActivity instance() {
		return instance;
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_setup) {		
			this.genericLogIn(_USERNAME, _PASWD, _DOMAIN);
		}
	}
	
	private void launchEchoCancellerCalibration(boolean sendEcCalibrationResult) {
		if (LinphoneManager.getLc().needsEchoCalibration() && mPrefs.isFirstLaunch()) {
			this.enableEcCalibrationResultSending(sendEcCalibrationResult);
		} else {
			success();
		}		
	}

	private void logIn(String username, String password, String domain, boolean sendEcCalibrationResult) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && getCurrentFocus() != null) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}

        saveCreatedAccount(username, password, domain);

		if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
			this.launchEchoCancellerCalibration(sendEcCalibrationResult);
		}
	}
	
	
	private LinphoneOnRegistrationStateChangedListener registrationListener = new LinphoneOnRegistrationStateChangedListener() {
		public void onRegistrationStateChanged(RegistrationState state) {
			if (state == RegistrationState.RegistrationOk) {
				LinphoneManager.removeListener(registrationListener);
				
				if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
					mHandler .post(new Runnable () {
						public void run() {
							launchEchoCancellerCalibration(true);
						}
					});
				}
			} else if (state == RegistrationState.RegistrationFailed) {
				LinphoneManager.removeListener(registrationListener);
				mHandler.post(new Runnable () {
					public void run() {
						Toast.makeText(SetupActivity.this, getString(R.string.first_launch_bad_login_password), Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	};
	public void checkAccount(String username, String password, String domain) {
		LinphoneManager.removeListener(registrationListener);
		LinphoneManager.addListener(registrationListener);
		
		saveCreatedAccount(username, password, domain);
	}

	public void linphoneLogIn(String username, String password, boolean validate) {
		if (validate) {
			checkAccount(username, password, getString(R.string.default_domain));
		} else {
			logIn(username, password, getString(R.string.default_domain), true);
		}
	}

	public void genericLogIn(String username, String password, String domain) {
		logIn(username, password, domain, false);
	}
	
	public void saveCreatedAccount(String username, String password, String domain) {
		if (accountCreated)
			return;
		
		boolean isMainAccountLinphoneDotOrg = domain.equals(getString(R.string.default_domain));
		boolean useLinphoneDotOrgCustomPorts = getResources().getBoolean(R.bool.use_linphone_server_ports);
		mPrefs.setNewAccountUsername(username);
		mPrefs.setNewAccountDomain(domain);
		mPrefs.setNewAccountPassword(password);
		
		if (isMainAccountLinphoneDotOrg && useLinphoneDotOrgCustomPorts) {
			if (getResources().getBoolean(R.bool.disable_all_security_features_for_markets)) {
				mPrefs.setNewAccountProxy(domain + ":5070");
				mPrefs.setTransport(getString(R.string.pref_transport_tcp_key));
			}
			else {
				mPrefs.setNewAccountProxy(domain + ":5080");
				mPrefs.setTransport(getString(R.string.pref_transport_tls_key));
			}
			
			mPrefs.setNewAccountExpires("604800");
			mPrefs.setNewAccountOutboundProxyEnabled(true);
			mPrefs.setStunServer(getString(R.string.default_stun));
			mPrefs.setIceEnabled(true);
			mPrefs.setPushNotificationEnabled(true);
		} else {
			String forcedProxy = getResources().getString(R.string.setup_forced_proxy);
			if (!TextUtils.isEmpty(forcedProxy)) {
				mPrefs.setNewAccountProxy(forcedProxy);
				mPrefs.setNewAccountOutboundProxyEnabled(true);
			}
		}
		
		if (getResources().getBoolean(R.bool.enable_push_id)) {
			String regId = mPrefs.getPushNotificationRegistrationID();
			String appId = getString(R.string.push_sender_id);
			if (regId != null && mPrefs.isPushNotificationEnabled()) {
				String contactInfos = "app-id=" + appId + ";pn-type=google;pn-tok=" + regId;
				mPrefs.setNewAccountContactParameters(contactInfos);
			}
		}
		
		try {
			mPrefs.saveNewAccount();
			accountCreated = true;
			success();
		} catch (LinphoneCoreException e) {
			e.printStackTrace();
		}
	}
	
	public void isAccountVerified() {
		Toast.makeText(this, getString(R.string.setup_account_validated), Toast.LENGTH_LONG).show();
		launchEchoCancellerCalibration(true);
	}

	public void isEchoCalibrationFinished() {
		success();
	}
	
	public void success() {
		mPrefs.firstLaunchSuccessful();
		setResult(Activity.RESULT_OK);
		finish();
	}

	@Override
	public void onEcCalibrationStatus(EcCalibratorStatus status, int delayMs) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SetupActivity.instance());
		SharedPreferences.Editor editor = prefs.edit();
		
		Context context = SetupActivity.instance() == null ? LinphoneService.instance().getApplicationContext() : SetupActivity.instance();
		
		if (status == EcCalibratorStatus.DoneNoEcho) {
			editor.putBoolean(context.getString(R.string.pref_echo_cancellation_key), false);
		} else if ((status == EcCalibratorStatus.Done) || (status == EcCalibratorStatus.Failed)) {
			editor.putBoolean(context.getString(R.string.pref_echo_cancellation_key), true);
		}
		editor.commit();
		if (mSendEcCalibrationResult) {
			sendEcCalibrationResult(status, delayMs);
		} else {
			SetupActivity.instance().isEchoCalibrationFinished();
		}
	}

	public void enableEcCalibrationResultSending(boolean enabled) {
		mSendEcCalibrationResult = enabled;
	}

	private void sendEcCalibrationResult(EcCalibratorStatus status, int delayMs) {
		try {
			XMLRPCClient client = new XMLRPCClient(new URL(getString(R.string.wizard_url)));

			XMLRPCCallback listener = new XMLRPCCallback() {
				Runnable runFinished = new Runnable() {
    				public void run() {
    					SetupActivity.instance().isEchoCalibrationFinished();
					}
	    		};

			    public void onResponse(long id, Object result) {
		    		mHandler.post(runFinished);
			    }

			    public void onError(long id, XMLRPCException error) {
			    	mHandler.post(runFinished);
			    }

			    public void onServerError(long id, XMLRPCServerException error) {
			    	mHandler.post(runFinished);
			    }
			};

			Log.i("Add echo canceller calibration result: manufacturer=" + Build.MANUFACTURER + " model=" + Build.MODEL + " status=" + status + " delay=" + delayMs + "ms");
		    client.callAsync(listener, "add_ec_calibration_result", Build.MANUFACTURER, Build.MODEL, status.toString(), delayMs);
		} 
		catch(Exception ex) {}
	}
}
