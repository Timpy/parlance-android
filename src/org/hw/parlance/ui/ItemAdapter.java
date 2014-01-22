package org.hw.parlance.ui;

import java.util.ArrayList;

import org.linphone.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Project Name: Parlance
 * @Author: Yanchao Yu
 * @Version: 1.8
 */
public class ItemAdapter extends BaseAdapter{
	private Context context;  
	private LayoutInflater inflater;  
	public ArrayList<NewRestaurantItem> arr;

	private String FINALID = null;

	/**
	 * Constructor to initial object of ItemAdapter with context
	 * @param context
	 */
	public ItemAdapter(Context context){
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		arr = new ArrayList<NewRestaurantItem>();
	}
	/**
	 * Method to get size of array list 
	 */
	@Override
	public int getCount() {
		return arr.size();
	}
	/**
	 * Method to get context of specific item
	 */
	@Override
	public Object getItem(int arg0) {
		return arr.get(arg0);
	}
	/**
	 * Method to get specific item's ID
	 */
	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	/**
	 * Method to prepare for list view item
	 */
	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		ViewHolder view_holder;
		if(arg1 == null){
			arg1 = inflater.inflate(R.layout.list_item_layout, null);
			view_holder = new ViewHolder();
			view_holder._rNameView = (TextView) arg1.findViewById(R.id.tv_rname);
			view_holder._rTeleView = (TextView) arg1.findViewById(R.id.tv_rtele);
			view_holder._rAddressView = (TextView) arg1.findViewById(R.id.tv_raddress);
			view_holder._rRankView = (TextView) arg1.findViewById(R.id.rank_textview);
			arg1.setTag(view_holder);
			
		} else {
			view_holder = (ViewHolder) arg1.getTag();
        }
		view_holder._rNameView.setText(arr.get(arg0).getName());
		view_holder._rTeleView.setText(arr.get(arg0).getTele());
		view_holder._rAddressView.setText(arr.get(arg0).getAddress());
		view_holder._rRankView.setText(arr.get(arg0).getRank());
				
		return arg1;
	}
    static class ViewHolder {
        TextView _rNameView;
        TextView _rTeleView;
        TextView _rAddressView;
        TextView _rRankView;
    }
}