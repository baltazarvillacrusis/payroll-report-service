package com.svi.payroll.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.datastax.driver.mapping.Result;
import com.google.gson.JsonParseException;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.Employer;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;
import com.svi.payroll.reports.object.CompanyRunDate;
import com.svi.payroll.reports.object.cassandraDAO.EmployeeDetails;
import com.svi.payroll.reports.object.cassandraDAO.EmployerDetails;
import com.svi.payroll.reports.object.cassandraDAO.PayrollCompute;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.PreviousEmployerUtil;
import com.svi.payroll.reports.util.ZipUtility;
import com.svi.payroll.reports.util.forms.AlphalistUtil;
import com.svi.payroll.reports.util.forms.BIRForm1601CUtil;
import com.svi.payroll.reports.util.forms.BIRForm2316Util;
import com.svi.payroll.reports.util.forms.BankRegisterSalaryLoanRepaymentUtil;
import com.svi.payroll.reports.util.forms.HDMFmcrfUtil;
import com.svi.payroll.reports.util.forms.HDMFstlrfUtil;
import com.svi.payroll.reports.util.forms.NetPayRegisterForBankUtil;
import com.svi.payroll.reports.util.forms.PayrollRegisterUtil;
import com.svi.payroll.reports.util.forms.PhilhealthRF1Util;
import com.svi.payroll.reports.util.forms.ReportUtil;
import com.svi.payroll.reports.util.forms.SSSR3FormUtil;
import com.svi.payroll.reports.util.forms.SSSR3TextFileUtil;
import com.svi.payroll.reports.util.forms.SSSR5Util;
import com.svi.payroll.reports.util.forms.ThirteenthMonthPayUtil;


public class ReportGenerator {
	
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	public String generateCurrentReports(boolean monthlyReport, boolean annualReport, String companyIdToBeGenerated) throws JsonParseException, IOException{	
		long startTime = System.currentTimeMillis();
		// get current run_date and company list
		CompanyList  companyList = new CompanyList();
		
		// get run-date and company ID list
		getRunDateAndCompanyList(companyList);		
		
		// TODO delete this later
		System.out.println("Current payroll period:  "+companyList.getRunDate());
		System.out.println("Generating reports for company:  "+companyIdToBeGenerated);
		
		if(companyList.getRunDate() == null){
			return null;
		}
		
		// get details for each company
		getEachCompanyDetails(companyList);	
		
		// get latest employee details version for each company
		getLatestVersions(companyList);
		
		// get employee objects for each company
		getEmployeesForEachCompany(companyList);	
				
		// get employees data from payroll compute table
		getEmployeesDataFromPayrollCompute(companyList);
		
		
		// TODO delete this later, used for debugging
		for(Company company : companyList){
			System.out.println("Latest Employee Details Version: "+company.getCompanyId()+" --> "+company.getVersion());
			System.out.println("Number of employees :  "+company.getEmployeeList().size());
		}

		// generating reports
		createReports(companyList,monthlyReport,annualReport,companyIdToBeGenerated);
		
		System.out.println("--->>>>>> Shutting down the executor service..");
		executorService.shutdown();
		
		// waiting for the executor service to shut down
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println(e);
		}

		System.out.println("--->>>>>> Executor service shut down.");
		
		
	
