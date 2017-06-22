package com.svi.payroll.reports.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.JsonParseException;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.object.Company;
import com.svi.payroll.reports.object.CompanyList;
import com.svi.payroll.reports.object.PreviousEmployer;
import com.svi.payroll.reports.util.forms.ReportUtil;

public class PreviousEmployerUtil {
	
	public static void getPreviousEmployerList(CompanyList companyList) throws JsonParseException, IOException{
		
		String inputFileDirectory = ReportEnum.PREVIOUS_EMPLOYER_INPUT_DIRECTORY.value();
		// gets previous employer list for each company each company
		for(Company company : companyList){	
			
			//stores the list of previous employers			
			Map<String,PreviousEmployer> previousEmployersList = new HashMap<String,PreviousEmployer>();
			
			//get all the available json files
			String runDateYear = ReportUtil.getFormattedDate(companyList.getRunDate(), "yyyy");
			File xmlFilesDir = new File(inputFileDirectory+runDateYear+"/"+company.getCompanyId());
			File[] listOfFiles = xmlFilesDir.listFiles();
			
			// create previous meployer object for each file and save it to a map
			if(listOfFiles != null){
				for (int i = 0; i < listOfFiles.length; i++) {			 
				      if (listOfFiles[i].isFile()) {
				    	  String employeeId =  FilenameUtils.removeExtension(listOfFiles[i].getName());				    	  
				    	  PreviousEmployer previousEmployer = JsonReader.readJsonFile(listOfFiles[i].getAbsolutePath());
				    	  if(previousEmployer != null){
				    		  previousEmployersList.put(employeeId, previousEmployer);
				    	  }
				      }
				}
			}
			
			// setting the company list of previous employers
			company.setPreviousEmployersList(previousEmployersList);			
		}
		
	}
	

}
