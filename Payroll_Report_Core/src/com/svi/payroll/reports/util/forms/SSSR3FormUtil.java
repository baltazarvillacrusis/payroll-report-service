package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.money.MonetaryAmount;
import net.sf.jasperreports.engine.JRException;
import org.javamoney.moneta.Money;
import com.datastax.driver.mapping.Result;
import com.svi.payroll.report.forms.SSSFormR3;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.EmployeeBean;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;
import com.svi.payroll.reports.object.cassandraDAO.EmployeeDetails;
import com.svi.payroll.reports.object.cassandraDAO.PayrollCompute;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.GovernmentDeductionsUtilitiy;

public class SSSR3FormUtil {
	/**
	 * Creates SSS R3 Form report for each company
	 * @param companyList List of company objects.
	 */
	public static boolean createForm(CompanyList companyList, String frequency, String companyIdToBeGenerated){
		
		// creating the form for each company
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
			System.out.println("Creating SSS R3 Form report for company with ID "+companyId+".");
			
			EmployeeBean employeeBean = new EmployeeBean();		
			
			// adding employees in the bean
			addEmployeesToBean(employeeBean, company.getEmployeeList(), companyId, runDate);
					
			// additional employees (terminated or resigned before this payroll run
			if(!ReportEnum.GENERATE_REPORT_SSSR3_FREQUENCY.value().equalsIgnoreCase(ReportCons.GENERATE_REPORT_SSS3_MONTHLY)){
				List<Employee> additionalEmployees = getEmployeeNotOnList(company, runDate, company.getVersion());
				addEmployeesToBean(employeeBean, additionalEmployees, companyId, runDate);
			}
						
			// creating SSS R3 contribution list		
			SSSFormR3 sssR3Form = new SSSFormR3(employeeBean,company.getCompanyDetails());	
			sssR3Form.setQuarterEnding(ReportUtil.getFormattedDate(runDate, "MMyyyy"));
			
			//setting directory path
			String directoryName = ReportUtil.getOutputDirectory(runDate, companyId,"SSS R3 Form", "pdf","monthly");			
			
			File file = new File(directoryName);
			
			// creating the form 	
			try {				
				sssR3Form.createFormStream(new FileOutputStream(file));		
			} catch (JRException | IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}				
			
		}		
		return isComplete;		
	}	

	
	
	private static List<Employee> getEmployeeNotOnList(Company company, String currentRunDate, String version){
		List<Employee> employeeList = new ArrayList<Employee>();	
		List<String> dummyIDList = new ArrayList<String>();	
		// add missing employee from the previous 2 months of the quarter
		for(int i=1; i<=2; i++){			
			String runDate = ReportUtil.subtractMonth(currentRunDate,i);		
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

	
	private static void addEmployeesToBean(EmployeeBean employeeBean, List<Employee> employeeList, String companyID, String runDate){
		for(Employee employee : employeeList){		
			
			// setting employee SSS contributions
			setEmployeeContribution(employee,companyID,runDate);	
			 
			// adding employee in the bean		
			employeeBean.add(employee);  // TODO add employees with SS contribution for at least one month
		}			
	}
	
	private static void setEmployeeContribution(Employee employee, String companyId, String currentRunDate){
		 // dictates the data to be retrieved
		 List<String> monthsSequence = new ArrayList<String>();
		 if(!ReportEnum.GENERATE_REPORT_SSSR3_FREQUENCY.value().equalsIgnoreCase(ReportCons.GENERATE_REPORT_SSS3_MONTHLY)){
			 monthsSequence =Arrays.asList("THIRD", "SECOND","FIRST");  //to be used in retrieving 3 months of SSS contribution
		 }
		 else{
			 monthsSequence.add("FIRST"); // retrieving of the current payroll period data
		 }
		 
		 Map<String,Double> quarterSSAmount = new HashMap<String,Double>();				 
		 Map<String,Double> quarterECAmount = new HashMap<String,Double>();	
	
		 // runs depending on the frequency of report generation
		 for(int i=0; i < monthsSequence.size(); i++){
			String dateToRetrieve = ReportUtil.subtractMonth(currentRunDate,i); // get previous months
			
			//get basic_pay from db											
			Result<PayrollCompute> employeePayBasic = CassandraConnectionUtility.getUserAccessor().getBasicPay(dateToRetrieve, companyId, employee.getID());	
								
			for(PayrollCompute employeeDetails : employeePayBasic){	
				BigDecimal payBasic = ReportUtil.zeroIfNull(employeeDetails.getBasicPay());	
				MonetaryAmount basicPay = Money.of(ReportUtil.zeroIfNull(payBasic), "PHP");				
				
				double sssSSAmount = GovernmentDeductionsUtilitiy
						.computeSSSContribution(basicPay).getNumber().doubleValue() +
						GovernmentDeductionsUtilitiy.computeSSSEmployerShare(basicPay)
						.getNumber().doubleValue();
				double sssECAmount = GovernmentDeductionsUtilitiy
						.computeSSSEmployerCompensation(basicPay)
						.getNumber().doubleValue();		
				
				quarterSSAmount.put(monthsSequence.get(i), sssSSAmount);
				quarterECAmount.put(monthsSequence.get(i), sssECAmount);	
			}
		
		 }
		 
		 employee.setQuarterSSAmount(quarterSSAmount);
		 employee.setQuarterECAmount(quarterECAmount);
		 
	}


}
