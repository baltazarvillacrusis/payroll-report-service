package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.svi.payroll.report.forms.SSSR3FormText;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;


public class SSSR3TextFileUtil {	

	/**
	 * Creates SSS R3 text file report for each company
	 * @param companyList List of company objects.
	 */
	public static boolean createForm(CompanyList companyList, String companyIdToBeGenerated){	
		
		boolean isComplete = true;
		// generate report for each company
		for(Company company : companyList){		
			if(company.getEmployeeList().size() == 0){
				continue;
			}			
			
			String runDate = companyList.getRunDate();			
			String companyId = company.getCompanyId();			
			if(!companyIdToBeGenerated.equalsIgnoreCase(companyId) & !companyIdToBeGenerated.equalsIgnoreCase(ReportCons.ALL_COMPANY)){
				continue;
			}
			System.out.println("Creating SSS R3 text file report for company with ID "+companyId+".");	
			
			// adding employees in the bean
			List<Employee> employeeBean = new ArrayList<Employee>();
			employeeBean.addAll(company.getEmployeeList());				
			
			// creating new form
			String applicablePeriod = ReportUtil.getFormattedDate(runDate, "MMyyyy");
			String dateNow = ReportUtil.getFormattedDate(new Date(), "MMddhhmm");
			SSSR3FormText sssR3Text = new SSSR3FormText(company.getCompanyDetails(),company.getPaymentDetail(),employeeBean, applicablePeriod);
			
			//setting directory path
			String fileName = "R3"+company.getCompanyDetails().getSssNumber()+applicablePeriod+"."+dateNow;
			String directoryName = ReportEnum.REPORT_OUTPUT_PATH.value()+"/"+runDate+"/"+"monthly"+"/"+companyId+"/" + "SSS R3 Text File" + "/";
						
			File directory = new File(String.valueOf(directoryName));
			directory.mkdirs();
			
			
			File file = new File(directoryName +fileName);
			
			// creating the form 	
			try {				
				sssR3Text.createTxtFile(new FileOutputStream(file));		
			} catch (IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}	
			
		}		
		return isComplete;		
	}
}
