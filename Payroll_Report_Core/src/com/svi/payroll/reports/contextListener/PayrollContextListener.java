package com.svi.payroll.reports.contextListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;



import com.svi.payroll.reports.enums.PayrollSummarySettingsEnum;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.GovernmentDeductionsUtilitiy;
import com.svi.payroll.reports.util.PayrollReportsSetup;

/***
 * The 1st part to be initialize
 * 
 * ***/
@WebListener
public class PayrollContextListener implements ServletContextListener  {

	public void contextDestroyed(ServletContextEvent arg0) {
		CassandraConnectionUtility.close();
	}

	public void contextInitialized(ServletContextEvent servletContextEvent) {
		
		ServletContext servletContext = servletContextEvent.getServletContext();
		/**
		 * Gets Config from web.xml
		 **/
		String realPath= servletContext.getRealPath("/");	
		String payrollSummaryConfig = servletContext.getInitParameter("PayrollSummaryConfig");
		PayrollSummarySettingsEnum.setContext(servletContext.getResourceAsStream(payrollSummaryConfig));
		
		// for SSS deduction setup
		String sssPath = servletContext.getInitParameter("SSSXmlPath");	
		GovernmentDeductionsUtilitiy.setSSS(realPath + sssPath);	
		
		// for reports property setup
		String reportIniPath = servletContext.getInitParameter("ReportIniPath");
		ReportEnum.readConfig(servletContext.getResourceAsStream(reportIniPath));	
		
		// for reports utility setup
		String images=servletContext.getInitParameter("REPORT_RESOURCES_IMAGES");
		String templates=servletContext.getInitParameter("REPORT_RESOURCES_TEMPLATES");				
		PayrollReportsSetup.setUp(servletContext,templates,images);				
	}

	

}
