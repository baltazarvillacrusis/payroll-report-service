package com.svi.payroll.reports.object.cassandraDAO;

import java.math.BigDecimal;
import java.util.Map;

/*import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
*/
//@Table(keyspace = "svi_payroll", name = "previous_employer_details", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class PreviousEmployerDetails {
	
	//@PartitionKey(0)
	//@Column(name="run_date")
	private String runDate = "";
	
	//@PartitionKey(1)
	//@Column(name="company_id")
	private String companyID = "";
	
	//@PartitionKey(2)
	//@Column(name="employee_id")
	private String employeeID = "";
	
	//@Column(name="employer_tin")
	private String tin;	
	
	
	//@Column(name="employer_name")
	private String name;
	
	//@Column(name="employer_address")
	private String address;
	
	//@Column(name="employer_zipcode")
	private String zipCode;
	
	//@Column(name="tax_withheld")
	private BigDecimal taxWithheld;
	

	//@Column(name = "nontaxable_income")
	private Map<String, BigDecimal> nonTaxableCompensations;
	
	//@Column(name = "taxable_income")
	private Map<String, BigDecimal> taxableCompensations;

	
	
	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public String getCompanyID() {
		return companyID;
	}

	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(String tin) {
		this.tin = tin;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public BigDecimal getTaxWithheld() {
		return taxWithheld;
	}

	public void setTaxWithheld(BigDecimal taxWithheld) {
		this.taxWithheld = taxWithheld;
	}

	public Map<String, BigDecimal> getNonTaxableCompensations() {
		return nonTaxableCompensations;
	}

	public void setNonTaxableCompensations(
			Map<String, BigDecimal> nonTaxableCompensations) {
		this.nonTaxableCompensations = nonTaxableCompensations;
	}

	public Map<String, BigDecimal> getTaxableCompensations() {
		return taxableCompensations;
	}

	public void setTaxableCompensations(Map<String, BigDecimal> taxableCompensations) {
		this.taxableCompensations = taxableCompensations;
	}
	

}
