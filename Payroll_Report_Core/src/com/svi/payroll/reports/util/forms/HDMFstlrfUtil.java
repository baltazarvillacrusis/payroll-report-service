package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRException;

import com.svi.payroll.report.forms.HDMFstlrf;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class HDMFstlrfUtil {
	
	/**
	 * Creates HDMF stlrf report for each company
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
			System.out.println("Creating HDMF stlrf report for company with ID "+company.getCompanyId()+".");
			
			// adding employees in the bean
			List<Employee> employeeBean = new ArrayList<Employee>();
			for(Employee employee : company.getEmployeeList()){
				if(employee.getAmount() > 0){
					employeeBean.add(getCopyOf(employee));	
				}								
			}			
			
			// creating new form
			HDMFstlrf form = new HDMFstlrf( new ArrayList<Employee>(employeeBean),company.getCompanyDetails());							
			form.setPeriodCovered(ReportUtil.getFormattedDate(companyList.getRunDate(), "MMMMM, yyyy"));		
			
			//setting directory path			
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(),company.getCompanyId(),"HDMF STLRF","pdf","monthly");
			
			// creating the form as pdf	and opening it after			
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

	 // to be replaced later by deep clone copying
	private static Employee getCopyOf(Employee employee){
		Employee employeeCopy = new Employee();
		employeeCopy.setPagibigMIDNumber(employee.getPagibigMIDNumber());
		employeeCopy.setApplicationNum(employee.getApplicationNum());
		employeeCopy.setLastName(employee.getLastName());
		employeeCopy.setFirstName(employee.getFirstName());
		employeeCopy.setMiddleName(employee.getMiddleName());
		employeeCopy.setExtensionName(employee.getExtensionName());
		employeeCopy.setLoanType(employee.getLoanType());
		employeeCopy.setAmount(employee.getAmount());		
		return employeeCopy;
	}
		

}
