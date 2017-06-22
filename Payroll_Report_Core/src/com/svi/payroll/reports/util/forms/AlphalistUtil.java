package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.sf.jasperreports.engine.JRException;

import com.svi.payroll.report.forms.AlphalistForms;
import com.svi.payroll.report.objects.Alphalist;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class AlphalistUtil {	
	
	/**
	 * Creates alphalist for each company
	 * @param companyList List of company objects.
	 */
	public static boolean createForm(CompanyList companyList, String companyIdToBeGenerated){	
		boolean isComplete = true;
		// generate report for each company
		for(Company company : companyList){	
			
			String runDate = companyList.getRunDate();			
			String companyId = company.getCompanyId();			
			if(!companyIdToBeGenerated.equalsIgnoreCase(companyId) & !companyIdToBeGenerated.equalsIgnoreCase(ReportCons.ALL_COMPANY)){
				continue;
			}
			System.out.println("Creating alphalist report for company with ID "+companyId+".");				
			
		
			ArrayList <Alphalist> employeeList1 = new ArrayList <Alphalist> ();	 // for schedule 5 alphalist, TODO later, cannot be produced by the app
			ArrayList <Alphalist> employeeList2 = new ArrayList <Alphalist> ();	// for schedule 6 alphalist, 
			ArrayList <Alphalist> employeeList3 = new ArrayList <Alphalist> ();	// for schedule 7.1 alphalist
			ArrayList <Alphalist> employeeList4 = new ArrayList <Alphalist> ();	// for schedule 7.2 alphalis
			ArrayList <Alphalist> employeeList5 = new ArrayList <Alphalist> ();	// for schedule 7.3 alphalist
			ArrayList <Alphalist> employeeList6 = new ArrayList <Alphalist> ();	// for schedule 7.4 alphalist, TODO later
			ArrayList <Alphalist> employeeList7 = new ArrayList <Alphalist> ();	// for schedule 7.5 alphalist
			
			// populating list of tables for each schedule (Note: alphalist is mutually exclusive)
			for(Alphalist alphalist : company.getAlphaList()){			
				
				if(alphalist.isTerminatedBeforeEndOfYear()){
					// for schedule 7.1 alphalist, employees terminated before end of Dec 31
					employeeList3.add(alphalist);
				}				
				else if(!alphalist.isRankAndFile() & alphalist.getAmountOfFringeBenefit() >0){
					// for schedule 6 alphalist, NOT rank and file employees with fringe benefits
					employeeList2.add(alphalist);					
				}
				else if(alphalist.isMinimumWageEarner() & alphalist.getTaxDue() > 0 & !alphalist.isWithPreviousEmployer()){
					// for schedule 7.2 alphalist, for minimum wage earners but with taxable income
					employeeList4.add(alphalist);
				}
				else if(alphalist.isMinimumWageEarner()){
					// for schedule 7.5 alphalist, for minimum wage earners
					employeeList7.add(alphalist);
				}
				else if(alphalist.isWithPreviousEmployer()){
					// for schedule 7.3 alphalist, with previous employer within the year
					employeeList6.add(alphalist);
				}				
				else{
					// for schedule 7.4 alphalist, with NO previous employer within the year					
					employeeList5.add(alphalist);
				}
			}
			
			// for better display of report
			// table still appears with no entries
			addDefaultEntry(employeeList1);
			addDefaultEntry(employeeList2);
			addDefaultEntry(employeeList3);
			addDefaultEntry(employeeList4);
			addDefaultEntry(employeeList5);
			addDefaultEntry(employeeList6);
			addDefaultEntry(employeeList7);
			
			// creating new form
			AlphalistForms form = new AlphalistForms(employeeList1,employeeList2,employeeList3,employeeList4,employeeList5,employeeList6,employeeList7);				
			
			// setting directory path
			String directoryName = ReportUtil.getOutputDirectory(runDate, companyId,"Alphalist", "pdf","annual");			
			File file = new File(directoryName);
			
			// creating the form as pdf			
			try {				
				form.createFormStream(new FileOutputStream(file));		
			} catch (JRException | IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}						
		}
		return isComplete;			
	}	
	
	private static void addDefaultEntry(ArrayList <Alphalist> employeeList){
		Alphalist alphalist = new Alphalist();
		if(employeeList.size() == 0){
			employeeList.add(alphalist);
		}
	}
	

}
