package com.svi.payroll.reports.enums;

/***
 *	 Currently not in use 
 * 
 ***/
public enum GddsEnum {

	COMPANY("Company"),
	GROUP("Group"),
	DEPARTMENT("Department"),
	SECTION("Section"),
	
	;
	
	
	String var;
	GddsEnum(String var){
		this.var = var;
	}
	
	public String getVal(){
		return this.var;
	}
	
	
	
}
