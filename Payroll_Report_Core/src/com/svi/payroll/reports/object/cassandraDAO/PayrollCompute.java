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

@Table(keyspace = "svi_payroll", name = "payroll_compute2", caseSensitiveKeyspace = false, caseSensitiveTable = false)
public class PayrollCompute {

	@PartitionKey(0)
	@Column(name = "run_date")
	private String runDate;	

	@PartitionKey(1)
	@Column(name = "company_id")
	private String companyId;

	@ClusteringColumn
	@Column(name = "employee_id")
	private String employeeId;

	@Column(name = "run_type")
	private String runType;

	private BigDecimal absent;

	@Column(name = "rerun_at")
	private Date reRunAt;

	@Column(name = "pay_basic")
	private BigDecimal basicPay;

	@Column(name = "late")
	private BigDecimal late;
	
	@Column(name = "leaves")
	private Map<String, BigDecimal> leaves;

	@Column(name = "overtimes")
	private Map<String, BigDecimal> overtimes;

	@Column(name = "compensation_tax")
	private Map<String, BigDecimal> taxableCompensations;

	@Column(name = "deduction_tax")
	private Map<String, BigDecimal> taxableDeductions;

	@Column(name = "pay_gross")
	private BigDecimal grossPay;

	@Column(name = "deduction_government")
	private Map<String, BigDecimal> governmentDeductions;

	@Column(name = "tax_withheld")
	private Map<String, BigDecimal> taxesWithheld;

	@Column(name = "compensation_non_tax")
	private Map<String, BigDecimal> nonTaxableCompensations;

	@Column(name = "pay_net")
	private BigDecimal netPay;

	@Column(name = "compensation_net_pay")
	private Map<String, BigDecimal> internalCompensations;

	@Column(name = "deduction_net_pay")
	private Map<String, BigDecimal> internalDeductions;

	@Column(name = "pay_net_true")
	private BigDecimal trueNetPay;
	

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employee_id) {
		this.employeeId = employee_id;
	}

	public String getRunType() {
		return runType;
	}

	public void setRunType(String run_type) {
		this.runType = run_type;
	}

	public BigDecimal getAbsent() {
		return absent;
	}

	public void setAbsent(BigDecimal absent) {
		this.absent = absent;
	}


	public BigDecimal getBasicPay() {
		return basicPay;
	}

	public void setBasicPay(BigDecimal pay_basic) {
		this.basicPay = pay_basic;
	}

	public BigDecimal getLate() {
		return late;
	}

	public void setLate(BigDecimal late) {
		this.late = late;
	}

	public Map<String, BigDecimal> getLeaves() {
		return leaves;
	}

	public void setLeaves(Map<String, BigDecimal> leaves) {
		this.leaves = leaves;
	}

	public Map<String, BigDecimal> getOvertimes() {
		return overtimes;
	}

	public void setOvertimes(Map<String, BigDecimal> overtimes) {
		this.overtimes = overtimes;
	}

	public Map<String, BigDecimal> getTaxableCompensations() {
		return taxableCompensations;
	}

	public void setTaxableCompensations(Map<String, BigDecimal> compensation_tax) {
		this.taxableCompensations = compensation_tax;
	}

	public Map<String, BigDecimal> getTaxableDeductions() {
		return taxableDeductions;
	}

	public void setTaxableDeductions(Map<String, BigDecimal> deduction_tax) {
		this.taxableDeductions = deduction_tax;
	}

	public BigDecimal getGrossPay() {
		return grossPay;
	}

	public void setGrossPay(BigDecimal pay_gross) {
		this.grossPay = pay_gross;
	}

	public Map<String, BigDecimal> getGovernmentDeductions() {
		return governmentDeductions;
	}

	public void setGovernmentDeductions(
			Map<String, BigDecimal> deduction_government) {
		this.governmentDeductions = deduction_government;
	}

	public Map<String, BigDecimal> getTaxesWithheld() {
		return taxesWithheld;
	}

	public void setTaxesWithheld(Map<String, BigDecimal> tax_withheld) {
		this.taxesWithheld = tax_withheld;
	}

	public Map<String, BigDecimal> getNonTaxableCompensations() {
		return nonTaxableCompensations;
	}

	public void setNonTaxableCompensations(
			Map<String, BigDecimal> compensation_non_tax) {
		this.nonTaxableCompensations = compensation_non_tax;
	}

	public BigDecimal getNetPay() {
		return netPay;
	}

	public void setNetPay(BigDecimal pay_net) {
		this.netPay = pay_net;
	}

	public Map<String, BigDecimal> getInternalCompensations() {
		return internalCompensations;
	}

	public void setInternalCompensations(
			Map<String, BigDecimal> compensation_net_pay) {
		this.internalCompensations = compensation_net_pay;
	}

	public Map<String, BigDecimal> getInternalDeductions() {
		return internalDeductions;
	}

	public void setInternalDeductions(Map<String, BigDecimal> deduction_net_pay) {
		this.internalDeductions = deduction_net_pay;
	}

	public BigDecimal getTrueNetPay() {
		return trueNetPay;
	}

	public void setTrueNetPay(BigDecimal pay_net_true) {
		this.trueNetPay = pay_net_true;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String company_id) {
		this.companyId = company_id;
	}
	
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

	public String getRunDate() {
		return runDate;
	}

	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}

	public Date getReRunAt() {
		return reRunAt;
	}

	public void setReRunAt(Date reRunAt) {
		this.reRunAt = reRunAt;
	}

	

}
