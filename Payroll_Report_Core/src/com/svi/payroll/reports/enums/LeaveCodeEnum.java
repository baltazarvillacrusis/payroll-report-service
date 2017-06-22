package com.svi.payroll.reports.enums;

/**
 * The leave code to be save to the list
 * ***/
public enum LeaveCodeEnum {

	SICK_LEAVE("SL"),

	VACATION_LEAVE("VL"),
	EMERGENCY_LEAVE("EL"),
	LWOP("LWOP"),
	MATERNITY_LEAVE("ML"),
	PATERNITY_LEAVE("PL"),
	PLSP("PLSP"),
	LVVAW("LVVAW"),
	SLW("SLW"),
	;
	
	String var;
	
	LeaveCodeEnum(String var){
		this.var = var;
	}
	
	public String getVal(){
		return this.var;
	}
	
}
