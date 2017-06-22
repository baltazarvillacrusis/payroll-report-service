package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

import com.svi.payroll.report.forms.PhilHealthRF1;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class PhilhealthRF1Util {

	/**
	 * Creates Philhealth RF1 form for each company
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
			System.out.println("Creating Philhealth RF1 report for company with ID "+companyId+".");				
			
			// adding employees to bean
			EmployeeBean employeeBean =  new EmployeeBean();	
			employeeBean.addAll(company.getEmployeeList());
			
			// creating new form
			PhilHealthRF1 form = new PhilHealthRF1(company.getCompanyDetails(),employeeBean);	
			form.setApplicablePeriod(ReportUtil.getFormattedDate(companyList.getRunDate(),"MMMMM, yyyy"));			
			
			// setting directory path
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(), companyId,"Philhealth RF1", "pdf","monthly");			
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
}
