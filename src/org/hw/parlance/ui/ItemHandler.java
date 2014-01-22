package org.hw.parlance.ui;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ItemHandler extends DefaultHandler {
	private String tempVal;
	private NewRestaurantItem _nItem;
	private ArrayList<NewRestaurantItem> restuarant_List;
	
	public ItemHandler(){
		restuarant_List = new ArrayList<NewRestaurantItem>();
	}
	
	public ArrayList<NewRestaurantItem> getList(){
		return restuarant_List;
	}
	
	public void startElement(String uri, String localName, String qName, Attributes attrs)
		throws SAXException{
		tempVal = "";
		
		if(qName.equalsIgnoreCase("hit")){
			_nItem = new NewRestaurantItem();
			_nItem.setRank(attrs.getValue("rank"));
			_nItem.setID(attrs.getValue("id"));
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException{
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{		
		if(qName.equalsIgnoreCase("hit")){
			restuarant_List.add(_nItem);
		} else if (qName.equalsIgnoreCase("name")){
			_nItem.setName(tempVal);
		}  else if (qName.equalsIgnoreCase("town")){
			_nItem.setTown(tempVal);
		} else if (qName.equalsIgnoreCase("district")){
			_nItem.setDistrict(tempVal);
		} else if (qName.equalsIgnoreCase("statecode")){
			_nItem.setStatecode(tempVal);
		} else if (qName.equalsIgnoreCase("zip")){
			_nItem.setZip(tempVal);
		} else if (qName.equalsIgnoreCase("country")){
			_nItem.setCountry(tempVal);
		} else if (qName.equalsIgnoreCase("street")){
			_nItem.setStreet(tempVal);
		} else if (qName.equalsIgnoreCase("lon")){
			_nItem.setLon(tempVal);
		} else if (qName.equalsIgnoreCase("lat")){
			_nItem.setLat(tempVal);
		}else if (qName.equalsIgnoreCase("phone")){
			_nItem.setTele(tempVal);
		}	
	}
}
