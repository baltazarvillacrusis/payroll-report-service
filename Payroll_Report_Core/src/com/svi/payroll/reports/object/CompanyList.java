package com.svi.payroll.reports.object;

import java.util.ArrayList;

public class CompanyList extends ArrayList<Company> {

	private static final long serialVersionUID = 1L;
	private String runDate; // used in accessing payroll_compute and employee_details details table, equal to cutoff_date
	
	public CompanyList(){
		
	}
	public CompanyList(String runDate){
		this.runDate = runDate;
	}
	
	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}	

}
