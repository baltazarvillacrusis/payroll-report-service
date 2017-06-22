package com.svi.payroll.reports.object.cassandraDAO;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface PayrollComputeAccessor {

	// accessing payroll_compute2 table
	@Query("SELECT * FROM payroll_compute2 WHERE run_date = :run_date AND company_id = :company_id")
	Result<PayrollCompute> getPayrollRun(@Param("run_date") String runAt,
			@Param("company_id") String companyId);

	@Query("SELECT DISTINCT run_date,company_id FROM payroll_compute2;")
	Result<PayrollCompute> getPayrollRunDateCompanyList();

	@Query("SELECT employee_id FROM payroll_compute2 WHERE run_date = :run_date AND company_id = :company_id")
	Result<PayrollCompute> getCompanyEmployeeIds(@Param("run_date") String runAt,
			@Param("company_id") String companyId);
	
	@Query("SELECT pay_basic FROM payroll_compute2 WHERE run_date = :run_date AND company_id = :company_id AND employee_id = :employee_id")
	Result<PayrollCompute> getBasicPay(@Param("run_date") String runAt,
			@Param("company_id") String companyId,
			@Param("employee_id") String employeeId);	
	
	@Query("SELECT compensation_non_tax,tax_withheld,deduction_government,deduction_tax FROM payroll_compute2 WHERE run_date = :run_date AND company_id = :company_id")
	Result<PayrollCompute> getNonTaxableCompensation(@Param("run_date") String runAt,
			@Param("company_id") String companyId);		

	// accessing employee_details2 table
	@Query("SELECT * FROM employee_details2 WHERE company_id = :company_id AND cutoff_date = :cutoff_date AND version = :version")
	Result<EmployeeDetails> getEmployeeDetails(@Param("company_id") String companyId,
			@Param("cutoff_date") String cutoffDate, 
			@Param("version") String version);
	
	@Query("SELECT * FROM employee_details2 WHERE company_id = :company_id  AND cutoff_date = :cutoff_date AND version = :version AND employee_id = :employee_id")
	Result<EmployeeDetails> getEmployeeDetails(@Param("company_id") String companyId, 
			@Param("cutoff_date") String cutoffDate, 
			@Param("version") String version,
			@Param("employee_id") String employee_id);
	
	@Query("SELECT  DISTINCT company_id,cutoff_date,version FROM employee_details2;")
	Result<EmployeeDetails> getVersions();
		

	// accessing employer_details table
	@Query("SELECT * FROM employer_details WHERE company_id = :company_id")
	Result<EmployerDetails> getEmployerDetails(
			@Param("company_id") String companyId);
	
	// accessing previous_employer_details table
	/*@Query("DELETE FROM previous_employer_details WHERE run_date = :run_date AND company_id = :company_id")
	void deletePrevEmployerData(@Param("run_date") String runAt,
			@Param("company_id") String companyId);			
	
	@Query("SELECT * FROM previous_employer_details WHERE run_date = :run_date AND company_id = :company_id AND employee_id = :employee_id")
	Result<PreviousEmployerDetails> getPrevEmployerData(@Param("run_date") String runAt,
			@Param("company_id") String companyId,
			@Param("employee_id") String employeeId);
*/

}
