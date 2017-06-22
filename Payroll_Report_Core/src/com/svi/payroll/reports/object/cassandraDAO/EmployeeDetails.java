package com.svi.payroll.reports.object.cassandraDAO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.svi.payroll.reports.util.forms.ReportUtil;

@Table(keyspace = "svi_payroll", name = "employee_details2", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class EmployeeDetails {
	
	@PartitionKey(0)
	@Column(name="company_id")
	private String companyId = "";	
	
	@PartitionKey(1)
	@Column(name="cutoff_date")
	private String cutOffDate = "";	
	
	@PartitionKey(2)
	@Column(name="version")
	private String version = "";	
	
	@ClusteringColumn
	@Column(name="employee_id")
	private String employeeID = "";	
	
	@Column(name="tin")
	private String tin = "";	
	
	@Column(name="termination_date")
	private String terminationDate = "";	
	
	@Column(name="first_name")
	private String firstName = "";	
	
	@Column(name="middle_name")
	private String middleName = "";
	
	@Column(name="last_name")
	private String lastName = "";	
	
	@Column(name="extension_name")
	private String extensionName = "";	
	
	@Column(name="rdo_code")
	private String rdoCode = "";			
	
	@Column(name="registered_address")
	private Map<String,String> registeredAddress;
	
	@Column(name="local_address")
	private Map<String,String> localAddress;	
	
	@Column(name="foreign_address")
	private Map<String,String> foreignAddress;	
	
	@Column(name="birth_date")
	private Date birthDate;
	
	@Column(name="tel_num")
	private String telNum = "";	
	
	@Column(name="is_single")
	private boolean single;	
	
	@Column(name="is_wife_claiming_exemption")
	private boolean wifeClaimingExemption;
	
	@Column(name="is_rank_and_file")
	private boolean rankAndFile;	
	

	@Column(name="atc")
	private String atc = "";	

	@Column(name="residence_status")
	private String residenceStatus = "";	
	
	@Column(name="tax_code")
	private String exemptionCode = "";		
	
	
	@Column(name="smw_rate_per_day")
	private BigDecimal smwRatePerDay;	
	
	@Column(name="smw_rate_per_month")
	private BigDecimal smwRatePerMonth;	
	
	@Column(name="is_minimum_wage_earner")
	private boolean minimumWageEarner;	
	
	@Column(name="previous_employer")
	private Map<String,String> previousEmployer;	
	
	@Column(name="ctc_num")
	private String ctcNum = "";	
	
	@Column(name="ctc_place_of_issue")
	private String ctcPlaceOfIssue = "";	
	
	@Column(name="ctc_date_of_issue")
	private Date ctcDateOfIssue;	
	
	@Column(name="ctc_amount_paid")
	private BigDecimal ctcAmountPaid;	
	
	@Column(name="dependents")
	private Map<String,Date> dependents;	
	
	@Column(name="sex")
	private String sex = "";	
	
	@Column(name="monthly_salary_bracket")
	private int monthlySalaryBracket;	
	
	@Column(name="philhealth_status")
	private String philhealthStatus = "";	
	
	@Column(name="effectivity_date_philhealth_status")
	private Date effectivityDatePhilhealthStatus;	
	
	@Column(name="pin")
	private String pin = "";	
	
	@Column(name="pagibig_mid_number")
	private String pagibigMIDNumber = "";	
	
	@Column(name="pagibig_acccount_number")
	private String pagibigAcccountNumber = "";	
	
	@Column(name="pagibig_membership_program")
	private String pagibigMembershipProgram = "";	
	
	@Column(name="pagibig_period_covered")
	private Date pagibigPeriodCovered;	
	
	@Column(name="pagibig_remark")
	private String pagibigRemark = "";	
	
	@Column(name="application_num")
	private String applicationNum = "";	
	
	@Column(name="cola")
	private BigDecimal cola;	
	
	@Column(name="bank_acct_num")
	private String bankAcctNum = "";	
	
	@Column(name="bank_name")
	private String bankName = "";	
	
	@Column(name="pagibig_ee_share")
	private BigDecimal pagibigEEShare;	
	
	@Column(name="pagibig_er_share")
	private BigDecimal pagibigERShare;
	
	@Column(name="sss_num")
	private String sssNum = "";
	
	@Column(name="tax_exemption")
	private BigDecimal taxExemption;	
	
	@Column(name="region_number_assigned")
	private String regionNumAssigned = "";
	
	@Column(name="is_substituted_filing")
	private Boolean substitutedFiling;
	
	
	// getters and setters	

	@Override
	public String toString() {

		ObjectMapper mapper = new ObjectMapper();

		try {

			// Convert object to JSON string
			String jsonInString = mapper.writeValueAsString(this);
			return jsonInString;

		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "";

	}


	public String getCompanyId() {
		return companyId;
	}


	public String getEmployeeID() {
		return employeeID;
	}


	public String getTin() {
		return tin;
	}


	public String getFirstName() {
		return firstName;
	}


	public String getMiddleName() {
		return middleName;
	}


	public String getLastName() {
		return lastName;
	}


	public String getExtensionName() {
		return extensionName;
	}


	public String getRdoCode() {
		return rdoCode;
	}


	public Map<String, String> getRegisteredAddress() {
		return registeredAddress;
	}


	public Map<String, String> getLocalAddress() {
		return localAddress;
	}


	public Map<String, String> getForeignAddress() {
		return foreignAddress;
	}


	public Date getBirthDate() {
		return birthDate;
	}


	public String getTelNum() {
		return telNum;
	}


	public boolean isSingle() {
		return single;
	}


	public boolean isWifeClaimingExemption() {
		return wifeClaimingExemption;
	}


	public BigDecimal getSmwRatePerDay() {
		return ReportUtil.zeroIfNull(smwRatePerDay);
	}


	public BigDecimal getSmwRatePerMonth() {
		return ReportUtil.zeroIfNull(smwRatePerMonth);
	}


	public boolean isMinimumWageEarner() {
		return minimumWageEarner;
	}



	public Map<String, String> getPreviousEmployer() {
		return previousEmployer;
	}


	public String getCtcNum() {
		return ctcNum;
	}


	public String getCtcPlaceOfIssue() {
		return ctcPlaceOfIssue;
	}


	public Date getCtcDateOfIssue() {
		return ctcDateOfIssue;
	}


	public BigDecimal getCtcAmountPaid() {
		return ReportUtil.zeroIfNull(ctcAmountPaid);
	}


	public Map<String, Date> getDependents() {
		return dependents;
	}


	public String getSex() {
		return sex;
	}


	public int getMonthlySalaryBracket() {
		return monthlySalaryBracket;
	}


	public String getPhilhealthStatus() {
		return philhealthStatus;
	}


	public Date getEffectivityDatePhilhealthStatus() {
		return effectivityDatePhilhealthStatus;
	}


	public String getPin() {
		return pin;
	}


	public String getPagibigMIDNumber() {
		return pagibigMIDNumber;
	}


	public String getPagibigAcccountNumber() {
		return pagibigAcccountNumber;
	}


	public String getPagibigMembershipProgram() {
		return pagibigMembershipProgram;
	}


	public Date getPagibigPeriodCovered() {
		return pagibigPeriodCovered;
	}


	public String getPagibigRemark() {
		return pagibigRemark;
	}


	public String getApplicationNum() {
		return applicationNum;
	}


	public BigDecimal getCola() {
		return ReportUtil.zeroIfNull(cola);
	}


	public String getBankAcctNum() {
		return bankAcctNum;
	}


	public String getBankName() {
		return bankName;
	}


	public BigDecimal getPagibigEEShare() {
		return ReportUtil.zeroIfNull(pagibigEEShare);
	}


	public BigDecimal getPagibigERShare() {
		return ReportUtil.zeroIfNull(pagibigERShare);
	}


	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}


	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}


	public void setTin(String tin) {
		this.tin = tin;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public void setExtensionName(String extensionName) {
		this.extensionName = extensionName;
	}


	public void setRdoCode(String rdoCode) {
		this.rdoCode = rdoCode;
	}


	public void setRegisteredAddress(Map<String, String> registeredAddress) {
		this.registeredAddress = registeredAddress;
	}


	public void setLocalAddress(Map<String, String> localAddress) {
		this.localAddress = localAddress;
	}


	public void setForeignAddress(Map<String, String> foreignAddress) {
		this.foreignAddress = foreignAddress;
	}


	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}


	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}


	public void setSingle(boolean single) {
		this.single = single;
	}


	public void setWifeClaimingExemption(boolean wifeClaimingExemption) {
		this.wifeClaimingExemption = wifeClaimingExemption;
	}


	public void setSmwRatePerDay(BigDecimal smwRatePerDay) {
		this.smwRatePerDay = smwRatePerDay;
	}


	public void setSmwRatePerMonth(BigDecimal smwRatePerMonth) {
		this.smwRatePerMonth = smwRatePerMonth;
	}


	public void setMinimumWageEarner(boolean minimumWageEarner) {
		this.minimumWageEarner = minimumWageEarner;
	}

	public void setPreviousEmployer(Map<String, String> previousEmployer) {
		this.previousEmployer = previousEmployer;
	}


	public void setCtcNum(String ctcNum) {
		this.ctcNum = ctcNum;
	}


	public void setCtcPlaceOfIssue(String ctcPlaceOfIssue) {
		this.ctcPlaceOfIssue = ctcPlaceOfIssue;
	}


	public void setCtcDateOfIssue(Date ctcDateOfIssue) {
		this.ctcDateOfIssue = ctcDateOfIssue;
	}


	public void setCtcAmountPaid(BigDecimal ctcAmountPaid) {
		this.ctcAmountPaid = ctcAmountPaid;
	}


	public void setDependents(Map<String, Date> dependents) {
		this.dependents = dependents;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public void setMonthlySalaryBracket(int monthlySalaryBracket) {
		this.monthlySalaryBracket = monthlySalaryBracket;
	}


	public void setPhilhealthStatus(String philhealthStatus) {
		this.philhealthStatus = philhealthStatus;
	}


	public void setEffectivityDatePhilhealthStatus(Date effectivityDatePhilhealthStatus) {
		this.effectivityDatePhilhealthStatus = effectivityDatePhilhealthStatus;
	}


	public void setPin(String pin) {
		this.pin = pin;
	}


	public void setPagibigMIDNumber(String pagibigMIDNumber) {
		this.pagibigMIDNumber = pagibigMIDNumber;
	}


	public void setPagibigAcccountNumber(String pagibigAcccountNumber) {
		this.pagibigAcccountNumber = pagibigAcccountNumber;
	}


	public void setPagibigMembershipProgram(String pagibigMembershipProgram) {
		this.pagibigMembershipProgram = pagibigMembershipProgram;
	}


	public void setPagibigPeriodCovered(Date pagibigPeriodCovered) {
		this.pagibigPeriodCovered = pagibigPeriodCovered;
	}


	public void setPagibigRemark(String pagibigRemark) {
		this.pagibigRemark = pagibigRemark;
	}


	public void setApplicationNum(String applicationNum) {
		this.applicationNum = applicationNum;
	}


	public void setCola(BigDecimal cola) {
		this.cola = cola;
	}


	public void setBankAcctNum(String bankAcctNum) {
		this.bankAcctNum = bankAcctNum;
	}


	public void setBankName(String bankName) {
		this.bankName = bankName;
	}


	public void setPagibigEEShare(BigDecimal pagibigEEShare) {
		this.pagibigEEShare = pagibigEEShare;
	}


	public void setPagibigERShare(BigDecimal pagibigERShare) {
		this.pagibigERShare = pagibigERShare;
	}


	public String getSssNum() {
		return sssNum;
	}


	public void setSssNum(String sssNum) {
		this.sssNum = sssNum;
	}


	public String getCutOffDate() {
		return cutOffDate;
	}


	public void setCutOffDate(String cutOffDate) {
		this.cutOffDate = cutOffDate;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public BigDecimal getTaxExemption() {
		return taxExemption;
	}


	public void setTaxExemption(BigDecimal taxExemption) {
		this.taxExemption = taxExemption;
	}


	public boolean isRankAndFile() {
		return rankAndFile;
	}


	public void setRankAndFile(boolean rankAndFile) {
		this.rankAndFile = rankAndFile;
	}


	public String getTerminationDate() {
		return terminationDate;
	}


	public void setTerminationDate(String terminationDate) {
		this.terminationDate = terminationDate;
	}


	public String getAtc() {
		return atc;
	}


	public void setAtc(String atc) {
		this.atc = atc;
	}


	public String getResidenceStatus() {
		return residenceStatus;
	}


	public void setResidenceStatus(String residenceStatus) {
		this.residenceStatus = residenceStatus;
	}


	public String getExemptionCode() {
		return exemptionCode;
	}


	public void setExemptionCode(String exemptionCode) {
		this.exemptionCode = exemptionCode;
	}


	public String getRegionNumAssigned() {
		return regionNumAssigned;
	}


	public void setRegionNumAssigned(String regionNumAssigned) {
		this.regionNumAssigned = regionNumAssigned;
	}


	public Boolean getSubstitutedFiling() {
		return substitutedFiling;
	}


	public void setSubstitutedFiling(Boolean substitutedFiling) {
		this.substitutedFiling = substitutedFiling;
	}



}
