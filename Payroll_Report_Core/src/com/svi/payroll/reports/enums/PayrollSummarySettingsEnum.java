package com.svi.payroll.reports.enums;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <p>
 * Value holder for constants derived from the configuration file.
 * <p>
 * Retrieves the data from a configuration file via InputStream. The config file
 * location is usually defined on the web.xml file.
 * 
 * @author SVI_15027
 *
 */
public enum PayrollSummarySettingsEnum {
	RUN_DATE("RUN_DATE"),
	COMPANY_ID("COMPANY_ID"),
	EMPLOYEE_ID("EMPLOYEE_ID"),
	RUN_TYPE("RUN_TYPE"),
	ABSENT("ABSENT"),
	RE_RUN_AT("RE_RUN_AT"),
	BASIC_PAY("BASIC_PAY"),
	LATE("LATE"),
	GROSS_PAY("GROSS_PAY"),
	NET_PAY("NET_PAY"),
	TRUE_NET_PAY("TRUE_NET_PAY"),
	LEAVES("LEAVES"),
	OVERTIMES("OVERTIMES"),
	TAXABLE_COMPENSATIONS("TAXABLE_COMPENSATIONS"),
	TAXABLE_DEDUCTIONS("TAXABLE_DEDUCTIONS"),
	GOVERNMENT_DEDUCTIONS("GOVERNMENT_DEDUCTIONS"),
	TAXES_WITHHELD("TAXES_WITHHELD"),
	NON_TAXABLE_COMPENSATIONS("NON_TAXABLE_COMPENSATIONS"),
	INTERNAL_COMPENSATIONS("INTERNAL_COMPENSATIONS"),
	INTERNAL_DEDUCTIONS("INTERNAL_DEDUCTIONS"),
	;
	private boolean fieldDisplayedUninitialized=true;
	private String value=""; 
	private boolean fieldDisplayed=false;
	private static Properties prop ;
	private static Map<String,Map<String,String>> propertyMap=new HashMap<>();
	private PayrollSummarySettingsEnum(String value){
		this.value=value;
	}

	public boolean isFieldDisplayed() {
		if (fieldDisplayedUninitialized) {
			fieldDisplayed = !prop.getProperty(value).trim().isEmpty();
			fieldDisplayedUninitialized = false;
		}
		return fieldDisplayed;
	}
	
	/**
	 * 
	 * @return the map of the of this specific property using "HEADER NAME" as the
	 *         map's key and "KEY FROM DATABASE MAP" as the map's value
	 */
	public Map<String,String> mapValues(){
		return propertyMap.get(this.toString());
	}

	/**
	 * <p>
	 * Value of the associated enum relative to the one set on the config file.
	 * 
	 * @return
	 */
	public String value() {
		return prop.getProperty(value).trim();
	}
	
	/**
	 * <p>
	 * Sets the InputStream representation of the config file.
	 * <p>
	 * The method where the Properties will derive the data from the
	 * config file using the input stream provided on server initialization.
	 * 
	 * @param inputStream InputStream of the config file.
	 */
	public static void setContext(InputStream inputStream){
		synchronized (inputStream) {
			if(prop==null){
				try {
					prop= new Properties();
					prop.load(inputStream);
					inputStream.close();
					mapConfigurations();
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

	/**
	 * <p>
	 * creates an entire map from the properties file for fields that have the
	 * format HEADER NAME<COLON>KEY FROM DATABASE MAP<COMMA>....
	 * <p>
	 * It loops through all items on the property file, then it splits the value using the delimiter
	 * comma "," to split it according to entries
	 * <p>
	 * Each entry is then split by a colon ":" , whose purpose is to serve as its key and
	 * value, which will be added to the map containing the entries.
	 * <p>
	 * if the data cannot be split like above, it will ignore it. if everything
	 * else was ignored, the item itself will only contain an empty map
	 * 
	 */
	private static void mapConfigurations() {
		for (Entry<Object, Object> properties : prop.entrySet()) {
			String value = (String) properties.getValue();
			String key = (String) properties.getKey();
			Map<String, String> settingData = Arrays.asList(value.split(","))
			        .stream()
			        .map(elem -> elem.split(":"))
			        .filter(elem -> elem.length==2)
			        .collect(Collectors.toMap(e -> e[0], e -> e[1]));
			propertyMap.put(key, settingData);
		}
	}

}
