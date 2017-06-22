package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

import com.svi.payroll.report.forms.HDMFmcrf;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class HDMFmcrfUtil {
	
	/**
	 * Creates HDMF mcrf form for each company
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
			System.out.println("Creating HDMF MCRF report for company with ID "+company.getCompanyId()+".");
			
			// making employeebean			
			EmployeeBean employeeBean =  new EmployeeBean();	
			employeeBean.addAll(company.getEmployeeList());
			
			// creating new form
			HDMFmcrf form = new HDMFmcrf(company.getCompanyDetails(),employeeBean);		
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(), company.getCompanyId(),"HDMF MCRF", "pdf","monthly");

			// creating the form as pdf and opening it after
			File file = new File(directoryName);			
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
