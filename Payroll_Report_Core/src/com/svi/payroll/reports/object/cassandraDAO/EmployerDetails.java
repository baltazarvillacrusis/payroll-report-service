package com.svi.payroll.reports.object.cassandraDAO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "svi_payroll", name = "employer_details", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class EmployerDetails {
	
	@PartitionKey(0)
	@Column(name="company_id")
	private String companyId = "";
	
	@Column(name="tin")
	private String tin = "";	
	
	@Column(name="company_name")
	private String companyName = "";
	
	@Column(name="sss_category")
	private String sssCategory = "";
	
	
	@Column(name="registered_address")
	private Map<String,String> registeredAddress;	
	
	@Column(name="payees_availing_tax")
	private boolean payeesAvailingTax;	
	
	@Column(name="specify_special_law")
	private String specifySpecialLaw = "";	
	
	@Column(name="rdo_code")
	private String rdoCode = "";	
	
	@Column(name="line_of_business")
	private String lineOfBusiness = "";
	
	@Column(name="tel_num")
	private String telNum = "";	
	
	@Column(name="mobile_no")
	private String mobileNo = "";	
	
	@Column(name="website")
	private String website = "";	
	
	@Column(name="category")
	private String category = "";	
	
	@Column(name="philhealth_num")
	private String philHealthNumber = "";	
	
	@Column(name="email_address")
	private String emailAddress = "";	
	
	@Column(name="pagibig_num")
	private String pagibigNumber = "";	
	
	@Column(name="sss_num")
	private String sssNumber = "";
	
	@Column(name="sss_payment_num")
	private String sssPaymentNumber = "";
	
	@Column(name="sss_payment_amount")
	private BigDecimal sssPaymentAmount;
	
	@Column(name="sss_payment_date")
	private Date sssPaymentDate;

	//getters and setters
	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public Map<String, String> getRegisteredAddress() {
		return registeredAddress;
	}

	public void setRegisteredAddress(Map<String, String> registeredAddress) {
		this.registeredAddress = registeredAddress;
	}

	public boolean isPayeesAvailingTax() {
		return payeesAvailingTax;
	}

	public void setPayeesAvailingTax(boolean payeesAvailingTax) {
		this.payeesAvailingTax = payeesAvailingTax;
	}

	public String getSpecifySpecialLaw() {
		return specifySpecialLaw;
	}

	public void setSpecifySpecialLaw(String specifySpecialLaw) {
		this.specifySpecialLaw = specifySpecialLaw;
	}

	public String getRdoCode() {
		return rdoCode;
	}

	public void setRdoCode(String rdoCode) {
		this.rdoCode = rdoCode;
	}

	public String getLineOfBusiness() {
		return lineOfBusiness;
	}

	public void setLineOfBusiness(String lineOfBusiness) {
		this.lineOfBusiness = lineOfBusiness;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getPhilHealthNumber() {
		return philHealthNumber;
	}

	public void setPhilHealthNumber(String philHealthNumber) {
		this.philHealthNumber = philHealthNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPagibigNumber() {
		return pagibigNumber;
	}

	public void setPagibigNumber(String pagibigNumber) {
		this.pagibigNumber = pagibigNumber;
	}

	public String getSssNumber() {
		return sssNumber;
	}

	public void setSssNumber(String sssNumber) {
		this.sssNumber = sssNumber;
	}

	public String getSssCategory() {
		return sssCategory;
	}

	public void setSssCategory(String sssCategory) {
		this.sssCategory = sssCategory;
	}

	public String getSssPaymentNumber() {
		return sssPaymentNumber;
	}

	public void setSssPaymentNumber(String sssPaymentNumber) {
		this.sssPaymentNumber = sssPaymentNumber;
	}

	public BigDecimal getSssPaymentAmount() {
		return sssPaymentAmount;
	}

	public void setSssPaymentAmount(BigDecimal sssPaymentAmount) {
		this.sssPaymentAmount = sssPaymentAmount;
	}

	public Date getSssPaymentDate() {
		return sssPaymentDate;
	}

	public void setSssPaymentDate(Date sssPaymentDate) {
		this.sssPaymentDate = sssPaymentDate;
	}	


}
