package org.hw.parlance.ui;
/**
 * Project Name: Parlance
 * @Author: Yanchao Yu
 * @Version: 1.8
 * Class: NewRestaurantItem.class
 */
public class NewRestaurantItem {
	private String _rRank;
	private String _rID;
	private String _rName;
	private String _rTele;
	private String _rAddress;
	private String _rLat;
	private String _rLon;
	private String _rTown;
	private String _rStatecode;
	private String _rDistrict;
	private String _rZip;
	private String _rCountry;
	private String _rStreet;
	/**
	 * Method to initial new restaurant item with name,. telephone number and address
	 * @param _rName name of restaurant
	 * @param _rTele telephone number of restaurant
	 * @param _rAddress address of restaurant
	 */
	public NewRestaurantItem(String _rID, String _rRank, String _rName,String _rTele,String _rAddress, String _rLat, String _rLon){
		this._rName = _rName;
		this._rTele = _rTele;
		this._rAddress = _rAddress;
		this._rLat = _rLat;
		this._rLon = _rLon;
		this._rRank = _rRank;
		this._rID = _rID;
	}
	
	public NewRestaurantItem(){
	}
	
	public String getRank(){
		return this._rRank;
	}
	public String getID(){
		return this._rID;
	}
	public String getName(){
		return this._rName;
	}
	public String getTele(){
		return this._rTele;
	}
	public String getAddress(){
		this._rAddress = _rStreet +", "+ _rDistrict + ", " + _rTown + ", " + _rCountry + ", " + _rStatecode +" " + _rZip;
		return _rAddress;
	}	
	public String getLon(){
		return this._rLon;
	}
	public String getLat(){
		return this._rLat;
	}
	public String getCountry(){
		return this._rCountry;
	}	
	public String getZip(){
		return this._rZip;
	}	
	public String getDistrict(){
		return this._rDistrict;
	}	
	public String getStatecode(){
		return this._rStatecode;
	}	
	public String getTown(){
		return this._rTown;
	}	
	public String getStreet(){
		return this._rStreet;
	}

	public void setID(String _rID){
		this._rID = _rID ;
	}
	public void setName(String _rName){
		this._rName = _rName ;
	}
	public void setRank(String _rRank){
		this._rRank = _rRank ;
	}
	public void setTele(String _rTele){
		this._rTele = _rTele;
	}
	public void setLon(String _rLat){
		this._rLon = _rLat;
	}
	public void setLat(String _rLon){
		this._rLat = _rLon ;
	}
	public void setCountry(String _rCountry){
		this._rCountry = _rCountry;
	}	
	public void setZip(String _rZip){
		this._rZip = _rZip;
	}	
	public void setDistrict(String _rDistrict){
		this._rDistrict = _rDistrict;
	}	
	public void setStatecode(String _rStatecode){
		this._rStatecode = _rStatecode;
	}	
	public void setTown(String _rTown){
		this._rTown = _rTown;
	}	
	public void setStreet(String _rStreet){
		this._rStreet = _rStreet;
	}
}