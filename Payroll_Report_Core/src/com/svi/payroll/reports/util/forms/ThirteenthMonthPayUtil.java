package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

import com.svi.payroll.report.forms.ThirteenthMonthPayForm;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class ThirteenthMonthPayUtil {
	/**
	 * Creates thirteenth month pay report for each company
	 * @param companyList List of company objects.
	 */
	public static boolean createForm(CompanyList companyList, String companyIdToBeGenerated){	
		
		boolean isComplete = true;
		// generate report for each company
		for(Company company : companyList){		
			if(company.getEmployeeList().size() == 0){
				continue;
			}
			
			String companyId = company.getCompanyId();			
			if(!companyIdToBeGenerated.equalsIgnoreCase(companyId) & !companyIdToBeGenerated.equalsIgnoreCase(ReportCons.ALL_COMPANY)){
				continue;
			}
			System.out.println("Creating thirteenth month pay report for company with ID "+company.getCompanyId()+".");
						
				
			// making employee bean			
			EmployeeBean employeeBean =  new EmployeeBean();	
			employeeBean.addAll(company.getEmployeeList());
			
			// creating new form
			String year = ReportUtil.getFormattedDate(companyList.getRunDate(), "yyyy");
			ThirteenthMonthPayForm form = new ThirteenthMonthPayForm(employeeBean);	
			form.setYear(year);
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(year, company.getCompanyId(),"Thirteenth Month Pay", "pdf","annual");			
			
			File file = new File(directoryName);
			
			// creating the form 	
			try {							
				form.createFormStream(new FileOutputStream(file));	
			} catch (JRException | IOException e) {		
				isComplete = false;
				e.printStackTrace();
			} 						
			
		}
		
		return isComplete;		
	}

	
	
	
	
	
	
	

}
