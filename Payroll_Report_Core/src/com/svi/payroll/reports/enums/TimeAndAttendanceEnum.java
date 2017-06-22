package com.svi.payroll.reports.enums;

/****
 * The configuration that is expected from timesheet
 * **/
public enum TimeAndAttendanceEnum {

	WORKER_ID("WORKER ID"),
	WORKERS("WORKERS"),
	COMISSARY("Commissary"),
	ALLOTMENT("Allotment"),
	ADVANCES("Advances"),
	RMA("RMA"),
	SL_WITH_PAY_DAYS("SL w/ pay(days)"),
	SL_WITHOUT_PAY_DAYS("SL w/o pay(days)"),
	ABSENT_DAYS("Absent(days)"),
	LATE_MINS("Late (hrs)"),
	REMARKS("Remarks"),
	REGULAR_HOURS("Regular hours"),
	
	// OT_CODES
	
	RD("RD"),		
	SD("SD"),
	SDRD("SDRD"),
	RH("RH"),
	RHRD("RHRD"),
	DH("DH"),
	DHRD("DHRD"),

	NS("NS"), // crucial, used in determining a map value as night differential or not in reports,  keys containing "NS" is automatically a night shift differential
	RD_NS("RD-NS"),
	SD_NS("SD-NS"),
	SDRD_NS("SDRD-NS"),
	RH_NS("RH-NS"),
	RHRD_NS("RHRD-NS"),
	DH_NS("DH-NS"),
	DHRD_NS("DHRD-NS"),

	OT("OT"), // crucial, used in determining a map value as overtime or not in reports, keys containing "OT" is automatically an overtime
	RD_OT("RD-OT"),
	SD_OT("SD-OT"),
	SDRD_OT("SDRD-OT"),
	RH_OT("RH-OT"),
	RHRD_OT("RHRD-OT"),
	DH_OT("DH-OT"),
	DHRD_OT("DHRD-OT"),

	OTNS("OTNS"),
	RD_OTNS("RD-OTNS"),
	SD_OTNS("SD-OTNS"),
	SDRD_OTNS("SDRD-OTNS"),
	RH_OTNS("RH-OTNS"),
	RHRD_OTNS("RHRD-OTNS"),
	DH_OTNS("DH-OTNS"),
	DHRD_OTNS("DHRD-OTNS"),

	/***Leaves***/
	
	SL("SL"),
	VL("VL"),
	EL("EL"),
	LWOP("LWOP"),
	ML("ML"),
	PL("PL"),
	PLSP("PLSP"),
	LVVAW("LVVAW"),
	SLW("SLW"),
	
	
	/***SVI CODES****/
	
	SVI_ND("SVI_ND"),
	SVI_SH("SVI_SH"),
	SVI_RH("SVI_RH"),
	SVI_OT("SVI_OT"),
	SVI_OT_ND("SVI_OT-ND"),
	SVI_SAT("SVI_SAT"),
	SVI_SUN("SVI_SUN"),
	;
	
	String var;
	TimeAndAttendanceEnum(String var){
		this.var = var;
	}
	
	public static TimeAndAttendanceEnum value(String value){
		  for (TimeAndAttendanceEnum category :TimeAndAttendanceEnum.values()) {
		        if (category.var.equals(value)) {
		            return category;
		        }
		    }   
		  return null;
	}

	public String getVar() {
		return var;
	}
	
	public void setVar(String var) {
		this.var = var;
	}
	

}
