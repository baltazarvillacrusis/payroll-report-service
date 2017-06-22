package com.svi.payroll.reports.util.forms;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.money.MonetaryAmount;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.javamoney.moneta.Money;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Result;
import com.svi.payroll.report.objects.Address;
import com.svi.payroll.report.objects.Alphalist;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.NonTaxableCompensationIncome;
import com.svi.payroll.report.objects.TaxSummary;
import com.svi.payroll.report.objects.TaxableCompensationIncome;
import com.svi.payroll.report.objects.Employee.SEX;
import com.svi.payroll.report.objects.Employee.STATUS;
import com.svi.payroll.report.objects.Employer;
import com.svi.payroll.report.objects.Employer.CATEGORY;
import com.svi.payroll.report.objects.EmployerContribution.MONTH;
import com.svi.payroll.report.objects.PaymentDetail;
import com.svi.payroll.reports.constants.MasterFileCons;
import com.svi.payroll.reports.constants.PreviousEmployerJsonCons;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.enums.LeaveCodeEnum;
import com.svi.payroll.reports.enums.PayslipTicketLabels;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.enums.TimeAndAttendanceEnum;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.PreviousEmployer;
import com.svi.payroll.reports.object.cassandraDAO.EmployeeDetails;
import com.svi.payroll.reports.object.cassandraDAO.EmployerDetails;
import com.svi.payroll.reports.object.cassandraDAO.PayrollCompute;
import com.svi.payroll.reports.object.cassandraDAO.PreviousEmployerDetails;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.GovernmentDeductionsUtilitiy;

public class ReportUtil {
	
	public static String getOutputDirectory(String runDate, String companyId, String reportName, String extension, String monthlyOrAnnual){
		//String reportDate = getFormattedDate(runDate,"yyyyMM");
		String directoryName = ReportEnum.REPORT_OUTPUT_PATH.value()+"/"+runDate+"/"+monthlyOrAnnual+"/"+companyId+"/" + reportName + "/";
		File directory = new File(String.valueOf(directoryName));
		directory.mkdirs();
		String reportOutputDir = directoryName+companyId+"_"+reportName+"_"+runDate+"."+extension;
		return reportOutputDir;
	}
	
	// for BIRForm 2316 Format
	public static String getOutputDirectory(String runDate, String companyId, String reportName, String extension, String fileName, String monthlyOrAnnual){
		//String reportDate = getFormattedDate(runDate,"yyyyMM");
		String directoryName = ReportEnum.REPORT_OUTPUT_PATH.value()+"/"+runDate+"/"+monthlyOrAnnual+"/"+companyId+"/" + reportName + "/";
		File directory = new File(String.valueOf(directoryName));
		directory.mkdirs();
		String reportOutputDir = directoryName+fileName+"."+extension;
		return reportOutputDir;
	}
	
	public static String getOutputDirectoryPayrollInternal(String runDate, String companyId, String reportName, String extension, String monthlyOrAnnual, String fileName){
		//String reportDate = getFormattedDate(runDate,"yyyyMM");
		String directoryName = ReportEnum.REPORT_OUTPUT_PATH.value()+"/"+runDate+"/"+monthlyOrAnnual+"/"+companyId+"/" + reportName + "/";
		File directory = new File(String.valueOf(directoryName));
		directory.mkdirs();
		String reportOutputDir = directoryName+companyId+"_"+fileName+"_"+runDate+"."+extension;
		return reportOutputDir;
	}
	
	public static BigDecimal zeroIfNull(BigDecimal decimal) {
		if (decimal == null) {
			decimal = BigDecimal.ZERO;
		}
		return decimal;
	}
	
