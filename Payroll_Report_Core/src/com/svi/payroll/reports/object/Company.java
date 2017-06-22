package com.svi.payroll.reports.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.svi.payroll.report.objects.Alphalist;
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.report.objects.Employer;
import com.svi.payroll.report.objects.PaymentDetail;

public class Company {

	private String companyId;
	private List<String> employeeIdList = new ArrayList<String>();
	private List <Employee>  employeeList = new ArrayList<Employee>();
	private Employer  companyDetails = new Employer();
	private PaymentDetail paymentDetail = new PaymentDetail();		
	private String payrollRuntype = "";
	private List <Alphalist> alphaList = new ArrayList <Alphalist> ();	
	private String version = "";
	private Map<String,PreviousEmployer> previousEmployersList = new HashMap<String,PreviousEmployer>(); // key is the employee id
	
	
	public void addEmployeeId(String employeeID){
		employeeIdList.add(employeeID);
	}
	
	public void addEmployee(Employee employee){
		employeeList.add(employee);
	}
	
	// getters and setters
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public List<String> getEmployeeIdList() {
		return employeeIdList;
	}
	public void setEmployeeIdList(List<String> employeeIdList) {
		this.employeeIdList = employeeIdList;
	}
	
	
	public List<Employee> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}

	public Employer getCompanyDetails() {
		return companyDetails;
	}

	public void setCompanyDetails(Employer companyDetails) {
		this.companyDetails = companyDetails;
	}

	public PaymentDetail getPaymentDetail() {
		return paymentDetail;
	}

	public void setPaymentDetail(PaymentDetail paymentDetail) {
		this.paymentDetail = paymentDetail;
	}

	public String getPayrollRuntype() {
		return payrollRuntype;
	}

	public void setPayrollRuntype(String payrollRuntype) {
		this.payrollRuntype = payrollRuntype;
	}

	public List<Alphalist> getAlphaList() {
		return alphaList;
	}

	public void setAlphaList(List<Alphalist> alphaList) {
		this.alphaList = alphaList;
	}
	
	public void addAlphalist(Alphalist alphalist){
		this.alphaList.add(alphalist);
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Map<String, PreviousEmployer> getPreviousEmployersList() {
		return previousEmployersList;
	}

	public void setPreviousEmployersList(
			Map<String, PreviousEmployer> previousEmployersList) {
		this.previousEmployersList = previousEmployersList;
	}
}