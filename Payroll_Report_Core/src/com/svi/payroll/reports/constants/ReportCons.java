package com.svi.payroll.reports.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class ReportCons {
	public final static String ALL_COMPANY = "all"; 
	public final static String MONTHLY_REPORT = "monthly"; // report frequency to be generated
	public final static String ANNUAL_REPORT = "annual"; // report frequency to be generated
		
	public final static String SEX_MALE_KEY = "MALE"; 
	public final static String SEX_FEMALE_KEY = "FEMALE"; 
	public static final String SSS_CATEGORY_BUSINESS = "business";
	public static final String SSS_CATEGORY_HOUSEHOLD = "household";
	public static final String CATEGORY_PRIVATE = "PRIVATE";
	public static final String CATEGORY_GOVERNMENT = "GOVERNMENT";
	public static final String ZIPCODE = "zipCode";	
	public final static String LOAN_KEY_SEPARATOR = "_";
	public final static String PAGIBIG_LOAN_DEDUCTION_KEY = "pagibig";  //should be same with hfiles
	
	public final static String GENERATE_REPORT_YES = "Y"; 
	public final static String GENERATE_REPORT_SSS3_MONTHLY = "M"; // monthly generation
	
	// tax codes, for tax exemption computation
	public final static String TAX_CODE_S = "S"; // single
	public final static String TAX_CODE_S1 = "S1"; // single, with 1 qualified dependent
	public final static String TAX_CODE_S2 = "S2"; // single, with 2 qualified dependent
	public final static String TAX_CODE_S3 = "S3"; // single, with 3 qualified dependent
	public final static String TAX_CODE_S4 = "S4"; // single, with 4 qualified dependent
	public final static String TAX_CODE_ME = "ME"; // married employee
	public final static String TAX_CODE_ME1 = "ME1"; // married employee, with 1 qualified dependent
	public final static String TAX_CODE_ME2 = "ME2"; // married employee, with 2 qualified dependent
	public final static String TAX_CODE_ME3 = "ME3"; // married employee, with 3 qualified dependent
	public final static String TAX_CODE_ME4 = "ME4"; // married employee, with 4 qualified dependent
	public final static String TAX_CODE_Z = "Z"; // employees with multiple employers for their 2nd, 3rd, or non-primary employers. This also applies to employees who failed to file application for registration.
	public final static String TAX_CODE_SMHF = "SMHF"; // ??
	
	public static final Map<String, String> REGION_NUMBER;  // map of province and region number

	
	
	// Casssy DB
	
	/**
	   The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, 
	   and so on. Thus, the caller should be prevented from constructing objects of 
	   this class, by declaring this private constructor. 
	  */
	  private ReportCons(){
	    //this prevents even the native class from 
	    //calling this ctor as well :
	    throw new AssertionError();
	  }
	  
	  static {
		    Map<String, String> map = new HashMap<String, String>();		   
		    map.put("ABRA","CAR");
		    map.put("AGUSAN DEL NORTE","XIII");
		    map.put("AGUSAN DEL SUR","XIII");
		    map.put("AKLAN","VI");
		    map.put("ALBAY","V");
		    map.put("ANTIQUE","VI");
		    map.put("APAYAO","CAR");
		    map.put("AURORA","III");
		    map.put("BASILAN","ARMM");
		    map.put("BATAAN","III");
		    map.put("BATANES","II");
		    map.put("BATANGAS","IV-A");
		    map.put("BENGUET","CAR");
		    map.put("BILIRAN","VIII");
		    map.put("BOHOL","VII");
		    map.put("BUKIDNON","X");
		    map.put("BULACAN","III");
		    map.put("CAGAYAN","II");
		    map.put("CAMARINES NORTE","V");
		    map.put("CAMARINES SUR","V");
		    map.put("CAMIGUIN","X");
		    map.put("CAPIZ","VI");
		    map.put("CATANDUANES","V");
		    map.put("CAVITE","IV-A");
		    map.put("CEBU","VII");
		    map.put("CITY OF ISABELA","IX");
		    map.put("CITY OF MANILA","NCR");
		    map.put("COMPOSTELA VALLEY","XI");
		    map.put("COTABATO (NORTH COTABATO)","XII");
		    map.put("COTABATO CITY","XII");
		    map.put("DAVAO DEL NORTE","XI");
		    map.put("DAVAO DEL SUR","XI");
		    map.put("DAVAO OCCIDENTAL","XI");
		    map.put("DAVAO ORIENTAL","XI");
		    map.put("DINAGAT ISLANDS","XIII");
		    map.put("EASTERN SAMAR","VIII");
		    map.put("GUIMARAS","VI");
		    map.put("IFUGAO","CAR");
		    map.put("ILOCOS NORTE","I");
		    map.put("ILOCOS SUR","I");
		    map.put("ILOILO","VI");
		    map.put("ISABELA","II");
		    map.put("KALINGA","CAR");
		    map.put("LA UNION","I");
		    map.put("LAGUNA","IV-A");
		    map.put("LANAO DEL NORTE","X");
		    map.put("LANAO DEL SUR","ARMM");
		    map.put("LEYTE","VIII");
		    map.put("MAGUINDANAO","ARMM");
		    map.put("MARINDUQUE","IV-B");
		    map.put("MASBATE","V");
		    map.put("MISAMIS OCCIDENTAL","X");
		    map.put("MISAMIS ORIENTAL","X");
		    map.put("MOUNTAIN PROVINCE","CAR");
		    map.put("NCR, CITY OF MANILA, FIRST DISTRICT","NCR");
		    map.put("NCR, FOURTH DISTRICT","NCR");
		    map.put("NCR, SECOND DISTRICT","NCR");
		    map.put("NCR, THIRD DISTRICT","NCR");
		    map.put("NEGROS OCCIDENTAL","NIR");
		    map.put("NEGROS ORIENTAL","NIR");
		    map.put("NORTHERN SAMAR","VIII");
		    map.put("NUEVA ECIJA","III");
		    map.put("NUEVA VIZCAYA","II");
		    map.put("OCCIDENTAL MINDORO","IV-B");
		    map.put("ORIENTAL MINDORO","IV-B");
		    map.put("PALAWAN","IV-B");
		    map.put("PAMPANGA","III");
		    map.put("PANGASINAN","I");
		    map.put("QUEZON","IV-A");
		    map.put("QUIRINO","II");
		    map.put("RIZAL","IV-A");
		    map.put("ROMBLON","IV-B");
		    map.put("SAMAR (WESTERN SAMAR)","VIII");
		    map.put("SARANGANI","XII");
		    map.put("SIQUIJOR","VII");
		    map.put("SORSOGON","V");
		    map.put("SOUTH COTABATO","XII");
		    map.put("SOUTHERN LEYTE","VIII");
		    map.put("SULTAN KUDARAT","XII");
		    map.put("SULU","ARMM");
		    map.put("SURIGAO DEL NORTE","XIII");
		    map.put("SURIGAO DEL SUR","XIII");
		    map.put("TARLAC","III");
		    map.put("TAWI-TAWI","ARMM");
		    map.put("ZAMBALES","III");
		    map.put("ZAMBOANGA DEL NORTE","IX");
		    map.put("ZAMBOANGA DEL SUR","IX");
		    map.put("ZAMBOANGA SIBUGAY","IX");
		  
		    REGION_NUMBER = Collections.unmodifiableMap(map);
	  }
}
