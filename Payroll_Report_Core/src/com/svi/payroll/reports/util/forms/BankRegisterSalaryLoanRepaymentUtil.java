package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.svi.payroll.report.forms.BankRegisterSalaryLoanRepayment;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class BankRegisterSalaryLoanRepaymentUtil {

	
	/**
	 * Creates Bank Register Salary Loan Repayment for each company
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
			System.out.println("Creating Bank Register Salary Loan Repayment report for company with ID "+company.getCompanyId()+".");
			
			// making employee objects with loans
			EmployeeBean employeeBean = new EmployeeBean();	
			for(Employee employee : company.getEmployeeList()){
				if(employee.getSalaryLoans().size() > 0){
					employeeBean.add(employee);
				}
			}
			 
			// creating new form
			BankRegisterSalaryLoanRepayment form = new BankRegisterSalaryLoanRepayment(employeeBean);							
			form.setPeriod(ReportUtil.getFormattedDate(companyList.getRunDate(),"MMMMM, yyyy"));		
			form.setCompanyName(company.getCompanyDetails().getName());	
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(), company.getCompanyId(), "Bank Register Loan Repayment", "xlsx","monthly");

			// creating the form 
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
