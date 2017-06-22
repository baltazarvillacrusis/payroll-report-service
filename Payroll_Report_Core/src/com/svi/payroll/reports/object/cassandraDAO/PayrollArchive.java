package com.svi.payroll.reports.object.cassandraDAO;

import java.util.Date;

import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(keyspace = "svi_payroll", name = "payroll_archive",

caseSensitiveKeyspace = false,
caseSensitiveTable = false)
public class PayrollArchive {

	@PartitionKey(0)
	private Date run_date;
	
	private String company_id;
	
	private String employee_id;
	
	private Date  rerun_at;
	
	private String data;

	public Date getRun_date() {
		return run_date;
	}

	public String getCompany_id() {
		return company_id;
	}

	public String getEmployee_id() {
		return employee_id;
	}

	public Date getRerun_at() {
		return rerun_at;
	}

	public String getData() {
		return data;
	}

	public void setRun_date(Date run_date) {
		this.run_date = run_date;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}

	public void setRerun_at(Date rerun_at) {
		this.rerun_at = rerun_at;
	}

	public void setData(String data) {
		this.data = data;
	}

	
	
}
