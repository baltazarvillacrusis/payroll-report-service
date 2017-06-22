package com.svi.payroll.reports.enums;

/**
 * The labes to appear on payroll payslip and register
 * **/
public enum PayslipTicketLabels {

	SSS("SSS"),
	HDMF("HDMF"),
	PHILHEALTH("PH"),
	BASIC_PAY("Basic Pay"),
	
	ABSENT("Absent"),
	LATE("Late"),
	
	NIGHT_SHIFT("Night Shift"),
	
	SSS_LOAN("SSS Loan"),
	HDMF_LOAN("HDMF Loan"),
	HDMF_CALAMITY_LOAN("HDMF Loan"),
	LOAN_KEY("Loans"),
	
	OTHERS_TAXABLE_DEDUCTIONS("Other Deductions"),
	
	NONTAXABALE_SALARY("Non-Taxable Salary"),	// TODO, ?
	
	NONTAXABALE_THIRTEENTH_OTHER_BENEFITS("13thOth"),
	NONTAXABALE_DEMINIMIS("DE MINIMIS"),
	NONTAXABALE_OTHER_COMPENSATION("OTHER NONTAXABLE ALLOWANCE"),
	NONTAXABALE_PREMIUM_PAID_ON_HEALTH("Pre"),
	NONTAXABALE_SSS_GSIS_AND_OTHERS("Contri"),
	NONTAXABALE_BASIC_MWE("basicMWE"),
	NONTAXABALE_HOLIDAY_PAY_MWE("holMWE"),
	NONTAXABALE_OVERTIME_PAY_MWE("otMWE"),
	NONTAXABALE_NIGHT_DIFF_PAY_MWE("ndMWE"),
	NONTAXABALE_HAZARD_PAY_MWE("hazMWE"),
	
	WITHOLDING_TAX("WT"),
	WITHOLDING_TAX_FRINGE_BENFIT("WTF"),
	COMISSARY("Comissary"),
	ALLOTMENT("Allotment"),
	ADVANCES("Advances"),
	RMA("RMA"),
	
	// keys used in compensation_tax cassandra field
	TAXABLE_BASIC_SALARY("Basic"),
	TAXABLE_REPRESENTATION("Rep"),
	TAXABLE_TRANSPORTATION("TRANSPORT ALLOWANCE"),
	TAXABLE_FIXED_HOUSING_ALLOWANCE("HOUSING ALLOWANCE"),
	TAXABLE_COMMISSION("Com"),
	TAXABLE_PROFIT_SHARING("Pro"),
	TAXABLE_FEES("Fee"),
	TAXABLE_COST_OF_LIVING_ALLOWANCE("Col"),
	TAXABLE_HAZARD_PAY("Haz"),
	TAXABLE_HOLIDAY_PAY("Hol"),
	TAXABLE_EXCESS_DEMINIMIS_BONUSES("Edb"),
	TAXABLE_FRINGE_BENEFIT("Fri"),
	TAXABLE_OTHERS("oth_"), // used as an indicator for other taxable  incomes example: oth_Rice-Allowance
	TAXABLE_SUPP_OTHERS("sup_"), // used as an indicator for other supplementary taxable  incomes example: sup_Rice-Allowance
	TAXABLE_SALARIES_AND_OTHER_FORMS("SalaryAndOthers"), // cummulative taxable incomes excluding basic and excess deminimis
	;
	
	private String var;
	PayslipTicketLabels(String var){
		this.var = var;
	}
	 public String getVal(){
		 return this.var;
		 
	 }
	
}
