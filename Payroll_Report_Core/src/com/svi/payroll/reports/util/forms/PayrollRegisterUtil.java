package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.svi.payroll.report.forms.PayrollRegister;
import com.svi.payroll.report.forms.PayrollRegisterInternal;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class PayrollRegisterUtil {
	
	/**
	 * Creates Payroll Register for each company
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
			System.out.println("Creating Payroll Register report for company with ID "+company.getCompanyId()+".");
			
			// making employee bean
			EmployeeBean employeeBean = new EmployeeBean();
			employeeBean.addAll(company.getEmployeeList());				
	
			// creating new form
			PayrollRegister form = new PayrollRegister(employeeBean);
			form.setPeriod(ReportUtil.getFormattedDate(companyList.getRunDate(),"MMMMM, yyyy"));		
			form.setCompanyName(company.getCompanyDetails().getName());								
			form.setRunType(company.getPayrollRuntype());	
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(), company.getCompanyId(), "Payroll Register", "xlsx","monthly");

			// creating the form as pdf and opening it after
			File file = new File(directoryName);
			try {				
				form.createExcelStream(new FileOutputStream(file));	
			} catch (IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}		
			
			
			// creating internal payroll register
			PayrollRegisterInternal formInternal = new PayrollRegisterInternal(employeeBean);
			formInternal.setPeriod(ReportUtil.getFormattedDate(companyList.getRunDate(),"MMMMM, yyyy"));		
			formInternal.setCompanyName(company.getCompanyDetails().getName());								
			formInternal.setRunType(company.getPayrollRuntype());	
			
			//setting directory path
			String directoryName2 = ReportUtil.getOutputDirectoryPayrollInternal(companyList.getRunDate(), company.getCompanyId(), "Payroll Register", "xlsx","monthly", "Payroll Register Internal");
		
			// creating the form as pdf and opening it after
			File file2 = new File(directoryName2);
			try {				
				
				formInternal.createExcelStream(new FileOutputStream(file2));	
			} catch (IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}	
		
		}		
		return isComplete;		
	}
	
}
