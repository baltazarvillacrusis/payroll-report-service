package com.svi.payroll.reports.enums;

/*****
 * Config Enum for the Deminimis Code
 * 
 ****/
public enum DeminimisEnum {

	VACATION_LEAVE("VACATION_LEAVE"),
	MEDICAL_CASH_ALLOWANCE_TO_DEPENDENDETS("MEDICAL_CASH_ALLOWANCE"),
	RICE_SUBSIDY("RICE_SUBSIDY"),
	CLOTHING_ALLOWANCE("CLONING_ALLOWANCE"),
	MEDICAL_BENEFITS("MEDICAL_BENEFITS"),
	LAUNDRY_ALLOWANCE("LAUNDRY_ALLOWANCE"),
	TANGIBLE_EMPLOYEE_ACHIEVEMENT("TANGIBLE_EMPLOYEE_ACHIEVEMENT"),
	OTHERS("OTHERS"),
	OVERTIME_MEAL_ALLOWANCE("OVERTIME_MEAL_ALLOWANCE"),
	;
	
	
	String var;
	
	DeminimisEnum(String var){
		this.var = var;
	}
	
	public String getVal(){
		return this.var;
	}
	
	public static DeminimisEnum value(String value){
		  for (DeminimisEnum category :DeminimisEnum.values()) {
		        if (category.var.equals(value)) {
		            return category;
		        }
		    }   
		  return null;
	}

	
}
