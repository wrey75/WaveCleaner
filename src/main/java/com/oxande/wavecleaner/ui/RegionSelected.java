package com.oxande.wavecleaner.ui;

import java.awt.Color;

/** 
 * A selected region.
 * 
 * TODO: enhance this class to a "first-class" driver with
 * the capability to draw itself rather than relying on
 * the component which use it. TO BE CHECKED CAREFULLY DUE
 * TO POSSIBLE COUPLING!
 * 
 * TODO: add height expressed in floating values.
 * 
 * @author wrey75
 *
 */
public class RegionSelected {
	int begin;
	int end;
	Color color = Color.LIGHT_GRAY;
	boolean active = true;
	String name;
	
	public RegionSelected(String name, int pos) {
		this.name = name;
		this.begin = pos;
		this.end = pos;
	}
	public RegionSelected(String name){
		this( name, -1);
	}
	
	public boolean isActive(){
		return active;
	}
}
