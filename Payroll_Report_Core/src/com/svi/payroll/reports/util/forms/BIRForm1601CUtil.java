package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.Result;
import com.svi.payroll.report.forms.BIRForm1601C;
import com.svi.payroll.report.objects.Address;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.PaymentDetail;
import com.svi.payroll.report.objects.PaymentDetail.TYPE;
import com.svi.payroll.report.objects.TaxAdjustment;
import com.svi.payroll.report.objects.TaxComputation;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.enums.PayslipTicketLabels;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;
import com.svi.payroll.reports.object.cassandraDAO.PayrollCompute;
import com.svi.payroll.reports.util.CassandraConnectionUtility;

public class BIRForm1601CUtil {
	/**
	 * Creates BIR Form 1601-C form for each company.
	 *  13th month pay is currently NOT included in the computation of total
	 *  amount of compensation, when generating year-end report.
	 * @param companyList List of company objects.
	 */
	public static boolean createForm(CompanyList companyList, String companyIdToBeGenerated){	
		boolean isComplete = true;
		// generate report for each company
		for(Company company : companyList){	
			
			String runDate = companyList.getRunDate();		
			String companyId = company.getCompanyId();			
			if(!companyIdToBeGenerated.equalsIgnoreCase(companyId) & !companyIdToBeGenerated.equalsIgnoreCase(ReportCons.ALL_COMPANY)){
				continue;
			}
			System.out.println("Creating BIR Form 1601-C report for company with ID "+companyId+".");				
			
			TaxComputation taxComputation = new TaxComputation();	 //TODO later
			getTaxComputationDetails(taxComputation, runDate, companyId);			
			
			getLessNonTaxablesMWE(taxComputation, company, runDate);
			
			List<TaxAdjustment> taxAdjustments = new ArrayList<TaxAdjustment>();  //TODO later, taxComputation property
			Map<TYPE, PaymentDetail> paymentDetails = new HashMap<TYPE, PaymentDetail>();  //TODO later
			
			// change employer registered address temporarily (exclude country and zip code)
			Address address = company.getCompanyDetails().getAddress();
			//String country = address.getCountry();
			String zipcode = address.getZipCode();
			//address.setCountry("");
			address.setZipCode("");			
			company.getCompanyDetails().setRegisteredAdd(company.getCompanyDetails().getAddress().completeAddress());
		
			// creating new form
			BIRForm1601C form = new BIRForm1601C(company.getCompanyDetails(),taxComputation,paymentDetails);
			form.setForTheMonth(ReportUtil.getFormattedDate(runDate, "MMyyyy"));
			
			
			// setting directory path
			String directoryName = ReportUtil.getOutputDirectory(runDate, companyId,"BIR Form 1601C", "pdf","monthly");			
			File file = new File(directoryName);
			
			// creating the form as pdf			
			try {				
				form.createFormStream(new FileOutputStream(file));		
			} catch (JRException | IOException e) {		
				isComplete = false;
				e.printStackTrace();
			}				
			
			// changing back the address
			//address.setCountry(country);
			address.setZipCode(zipcode);
			company.getCompanyDetails().setRegisteredAdd(company.getCompanyDetails().getAddress().completeAddress());
		}
		return isComplete;			
	}
	
	
	private static void getTaxComputationDetails(TaxComputation taxComputation, String runDate, String companyID){
		// getting total gross pay of all employees		
		double totalGrossPay = getTotalGrossPay(runDate, companyID);
		
		// getting total non taxable compensation of all employees		
		List<Double> grandTotals = getTotalNonTaxableCompenstaion(runDate, companyID);
		double totalNonTaxable = grandTotals.get(0);
		
		// getting total tax withheld of all employees		
		double totalTaxWithheld = grandTotals.get(1);		
		
		// getting total tax withheld of all employees		
		double totalGovDeductions = grandTotals.get(2);	
		
		// getting total tax withheld of all employees		
		double totalPremiumsPaidOnHealth = grandTotals.get(3);	
		
		// creating the taxComputation object
		taxComputation.setTotalAmtCompensation(totalGrossPay+totalNonTaxable-totalGovDeductions-totalPremiumsPaidOnHealth);			
		taxComputation.setNonTaxableOthers(totalNonTaxable);		
		taxComputation.setRequiredTaxWithheld(totalTaxWithheld);
	}
	
