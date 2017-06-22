package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import com.datastax.driver.mapping.Result;
import com.google.gson.JsonParseException;
import com.svi.payroll.report.forms.BIRForm2316;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.NonTaxableCompensationIncome;
import com.svi.payroll.report.objects.TaxSummary;
import com.svi.payroll.report.objects.TaxableCompensationIncome;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;
import com.svi.payroll.reports.object.PreviousEmployer;
import com.svi.payroll.reports.object.cassandraDAO.EmployeeDetails;
import com.svi.payroll.reports.object.cassandraDAO.PayrollCompute;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.PreviousEmployerUtil;

public class BIRForm2316Util {
	
	
	/**
	 * Creates BIR Form 2316 for each company
	 * @param companyList List of company objects.
	 * @throws IOException 
	 * @throws JsonParseException 
	 */
	public static boolean createForm(CompanyList companyList, String companyIdToBeGenerated) throws JsonParseException, IOException{	
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
			System.out.println("Creating  BIR Form 2316 report for company with ID "+companyId+".");				

			List<Employee> allEmployees = new ArrayList<Employee>();
			
			allEmployees.addAll(company.getEmployeeList());
			
			List<Employee> additionalEmployees = getEmployeeNotOnList(company,runDate,company.getVersion());
			allEmployees.addAll(additionalEmployees);
			
			
			String forTheYear = ReportUtil.getFormattedDate(runDate, "yyyy");
			for(Employee employee : allEmployees){				
				
				String employeeTin = "No TIN"; // default tin label for file name
				if(employee.getTin() != null && employee.getTin().trim().length() != 0){
					employeeTin = employee.getTin();
				}
				String pdfFileName = employee.getLastName()+"_"+employeeTin+"_"+"1231"+forTheYear;
				
				System.out.println("  BIR Form 2316 for employee with name  "+employee.getCompleteName()+".");	
				
				// for non-taxable compensation income
				NonTaxableCompensationIncome nonTaxableCompensation = new NonTaxableCompensationIncome();
				
				// for taxable compensation income
				TaxableCompensationIncome taxableCompensation = new TaxableCompensationIncome();
				
				// for tax summary
				TaxSummary taxSummary = new TaxSummary();
				
				// setting taxable and non taxable compensation 
				setTaxableAndNonTaxableComp(taxableCompensation, nonTaxableCompensation, taxSummary, runDate, company, employee);
					
				// creating new form
				BIRForm2316 form = new BIRForm2316(employee,nonTaxableCompensation,taxableCompensation,taxSummary);
				form.setForTheYear(forTheYear);
				
				// setting directory path
				String directoryName = ReportUtil.getOutputDirectory(runDate, companyId,"BIR Form 2316", "pdf",pdfFileName,"annual");			
				File file = new File(directoryName);
				
				// creating the form as pdf			
				try {				
					form.createFormStream(new FileOutputStream(file));		
				} catch (JRException | IOException e) {		
					isComplete = isComplete & false;
					e.printStackTrace();
				}		
			}
					
		}
		return isComplete;			
	}
	
	private static void setTaxableAndNonTaxableComp(TaxableCompensationIncome taxableCompensation, NonTaxableCompensationIncome nonTaxableCompensation,TaxSummary taxSummary, String runDate, Company company, Employee employee){
		
		String runDateYear = "";
		if(runDate != null & runDate.trim().length() > 4){
			runDateYear = runDate.substring(0,4);
		}
		// getting other taxable and non-taxable compensation		
		ReportUtil.getCompensationDetails(taxableCompensation,nonTaxableCompensation,taxSummary, runDateYear, company, employee);	
		
	}
	
	private static List<Employee> getEmployeeNotOnList(Company company, String currentRunDate, String version){
		List<Employee> employeeList = new ArrayList<Employee>();	
		List<String> dummyIDList = new ArrayList<String>();	
		// add missing employee from the previous 2 months of the quarter
		for(int i=1; i<=11; i++){			
			String runDate = ReportUtil.subtractMonth(currentRunDate,i);	
			
			// making sure to get other employees from the same year
			if(currentRunDate != null & currentRunDate.trim().length() >= 4){
				if(!runDate.substring(0,4).equalsIgnoreCase(currentRunDate.substring(0, 4))){
					continue;
				}
			}
			// get all employee IDs included in the payroll run for the given run date
			Result<PayrollCompute> employeeIDList = CassandraConnectionUtility.getUserAccessor().getCompanyEmployeeIds(runDate, company.getCompanyId());
			for(PayrollCompute employeeID : employeeIDList){
				String newEmployeeID = employeeID.getEmployeeId();
				
				// check if employee already exists
				if(!company.getEmployeeIdList().contains(newEmployeeID) & !dummyIDList.contains(newEmployeeID)){
					
					// get employee details
					Result<EmployeeDetails> employeeReportDetails = CassandraConnectionUtility.getUserAccessor().getEmployeeDetails(company.getCompanyId(),currentRunDate,version,newEmployeeID);	
					EmployeeDetails employeeDetails =  employeeReportDetails.one();
					if (employeeDetails != null){						
						
						// create new employee then add to the new list of employees
						Employee employee = ReportUtil.createEmployee(employeeDetails, runDate, company);						
						employeeList.add(employee);
						dummyIDList.add(newEmployeeID);
					}				
				}			
			}
		}
		return employeeList;
	}
	
}
