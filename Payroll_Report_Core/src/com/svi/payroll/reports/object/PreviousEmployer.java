package com.svi.payroll.reports.object;

import java.util.Map;



public class PreviousEmployer {
	
	private String tin;

	private String completeName;

	private String regAdd;

	private String zipCode;
	
	private double taxWthHld;

	private Map<String, Double> nonTaxable;

	private Map<String, Double> taxable;
	
	
	
	public String getTin() {
		return tin;
	}
	public String getCompleteName() {
		return completeName;
	}
	public String getRegAdd() {
		return regAdd;
	}
	public String getZipCode() {
		return zipCode;
	}
	public Map<String, Double> getNonTaxable() {
		return nonTaxable;
	}
	public Map<String, Double> getTaxable() {
		return taxable;
	}
	public void setTin(String tin) {
		this.tin = tin;
	}
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}
	public void setRegAdd(String regAdd) {
		this.regAdd = regAdd;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public void setNonTaxable(Map<String, Double> nonTaxable) {
		this.nonTaxable = nonTaxable;
	}
	public void setTaxable(Map<String, Double> taxable) {
		this.taxable = taxable;
	}
	public double getTaxWthHld() {
		return taxWthHld;
	}
	public void setTaxWthHld(double taxWthHld) {
		this.taxWthHld = taxWthHld;
	}
	
	
}