package com.svi.payroll.reports.enums;

/***
 * Not in use
 * ***/
public enum GovIdEnum {


	SSS("SSS"),
	TIN("TIN"),
	PHILHEALTH("PHILHEALTH"),
	OTHERS("OTHERS"),
	
	;
	
	
	String var;
	GovIdEnum(String var){
		this.var = var;
	}
	
	public String getVal(){
		return this.var;
	}
	
}
