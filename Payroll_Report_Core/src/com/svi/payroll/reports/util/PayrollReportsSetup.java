package com.svi.payroll.reports.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import com.svi.payroll.report.forms.AlphalistForms;
import com.svi.payroll.report.forms.BIRForm1601C;
import com.svi.payroll.report.forms.BIRForm1604CF;
import com.svi.payroll.report.forms.BIRForm2316;
import com.svi.payroll.report.forms.BankRegisterSalaryLoanRepayment;
import com.svi.payroll.report.forms.HDMFmcrf;
import com.svi.payroll.report.forms.HDMFstlrf;
import com.svi.payroll.report.forms.NetPayRegisterForBank;
import com.svi.payroll.report.forms.PayrollRegister;
import com.svi.payroll.report.forms.PayrollRegisterInternal;
import com.svi.payroll.report.forms.PhilHealthRF1;
import com.svi.payroll.report.forms.SSSFormR3;
import com.svi.payroll.report.forms.SSSFormR5;
import com.svi.payroll.report.forms.ThirteenthMonthPayForm;

public abstract class PayrollReportsSetup {
    private static ServletContext SERVLET_CONTEXT;
	private static String TEMPLATE;
	private static String IMAGES;
    
    public static void setUp(ServletContext SERVLET_CONTEXT, String templates, String images) {
    	PayrollReportsSetup.SERVLET_CONTEXT=SERVLET_CONTEXT;
    	PayrollReportsSetup.TEMPLATE=templates;
    	PayrollReportsSetup.IMAGES=images;

    	BankRegisterSalaryLoanRepaymentSetUp();    	
		BIRForm1601CSetUp();
		BIRForm2316SetUp();
		HDMFmcrfSetUp();
		HDMFstlrfSetUp();
		NetPayRegisterForBankSetUp();
		PhilHealthRF1SetUp();		
		SSSFormR5SetUp();	
		SSSFormR3SetUp();		
		PayrollRegisterSetUp();
		AlphalistFormsSetUp();
		ThirteenthMonthPay();
		
		// remaining report not yet integrated
		BIRForm1604CFSetUp();
		
    }
    private static void PhilHealthRF1SetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "PhilHealthRF1.jasper");
		PhilHealthRF1.setTemplateFile(template);
		
		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "PhilHealthRF1Header.png");
		PhilHealthRF1.setHeaderImageFile(imageTemplateFile);
		
		InputStream footerTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "PhilHealthRF1Footer.png");
		PhilHealthRF1.setFooterImageFile(footerTemplateFile);
	}

	private static void NetPayRegisterForBankSetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "NetPayRegisterForBank.xlsx");
		NetPayRegisterForBank.setTemplateFile(template);
	}

	private static void BankRegisterSalaryLoanRepaymentSetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "BankRegisterSalaryLoanRepayment.xlsx");
		BankRegisterSalaryLoanRepayment.setTemplateFile(template);
	}

	private static void BIRForm1604CFSetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "BIRForm1604CF.jasper");
		BIRForm1604CF.setTemplateFile(template);
		
		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "BIRForm1604CF.png");
		BIRForm1604CF.setImageTemplateFile(imageTemplateFile);
	}

	private static void BIRForm2316SetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "BIRForm2316.jasper");
		BIRForm2316.setTemplateFile(template);
		
		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "BIRForm2316.png");
		BIRForm2316.setImageTemplateFile(imageTemplateFile);
	}

	private static void HDMFstlrfSetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "HDMFstlrf.jasper");
		HDMFstlrf.setTemplateFile(template);
		
		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "HDMFstlrf.png");
		HDMFstlrf.setImageTemplateFile(imageTemplateFile);
	}

	private static void HDMFmcrfSetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "HDMFmcrf.jasper");
		HDMFmcrf.setTemplateFile(template);
		
		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "HDMFMCRFHeader.png");
		HDMFmcrf.setHeaderImageFile(imageTemplateFile);
		
		InputStream footerTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "HDMFMCRFFooter.png");
		HDMFmcrf.setFooterImageFile(footerTemplateFile);
	}

	private static void SSSFormR5SetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "SSSFormR5.jasper");
		SSSFormR5.setTemplateFile(template);
		
		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "SSSFormR5.png");
		SSSFormR5.setFormImageFile(imageTemplateFile);
		
		InputStream blankTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "blank.png");
		SSSFormR5.setBlankImageFile(blankTemplateFile);
		
		InputStream checkTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "check.png");
		SSSFormR5.setCheckImageFile(checkTemplateFile);
	}

	private static void AlphalistFormsSetUp()  {
		InputStream template = SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "AlphalistForm.jasper");
		AlphalistForms.setTemplateFile(template);
	}

	private static void BIRForm1601CSetUp()  {
		InputStream template = SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "BIRForm1601C.jasper");
		BIRForm1601C.setTemplateFile(template);

		InputStream imageTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "BIRForm1601C.png");
		BIRForm1601C.setImageTemplateFile(imageTemplateFile);
	}
	
	private static void SSSFormR3SetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "SSSFormR3.jasper");
		SSSFormR3.setTemplateFile(template);
		
		InputStream sssLogo = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "ssslogo.png");
		SSSFormR3.setSssLogo(sssLogo);
		
		InputStream blankTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "blank.png");
		SSSFormR3.setBlankImageFile(blankTemplateFile);
		
		InputStream checkTemplateFile = SERVLET_CONTEXT.getResourceAsStream(IMAGES + "check.png");
		SSSFormR3.setCheckImageFile(checkTemplateFile);
	}
	
	private static void PayrollRegisterSetUp()  {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "PayrollRegister.xlsx");
		PayrollRegister.setTemplateFile(template);
		
		
		InputStream template2 =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "PayrollRegisterInternal.xlsx");
		PayrollRegisterInternal.setTemplateFile(template2);
		System.out.println("TEMPLATE: "+SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "PayrollRegisterInternal.xlsx".toString()));
	}
	
	private static void ThirteenthMonthPay() {
		InputStream template =SERVLET_CONTEXT.getResourceAsStream(TEMPLATE + "ThirteenthMonthPayForm.jasper");
		ThirteenthMonthPayForm.setTemplateFile(template);
	}
	
	
}
