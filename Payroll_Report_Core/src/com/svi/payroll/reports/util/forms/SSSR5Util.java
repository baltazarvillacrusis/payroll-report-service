package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import com.svi.payroll.report.forms.SSSFormR5;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.EmployerContribution;
import com.svi.payroll.report.objects.PaymentDetail;
import com.svi.payroll.report.objects.PaymentDetail.TYPE;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;

public class SSSR5Util {

/**
	 * Creates SSS R5 report for each company
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
			System.out.println("Creating SSS R5 report for company with ID "+company.getCompanyId()+".");
						
			// setting other company details
			setCompanyDetails(company,companyList.getRunDate());			
			
			// setting penalty details
			Map<String, List<EmployerContribution>> penalties = new HashMap<String, List<EmployerContribution>>(); // TODO later
			
			// setting payment details
			Map<TYPE, PaymentDetail> sssPaymentDetails = new HashMap<TYPE, PaymentDetail>();	// TODO later
			
			// creating new form
			SSSFormR5 form = new SSSFormR5(company.getCompanyDetails(),penalties,sssPaymentDetails);		
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(companyList.getRunDate(), company.getCompanyId(),"SSS R5 Form", "pdf","monthly");			
			
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

	
	private static void setCompanyDetails(Company company, String runDate){
		// getting SSS EC and SS total amounts
		double sssSSAmountTotal = 0.0; 
		double sssECAmountTotal = 0.0; 
		for(Employee employee : company.getEmployeeList()){	
			sssSSAmountTotal = sssSSAmountTotal + employee.getSssSSAmount();
			sssECAmountTotal = sssECAmountTotal + employee.getSssECAmount();				
		}
		
		// setting sss total contributions
		Map<String, EmployerContribution> empConList = new HashMap<String, EmployerContribution>(); // it is a list to make it flexible (form can accept more than one month)
		EmployerContribution empCon1 = new EmployerContribution();
		
		// setting total contributions
		empCon1.setSsContribution(sssSSAmountTotal);	
		empCon1.setEmployeeCompensation(sssECAmountTotal);		
		
		//getting the month
		empCon1.setMonth(ReportUtil.getMonth(runDate));	
		
		// getting the year			
		empCon1.setYear(ReportUtil.getFormattedDate(runDate, "yyyy"));	
		empConList.put(empCon1.getMonth().getValue(), empCon1);
		
		company.getCompanyDetails().setEmpConList(empConList);
		company.getCompanyDetails().setTelNum(company.getCompanyDetails().getTelNum());
		company.getCompanyDetails().setMobileNo(company.getCompanyDetails().getMobileNo());
	}

}
