package com.svi.payroll.reports.constants;

public class MasterFileCons {
	
	// FOR EMPLOYEE AND EMPLOYER DETAILS
	public final static int EMPLOYEE_ID_COL_INDEX = 1; 
	public final static int EMPLOYEE_IS_MWE = 2; 
	public final static int TIN_COL_INDEX = 3; 
	public final static int NAME_COL_INDEX = 4; 
	public final static int ADDRESS_COL_INDEX =5; 
	public final static int ZIPCODE_COL_INDEX = 6; 
	public final static int TAX_WITHHELD_COL_INDEX = 7; 
	
	// FOR NON TAXABLES INDICES
	public final static int BASIC_SALARY_MWE_COL_INDEX = 8; 
	public final static int HOLIDAY_PAY_MWE_COL_INDEX = 9; 
	public final static int OVERTIME_PAY_MWE_COL_INDEX = 10; 
	public final static int NIGHT_DIFF_PAY_MWE_COL_INDEX = 11; 
	public final static int HAZARD_PAY_MWE_COL_INDEX = 12; 
	public final static int THIRTEENTH_MONTH_AND_OTHER_COL_INDEX = 13; 
	public final static int DEMINIMIS_BENEFITS_COL_INDEX = 14; 
	public final static int SSS_GSIS_AND_OTHERS_COL_INDEX = 15; 
	public final static int SALARIES_AND_OTHER_FORMS_COL_INDEX = 16; 
	
	// FOR TAXABLE INDICES
	public final static int BASIC_SALARY_TAXABLE_COL_INDEX = 17; 
	public final static int THIRTEENTH_MONTH_AND_OTHER_TAXABLE_COL_INDEX = 18; 
	public final static int SALARIES_AND_OTHER_FORMS_TAXABLE_COL_INDEX = 19; 
	
	
	
	/**
	   The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, 
	   and so on. Thus, the caller should be prevented from constructing objects of 
	   this class, by declaring this private constructor. 
	  */
	  private MasterFileCons(){
	    //this prevents even the native class from 
	    //calling this ctor as well :
	    throw new AssertionError();
	  }

}
