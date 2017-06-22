package com.svi.payroll.reports.util;



import java.io.InputStream;

import javax.money.MonetaryAmount;

import com.svi.sss.module.SSS;




/***
 * List of government deductions
 * SSS, Philhealth 
 * 
 ***/
public class GovernmentDeductionsUtilitiy {
 private static SSS sss = new SSS();
	
	/**Should be accessed First**/
	public static void setSSS( String sssXmlPath){		
		sss.setContributionTable(sssXmlPath);
	}

	
	/**
	 * Compute SSS contribution from a monthlySalary
	 * Retrieves data from the SSS Employee table 
	 * 
	 **/
	public static MonetaryAmount computeSSSContribution(MonetaryAmount monthlySalary) {

		return sss.computeContribution(monthlySalary);
		
	}

	
	/**
	 * Compute SSS employer share from a monthlySalary
	 * Retrieves data from the SSS Employee table 
	 * 
	 **/
	public static MonetaryAmount computeSSSEmployerShare(MonetaryAmount monthlySalary) {

		return sss.computeEmployerShare(monthlySalary);
		
	}

	/**
	 * Compute SSS employer compensation from a monthlySalary
	 * Retrieves data from the SSS Employee table 
	 * 
	 **/
	public static MonetaryAmount computeSSSEmployerCompensation(MonetaryAmount monthlySalary) {

		return sss.computeEmployerCompensation(monthlySalary);
		
		
	}
	
}