	public static String getFormattedDate(String date, String format){
		if(date == null || format == null){
			return "";
		}
		
		Date payrollPeriod = null;
		try {
			payrollPeriod = new SimpleDateFormat("yyyyMM").parse(date);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		DateFormat formattedDate = new SimpleDateFormat(format);		
		String period = formattedDate.format(payrollPeriod);	
		
		return period;
	}	
	
	public static String getFormattedDate(Date date, String format){
		if(date == null || format == null){
			return "";
		}		
		DateFormat formattedDate = new SimpleDateFormat(format);		
		String period = formattedDate.format(date);			
		return period;
	}	
		
	public static boolean periodIsEndOfQuarter(String date){
		String monthString = date.substring(4);
		
		int month = -1;
		try {
			month = Integer.parseInt(monthString);
		} catch (NumberFormatException e) {			
			e.printStackTrace();
		}
		
		if(month == 3 || month == 6 || month == 9 || month == 12){
			return true;
		}		
		return false;
	}
	
	public static String subtractMonth(String date, int numberOfMonths ){
		Date payrollPeriod = null;
		try {
			payrollPeriod = new SimpleDateFormat("yyyyMM").parse(date);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(payrollPeriod);
		cal.add(Calendar.MONTH, -1*numberOfMonths);
		
		return getFormattedDate(cal.getTime(),"yyyyMM");		
	}
	
	public static Employee createEmployee(EmployeeDetails employeeDetails, String runDate, Company company){
		Employee employee = new Employee();			
		// basic info
		employee.setID(employeeDetails.getEmployeeID());
		employee.setTin(filterString(employeeDetails.getTin()));
		employee.setFirstName(employeeDetails.getFirstName());
		employee.setMiddleName(employeeDetails.getMiddleName());	
		employee.setLastName(employeeDetails.getLastName());
		employee.setExtensionName(employeeDetails.getExtensionName());
		employee.setRdoCode(employeeDetails.getRdoCode());
		
		// address
		if(employeeDetails.getRegisteredAddress() != null){
			employee.setRegisteredAdd(getAddressNoCountryAndZipcode(employeeDetails.getRegisteredAddress()).completeAddress());
			employee.setRegisteredAddZipCode(getAddressMapValue(employeeDetails.getRegisteredAddress(),ReportCons.ZIPCODE));
		}
		if(employeeDetails.getLocalAddress() != null){
			employee.setLocalAdd(getAddressNoCountryAndZipcode(employeeDetails.getLocalAddress()).completeAddress());
			employee.setLocalAddZipCode(getAddressMapValue(employeeDetails.getLocalAddress(),ReportCons.ZIPCODE));
		}
		if(employeeDetails.getForeignAddress() != null){
			employee.setForeignAdd((getAddressNoCountryAndZipcode(employeeDetails.getForeignAddress()).completeAddress()));
			employee.setForeignAddZipCode(getAddressMapValue(employeeDetails.getForeignAddress(),ReportCons.ZIPCODE));
		}		
	
		employee.setBirthDate(getFormattedDate(employeeDetails.getBirthDate(),"MMddyyyy"));
		employee.setTelNum(employeeDetails.getTelNum());
		employee.setIsSingle(employeeDetails.isSingle());
		employee.setIsWifeClaimingExemption(employeeDetails.isWifeClaimingExemption());
		employee.setSmwRatePerDay(zeroIfNull(employeeDetails.getSmwRatePerDay()).doubleValue()); 
		employee.setSmwRatePerMonth(zeroIfNull(employeeDetails.getSmwRatePerMonth()).doubleValue()); 
		employee.setIsMinimumWageEarner(employeeDetails.isMinimumWageEarner());
		employee.setRdoCode(employeeDetails.getRdoCode());		
		employee.setExemptionCode(employeeDetails.getExemptionCode());
		employee.setTaxExemption(computeTaxExemption(employeeDetails.getExemptionCode()));
		
		// employer info
		Employer presentEmployer = company.getCompanyDetails();
		if(ReportCons.TAX_CODE_Z.equalsIgnoreCase(employee.getExemptionCode().trim())){
			presentEmployer.setIsMainEmployer(false);  // TODO what if tax code is Z but is due to failure to file application for BIR registration
		}
		else{
			presentEmployer.setIsMainEmployer(true); 
		}		
		employee.setPresentEmployer(presentEmployer);
	
		employee.setCtcNum(employeeDetails.getCtcNum());
		employee.setCtcPlaceOfIssue(employeeDetails.getCtcPlaceOfIssue());					
		employee.setCtcDateOfIssue(getFormattedDate(employeeDetails.getCtcDateOfIssue(),"MMddyyyy"));
		employee.setCtcAmountPaid(zeroIfNull(employeeDetails.getCtcAmountPaid()).doubleValue());
		employee.setDependents(getDependents(employeeDetails.getDependents()));						
		if(employeeDetails.getSex().equalsIgnoreCase(ReportCons.SEX_MALE_KEY)){
			employee.setSex(SEX.MALE);
		}
		else if(employeeDetails.getSex().equalsIgnoreCase(ReportCons.SEX_FEMALE_KEY)){
			employee.setSex(SEX.FEMALE);
		}						
				
		if(employeeDetails.getPhilhealthStatus().equalsIgnoreCase(STATUS.SEPARATED.getCode())){
			employee.setStatus(STATUS.SEPARATED);
		}
		else if(employeeDetails.getPhilhealthStatus().equalsIgnoreCase(STATUS.NO_EARNINGS.getCode())){
			employee.setStatus(STATUS.NO_EARNINGS);
		}
		else if(employeeDetails.getPhilhealthStatus().equalsIgnoreCase(STATUS.NEWLY_HIRED.getCode())){
			employee.setStatus(STATUS.NEWLY_HIRED);
		}	
		
		// government info
		employee.setSssNumber(employeeDetails.getSssNum().replace("-","").trim());				
		employee.setEffectivityDate(getFormattedDate(employeeDetails.getEffectivityDatePhilhealthStatus(),"MMddyyyy"));					
		employee.setPin(filterString(employeeDetails.getPin()));		
		employee.setPagibigMIDNumber(employeeDetails.getPagibigMIDNumber());
		employee.setPagibigAcccountNumber(employeeDetails.getPagibigAcccountNumber());
		employee.setPagibigMembershipProgram(employeeDetails.getPagibigMembershipProgram());
		
		if(employeeDetails.getPagibigPeriodCovered() == null || employeeDetails.getPagibigPeriodCovered().toString().trim().length() == 0){
			employee.setPagibigPeriodCovered(runDate);
		}
		else{
			employee.setPagibigPeriodCovered(getFormattedDate(employeeDetails.getPagibigPeriodCovered(),"yyyyMM"));
		}		
		
		employee.setPagibigRemark(employeeDetails.getPagibigRemark());
		employee.setApplicationNum(employeeDetails.getApplicationNum());			
		
		// other info
		employee.setBankAcctNum(employeeDetails.getBankAcctNum());
		employee.setBankName(employeeDetails.getBankName());
		
		if(Boolean.TRUE.equals(employee.getIsWifeClaimingExemption())){
			employee.setTaxExemption(0.0);  // wife is already claiming tax exemption for qualified dependents
		}
		
		employee.setIsRankAndFile(employeeDetails.isRankAndFile());
		if(employeeDetails.getTerminationDate() != null & employeeDetails.getTerminationDate().trim().length() != 0){
			employee.setIsTerminatedBeforeEndOfYear(true);
		}		
		employee.setAtc(employeeDetails.getAtc());
		employee.setResidenceStatus(employeeDetails.getResidenceStatus());		
		employee.setRegionNumAssigned(employeeDetails.getRegionNumAssigned());
		employee.setSubstitutedFiling(employeeDetails.getSubstitutedFiling());	
		return employee;
	}
	
	public static Employer createEmployer(EmployerDetails employerDetails, Company company){
		Employer employer = new Employer();		
		employer.setTin(employerDetails.getTin());
		employer.setName(employerDetails.getCompanyName());
		employer.setRegisteredAdd(getAddress(employerDetails.getRegisteredAddress()).completeAddress());
		employer.setAddress(getAddress(employerDetails.getRegisteredAddress()));
		employer.setPayeesAvailingTax(employerDetails.isPayeesAvailingTax());
		employer.setSpecify(employerDetails.getSpecifySpecialLaw());
		employer.setRdoCode(employerDetails.getRdoCode());		
		employer.setLineOfBusiness(employerDetails.getLineOfBusiness());		
		employer.setTelNum(formatString(employerDetails.getTelNum()));
		employer.setMobileNo(formatString(employerDetails.getMobileNo()));
		employer.setWebsite(employerDetails.getWebsite());
		if(ReportCons.CATEGORY_PRIVATE.equalsIgnoreCase(employerDetails.getCategory())){
			employer.setCategory(Employer.CATEGORY.PRIVATE);
		}
		else if(ReportCons.CATEGORY_GOVERNMENT.equalsIgnoreCase(employerDetails.getCategory())){
			employer.setCategory(Employer.CATEGORY.GOVERNMENT);
		}					
		else{
			employer.setCategory(Employer.CATEGORY.DEFAULT);
		}
		
		// setting employer SSS category		
		if(ReportCons.SSS_CATEGORY_BUSINESS.equalsIgnoreCase(employerDetails.getSssCategory())){
			employer.setSssCategory(CATEGORY.BUSINESS);
		}
		else if(ReportCons.SSS_CATEGORY_HOUSEHOLD.equalsIgnoreCase(employerDetails.getSssCategory())){
			employer.setSssCategory(CATEGORY.HOUSEHOLD);
		}
		else{
			employer.setSssCategory(CATEGORY.DEFAULT);
		}				
		employer.setPhilHealthNumber(employerDetails.getPhilHealthNumber());		
		employer.setEmailAddress(employerDetails.getEmailAddress());			
		employer.setPagibigNumber(employerDetails.getPagibigNumber());
		employer.setSssNumber(employerDetails.getSssNumber());	
		employer.setRegisteredAddZipCode(getAddress(employerDetails.getRegisteredAddress()).getZipCode());
		
		// setting company payment detail
		PaymentDetail paymentDetail = new PaymentDetail();		
		paymentDetail.setAmount(ReportUtil.zeroIfNull(employerDetails.getSssPaymentAmount()).doubleValue());
		paymentDetail.setDate(ReportUtil.getFormattedDate(employerDetails.getSssPaymentDate(),"MMM dd, yyyy"));
		paymentDetail.setNumber(employerDetails.getSssPaymentNumber());		
		company.setPaymentDetail(paymentDetail);
		
		
		return employer;
	}

	public static void getEmployeeDataFromPayrollCompute(Employee employee, PayrollCompute payrollCompute, Company company){	
		if(employee == null | payrollCompute == null){
			return;
		}		
		// setting data used by reports
		// used by payroll summary
		employee.setBasicSalary(ReportUtil.zeroIfNull(payrollCompute.getBasicPay()).doubleValue());		
		
		
		employee.setLate(ReportUtil.zeroIfNull(payrollCompute.getLate()).doubleValue());		
		employee.setAbsences(ReportUtil.zeroIfNull(payrollCompute.getAbsent()).doubleValue());		
		employee.setOvertime(computeOvertimes(payrollCompute.getOvertimes())); 		
		employee.setNightShift(computeNightShiftDiff(payrollCompute.getOvertimes())); 	
		employee.setNightShiftOverTime(computeNightShiftOvertime(payrollCompute.getOvertimes()));
		if(payrollCompute.getLeaves() != null){
			employee.setVacationLeave(ReportUtil.zeroIfNull(payrollCompute.getLeaves().get(LeaveCodeEnum.VACATION_LEAVE.getVal())).doubleValue());		
			employee.setSickLeave(ReportUtil.zeroIfNull(payrollCompute.getLeaves().get(LeaveCodeEnum.SICK_LEAVE.getVal())).doubleValue());		
		}
		
		
		employee.setGrossPay(ReportUtil.zeroIfNull(payrollCompute.getGrossPay()).doubleValue());	
		if(payrollCompute.getGovernmentDeductions() != null){
			employee.setSssEmployeeShare(ReportUtil.zeroIfNull(payrollCompute.getGovernmentDeductions().get(PayslipTicketLabels.SSS.getVal())).doubleValue());		
			employee.setPagibigEEShare(ReportUtil.zeroIfNull(payrollCompute.getGovernmentDeductions().get(PayslipTicketLabels.HDMF.getVal())).doubleValue());		
			employee.setPersonalSharePhil(ReportUtil.zeroIfNull(payrollCompute.getGovernmentDeductions().get(PayslipTicketLabels.PHILHEALTH.getVal())).doubleValue());		
			employee.setWithholdingTax(ReportUtil.zeroIfNull(payrollCompute.getGovernmentDeductions().get(PayslipTicketLabels.WITHOLDING_TAX.getVal())).doubleValue());		
			employee.setOthers(ReportUtil.zeroIfNull(payrollCompute.getGovernmentDeductions().get(PayslipTicketLabels.OTHERS_TAXABLE_DEDUCTIONS.getVal())).doubleValue());		
		}
		
		employee.setNetPay(ReportUtil.zeroIfNull(payrollCompute.getNetPay()).doubleValue());		
		
		
		if(payrollCompute.getInternalDeductions() != null){
			employee.setComissary(ReportUtil.zeroIfNull(payrollCompute.getInternalDeductions().get(PayslipTicketLabels.COMISSARY.getVal())).doubleValue());		
			employee.setAllotment(ReportUtil.zeroIfNull(payrollCompute.getInternalDeductions().get(PayslipTicketLabels.ALLOTMENT.getVal())).doubleValue());		
			employee.setSssLoan(ReportUtil.zeroIfNull(payrollCompute.getInternalDeductions().get(PayslipTicketLabels.LOAN_KEY.getVal()+PayslipTicketLabels.SSS_LOAN.getVal())).doubleValue());		
			employee.setPagibigLoan(ReportUtil.zeroIfNull(payrollCompute.getInternalDeductions().get(PayslipTicketLabels.LOAN_KEY.getVal()+PayslipTicketLabels.HDMF_LOAN.getVal())).doubleValue());		
		}
		
		
		
		employee.setAmountToBeReceived(ReportUtil.zeroIfNull(payrollCompute.getTrueNetPay()).doubleValue());
		
		// for phil-health report
		employee.setMonthlySalaryBracket(getMonthlySalaryBracket(employee.getBasicSalary()));
	
		// used by bank register salary loan repayment report
		employee.setSalaryLoans(getEmployeeLoans(payrollCompute.getInternalDeductions()));
		
		// TODO set monthly compensation of employee (= basic_pay + COLA)
		employee.setMonthlyCompensation(ReportUtil.zeroIfNull(payrollCompute.getBasicPay()).doubleValue());
				
		
		// used by HDMF MCRF report
		getGovernmentDeductions(employee, payrollCompute.getGovernmentDeductions());
		
		// used by HDMF STLRF report
		getPagibigLoans(employee, payrollCompute.getInternalDeductions());
		
		// used by net pay register report
		employee.setNetPay(ReportUtil.zeroIfNull(payrollCompute.getTrueNetPay()).doubleValue());	
		
		// used by SSS R3 Text File report
		getSSSAmountsContri(employee,ReportUtil.zeroIfNull(payrollCompute.getBasicPay()));
		
		
		// setting payroll run type
		company.setPayrollRuntype(payrollCompute.getRunType());
	}
		
	public static MONTH getMonth(String date){
		Date payrollPeriod = null;
		try {
			payrollPeriod = new SimpleDateFormat("yyyyMM").parse(date);
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		
		 Calendar cal = Calendar.getInstance();
		 cal.setTime(payrollPeriod);
		 int month = cal.get(Calendar.MONTH)+1;  // plus 1 since cal.get() is zero based
	        switch(month){
	            case 1:
	                return MONTH.JANUARY; 
	            case 2:
	            	return MONTH.FEBRUARY; 
	            case 3:
	            	return MONTH.MARCH; 
	            case 4:
	            	return MONTH.APRIL;
	            case 5:
	            	return MONTH.MAY;
	            case 6:
	            	return MONTH.JUNE;
	            case 7:
	            	return MONTH.JULY;
	            case 8:
	            	return MONTH.AUGUST;
	            case 9:
	            	return MONTH.SEPTEMBER;
	            case 10:
	            	return MONTH.OCTOBER;
	            case 11:
	            	return MONTH.NOVEMBER;
	            case 12:
	            	return MONTH.DECEMBER;
	            default:
	            	return MONTH.DEFAULT;
	        } 
	    }
	
	private static Address getAddress(Map<String,String> addressMap) {	
		Address address = new Address();
		if(addressMap != null){
			address.setFloorNum(getAddressMapValue(addressMap,"floorNum"));
			address.setBuildingName(getAddressMapValue(addressMap,"buildingName"));
			address.setBlockNo(getAddressMapValue(addressMap,"blockNo"));
			address.setStreet(getAddressMapValue(addressMap,"street"));
			address.setBarangay(getAddressMapValue(addressMap,"barangay"));
			address.setSubdivision(getAddressMapValue(addressMap,"subdivision"));	
			address.setCity(getAddressMapValue(addressMap,"city"));
			address.setProvince(getAddressMapValue(addressMap,"province"));
			address.setCountry(getAddressMapValue(addressMap,"country"));
			address.setZipCode(getAddressMapValue(addressMap,ReportCons.ZIPCODE));
		}
		return address;	  
	}
	
	// for BIR form 2316
	private static Address getAddressNoCountryAndZipcode(Map<String,String> addressMap) {	
		Address address = new Address();
		if(addressMap != null){
			address.setFloorNum(getAddressMapValue(addressMap,"floorNum"));
			address.setBuildingName(getAddressMapValue(addressMap,"buildingName"));
			address.setBlockNo(getAddressMapValue(addressMap,"blockNo"));
			address.setStreet(getAddressMapValue(addressMap,"street"));
			address.setBarangay(getAddressMapValue(addressMap,"barangay"));
			address.setSubdivision(getAddressMapValue(addressMap,"subdivision"));	
			address.setCity(getAddressMapValue(addressMap,"city"));
			address.setProvince(getAddressMapValue(addressMap,"province"));			
		}
		return address;	  
	}
	
	private static String getAddressMapValue(Map<String,String> addressMap, String key){
		if(addressMap != null){
			if(addressMap.containsKey(key)){
				if(addressMap.get(key) == null){
					return "";
				}
				return addressMap.get(key);
			}	
		}
		return "";		
	}
	
	private static Map<String,String> getDependents(Map<String,Date> dependents){
		Map<String,String> dependentsNew = new HashMap<String,String>();
		if(dependents != null){
			for (Map.Entry<String, Date> entry : dependents.entrySet()){
				dependentsNew.put(entry.getKey(),ReportUtil.getFormattedDate(entry.getValue(),"MMddyyyy"));
			}
		}
		return dependentsNew;
	}
	
	 // removes unnecessary characters in a String
	private static String formatString(String str){
		 if(str == null){
			 return "";
		 }
		 String newString = str.replace("-","");
		 return newString.trim();
	 }
	 
	public static double computeOvertimes(Map<String, BigDecimal> overtimes){
		 double totalOvertimes = 0.0;
		 if(overtimes != null){
			 for(Map.Entry<String, BigDecimal> entry : overtimes.entrySet()){
				 if(entry.getKey().contains(TimeAndAttendanceEnum.OT.getVar()) & !entry.getKey().contains(TimeAndAttendanceEnum.NS.getVar())){
					 totalOvertimes = totalOvertimes + ReportUtil.zeroIfNull(entry.getValue()).doubleValue();
				 }
				 else if(entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.RD.getVar()) |
						 entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.SD.getVar()) |
						 entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.SDRD.getVar()) |
						 entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.RH.getVar()) |
						 entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.RHRD.getVar()) |
						 entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.DH.getVar()) |
						 entry.getKey().equalsIgnoreCase(TimeAndAttendanceEnum.DHRD.getVar())){
					 totalOvertimes = totalOvertimes + ReportUtil.zeroIfNull(entry.getValue()).doubleValue();
				 }
			 }
		 }		 
		 return totalOvertimes;		 
	 }
	 
	public static double computeNightShiftDiff(Map<String, BigDecimal> overtimes){
		 double totalNS = 0.0;
		 if(overtimes != null){
			 for(Map.Entry<String, BigDecimal> entry : overtimes.entrySet()){
				 if(entry.getKey().contains(TimeAndAttendanceEnum.NS.getVar()) & !entry.getKey().contains(TimeAndAttendanceEnum.OT.getVar())){
					 totalNS = totalNS + ReportUtil.zeroIfNull(entry.getValue()).doubleValue();
				 }
			 }
		 }		 
		 return totalNS;		 
	 }
	public static double computeNightShiftOvertime(Map<String, BigDecimal> overtimes){
		 double totalNSOT = 0.0;
		 if(overtimes != null){
			 for(Map.Entry<String, BigDecimal> entry : overtimes.entrySet()){
				 if(entry.getKey().contains(TimeAndAttendanceEnum.NS.getVar()) & entry.getKey().contains(TimeAndAttendanceEnum.OT.getVar())){
					 totalNSOT = totalNSOT + ReportUtil.zeroIfNull(entry.getValue()).doubleValue();
				 }
			 }
		 }		 
		 return totalNSOT;		 
	 }
	 
	private static  Map<String, BigDecimal> getEmployeeLoans(Map<String, BigDecimal> internalDeductions ){
		 Map<String, BigDecimal> employeeLoans = new HashMap<String, BigDecimal>();
		 if(internalDeductions != null){					
				for (Map.Entry<String, BigDecimal> entry : internalDeductions.entrySet()){
					String key = entry.getKey();
					String[] splittedkey = key.split(ReportCons.LOAN_KEY_SEPARATOR);
					if(splittedkey[0].equalsIgnoreCase(PayslipTicketLabels.LOAN_KEY.getVal()) || key.contains(PayslipTicketLabels.LOAN_KEY.getVal())){
					   if(splittedkey.length > 1){
						   employeeLoans.put(key.substring(PayslipTicketLabels.LOAN_KEY.getVal().length()+1), entry.getValue());						   
					   }
					   else{
						   employeeLoans.put(key, entry.getValue());
					   }					   
					   
				   }	
				}			
		 }
		 return employeeLoans;
	 }
	 
	private static void getGovernmentDeductions(Employee employee, Map<String, BigDecimal> governmentDeductions){
		 if(governmentDeductions != null){
			if(governmentDeductions.containsKey(PayslipTicketLabels.HDMF.getVal())){					
				employee.setPagibigEEShare(ReportUtil.zeroIfNull(governmentDeductions.get(PayslipTicketLabels.HDMF.getVal())).doubleValue());
				employee.setPagibigERShare(getEmployerShare(employee.getBasicSalary()));	
			}		
			
			if(governmentDeductions.containsKey(PayslipTicketLabels.PHILHEALTH.getVal())){					
				employee.setPersonalSharePhil(governmentDeductions.get(PayslipTicketLabels.PHILHEALTH.getVal()).doubleValue());
				employee.setEmployerSharePhil(employee.getPersonalSharePhil());				
			}	
		}		
	 }

	private static double getEmployerShare(double basicPay){
		if(basicPay <= 0){
			return 0.0;
		}
		if(basicPay >= 1500){
			return 100;
		}
		else{
			return basicPay*0.02;
		}		
	}

	private static void getPagibigLoans(Employee employee, Map<String, BigDecimal> internalDeductions){
		if(internalDeductions != null){	
			for (Map.Entry<String, BigDecimal> entry : internalDeductions.entrySet()){				
				   String key = entry.getKey();
				   String[] splittedkey = key.split("_");
				   if(splittedkey[0].equalsIgnoreCase(PayslipTicketLabels.LOAN_KEY.getVal()) || key.contains(PayslipTicketLabels.LOAN_KEY.getVal())){
					   if(splittedkey.length > 1){
						   if(splittedkey[1].toLowerCase().contains(ReportCons.PAGIBIG_LOAN_DEDUCTION_KEY)){ 										  
								if(entry.getValue() != null){
									String loanType = key.substring(PayslipTicketLabels.LOAN_KEY.getVal().length()+1);
									String pagibigLoanType = loanType.substring(ReportCons.PAGIBIG_LOAN_DEDUCTION_KEY.length()+1);																		
									employee.setLoanType(pagibigLoanType);											
									employee.setAmount(entry.getValue().doubleValue());	
								}
							}
					   }
						
				   }
			 }
		}
	}
	
	private static void getSSSAmountsContri(Employee employee, BigDecimal payBasic){
		MonetaryAmount basicPay = Money.of(payBasic, "PHP");				
		//add dummy amounts
		double sssSSAmount = GovernmentDeductionsUtilitiy
				.computeSSSContribution(basicPay).getNumber().doubleValue() +
				GovernmentDeductionsUtilitiy.computeSSSEmployerShare(basicPay)
				.getNumber().doubleValue();
		double sssECAmount = GovernmentDeductionsUtilitiy
				.computeSSSEmployerCompensation(basicPay)
				.getNumber().doubleValue();		
		employee.setSssECAmount(sssECAmount);
		employee.setSssSSAmount(sssSSAmount);	
		
	}

	public static void getCompensationDetails(TaxableCompensationIncome taxableCompensation,NonTaxableCompensationIncome nonTaxableCompensation, TaxSummary taxSummary, String runDateYear, Company company, Employee employee){		
		// declaration of variables
		double totalRepresentation = 0.0;
		double totalTransportation = 0.0;
		double totalFixedHousingAllow = 0.0;
		double totalCommission = 0.0;
		double totalProfitSharing = 0.0;
		double totalFees = 0.0;
		double totalHazardPay = 0.0;		
		double totalHolidayPay = 0.0;
		double totalExcessDeminimisBonuses = 0.0;
		double totalOvertimes = 0.0;
		double totalNigthShiftDiff = 0.0;		
		double totalBasicPay = 0.0;
		double totalCola = 0.0;		
		double totalDeminimis = 0.0;
		double total13thAndOtherBen = 0.0;
		double totalNonTaxableOtherComp = 0.0;
		double totalGovernmentDeductions = 0.0;
		double totalPremiumPaidOnHealth = 0.0;
		double totalTaxWithheldPresentEmployer = 0.0;
		double totalFringeBenefits = 0.0;
		double totalTaxWithheldFringeBenefits = 0.0;
		double totalGrossPay = 0.0;
		double totalLates = 0.0;
		double totalAbsences = 0.0;
		double totalNonTaxableIncome = 0.0;
		
		Map<String,Double> totalTaxableOthers = new HashMap<String,Double>();
		Map<String,Double> totalTaxableSuppOthers = new HashMap<String,Double>();		
		
		// TODO replace this by a query using payroll accessor if possible
		String query = "SELECT compensation_tax, pay_basic, late, absent, overtimes, compensation_non_tax, deduction_government, tax_withheld, pay_gross, deduction_tax FROM payroll_compute2 WHERE run_date IN ("+ getInclusiveRunDates(runDateYear)+") AND company_id = '"+company.getCompanyId()+"' AND employee_id = '"+employee.getID()+"';";
		//System.out.println("QUERY: "+query);
		ResultSet result = CassandraConnectionUtility.getTaxComputationDetail(query);	// TODO, use payroll compute accessor class
		
		while (!result.isExhausted()) {
			Row row = result.one();	
			if(row != null){
				Map<String,BigDecimal> compensationTaxable = row.getMap("compensation_tax",String.class,BigDecimal.class);
				Map<String,BigDecimal> compensationNonTaxable = row.getMap("compensation_non_tax",String.class,BigDecimal.class);
				Map<String,BigDecimal> governMentDeductions = row.getMap("deduction_government",String.class,BigDecimal.class);
				Map<String,BigDecimal> taxesWithheld = row.getMap("tax_withheld",String.class,BigDecimal.class);
				Map<String, BigDecimal> overtimes = row.getMap("overtimes",String.class,BigDecimal.class);
				Map<String, BigDecimal> deductionTax = row.getMap("deduction_tax",String.class,BigDecimal.class);

				// NONTAXABLE INCOMES
				if(compensationNonTaxable != null){
					totalDeminimis = totalDeminimis + zeroIfNull(compensationNonTaxable.get(PayslipTicketLabels.NONTAXABALE_DEMINIMIS.getVal())).doubleValue();
					total13thAndOtherBen = total13thAndOtherBen + zeroIfNull(compensationNonTaxable.get(PayslipTicketLabels.NONTAXABALE_THIRTEENTH_OTHER_BENEFITS.getVal())).doubleValue();
					totalNonTaxableOtherComp = totalNonTaxableOtherComp + zeroIfNull(compensationNonTaxable.get(PayslipTicketLabels.NONTAXABALE_OTHER_COMPENSATION.getVal())).doubleValue();				
				}
				
				totalLates = totalLates +zeroIfNull(row.getDecimal("late")).doubleValue();	
				totalAbsences = totalAbsences +zeroIfNull(row.getDecimal("absent")).doubleValue();	
				
				if(governMentDeductions != null){
					totalGovernmentDeductions = totalGovernmentDeductions + getTotalGovDeductions(governMentDeductions);
				}		
				
				// TAXABLE INCOMES
				if(compensationTaxable != null){
					totalRepresentation = totalRepresentation + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_REPRESENTATION.getVal())).doubleValue();
					totalTransportation = totalTransportation + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_TRANSPORTATION.getVal())).doubleValue();
					totalFixedHousingAllow = totalFixedHousingAllow + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_FIXED_HOUSING_ALLOWANCE.getVal())).doubleValue();
					totalCommission = totalCommission + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_COMMISSION.getVal())).doubleValue();
					totalProfitSharing = totalProfitSharing + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_PROFIT_SHARING.getVal())).doubleValue();
					totalFees = totalFees + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_FEES.getVal())).doubleValue();
					totalHazardPay = totalHazardPay + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_HAZARD_PAY.getVal())).doubleValue();
					totalHolidayPay = totalHolidayPay + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_HOLIDAY_PAY.getVal())).doubleValue();
					totalExcessDeminimisBonuses = totalExcessDeminimisBonuses + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_EXCESS_DEMINIMIS_BONUSES.getVal())).doubleValue();
					totalFringeBenefits = totalFringeBenefits + zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_FRINGE_BENEFIT.getVal())).doubleValue();
					totalCola = totalCola +zeroIfNull(compensationTaxable.get(PayslipTicketLabels.TAXABLE_COST_OF_LIVING_ALLOWANCE.getVal())).doubleValue();
				}
				
				// getting taxable others and supplementary others
				getTaxableOthers(totalTaxableOthers, totalTaxableSuppOthers, compensationTaxable);				
				
				
				// NONTAXABLE if MWE
				totalOvertimes = totalOvertimes + computeOvertimes(overtimes) + computeNightShiftOvertime(overtimes);
				totalNigthShiftDiff = totalNigthShiftDiff + computeNightShiftDiff(overtimes);
				totalBasicPay = totalBasicPay +zeroIfNull(row.getDecimal("pay_basic")).doubleValue();	
				
				// FOR TAX SUMMARY			
				if(taxesWithheld != null){
					totalTaxWithheldPresentEmployer = totalTaxWithheldPresentEmployer + getTotalMapValues(taxesWithheld);
					totalTaxWithheldFringeBenefits = totalTaxWithheldFringeBenefits + zeroIfNull(taxesWithheld.get(PayslipTicketLabels.WITHOLDING_TAX_FRINGE_BENFIT.getVal())).doubleValue();
				}
				if(deductionTax != null){
					totalPremiumPaidOnHealth = totalPremiumPaidOnHealth + getTotalMapValues(deductionTax);	
				}
				
				// FOR ALPHALIST
				totalGrossPay = totalGrossPay +zeroIfNull(row.getDecimal("pay_gross")).doubleValue();	
				if(compensationNonTaxable != null){
					totalNonTaxableIncome = totalNonTaxableIncome + getTotalMapValues(compensationNonTaxable);	
				}
			}	
		}			
		
		Alphalist alphalist = new Alphalist();		
		// getting previous employer details, should be done before computation of tax due since previous employer total taxable income is needed
		setPreviousEmployerDetails(employee, alphalist, company, taxSummary);

		// set employee thirteenth month pay
		if(ReportEnum.THIRTEENTH_MONTH_PAY_FORMULA.value().trim().equalsIgnoreCase("basic")){
			employee.setThirteenthMonthPay(employee.getBasicSalary());
		}
		else{
			// use handbook formula		
			employee.setThirteenthMonthPay((totalBasicPay - totalLates - totalAbsences)/12);
		}
		
		// adding 13th month pay in total13thAndOtherBen
		// assumption: 13th month pay is not included in total13thAndOtherBen map value in cassy table field 
		total13thAndOtherBen = total13thAndOtherBen + employee.getThirteenthMonthPay();
		
		// setting actual pay per month ( basic pay less absences and lates
		totalBasicPay = totalBasicPay - totalLates - totalAbsences - totalGovernmentDeductions;
				
		
		// setting taxable and non-taxable compensation of employees, considering if the employee is MWE
		if(Boolean.TRUE.equals(employee.getIsMinimumWageEarner())){			
			nonTaxableCompensation.setBasicSalarySMWE(totalBasicPay);
			nonTaxableCompensation.setHazardPayMWE(totalHazardPay);
			nonTaxableCompensation.setHolidayPayMWE(totalHolidayPay);
			taxableCompensation.setTaxable13thMonthAndOtherBenefits(totalExcessDeminimisBonuses+totalFringeBenefits);
			nonTaxableCompensation.setNightShiftDiffMWE(totalNigthShiftDiff);
			nonTaxableCompensation.setOvertimePayMWE(totalOvertimes);
		
		}
		else{			
			taxableCompensation.setBasicSalary(totalBasicPay);
			taxableCompensation.setHazarddPay(totalHazardPay);
			taxableCompensation.setTaxable13thMonthAndOtherBenefits(totalHolidayPay+totalExcessDeminimisBonuses+totalFringeBenefits);
			taxableCompensation.setOvertimePay(totalOvertimes+totalNigthShiftDiff);		
		}
		
		taxableCompensation.setRepresentation(totalRepresentation);
		taxableCompensation.setTransportation(totalTransportation);
		taxableCompensation.setFixedHousingAllow(totalFixedHousingAllow);
		taxableCompensation.setCommission(totalCommission);
		taxableCompensation.setProfitSharing(totalProfitSharing);
		taxableCompensation.setFees(totalFees);		
		taxableCompensation.setCostOfLivingAllowance(totalCola);
		taxableCompensation.setTaxableOthers(totalTaxableOthers);
		taxableCompensation.setSupplementaryOthers(totalTaxableSuppOthers);		
		
		nonTaxableCompensation.setDeMinimis(totalDeminimis);
		nonTaxableCompensation.setThirteenthMonthAndOtherBenefits(total13thAndOtherBen);
		nonTaxableCompensation.setOtherFormsOfCompensation(totalNonTaxableOtherComp);
		nonTaxableCompensation.setContributionsAndUnionDues(totalGovernmentDeductions);
		
		// for tax summary values
		taxSummary.setPremiumsPaidOnHealth(totalPremiumPaidOnHealth);
		taxSummary.setTaxWithheldPresentEmployer(totalTaxWithheldPresentEmployer);
		taxSummary.setTotalExemptions(employee.getTaxExemption());			
		if(Boolean.TRUE.equals(employee.getIsMinimumWageEarner())){			
			taxSummary.setTotalExemptions(0.0);	
		}
		
			
		// compute net taxable compensation and tax due
		double netTaxableCompensation = taxableCompensation.getTotal() + taxSummary.getTaxableIncomeFromPreviousEmployer()-taxSummary.getTotalExemptions()
				-totalPremiumPaidOnHealth;		
		taxSummary.setTaxDue(computeTaxDue(netTaxableCompensation));
		taxSummary.setTotalTaxesWithheldAsAdjusted(taxSummary.getTaxDue()-alphalist.getTaxWithheldJanToNovPrevious());
		
		// creating and adding employee to alphalist
		// TODO separate as one method
			
		alphalist.setTin(employee.getTin());
		alphalist.setFirstName(employee.getFirstName());	
		alphalist.setLastName(employee.getLastName());
		alphalist.setMiddleName(employee.getMiddleName());
		alphalist.setAddress(employee.getRegisteredAdd());
		alphalist.setAmountOfFringeBenefit(totalFringeBenefits);		
		alphalist.setTaxWithheldFringe(totalTaxWithheldFringeBenefits);
		alphalist.setRankAndFile(employee.getIsRankAndFile());
		alphalist.setTerminatedBeforeEndOfYear(employee.getIsTerminatedBeforeEndOfYear());
		alphalist.setGrossCompensationIncomePresent(totalGrossPay+totalNonTaxableIncome); 
		alphalist.setTaxWithheldJanToNovPresent(totalTaxWithheldPresentEmployer);
		
		alphalist.setPresentEmployerTaxable(taxableCompensation);
		alphalist.setPremiumPaidOnHealth(totalPremiumPaidOnHealth);		
		alphalist.setNetTaxableCompensationIncome(netTaxableCompensation);
		if(netTaxableCompensation <  0){
			alphalist.setNetTaxableCompensationIncome(0.0);
		}
		
		alphalist.setTaxDue(taxSummary.getTaxDue());
		alphalist.setMinimumWageEarner(employee.getIsMinimumWageEarner());
		alphalist.setPresentEmployerNonTaxable(nonTaxableCompensation);
		alphalist.setFactorUsed(((employee.getSmwRatePerMonth()*10.0)/(employee.getSmwRatePerDay()*10.0))*12); //TODO,  number of days in a year
		alphalist.setSubstitutedFiling(employee.getSubstitutedFiling());		
		alphalist.setAtc(employee.getAtc());
		alphalist.setResidenceStatus(employee.getResidenceStatus());
		alphalist.setExemptionCode(employee.getExemptionCode());
		alphalist.setExemptionAmount(employee.getTaxExemption());
		if(Boolean.TRUE.equals(employee.getIsMinimumWageEarner())){			
			alphalist.setExemptionAmount(0.0);
		}
		alphalist.setRegionNumber(ReportCons.REGION_NUMBER.get(company.getCompanyDetails().getAddress().getProvince().trim().toUpperCase()));
		
		
		company.addAlphalist(alphalist);
	}
		
	private static String getInclusiveRunDates(String runDateYear){
		String inclusiveRunDates ="'"+runDateYear+"01'," +
				"'"+runDateYear+"02'," +
				"'"+runDateYear+"03'," +
				"'"+runDateYear+"04'," +
				"'"+runDateYear+"05'," +
				"'"+runDateYear+"06'," +
				"'"+runDateYear+"07'," +
				"'"+runDateYear+"08'," +
				"'"+runDateYear+"09'," +
				"'"+runDateYear+"10'," +
				"'"+runDateYear+"11'," +
				"'"+runDateYear+"12'" ;
		return inclusiveRunDates;
	}

	private static void getTaxableOthers(Map<String,Double> totalTaxableOthers,Map<String,Double> totalTaxableSuppOthers, Map<String,BigDecimal> compensationTaxable){
		if (compensationTaxable != null){
			for(Map.Entry<String,BigDecimal> entry : compensationTaxable.entrySet()){
				String key = entry.getKey();
				double value = zeroIfNull(entry.getValue()).doubleValue();
				
				// for taxable others
				if(key.startsWith(PayslipTicketLabels.TAXABLE_OTHERS.getVal())){					
					String taxableNameKey = key.substring(PayslipTicketLabels.TAXABLE_OTHERS.getVal().length(), key.length());
					
					// adding in the taxable others map (currently max of 2 entries, the rest will not be displayed in reports)
					if(totalTaxableOthers.containsKey(taxableNameKey)){
						totalTaxableOthers.put(taxableNameKey, totalTaxableOthers.get(taxableNameKey)+value);
					}
					else{
						totalTaxableOthers.put(taxableNameKey, value);
					}
				}
				
				// for taxable others
				if(key.startsWith(PayslipTicketLabels.TAXABLE_SUPP_OTHERS.getVal())){					
					String taxableNameKey = key.substring(PayslipTicketLabels.TAXABLE_SUPP_OTHERS.getVal().length(), key.length());
					
					// adding in the taxable others map (currently max of 2 entries, the rest will not be displayed in reports)
					if(totalTaxableSuppOthers.containsKey(taxableNameKey)){
						totalTaxableSuppOthers.put(taxableNameKey, totalTaxableSuppOthers.get(taxableNameKey)+value);
					}
					else{
						totalTaxableSuppOthers.put(taxableNameKey, value);
					}					
				}
			}
		}
		
	}
	
	public static double getTotalGovDeductions(Map<String,BigDecimal> map){
		double totalValue = 0.0;
		if(map != null){			
			for(Map.Entry<String,BigDecimal> entry : map.entrySet()){
				if(entry.getKey().equalsIgnoreCase(PayslipTicketLabels.WITHOLDING_TAX.getVal())){ // TODO to be deleted later, once it was already removed in the map of government deductions
					continue;
				}
				totalValue = totalValue + zeroIfNull(entry.getValue()).doubleValue();
			}
		}
		return totalValue;
	}
	
	public static double getTotalMapValues(Map<String,BigDecimal> map){
		double totalValue = 0.0;
		if(map != null){			
			for(Map.Entry<String,BigDecimal> entry : map.entrySet()){				
				totalValue = totalValue + zeroIfNull(entry.getValue()).doubleValue();
			}
		}
		return totalValue;
	}

	// tax computation as of May 2017,should be replaced later by the 
	// new approved tax computation, see commented method below
	private static double computeTaxDue(double netTaxableCompensation){
		double taxDue = 0.0;
		if(netTaxableCompensation < 0){
			return taxDue;
		}
		
		if(netTaxableCompensation <= 10000){
			taxDue = netTaxableCompensation * 0.05;
		}
		else if(netTaxableCompensation > 10000 & netTaxableCompensation <= 30000){
			taxDue = 500+ (netTaxableCompensation-10000) * 0.1;
		}
		else if(netTaxableCompensation > 30000 & netTaxableCompensation <= 70000){
			taxDue = 2500+ (netTaxableCompensation-30000) * 0.15;
		}
		else if(netTaxableCompensation > 70000 & netTaxableCompensation <= 140000){
			taxDue = 8500+ (netTaxableCompensation-70000) * 0.2;
		}
		else if(netTaxableCompensation > 140000 & netTaxableCompensation <= 250000){
			taxDue = 22500+ (netTaxableCompensation-140000) * 0.25;
		}
		else if(netTaxableCompensation > 250000 & netTaxableCompensation <=500000){
			taxDue = 50000+ (netTaxableCompensation-250000) * 0.3;
		}
		else {
			taxDue = 125000+ (netTaxableCompensation-500000) * 0.32;
		}
		return taxDue;		
	}
	

	// newly approved tax computation
	// uncomment this and comment the previous method (same name) to use this
	/*private static double computeTaxDue(double netTaxableCompensation){
		double taxDue = 0.0;
		if(netTaxableCompensation < 0){
			return taxDue;
		}
		
		if(netTaxableCompensation <= 10000){
			taxDue = netTaxableCompensation * 0.05;
		}
		else if(netTaxableCompensation > 10000 & netTaxableCompensation <= 30000){
			taxDue = 500+ (netTaxableCompensation-10000) * 0.1;
		}
		else if(netTaxableCompensation > 30000 & netTaxableCompensation <= 70000){
			taxDue = 2500+ (netTaxableCompensation-30000) * 0.15;
		}
		else if(netTaxableCompensation > 70000 & netTaxableCompensation <= 140000){
			taxDue = 8500+ (netTaxableCompensation-70000) * 0.2;
		}
		else if(netTaxableCompensation > 140000 & netTaxableCompensation <= 250000){
			taxDue = 22500+ (netTaxableCompensation-140000) * 0.25;
		}
		else if(netTaxableCompensation > 250000 & netTaxableCompensation <=500000){
			taxDue = 50000+ (netTaxableCompensation-250000) * 0.3;
		}
		else {
			taxDue = 125000+ (netTaxableCompensation-500000) * 0.32;
		}
		return taxDue;		
	}*/
	
	public static String getReportFrequency(String reportName){
		List<String> monthlyReports = Arrays.asList("Bank Register Loan Repayment",
				"BIR Form 1601C",
				"HDMF MCRF",
				"HDMF STLRF",
				"Net Pay Register For Bank",
				"Payroll Register",
				"Philhealth RF1",
				"SSS R3 Form",
				"SSS R3 Text File",
				"SSS R5 Form"); 
		List<String> annualReports = Arrays.asList("Alphalist",
				"BIR Form 2316"); 
		
		for(String str : annualReports){
			if(str.trim().equalsIgnoreCase(reportName.trim())){
				return ReportCons.ANNUAL_REPORT;
			}
		}
		
		for(String str : monthlyReports){
			if(str.trim().equalsIgnoreCase(reportName.trim())){
				return ReportCons.MONTHLY_REPORT;
			}
		}
		
		
		// use this for case sensitive input
		/*
		if(annualReports.contains(reportName)){
			return ANNUAL_REPORT;
		}
		
		if(monthlyReports.contains(reportName)){
			return MONTHLY_REPORT;
		}*/
		
		
		
		return null;
	}
	
	public static boolean isNullOrEmpty(String str){
		if(str == null || str.trim().length() == 0){
			return true;
		}
		return false;
	}

	private static int getMonthlySalaryBracket(double basicPay){
		int bracket = 1;	// if basic pay is less than 9000
		for(int i=2; i<=28;i++){			
			if(basicPay >= (9000+((i-2)*1000)) && basicPay < (9000+((i-1)*1000))){
				bracket = i;
				break;
			}
			// if basic pay is greater than 35000
			if(i == 28){
				if(basicPay >= (9000+((i-2)*1000))){
					bracket = i;
				}
			}			
		}		
		return bracket;
	}

	// removes unnecessary character of a String
	// needed for proper formatting and display of data in reports
	private static String filterString(String str){
		if(str == null | "N/A".equalsIgnoreCase(str)){
			return "";
		}	
		else{
			return str.replace("-","").replace(" ", "").trim();
		}
	}
	
	private static double computeTaxExemption(String taxCode){		
		double taxExemption = 50000.0;
		
		if(taxCode == null | taxCode.trim().length() == 0){
			return taxExemption; // default exemption when tax code was not set properly, can be modified later
		}
		
		taxCode = convertJewelmerTaxCode(taxCode); // moving forward, is it still needed?
		
		if(ReportCons.TAX_CODE_S.equalsIgnoreCase(taxCode.trim()) | ReportCons.TAX_CODE_ME.equalsIgnoreCase(taxCode.trim())){
			taxExemption = 50000;
		}
		else if(ReportCons.TAX_CODE_S1.equalsIgnoreCase(taxCode.trim()) | ReportCons.TAX_CODE_ME1.equalsIgnoreCase(taxCode.trim())){
			taxExemption = 75000;
		}
		else if(ReportCons.TAX_CODE_S2.equalsIgnoreCase(taxCode.trim()) | ReportCons.TAX_CODE_ME2.equalsIgnoreCase(taxCode.trim())){
			taxExemption = 100000;
		}
		else if(ReportCons.TAX_CODE_S3.equalsIgnoreCase(taxCode.trim()) | ReportCons.TAX_CODE_ME3.equalsIgnoreCase(taxCode.trim())){
			taxExemption = 125000;
		}
		else if(ReportCons.TAX_CODE_S4.equalsIgnoreCase(taxCode.trim()) | ReportCons.TAX_CODE_ME4.equalsIgnoreCase(taxCode.trim())){
			taxExemption = 150000;
		}
		else if(ReportCons.TAX_CODE_Z.equalsIgnoreCase(taxCode.trim())){
			taxExemption = 0.0; // zero exemption, personal and additional exemptions claimed on the main employer
		}		
		
		return taxExemption;
	}
	
	// TODO, to be deleted later?
	private static String convertJewelmerTaxCode(String taxCode){		
		String taxCodeRes  = taxCode;
		switch (taxCode.toUpperCase().trim()) {
		 	  case "SMHF":
				   taxCodeRes = ReportCons.TAX_CODE_ME; // or S
				   break;	
			  case "MHF1":
				   taxCodeRes = ReportCons.TAX_CODE_ME1; // or S1
				   break;
			  case "MHF2":
				   taxCodeRes = ReportCons.TAX_CODE_ME2; // or S2
				   break;
			  case "MHF3":
				   taxCodeRes = ReportCons.TAX_CODE_ME3; // or S3
				   break;
			  case "MHF4":
				   taxCodeRes = ReportCons.TAX_CODE_ME4; // or S4
				   break;		   
		}
		return taxCodeRes;
	}
	
	/**
	 * Validates the input file if it is a valid excel file based on extension (xls or xlsx).
	 * <p>
	 * @param uploadedInputStream  The input stream being uploaded.
	 * @param fileDetail Some detail about the input stream file being uploaded.
	 * @return Workbook if it is a valid excel file. Otherwise, returns null.
	 * @throws IOException 
	 */
	public static Workbook validateExcel(InputStream uploadedInputStream, FormDataContentDisposition fileDetail) throws IOException{
		String extension = "";
		if(fileDetail.getFileName() != null){
			extension = FilenameUtils.getExtension(fileDetail.getFileName());
		}
		// create a workbook based on excel file version
		Workbook workbook = null;			
		if(extension.equalsIgnoreCase("xls")){				
			workbook =  new HSSFWorkbook(uploadedInputStream);
			
		}
		else if(extension.equalsIgnoreCase("xlsx")){
			workbook  = new XSSFWorkbook(uploadedInputStream);			
		}		
		return workbook ;		
	}

	/**
	 * Gets the cell value as a String.
	 * @param cell The cell where the value is to be retrieved.
	 * @return The value of the cell.
	 */
	public static String getCellValue(Cell cell){
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell.getStringCellValue().trim();
	}
	
	public static void insertRowToCassy(org.apache.poi.ss.usermodel.Row row, String runDate, String companyID){		
		// settinf the mapper
		Mapper<PreviousEmployerDetails> mapper = CassandraConnectionUtility.getManager().mapper(PreviousEmployerDetails.class);
		
		// creating the object
		PreviousEmployerDetails prev = new PreviousEmployerDetails();
		prev = createPreviousEmployerDetails(row, runDate, companyID);
		
		// inserting the object to cassy table
		if(!(prev.getEmployeeID() == null || prev.getEmployeeID().trim().length() == 0)){
			mapper.save(prev);
		}
		
	}
	
	public static PreviousEmployerDetails createPreviousEmployerDetails(org.apache.poi.ss.usermodel.Row row,String runDate, String companyID){
		PreviousEmployerDetails previousEmployer = new PreviousEmployerDetails();
		previousEmployer.setRunDate(runDate);
		previousEmployer.setCompanyID(companyID);
		
		// employee id
		if(!isNullOrEmpty(row.getCell(MasterFileCons.EMPLOYEE_ID_COL_INDEX))){
			previousEmployer.setEmployeeID(getCellValue(row.getCell(MasterFileCons.EMPLOYEE_ID_COL_INDEX)));
		}
		else{
			// return immediately if employee id is not properly defined
			return previousEmployer;
		}
		
		// is employee MWE?
		boolean employeeIsMWE = false;
		if(!isNullOrEmpty(row.getCell(MasterFileCons.EMPLOYEE_IS_MWE))){
			String isMWE = getCellValue(row.getCell(MasterFileCons.EMPLOYEE_IS_MWE));
			if(isMWE.trim().equalsIgnoreCase("YES")){
				employeeIsMWE = true;
			}
		}
		
		// employer tin
		if(!isNullOrEmpty(row.getCell(MasterFileCons.TIN_COL_INDEX))){
			previousEmployer.setTin(getCellValue(row.getCell(MasterFileCons.TIN_COL_INDEX)));
		}
		
		// employer name
		if(!isNullOrEmpty(row.getCell(MasterFileCons.NAME_COL_INDEX))){
			previousEmployer.setName(getCellValue(row.getCell(MasterFileCons.NAME_COL_INDEX)));
		}
		
		// employer address
		if(!isNullOrEmpty(row.getCell(MasterFileCons.ADDRESS_COL_INDEX))){
			previousEmployer.setAddress(getCellValue(row.getCell(MasterFileCons.ADDRESS_COL_INDEX)));
		}
		
		// employer zipcode
		if(!isNullOrEmpty(row.getCell(MasterFileCons.ZIPCODE_COL_INDEX))){
			previousEmployer.setZipCode(getCellValue(row.getCell(MasterFileCons.ZIPCODE_COL_INDEX)));
		}
		
		// employee total tax withheld from employer
		if(!isNullOrEmpty(row.getCell(MasterFileCons.TAX_WITHHELD_COL_INDEX))){			
			previousEmployer.setTaxWithheld(parseToBigDecimal(row.getCell(MasterFileCons.TAX_WITHHELD_COL_INDEX)));			
		}
		
		// getting non taxable and taxable details
		Map<String, BigDecimal> nonTaxableCompensations = new HashMap<String, BigDecimal>();
		Map<String, BigDecimal> taxableCompensations = new HashMap<String, BigDecimal>();
		
		// FOR NON-TAXABLE DETAILS
		// 13th month pay and other
		addToPreviousEmployerIncome(row, MasterFileCons.THIRTEENTH_MONTH_AND_OTHER_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_THIRTEENTH_OTHER_BENEFITS.getVal());
			
		// deminimis
		addToPreviousEmployerIncome(row, MasterFileCons.DEMINIMIS_BENEFITS_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_DEMINIMIS.getVal());
		
		// SSS, GSIS and other contributions
		addToPreviousEmployerIncome(row, MasterFileCons.SSS_GSIS_AND_OTHERS_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_SSS_GSIS_AND_OTHERS.getVal());

		// salaries and other forms of non taxable compensation
		addToPreviousEmployerIncome(row, MasterFileCons.SALARIES_AND_OTHER_FORMS_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_OTHER_COMPENSATION.getVal());
		
		// FOR TAXABLE DETAILS					
		// 13th month pay and other
		addToPreviousEmployerIncome(row, MasterFileCons.THIRTEENTH_MONTH_AND_OTHER_TAXABLE_COL_INDEX, taxableCompensations, PayslipTicketLabels.TAXABLE_EXCESS_DEMINIMIS_BONUSES.getVal());
		
		// salaries and other forms of non taxable compensation
		addToPreviousEmployerIncome(row, MasterFileCons.SALARIES_AND_OTHER_FORMS_TAXABLE_COL_INDEX, taxableCompensations, PayslipTicketLabels.TAXABLE_SALARIES_AND_OTHER_FORMS.getVal());
	
		// getting other info depending on whether an employee is MWE or not
		if(employeeIsMWE){			
			// FOR NONTAXABLE DETAILS
			// basic mwe
			addToPreviousEmployerIncome(row, MasterFileCons.BASIC_SALARY_MWE_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_BASIC_MWE.getVal());

			// holiday mwe
			addToPreviousEmployerIncome(row, MasterFileCons.HOLIDAY_PAY_MWE_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_HOLIDAY_PAY_MWE.getVal());
			
			// overtime mwe
			addToPreviousEmployerIncome(row, MasterFileCons.OVERTIME_PAY_MWE_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_OVERTIME_PAY_MWE.getVal());
			
			// night shift differential mwe
			addToPreviousEmployerIncome(row, MasterFileCons.NIGHT_DIFF_PAY_MWE_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_NIGHT_DIFF_PAY_MWE.getVal());
			
			// hazard pay mwe
			addToPreviousEmployerIncome(row, MasterFileCons.HAZARD_PAY_MWE_COL_INDEX, nonTaxableCompensations, PayslipTicketLabels.NONTAXABALE_HAZARD_PAY_MWE.getVal());
	
		}
		else{			
			// FOR TAXABLE DETAILS
			// basic salary
			addToPreviousEmployerIncome(row, MasterFileCons.BASIC_SALARY_TAXABLE_COL_INDEX, taxableCompensations, PayslipTicketLabels.TAXABLE_BASIC_SALARY.getVal());
		}
		
		// setting taxable and non-taxable incomes
		previousEmployer.setNonTaxableCompensations(nonTaxableCompensations);
		previousEmployer.setTaxableCompensations(taxableCompensations);
		
		return previousEmployer;
	}
	
	/**
	 * Checks if the cell is empty, null or trailing spaces.
	 * 
	 * @param cell The cell to be checked.
	 * @return TRUE if the cell is empty, null or trailing spaces. Otherwise, returns FALSE.
	 */
	private static boolean isNullOrEmpty(Cell cell){		
		if(cell == null){
			return true;
		}
		if(cell.getCellType() == Cell.CELL_TYPE_BLANK){
			return true;
		}
		
		cell.setCellType(Cell.CELL_TYPE_STRING);		
		if(cell.getStringCellValue().trim().length() == 0){
			return true;
		}		
		
		return false;
	}

	private static BigDecimal parseToBigDecimal(Cell cell){
		String value = getCellValue(cell);
		try {
			double valueDouble = Double.parseDouble(value);			
			BigDecimal taxWithheld = BigDecimal.valueOf(valueDouble);
			return taxWithheld;
		} catch (NumberFormatException e) {
			return BigDecimal.ZERO;
		}
	}

	private static void addToPreviousEmployerIncome(org.apache.poi.ss.usermodel.Row row, int rowIndex, Map<String,BigDecimal> incomeMap, String mapKey){
		if(!isNullOrEmpty(row.getCell(rowIndex))){
			BigDecimal value = parseToBigDecimal(row.getCell(rowIndex));
			if(value != BigDecimal.ZERO){
				incomeMap.put(mapKey,value);
			}
		}	
	}

	/*
	private static void getPreviousEmployerDetailsOld(Alphalist alphalist, String runDateYear, Company company, Employee employee, TaxSummary taxSummary){
		
		Result<PreviousEmployerDetails> previousEmployerDetailsList = CassandraConnectionUtility.getUserAccessor().getPrevEmployerData(runDateYear, company.getCompanyId(), employee.getID());
		PreviousEmployerDetails previousEmployer = previousEmployerDetailsList.one();
		if(previousEmployer != null){
			alphalist.setWithPreviousEmployer(true);
			
			// for Alphalist reports
			// for non-taxable
			Map<String, BigDecimal> nonTaxable = previousEmployer.getNonTaxableCompensations();
			NonTaxableCompensationIncome previousEmployerNonTaxable = new NonTaxableCompensationIncome();
			previousEmployerNonTaxable.setThirteenthMonthAndOtherBenefits(getMapValue(PayslipTicketLabels.NONTAXABALE_THIRTEENTH_OTHER_BENEFITS.getVal(),nonTaxable));
			previousEmployerNonTaxable.setDeMinimis(getMapValue(PayslipTicketLabels.NONTAXABALE_DEMINIMIS.getVal(),nonTaxable));
			previousEmployerNonTaxable.setContributionsAndUnionDues(getMapValue(PayslipTicketLabels.NONTAXABALE_SSS_GSIS_AND_OTHERS.getVal(),nonTaxable));
			previousEmployerNonTaxable.setOtherFormsOfCompensation(getMapValue(PayslipTicketLabels.NONTAXABALE_OTHER_COMPENSATION.getVal(),nonTaxable));
				
			
			// for taxable
			Map<String, BigDecimal> taxable = previousEmployer.getTaxableCompensations();
			TaxableCompensationIncome previousEmployerTaxable = new TaxableCompensationIncome();
			previousEmployerTaxable.setBasicSalary(getMapValue(PayslipTicketLabels.TAXABLE_BASIC_SALARY.getVal(),taxable));
			previousEmployerTaxable.setSalaryAndOtherCompenstion(getMapValue(PayslipTicketLabels.TAXABLE_SALARIES_AND_OTHER_FORMS.getVal(),taxable));			
			previousEmployerTaxable.setTaxable13thMonthAndOtherBenefits(getMapValue(PayslipTicketLabels.TAXABLE_EXCESS_DEMINIMIS_BONUSES.getVal(),taxable));		
			
			// for minimum wage earners			
			previousEmployerNonTaxable.setBasicSalarySMWE(getMapValue(PayslipTicketLabels.NONTAXABALE_BASIC_MWE.getVal(),nonTaxable));
			previousEmployerNonTaxable.setHolidayPayMWE(getMapValue(PayslipTicketLabels.NONTAXABALE_HOLIDAY_PAY_MWE.getVal(),nonTaxable));
			previousEmployerNonTaxable.setOvertimePayMWE(getMapValue(PayslipTicketLabels.NONTAXABALE_OVERTIME_PAY_MWE.getVal(),nonTaxable));
			previousEmployerNonTaxable.setNightShiftDiffMWE(getMapValue(PayslipTicketLabels.NONTAXABALE_NIGHT_DIFF_PAY_MWE.getVal(),nonTaxable));
			previousEmployerNonTaxable.setHazardPayMWE(getMapValue(PayslipTicketLabels.NONTAXABALE_HAZARD_PAY_MWE.getVal(),nonTaxable));
			
			// TODO determine minimum wage earners from HFILES or what? an replace below code
			if(previousEmployerNonTaxable.getBasicSalarySMWE() > 0){
				alphalist.setMinimumWageEarner(true);
				employee.setIsMinimumWageEarner(true);
			}
			
			alphalist.setPreviousEmployerNonTaxable(previousEmployerNonTaxable);	
			alphalist.setPreviousEmployerTaxable(previousEmployerTaxable);		
			
			alphalist.setTaxWithheldJanToNovPrevious(zeroIfNull(previousEmployer.getTaxWithheld()).doubleValue());
			alphalist.setGrossCompensationIncomePresent(previousEmployerNonTaxable.getTotal()+
					previousEmployerTaxable.getBasicSalary()+
					previousEmployerTaxable.getTaxable13thMonthAndOtherBenefits()+
					previousEmployerTaxable.getSalaryAndOtherCompenstion());
			
			
			alphalist.setGrossCompensationIncomePrevious(previousEmployerNonTaxable.getTotal()+
					previousEmployerTaxable.getBasicSalary()+
					previousEmployerTaxable.getTaxable13thMonthAndOtherBenefits()+
					previousEmployerTaxable.getSalaryAndOtherCompenstion());
			
			// for BIR form 2316 report
			Employer prevEmployer = new Employer();		 			
			prevEmployer.setName(previousEmployer.getName());
			prevEmployer.setRegisteredAdd(previousEmployer.getAddress());
			prevEmployer.setRegisteredAddZipCode(previousEmployer.getZipCode());
			prevEmployer.setTin(previousEmployer.getTin());				
			employee.setPreviousEmployer(prevEmployer);
			taxSummary.setTaxWithheldPreviousEmployer(zeroIfNull(previousEmployer.getTaxWithheld()).doubleValue());
			taxSummary.setTaxableIncomeFromPreviousEmployer(previousEmployerTaxable.getBasicSalary()+
					previousEmployerTaxable.getTaxable13thMonthAndOtherBenefits()+previousEmployerTaxable.getSalaryAndOtherCompenstion());
	
		
		}			
		else{
			alphalist.setWithPreviousEmployer(false);
		}		
	}
	*/
	private static double getMapValue(String key, Map<String, Double> map){		
		double value = 0.0;
		if(map != null){
			if(map.containsKey(key)){
				value = map.get(key).doubleValue();
			}
		}
		return value;
	}

	private static void setPreviousEmployerDetails(Employee employee, Alphalist alphalist, Company company, TaxSummary taxSummary){
		
		if(company.getPreviousEmployersList() != null){
			
			System.out.println("Employee ID: "+employee.getID());
			if(company.getPreviousEmployersList().containsKey(employee.getID())){
				PreviousEmployer prevEmployer = new PreviousEmployer();
				prevEmployer =	company.getPreviousEmployersList().get(employee.getID());
				
				employee.setHasPreviousEmployer(true);
				
				// for BIR form 2316 report
				Employer prevEmp = new Employer();		 			
				prevEmp.setName(prevEmployer.getCompleteName());
				prevEmp.setRegisteredAdd(prevEmployer.getRegAdd());
				prevEmp.setRegisteredAddZipCode(prevEmployer.getZipCode());
				prevEmp.setTin(prevEmployer.getTin());				
				employee.setPreviousEmployer(prevEmp);
				taxSummary.setTaxWithheldPreviousEmployer(prevEmployer.getTaxWthHld());
				taxSummary.setTaxableIncomeFromPreviousEmployer(prevEmployer.getTaxable().get(PreviousEmployerJsonCons.TAXABALE_BASIC_SALARY)+
						prevEmployer.getTaxable().get(PreviousEmployerJsonCons.TAXABALE_THIRTEENTH_OTHER_BENEFITS)+prevEmployer.getTaxable().get(PreviousEmployerJsonCons.TAXABALE_OTHER_COMPENSATION));
						
				// for Alphalist reports
				alphalist.setWithPreviousEmployer(true);
				// for non-taxable
				Map<String, Double> nonTaxable = prevEmployer.getNonTaxable();
				NonTaxableCompensationIncome previousEmployerNonTaxable = new NonTaxableCompensationIncome();
				previousEmployerNonTaxable.setThirteenthMonthAndOtherBenefits(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_THIRTEENTH_OTHER_BENEFITS,nonTaxable));
				previousEmployerNonTaxable.setDeMinimis(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_DEMINIMIS,nonTaxable));
				previousEmployerNonTaxable.setContributionsAndUnionDues(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_SSS_GSIS_AND_OTHERS,nonTaxable));
				previousEmployerNonTaxable.setOtherFormsOfCompensation(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_OTHER_COMPENSATION,nonTaxable));
									
				// for taxable
				Map<String, Double> taxable = prevEmployer.getTaxable();
				TaxableCompensationIncome previousEmployerTaxable = new TaxableCompensationIncome();
				previousEmployerTaxable.setBasicSalary(getMapValue(PreviousEmployerJsonCons.TAXABALE_BASIC_SALARY,taxable));
				previousEmployerTaxable.setSalaryAndOtherCompenstion(getMapValue(PreviousEmployerJsonCons.TAXABALE_OTHER_COMPENSATION,taxable));			
				previousEmployerTaxable.setTaxable13thMonthAndOtherBenefits(getMapValue(PreviousEmployerJsonCons.TAXABALE_THIRTEENTH_OTHER_BENEFITS,taxable));		
				
				// for minimum wage earners			
				previousEmployerNonTaxable.setBasicSalarySMWE(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_BASIC_MWE,nonTaxable));
				previousEmployerNonTaxable.setHolidayPayMWE(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_HOLIDAY_PAY_MWE,nonTaxable));
				previousEmployerNonTaxable.setOvertimePayMWE(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_OVERTIME_PAY_MWE,nonTaxable));
				previousEmployerNonTaxable.setNightShiftDiffMWE(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_NIGHT_DIFF_PAY_MWE,nonTaxable));
				previousEmployerNonTaxable.setHazardPayMWE(getMapValue(PreviousEmployerJsonCons.NONTAXABALE_HAZARD_PAY_MWE,nonTaxable));
								
				alphalist.setPreviousEmployerNonTaxable(previousEmployerNonTaxable);	
				alphalist.setPreviousEmployerTaxable(previousEmployerTaxable);		
				
				alphalist.setTaxWithheldJanToNovPrevious(prevEmployer.getTaxWthHld());				
				
				alphalist.setGrossCompensationIncomePrevious(previousEmployerNonTaxable.getTotal()+
						previousEmployerTaxable.getBasicSalary()+
						previousEmployerTaxable.getTaxable13thMonthAndOtherBenefits()+
						previousEmployerTaxable.getSalaryAndOtherCompenstion());
				
			}
		}
	
		
	}
	
}
