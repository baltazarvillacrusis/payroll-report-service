package com.svi.payroll.reports.enums;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Value holder for constants derived from the configuration file.
 * <p>
 * Retrieves the data from a configuration file. Default value for
 * enum constants, not found in the configuration file, is an empty string.
 */
public enum ReportEnum {
	CASSANDRA_KEYSPACE("CASSANDRA_KEYSPACE"),
	CASSANDRA_IP_ADD("CASSANDRA_IP_ADD"),
	CASSANDRA_EMPLOYER_DETAILS_TABLE("CASSANDRA_EMPLOYER_DETAILS_TABLE"),
	CASSANDRA_EMPLOYEE_DETAILS_TABLE("CASSANDRA_EMPLOYE_DETAILS_TABLE"),
	CASSANDRA_PAYROLL_COMPUTE_TABLE("CASSANDRA_PAYROLL_COMPUTE_TABLE"),
	REPORT_OUTPUT_PATH("REPORT_OUTPUT_PATH"),
	GENERATE_REPORT_SSSFORMR5("GENERATE_REPORT_SSSFORMR5"),
	GENERATE_REPORT_SSSFORMR3("GENERATE_REPORT_SSSFORMR3"),
	GENERATE_REPORT_SSSR3("GENERATE_REPORT_SSSR3"),
	GENERATE_REPORT_PHILHEALTHRF1("GENERATE_REPORT_PHILHEALTHRF1"),
	GENERATE_REPORT_NETPAYREGISTERFORBANK("GENERATE_REPORT_NETPAYREGISTERFORBANK"),
	GENERATE_REPORT_HDMFMCRF("GENERATE_REPORT_HDMFMCRF"),
	GENERATE_REPORT_HDMFSTLRF("GENERATE_REPORT_HDMFSTLRF"),
	GENERATE_REPORT_BIRFORM1604CF("GENERATE_REPORT_BIRFORM1604CF"),
	GENERATE_REPORT_BIRFORM2316("GENERATE_REPORT_BIRFORM2316"),
	GENERATE_REPORT_BIRFORM1601C("GENERATE_REPORT_BIRFORM1601C"),
	GENERATE_REPORT_BANKREGISTRYLOANREPAYMENT("GENERATE_REPORT_BANKREGISTRYLOANREPAYMENT"),
	GENERATE_REPORT_ALPHALISTFORBIRFORM1604("GENERATE_REPORT_ALPHALISTFORBIRFORM1604"),
	GENERATE_REPORT_PAYROLLREGISTER("GENERATE_REPORT_PAYROLLREGISTER"),	
	GENERATE_REPORT_SSSR3_FREQUENCY("GENERATE_REPORT_SSSR3_FREQUENCY"),
	GENERATE_REPORT_THIRTEENTH_MONTH_PAY("GENERATE_REPORT_THIRTEENTH_MONTH_PAY"),
	THIRTEENTH_MONTH_PAY_FORMULA("THIRTEENTH_MONTH_PAY_FORMULA"),
	PREVIOUS_EMPLOYER_INPUT_DIRECTORY("PREVIOUS_EMPLOYER_INPUT_DIRECTORY"),
	APPLICATION_NAME("APPLICATION_NAME");
	
	
	private String value; 
	private static Properties properties;
	private ReportEnum(String value){
		this.value= value;
	}

	/** 
	 * Returns the value of the enum constant as defined in the configuration file.
	 * <p>
	 * @return Value of the enum constant as set on the configuration file. Returns
	 * empty string if the enum constant does not exist.
	 */
	public String value() {		
		if(properties.containsKey(value)){
			return properties.getProperty(value).trim();
		}
		return "";					
	}

	/**
	 * Reads the configuration file and sets the value of the enum constant
	 * as defined in the configuration file.
	 * <p>
	 * @param iniFile Config file to be read.
	 */
	public static void readConfig(InputStream inputStream){
		// reading the file as an input stream
		
		// storing the data in properties object
		if(inputStream != null) {			
			try {				
				properties = new Properties();
				properties.load(inputStream);
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				try {
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}			
		}
		
	}	
	
}