	private static double getTotalGrossPay(String runDate, String companyID){
		// setting the query
		String query = "SELECT SUM(pay_gross) FROM payroll_compute2 WHERE run_date ='"+ runDate+"' AND company_id = '"+companyID+"';";
				
		ResultSet result = CassandraConnectionUtility.getTaxComputationDetail(query);	
		Row row = result.one();
		
		double totalGrossPay = 0.0;
		if(row != null){
			totalGrossPay = ReportUtil.zeroIfNull(row.getDecimal(0)).doubleValue();
		}		
		return totalGrossPay;
	}
	
	private static List<Double> getTotalNonTaxableCompenstaion(String runDate, String companyID){
		List<Double> totals = new ArrayList<Double>();
		double grandTotalNontaxable = 0.0;
		double grandTotalTaxWithheld = 0.0;
		double totalGovernmentDeductions= 0.0;
		double totalPremiumsPaidOnHealth= 0.0;
		
		Result<PayrollCompute> nonTaxableComp = CassandraConnectionUtility.getUserAccessor().getNonTaxableCompensation(runDate, companyID);
		for(PayrollCompute payrollNonTaxable : nonTaxableComp){
			
			grandTotalNontaxable = grandTotalNontaxable + ReportUtil.getTotalMapValues(payrollNonTaxable.getNonTaxableCompensations());			
			grandTotalTaxWithheld = grandTotalTaxWithheld + ReportUtil.getTotalMapValues(payrollNonTaxable.getTaxesWithheld());
			totalGovernmentDeductions = totalGovernmentDeductions + ReportUtil.getTotalGovDeductions(payrollNonTaxable.getGovernmentDeductions());
			totalPremiumsPaidOnHealth = totalPremiumsPaidOnHealth + ReportUtil.getTotalMapValues(payrollNonTaxable.getTaxableDeductions());
		
		}
		grandTotalNontaxable = grandTotalNontaxable+totalPremiumsPaidOnHealth+totalGovernmentDeductions;
		totals.add(grandTotalNontaxable);
		totals.add(grandTotalTaxWithheld);
		totals.add(totalGovernmentDeductions);
		totals.add(totalPremiumsPaidOnHealth);
		
		return totals;
	}
	
	
	private static void getLessNonTaxablesMWE(TaxComputation taxComputation, Company company, String runDate){
		double totalHazardPay = 0.0;		
		double totalHolidayPay = 0.0;
		double totalOvertimes = 0.0;
		double totalNigthShiftDiff = 0.0;	
		double totalBasicPay = 0.0;
		String employeeID = "";
		
		String query = "SELECT employee_id, compensation_tax, pay_basic, overtimes FROM payroll_compute2 WHERE run_date = '"+runDate+"' AND company_id = '"+company.getCompanyId()+"';";
		//System.out.println("QUERY: "+query);
		ResultSet result = CassandraConnectionUtility.getTaxComputationDetail(query);	
		
		while (!result.isExhausted()) {
			Row row = result.one();	
			if(row != null){
				employeeID = row.getString("employee_id");			
				
				if(verifyIDIfMWE(employeeID,company.getEmployeeList())){
				
					Map<String,BigDecimal> compensationTaxable = row.getMap("compensation_tax",String.class,BigDecimal.class);
					totalHazardPay = totalHazardPay + ReportUtil.zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_HAZARD_PAY.getVal())).doubleValue();
					totalHolidayPay = totalHolidayPay + ReportUtil.zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_HOLIDAY_PAY.getVal())).doubleValue();
										
					// over times and night shift differentials
					Map<String, BigDecimal> overtimes = row.getMap("overtimes",String.class,BigDecimal.class);
					totalOvertimes = totalOvertimes + ReportUtil.computeOvertimes(overtimes) + ReportUtil.computeNightShiftOvertime(overtimes);
					totalNigthShiftDiff = totalNigthShiftDiff + ReportUtil.computeNightShiftDiff(overtimes);
					
					totalBasicPay = totalBasicPay +ReportUtil.zeroIfNull(row.getDecimal("pay_basic")).doubleValue();	
				}
			}
		}
		// for MWES only
		taxComputation.setNonTaxableMWES(totalBasicPay);   
		taxComputation.setNonTaxableHONHPay(totalHazardPay+totalHolidayPay+totalOvertimes+totalNigthShiftDiff); 
				
	}
	
	private static boolean verifyIDIfMWE(String employeeID, List<Employee> employeeList){
		
		boolean isMWE = false;
		for(Employee employee: employeeList){
			if(employee.getID().equalsIgnoreCase(employeeID)){				
				if(Boolean.TRUE.equals(employee.getIsMinimumWageEarner())){
					isMWE = true;
				}				
				break;
			}
		}
		return isMWE;
	}

}
