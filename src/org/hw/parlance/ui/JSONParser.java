package org.hw.parlance.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONParser {
	private static JSONParser instance;
	private InputStream input_stream = null;
	private JSONObject jObj = null;
	private ArrayList<String> _idList;
	
	public JSONParser(){
		_idList = new ArrayList<String>();
		instance = this;
	}
	
	public JSONParser getinstance(){
		return instance;
	}
	
	public ArrayList<String> parseJSON(String message){
		String json_str = message;//"{\"acceptedSlots\":[{\"name\":\"food\",\"value\":\"chinese\",\"prob\":\"1\"}],\"matchingVenues\":[\"21346006\",\"21386565\",\"21374762\",\"21374200\",\"21353555\",\"21379927\",\"21329695\",\"21368884\",\"21380317\",\"21374629\",\"21354697\",\"21358504\",\"21343242\",\"21384542\",\"32630700\",\"21357809\",\"21349123\",\"62302340\",\"21374560\",\"21359591\",\"21380935\",\"21374237\",\"21344058\",\"\"]}";
		
		String result = json_str.substring(json_str.indexOf("matchingVenues"));
		result = result.replace("matchingVenues\" : [\"", "");
		String[] s = result.split("\" ,\"");
		for(int i = 0; i < s.length; i++){
			if (isInteger(s[i])){
				_idList.add(s[i]);
		    }			
		}	
		return _idList;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}	

}
