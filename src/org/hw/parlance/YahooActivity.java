package org.hw.parlance;
 
import org.linphone.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebView;
/**
 * Project Name: Parlance App
 * @Author: Yanchao Yu
 * @Version: 1.1
 * Class: YahooActivity.class
 */
public class YahooActivity extends Activity{

	private WebView yahoo_WebView = null;
	
	private static final int CALL_BACK = 1; // menu item refer. number
	
	private String yahoo_URL = "http://local.yahoo.com/details?id="; // URL for Yahoo search
		
	private String search_id = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		
		boolean isID= getIntent().getExtras().getBoolean("isID");
		
		if(isID){
			search_id = getIntent().getExtras().getString("sID");
			yahoo_WebView = (WebView)findViewById(R.id.yahoo_webview);
			String url = yahoo_URL + search_id;
			yahoo_WebView.loadUrl(url);					
		}
        

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, CALL_BACK, 0, "Back to call");
        return true;
	}

	/**
	 * Method to activate menu items 
	 */
	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CALL_BACK:
                showDialog(CALL_BACK);
                break;
        }
        return true;
    }
 
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case CALL_BACK:
                return new AlertDialog.Builder(this)
                		.setMessage("Do you want to make new call?")
                		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                			 public void onClick(DialogInterface dialog, int whichButton) {
                				 startActivity(new Intent(YahooActivity.this,ParlanceActivity.class));
                             }
                         })
                         .setNegativeButton(
                                 android.R.string.cancel, new DialogInterface.OnClickListener() {
                                     public void onClick(DialogInterface dialog, int whichButton) {
                                         // Noop.
                                     }
                         })
                         .create();
        }
        return null;
    }
}

