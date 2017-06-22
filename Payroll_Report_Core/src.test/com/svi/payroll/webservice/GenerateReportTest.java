package com.svi.payroll.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonParseException;
import com.svi.payroll.report.forms.AlphalistForms;
import com.svi.payroll.report.forms.BIRForm1601C;
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
import com.svi.payroll.report.objects.Employee;
import com.svi.payroll.reports.ReportGenerator;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.contextListener.PayrollContextListener;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.object.PreviousEmployer;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.GovernmentDeductionsUtilitiy;
import com.svi.payroll.reports.util.JsonReader;
import com.svi.payroll.reports.util.forms.ReportUtil;
import com.svi.payroll.reports.webservice.GenerateReport;

public class GenerateReportTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		// report enum
		InputStream enumReport = new FileInputStream(new File("WebContent/WEB-INF/config/Report.ini"));
		ReportEnum.readConfig(enumReport);
		
		//for NetPayRegisterForBank
		InputStream template = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/NetPayRegisterForBank.xlsx"));
		NetPayRegisterForBank.setTemplateFile(template);		
	
		// for PhilHealthRF1
		InputStream template1 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/PhilHealthRF1.jasper"));
		PhilHealthRF1.setTemplateFile(template1);
		
		InputStream imageTemplateFile1 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/PhilHealthRF1Header.png"));
		PhilHealthRF1.setHeaderImageFile(imageTemplateFile1);
		
		InputStream footerTemplateFile1 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/PhilHealthRF1Footer.png"));
		PhilHealthRF1.setFooterImageFile(footerTemplateFile1);
		
		// for HDMFmcrf
		InputStream template2 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/HDMFmcrf.jasper"));
		HDMFmcrf.setTemplateFile(template2);
		
		InputStream imageTemplateFile2 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/HDMFMCRFHeader.png"));
		HDMFmcrf.setHeaderImageFile(imageTemplateFile2);
		
		InputStream footerTemplateFile2 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/HDMFMCRFFooter.png"));
		HDMFmcrf.setFooterImageFile(footerTemplateFile2);
		
		
		// for HDMFstlrf
		InputStream template3 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/HDMFstlrf.jasper"));
		HDMFstlrf.setTemplateFile(template3);
		
		InputStream imageTemplateFile3 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/HDMFstlrf.png"));
		HDMFstlrf.setImageTemplateFile(imageTemplateFile3);
		
		//for BankRegisterSalaryLoanRepayment
		InputStream template4 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/BankRegisterSalaryLoanRepayment.xlsx"));
		BankRegisterSalaryLoanRepayment.setTemplateFile(template4);
		
		//setting sss R3 config
		GovernmentDeductionsUtilitiy.setSSS("WebContent/WEB-INF/config/SSS.xml");
		
		// for SSS R5
		InputStream template5 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/SSSFormR5.jasper"));
		SSSFormR5.setTemplateFile(template5);
	
		InputStream blankImageFile5 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/blank.png"));
		SSSFormR5.setBlankImageFile(blankImageFile5);
		
		InputStream checkImageFile5 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/check.png"));
		SSSFormR5.setCheckImageFile(checkImageFile5);
		
		InputStream imageTemplateFile5 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/SSSFormR5.png"));
		SSSFormR5.setFormImageFile(imageTemplateFile5);
		
		// for SSS R3 Form
		InputStream template6 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/SSSFormR3.jasper"));
		SSSFormR3.setTemplateFile(template6);
		SSSFormR3.setBlankImageFile(blankImageFile5);
		SSSFormR3.setCheckImageFile(checkImageFile5);		
		InputStream sssLogo = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/ssslogo.png"));
		SSSFormR3.setSssLogo(sssLogo);
		
		// for BIR FORM 1601 C Form
		InputStream template7 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/BIRForm1601C.jasper"));
		BIRForm1601C.setTemplateFile(template7);
		
		InputStream imageTemplateFile7 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/BIRForm1601C.png"));
		BIRForm1601C.setImageTemplateFile(imageTemplateFile7);
		
		// for BIR FORM 2316 Form
		InputStream template8 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/BIRForm2316.jasper"));
		BIRForm2316.setTemplateFile(template8);
		
		InputStream imageTemplateFile8 = new FileInputStream(new File("WebContent/WEB-INF/ReportResources/FormImages/BIRForm2316.png"));
		BIRForm2316.setImageTemplateFile(imageTemplateFile8);
		
		//for payroll register
		InputStream template9 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/PayrollRegister.xlsx"));
		PayrollRegister.setTemplateFile(template9);
		
		InputStream template11 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/PayrollRegisterInternal.xlsx"));
		PayrollRegisterInternal.setTemplateFile(template11);
		
		//for alphalist
		InputStream template10 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/AlphalistForm.jasper"));
		AlphalistForms.setTemplateFile(template10);
		
		//for alphalist
		InputStream template12 =new FileInputStream(new File("WebContent/WEB-INF/ReportResources/JasperTemplates/ThirteenthMonthPayForm.jasper"));
		ThirteenthMonthPayForm.setTemplateFile(template12);
				
		
		
	}
	
	
	 @After
	 public void closeConnections() throws Exception {
		CassandraConnectionUtility.close();
	 }

	 @Test
	public void createReports() throws JsonParseException, IOException{		 
	 //ReportUtil.insertRowToCassy(null);
	ReportGenerator reportGenerator = new ReportGenerator();				
		 reportGenerator.generateCurrentReports(true,true,"all");
		
		
	
	 }

	
	 
	
	
	
}
