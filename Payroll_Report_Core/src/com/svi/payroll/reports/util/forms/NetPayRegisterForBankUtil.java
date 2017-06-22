package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.svi.payroll.report.forms.NetPayRegisterForBank;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class NetPayRegisterForBankUtil {
	
	/**
	 * Creates Net Pay Register For Bank for each company
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
			System.out.println("Creating net pay register for bank report for company with ID "+company.getCompanyId()+".");
			
			// adding employees in the bean
			EmployeeBean employeeBean = new EmployeeBean();		
			employeeBean.addAll(company.getEmployeeList());
		
			// creating form
			NetPayRegisterForBank form = new NetPayRegisterForBank(employeeBean);				
			form.setPeriod(ReportUtil.getFormattedDate(companyList.getRunDate(),"MMMMM, yyyy"));		
			form.setCompanyName(company.getCompanyDetails().getName());	
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(), company.getCompanyId(),
					"Net Pay Register For Bank", "xlsx","monthly");

			// creating the form as pdf and opening it after
			File file = new File(directoryName);		
			try {				
				form.createExcelStream(new FileOutputStream(file));	
			} catch (IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}			
		}
		
		return isComplete;		
	}
}