		// returning back the file to be downloaded	
		String reportDate = companyList.getRunDate();
		ZipUtility util=new ZipUtility();
		List<File> listFiles=new ArrayList<>();
		String destination = null;
		if(companyIdToBeGenerated.equalsIgnoreCase(ReportCons.ALL_COMPANY)){
			if(monthlyReport && annualReport){
				File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate);
				if(checkDirectory(file)){
					listFiles.add(file);
					destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+".zip";
				}
				
			}
			else if(monthlyReport){
				File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"monthly");
				if(checkDirectory(file)){
					listFiles.add(file);
					destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"monthly"+".zip";
				}				
			}
			else if(annualReport){
				File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"annual");
				if(checkDirectory(file)){
					listFiles.add(file);
					destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"annual"+".zip";
				}
			}
		}
		else{
			if(monthlyReport){
				File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"monthly"+File.separator+companyIdToBeGenerated);
				if(checkDirectory(file)){
					listFiles.add(file);
					destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"monthly"+File.separator+companyIdToBeGenerated+"_Monthly_Reports.zip";
				}
			}
			else if(annualReport){
				File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"annual"+File.separator+companyIdToBeGenerated);
				if(checkDirectory(file)){
					listFiles.add(file);
					destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"annual"+File.separator+companyIdToBeGenerated+"_Annual_Reports.zip";
				}
			}			
		}
		
		if(destination != null){
			try {
				util.zip(listFiles, destination);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total run time in generating reports: " + totalTime + " ms");
		
		return destination;			
	}
	
	public String generateHistoricalReports(boolean monthlyReport, boolean annualReport, String companyIdToBeGenerated, String runDate) throws JsonParseException, IOException{	
		long startTime = System.currentTimeMillis();
		// get current run_date and company list
		CompanyList  companyList = new CompanyList();
		companyList.setRunDate(runDate);
		
		// adding the specific company 
		if(companyIdToBeGenerated.trim().equalsIgnoreCase(ReportCons.ALL_COMPANY)){
			// get run-date and company ID list
			getCompanyListForGivenRunDate(companyList);		
		}
		else{
			Company company = new Company();
			company.setCompanyId(companyIdToBeGenerated);		
			companyList.add(company);				
		}
		
		
		// TODO delete this later
		System.out.println("Current payroll period:  "+companyList.getRunDate());
		System.out.println("Generating reports for company:  "+companyIdToBeGenerated);
		
		if(companyList.getRunDate() == null){
			return null;
		}
		
		// get details for each company
		getEachCompanyDetails(companyList);			
		
		// get latest employee details version for each company
		getLatestVersions(companyList);		
		
		// get employeeIDs per company
		getEmployeeIdsPerCompany(companyList);
		
		// get employee objects for each company
		getEmployeesForEachCompany(companyList);	
				
		// get employees data from payroll compute table
		getEmployeesDataFromPayrollCompute(companyList);
		
		// if empty, do not create report
		if(companyList.get(0).getEmployeeList().size() == 0){
			return null;
		}
		
		// TODO delete this later, used for debugging
		for(Company company : companyList){
			System.out.println("Latest Employee Details Version: "+company.getCompanyId()+" --> "+company.getVersion());
			System.out.println("Number of employees :  "+company.getEmployeeList().size());
		}		
	
		// generating reports
		createReports(companyList,monthlyReport,annualReport,companyIdToBeGenerated);
		
		System.out.println("--->>>>>> Shutting down the executor service..");
		executorService.shutdown();
		
		// waiting for the executor service to shut down
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			System.out.println(e);
		}

		System.out.println("--->>>>>> Executor service shut down.");
		
		
	
		// returning back the file to be downloaded			
		ZipUtility util = new ZipUtility();
		List<File> listFiles=new ArrayList<>();
		String destination = null;
		
		
		if(companyIdToBeGenerated.equalsIgnoreCase(ReportCons.ALL_COMPANY)){			
			File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate);
			if(checkDirectory(file)){
				listFiles.add(file);
				destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+".zip";
			}
		}
		else{
			
			String directoryPathMonthly= ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+File.separator+ReportCons.MONTHLY_REPORT+File.separator+companyIdToBeGenerated;
			String directoryPathAnnual = ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+File.separator+ReportCons.ANNUAL_REPORT+File.separator+companyIdToBeGenerated;
			File  dirMonthly = new File(directoryPathMonthly);
			File  dirAnnual = new File(directoryPathAnnual);	
			if(dirMonthly.exists()){
				listFiles.add(dirMonthly);
			}
			if(dirAnnual.exists()){
				listFiles.add(dirAnnual);
			}
			if(listFiles.size() > 0){				
				destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+File.separator+companyIdToBeGenerated+"_All_Reports.zip";
			}
		}
		
		if(destination != null){
			try {
				util.zip(listFiles, destination);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		
		/*
		if(monthlyReport){
			File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"monthly"+File.separator+companyIdToBeGenerated);
			if(checkDirectory(file)){
				listFiles.add(file);
				destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"monthly"+File.separator+companyIdToBeGenerated+"_Monthly_Reports.zip";
				try {
					util.zip(listFiles, destination);
				} catch (IOException e) {			
					e.printStackTrace();
				}
				listFiles.clear();
			}
		}
		
		if(annualReport){
			File file = new File(ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"annual"+File.separator+companyIdToBeGenerated);
			if(checkDirectory(file)){
				listFiles.add(file);
				destination=ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+reportDate+File.separator+"annual"+File.separator+companyIdToBeGenerated+"_Annual_Reports.zip";
			
				try {
					util.zip(listFiles, destination);
				} catch (IOException e) {			
					e.printStackTrace();
				}
			}
		}			*/
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total run time in generating reports: " + totalTime + " ms");
		
		return destination;			
	}
		
	// TODO find a more efficient way of getting current date
	// use this: SELECT * FROM payroll_compute ORDER BY run_date DESC LIMIT 1;
	private void getRunDateAndCompanyList(CompanyList companyList){
		// get list of run dates
		Result<PayrollCompute> runDateAndCompanyID = CassandraConnectionUtility.getUserAccessor().getPayrollRunDateCompanyList();	
		
		// store distinct data with natural ordering
		TreeSet<String> treeSet = new TreeSet<String>();
		List<CompanyRunDate> runDateCompanylist = new ArrayList<CompanyRunDate>();
		
		
		for(PayrollCompute payrollCompute: runDateAndCompanyID){		
			CompanyRunDate companyRunDate = new CompanyRunDate();
			companyRunDate.setRunDate(payrollCompute.getRunDate());
			companyRunDate.setCompanyId(payrollCompute.getCompanyId());
			runDateCompanylist.add(companyRunDate);
			treeSet.add(payrollCompute.getRunDate());			
		}	
		// returns if no data available
		if(treeSet.size() == 0){
			return;
		}
		
		// sets the current date
		String currentDate = treeSet.last();
		companyList.setRunDate(currentDate);		
	
	
		// sets list of company
		List<String> addedCompanyIds = new ArrayList<String>();
		for (CompanyRunDate companyRunDate: runDateCompanylist){
			if(companyRunDate.getRunDate().equals(currentDate)){
				if(!addedCompanyIds.contains(companyRunDate.getCompanyId())){
					Company company = new Company();
					company.setCompanyId(companyRunDate.getCompanyId());
					companyList.add(company);
					addedCompanyIds.add(companyRunDate.getCompanyId());
				}				
			}
		}
		
		// TODO check later if this is still necessary
		// get employee IDs per company		
		getEmployeeIdsPerCompany(companyList);
		
	}
	// TODO find a more efficient way of getting current date
	// use this: SELECT * FROM payroll_compute ORDER BY run_date DESC LIMIT 1;
	private void getCompanyListForGivenRunDate(CompanyList companyList){
		// get list of run dates
		Result<PayrollCompute> runDateAndCompanyID = CassandraConnectionUtility.getUserAccessor().getPayrollRunDateCompanyList();	
		
		// store distinct data with natural ordering		
		List<CompanyRunDate> runDateCompanylist = new ArrayList<CompanyRunDate>();		
		
		for(PayrollCompute payrollCompute: runDateAndCompanyID){		
			CompanyRunDate companyRunDate = new CompanyRunDate();
			companyRunDate.setRunDate(payrollCompute.getRunDate());
			companyRunDate.setCompanyId(payrollCompute.getCompanyId());
			runDateCompanylist.add(companyRunDate);					
		}	
	
	
		// sets list of company
		List<String> addedCompanyIds = new ArrayList<String>();
		for (CompanyRunDate companyRunDate: runDateCompanylist){
			if(companyRunDate.getRunDate().equals(companyList.getRunDate())){
				if(!addedCompanyIds.contains(companyRunDate.getCompanyId())){
					Company company = new Company();
					company.setCompanyId(companyRunDate.getCompanyId());
					companyList.add(company);
					addedCompanyIds.add(companyRunDate.getCompanyId());
				}				
			}
		}
		
		// TODO check later if this is still necessary
		// get employee IDs per company		
		getEmployeeIdsPerCompany(companyList);
		
	}
	
	private void getEachCompanyDetails(CompanyList companyList){
		for(Company company: companyList){			
			Result<EmployerDetails> companyDetails = CassandraConnectionUtility.getUserAccessor().getEmployerDetails(company.getCompanyId());
			EmployerDetails employerDetails = companyDetails.one();
			if(employerDetails != null){
				Employer employer = ReportUtil.createEmployer(employerDetails, company);	
				company.setCompanyDetails(employer);
			}				
		}
	}
	
	private void getEmployeesForEachCompany(CompanyList companyList){
		// get employee objects for each company
		for(Company company: companyList){				
			Result<EmployeeDetails> employeeReportDetails = CassandraConnectionUtility.getUserAccessor().getEmployeeDetails(company.getCompanyId(),companyList.getRunDate(),company.getVersion()); // run_date and cutoff_date are the same in format and value
			
			for(EmployeeDetails employeeDetails : employeeReportDetails){						
				// adds those employees included in the payroll run
				if(company.getEmployeeIdList().contains(employeeDetails.getEmployeeID())){
					Employee employee = ReportUtil.createEmployee(employeeDetails,companyList.getRunDate(), company);	
								
					// adding the employee in the company employee list
					company.addEmployee(employee);
				}
			}
		}
				
	}

	private void getEmployeesDataFromPayrollCompute(CompanyList companyList){
		// get employee objects for each company
		for(Company company: companyList){				
			
			Result<PayrollCompute> employeePayrollData = CassandraConnectionUtility.getUserAccessor().getPayrollRun(companyList.getRunDate(), company.getCompanyId());
			if(employeePayrollData != null){
				for(PayrollCompute payrollCompute : employeePayrollData){						
					// update employee info data from payroll compute table	
					ReportUtil.getEmployeeDataFromPayrollCompute(getEmployee(company.getEmployeeList(),payrollCompute.getEmployeeId()),payrollCompute, company);	
				}
			}
		}	
	}
	
	private Employee getEmployee(List<Employee> employeelist, String employeeId){		
		// searching employee with ID employeeID
		for(Employee employee : employeelist){			
			if(employee.getID().equalsIgnoreCase(employeeId)){
				return employee;
			}
		}		
		return null;		
	}
	
	private void createReports(CompanyList companyList, boolean monthlyReports, boolean annualReports, String companyIdToBeGenerated) throws JsonParseException, IOException{
		// generating monthly reports
		if(monthlyReports){		
			// generating report
			if(ReportEnum.GENERATE_REPORT_PHILHEALTHRF1.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(PhilhealthRF1Util.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   PHILHEALTHRF1 report was generated completely.");
						}
				    }
				});
			}
			
			// generating report
			if(ReportEnum.GENERATE_REPORT_HDMFMCRF.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(HDMFmcrfUtil.createForm(companyList,companyIdToBeGenerated)){				
							System.out.println("   HDMFMCRF report was generated completely.");
						}
				    }
				});			
			}	
		
			// generating report
			if(ReportEnum.GENERATE_REPORT_HDMFSTLRF.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(HDMFstlrfUtil.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   HDMFSTLRF report was generated completely.");
						}
				    }
				});		
			}
				
			// generating report
			if(ReportEnum.GENERATE_REPORT_BANKREGISTRYLOANREPAYMENT.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(BankRegisterSalaryLoanRepaymentUtil.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   BANK REGISTRY LOAN REPAYMENT report was generated completely.");
						}
				    }
				});	
			}
			
			// generating report
			if(ReportEnum.GENERATE_REPORT_NETPAYREGISTERFORBANK.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(NetPayRegisterForBankUtil.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   NET PAY REGISTER FOR BANK report was generated completely.");
						}
				    }
				});	
			}
			
			// generating report
			if(ReportEnum.GENERATE_REPORT_SSSR3.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(SSSR3TextFileUtil.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   SSS R3 Text File report was generated completely.");
						}
				    }
				});	
			}
			
			// generating report
			if(ReportEnum.GENERATE_REPORT_SSSFORMR5.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(SSSR5Util.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   SSS R5 report was generated completely.");
						}
				    }
				});				
			}
			
			// generating report
			if(ReportEnum.GENERATE_REPORT_SSSFORMR3.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				
				// checks if monthly or quarterly generation of report
				if(ReportEnum.GENERATE_REPORT_SSSR3_FREQUENCY.value().equalsIgnoreCase(ReportCons.GENERATE_REPORT_SSS3_MONTHLY)){
					executorService.execute(new Runnable() {
					    public void run() {
					    	if(SSSR3FormUtil.createForm(companyList,ReportEnum.GENERATE_REPORT_SSSR3_FREQUENCY.value(),companyIdToBeGenerated)){
								System.out.println("   SSS R3 Collection List report was generated completely.");
							}
					    }
					});	
				}
				else{
					if(ReportUtil.periodIsEndOfQuarter(companyList.getRunDate())){
						executorService.execute(new Runnable() {
						    public void run() {
						    	if(SSSR3FormUtil.createForm(companyList,ReportEnum.GENERATE_REPORT_SSSR3_FREQUENCY.value(),companyIdToBeGenerated)){
									System.out.println("   SSS R3 Collection List report was generated completely.");
								}
						    }
						});	
					}				
				}			
			}
			
			// generating report
			if(ReportEnum.GENERATE_REPORT_BIRFORM1601C.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(BIRForm1601CUtil.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   BIR Form 1601-C report was generated completely.");
						}
				    }
				});	
			}
	
			// generating report
			if(ReportEnum.GENERATE_REPORT_PAYROLLREGISTER.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				executorService.execute(new Runnable() {
				    public void run() {
				    	if(PayrollRegisterUtil.createForm(companyList,companyIdToBeGenerated)){
							System.out.println("   PAYROLL REGISTER report was generated completely.");
						}
				    }
				});	
			}
		}
		
		
		// generating annual reports
		if(annualReports){
			// get list of previous employers for each company
			PreviousEmployerUtil.getPreviousEmployerList(companyList);			
		
			if(ReportEnum.GENERATE_REPORT_BIRFORM2316.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				
				if(BIRForm2316Util.createForm(companyList,companyIdToBeGenerated)){
					System.out.println("       BIR Form 2316 report was generated completely.");
				}
			}
			
			if(ReportEnum.GENERATE_REPORT_ALPHALISTFORBIRFORM1604.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
								
				if(AlphalistUtil.createForm(companyList,companyIdToBeGenerated)){
					System.out.println("       Alphalist report was generated completely.");
				}
			}	
			
			if(ReportEnum.GENERATE_REPORT_THIRTEENTH_MONTH_PAY.value().trim().equalsIgnoreCase(ReportCons.GENERATE_REPORT_YES)){
				
				if(ThirteenthMonthPayUtil.createForm(companyList,companyIdToBeGenerated)){
					System.out.println("       Thirteenth month pay report was generated completely.");
				}
			}	
		}		
		
	}
	
	private void getLatestVersions(CompanyList companyList){
		// initialize company version to -1
		Map<String,Integer> companyVersionMap = new HashMap<String,Integer>();
		for(Company company : companyList){
			companyVersionMap.put(company.getCompanyId(), -1);
		}
		
		Result<EmployeeDetails> employeeDetailsVersions = CassandraConnectionUtility.getUserAccessor().getVersions(); 
		
		// updating the version-company map based on the rows retrieved
		for(EmployeeDetails employeeDetails : employeeDetailsVersions){	
			
			// check if the retrieved company is on the list
			if(!companyVersionMap.containsKey(employeeDetails.getCompanyId())){
				continue;
			}
			if(employeeDetails.getCutOffDate().equalsIgnoreCase(companyList.getRunDate())){				
				int newVersion = -1;
				try {
					newVersion = Integer.parseInt(employeeDetails.getVersion());
				} catch (NumberFormatException e) {
					System.out.println("Cannot parse version number to integer.");
					e.printStackTrace();
				}
				
				String companyId = employeeDetails.getCompanyId();
				
				if(companyVersionMap.get(companyId) < newVersion){
					companyVersionMap.put(companyId, newVersion);
				}
			}			
		}
		
		// setting back to company the latest version
		for(Company company : companyList){
			company.setVersion(companyVersionMap.get(company.getCompanyId())+"");
		}
		
	}
		
	private boolean checkDirectory(File file){
		return file.exists();
	}
	
	private void getEmployeeIdsPerCompany(CompanyList companyList){
		// get employee IDs per company
		for(Company company: companyList){			
			Result<PayrollCompute> employeeIds = CassandraConnectionUtility.getUserAccessor().getCompanyEmployeeIds(companyList.getRunDate(),company.getCompanyId());	
			for(PayrollCompute payrollCompute : employeeIds){				
				company.addEmployeeId(payrollCompute.getEmployeeId());
			}						
		}	
	}
}
