package com.svi.payroll.reports.webservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.svi.payroll.reports.ReportGenerator;
import com.svi.payroll.reports.constants.ReportCons;
import com.svi.payroll.reports.enums.ReportEnum;
import com.svi.payroll.reports.util.CassandraConnectionUtility;
import com.svi.payroll.reports.util.ZipUtility;
import com.svi.payroll.reports.util.forms.ReportUtil;

@Path("/report")
public class GenerateReport {
	
	@GET
	@Path("/all/generate")
	@Produces("application/x-rar-compressed,application/octet-stream")
	/**
	 * Generates ALL reports (monthly and annual) for ALL companies for the current payroll period.
	 * @return
	 */
	public Response generateAllReports() {
		try {			
			ReportGenerator reportGenerator = new ReportGenerator();			
			String generateReports = reportGenerator.generateCurrentReports(true,true,ReportCons.ALL_COMPANY);
			if(generateReports == null){
				return Response.status(404).build();
			}
			
			File file = new File(generateReports);
			InputStream e = new FileInputStream(file);
			return Response.ok().header("Content-Disposition", "attachment; filename=" + file.getName()).entity(e)
					.build();
		
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@GET
	@Path("/{monthlyOrAnnual}/generate")
	@Produces("application/x-rar-compressed,application/octet-stream")
	/**
	 * Generates ALL reports (monthly or annual) for ALL companies for the current payroll period.
	 * @return
	 */
	public Response generateAllMonthlyReports(@PathParam("monthlyOrAnnual") String monthlyOrAnnual) {
		boolean monthly = false;
		boolean annual = false;
		if(monthlyOrAnnual.equalsIgnoreCase(ReportCons.MONTHLY_REPORT)){
			monthly = true;
		}
		else if(monthlyOrAnnual.equalsIgnoreCase(ReportCons.ANNUAL_REPORT)){
			annual = true;
		}
		else{
			return Response.status(404).build();
		}
		
		try {			
			ReportGenerator reportGenerator = new ReportGenerator();			
			String generateReports = reportGenerator.generateCurrentReports(monthly,annual,ReportCons.ALL_COMPANY);
			if(generateReports == null){
				return Response.status(404).build();
			}
		
			File file = new File(generateReports);
			InputStream e = new FileInputStream(file);
			return Response.ok().header("Content-Disposition", "attachment; filename=" + file.getName()).entity(e)
					.build();
		
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}
		
	@GET
	@Path("/{monthlyOrAnnual}/{companyId}/generate")
	@Produces("application/x-rar-compressed,application/octet-stream")
	/**
	 * Generates ALL reports (monthly) for ALL companies for the current payroll period.
	 * @return
	 */
	public Response generateAllMonthlyReportsPerCompany(@PathParam("companyId") String companyId,
			@PathParam("monthlyOrAnnual") String monthlyOrAnnual) {
		
		boolean monthly = false;
		boolean annual = false;
		if(monthlyOrAnnual.equalsIgnoreCase(ReportCons.MONTHLY_REPORT)){
			monthly = true;
		}
		else if(monthlyOrAnnual.equalsIgnoreCase(ReportCons.ANNUAL_REPORT)){
			annual = true;
		}
		else{
			return Response.status(404).build();
		}
		
		if(companyId == null | companyId.trim().length() == 0){
			return Response.status(404).build();
		}
		try {			
			ReportGenerator reportGenerator = new ReportGenerator();			
			String generateReports = reportGenerator.generateCurrentReports(monthly,annual,companyId);
			if(generateReports == null){
				return Response.status(404).build();
			}
		
			File file = new File(generateReports);
			InputStream e = new FileInputStream(file);
			return Response.ok().header("Content-Disposition", "attachment; filename=" + file.getName()).entity(e)
					.build();
		
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}

	@GET
	@Path("/all-reports/{runDate}/{companyID}/download")
	@Produces("application/x-rar-compressed,application/octet-stream")
	/**
	 * Generates ALL reports for the current payroll period and for specific company.
	 * @return
	 */
	public Response getAllCompanyReports(@PathParam("runDate") String runDate, @PathParam("companyID") String companyID) {
		if(ReportUtil.isNullOrEmpty(runDate) | ReportUtil.isNullOrEmpty(companyID)){
			return Response.status(404).build();
		}		
		
		try {	
			// directory path of the report
			String directoryPath = ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate;
			System.out.println("directoryPath: "+directoryPath);
			// file name and full path of the report
			String targetFilePath = directoryPath+File.separator+companyID+"_All_Reports.zip";				
			System.out.println("targetFilePath: "+targetFilePath);
			boolean allCompany = companyID.trim().equalsIgnoreCase(ReportCons.ALL_COMPANY);
			if(allCompany){
				targetFilePath = directoryPath+".zip";	
			}
			
			
			File targetFile = new File(targetFilePath);			
			
			// checks if the target report zipped file already exists.
			if(!targetFile.exists()){
				// directories to be included in the zipped report
				String directoryPathMonthly= ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+File.separator+ReportCons.MONTHLY_REPORT+File.separator+companyID;
				String directoryPathAnnual = ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+File.separator+ReportCons.ANNUAL_REPORT+File.separator+companyID;
				String directoryPathAll = ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate;
				File  dirMonthly = new File(directoryPathMonthly);
				File  dirAnnual = new File(directoryPathAnnual);	
				File  dirAll = new File(directoryPathAll);	
				// checks if the target report directory exists
				File  dir = new File(directoryPath);
				if (dir.exists()) {  // if exists, zipped the monthly and annual reports for the company										
					ZipUtility util=new ZipUtility();
					List<File> listFiles=new ArrayList<>();
					
					// checking if report directories exists, if YES, include them in the zipped
					if(!allCompany){
						if(dirMonthly.exists()){
							listFiles.add(new File(directoryPathMonthly));
						}
						if(dirAnnual.exists()){
							listFiles.add(new File(directoryPathAnnual));
						}
					}
					else{
						if(dirAll.exists()){
							listFiles.add(new File(directoryPathAll));
						}
					}					
					
					// zipping the file if nonempty
					if(listFiles.size() > 0){						
						try {
							util.zip(listFiles, targetFilePath);
						} catch (IOException e) {			
							e.printStackTrace();
						}				
						
						InputStream e = new FileInputStream(targetFile);
						System.out.println("Report File Name: "+targetFile.getName());
						System.out.println("Report File Path: "+targetFile.getAbsolutePath());
						return Response.ok().header("Content-Disposition", "attachment; filename=" + targetFile.getName()).entity(e)
								.build();
					}
					else{
						return generateHistoricalReports(dirMonthly, dirAnnual, companyID, targetFilePath, runDate);	
					}
					
				}
				else{ 
					return generateHistoricalReports(dirMonthly, dirAnnual, companyID, targetFilePath, runDate);	
				}								
			}
			else{ // sends the file automatically if it exists				
				InputStream e = new FileInputStream(targetFile);
				System.out.println("Requested company zipped report already exists.");
				System.out.println("Report File Name: "+targetFile.getName());
				System.out.println("Report File Path: "+targetFile.getAbsolutePath());
				return Response.ok().header("Content-Disposition", "attachment; filename=" + targetFile.getName()).entity(e)
						.build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
			
		
		
		
		
	}
	
	@GET
	@Path("/{reportType}/{runDate}/{companyID}/download")
	@Produces("application/x-rar-compressed,application/octet-stream")
	/**
	 * Generates ALL reports for the current payroll period and for specific company.
	 * @return
	 */
	public Response getCompanyReportsPerType(@PathParam("runDate") String runDate, @PathParam("companyID") String companyID, @PathParam("reportType") String reportType) {
		if(ReportUtil.isNullOrEmpty(runDate) | ReportUtil.isNullOrEmpty(companyID) | ReportUtil.isNullOrEmpty(reportType)){
			return Response.status(404).build();
		}
	
		if(companyID.trim().equalsIgnoreCase(ReportCons.ALL_COMPANY)){
			return Response.status(404).build();  // does not make sense to download all reports of the same type for all companies
		}
		
		
		try {	
			// determine if the report is monthly or annual report
			// used in determining the correct directory location of the report
			String monthlyOrAnnual = ReportUtil.getReportFrequency(reportType);		
			
			String directoryPath = ReportEnum.REPORT_OUTPUT_PATH.value()+File.separator+runDate+File.separator+monthlyOrAnnual+File.separator+companyID+File.separator+reportType;
			String targetFilePath = directoryPath+".zip";
			File targetFile = new File(targetFilePath);
			
			// checks if the target report zipped file already exists.
			if(!targetFile.exists()){
				
				// checks if the target report directory exists
				File  dir = new File(directoryPath);
				if (dir.exists()) {  // if exists, zipped the directory
					ZipUtility util=new ZipUtility();
					List<File> listFiles=new ArrayList<>();
					listFiles.add(new File(directoryPath));
					
					// zipping the file
					try {
						util.zip(listFiles, targetFilePath);
					} catch (IOException e) {			
						e.printStackTrace();
					}				
					
					InputStream e = new FileInputStream(targetFile);
					System.out.println("Report File Name: "+targetFile.getName());
					System.out.println("Report File Path: "+targetFile.getAbsolutePath());
					return Response.ok().header("Content-Disposition", "attachment; filename=" + targetFile.getName()).entity(e)
							.build();
				}
				else{ 
					System.out.println("Requested company report does not exist");
					return Response.status(404).build();
				}								
			}
			else{ // sends the file automatically if it exists				
				InputStream e = new FileInputStream(targetFile);
				System.out.println("Requested company zipped report already exists.");
				System.out.println("Report File Name: "+targetFile.getName());
				System.out.println("Report File Path: "+targetFile.getAbsolutePath());
				return Response.ok().header("Content-Disposition", "attachment; filename=" + targetFile.getName()).entity(e)
						.build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}

	private Response generateHistoricalReports(File dirMonthly, File dirAnnual, String companyID, String targetFilePath, String runDate){
		System.out.println("Requested company report does not exist.");
		System.out.println("Will try to generate it from the database.");
		
		try {			
			ReportGenerator reportGenerator = new ReportGenerator();			
			String generateReports = reportGenerator.generateHistoricalReports(!dirMonthly.exists(),!dirAnnual.exists(),companyID, runDate);
			if(generateReports == null){
				return Response.status(404).build();
			}			
			
			File file = new File(generateReports);
			InputStream e = new FileInputStream(file);
			return Response.ok().header("Content-Disposition", "attachment; filename=" + file.getName()).entity(e)
					.build();		
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
		
	}
	
/*	
	@POST
	@Path("/previous-employer/{companyID}/{runDate}/upload")		
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadEmployeesWithPreviousEmployer(@FormDataParam("previous-employer-create") InputStream uploadedInputStream,
			@FormDataParam("previous-employer-create") FormDataContentDisposition fileDetail, @PathParam("runDate") String runDate, @PathParam("companyID") String companyId){		
		System.out.println("Uploading list of employees with previous employer for company "+companyId+" for the year "+runDate);
		
		// validating input
		if(!(uploadedInputStream == null || fileDetail == null || companyId.trim().length() == 0 || runDate.trim().length() == 0 )){	
			
			try {
				// getting the valid workbook
				Workbook workbook = ReportUtil.validateExcel(uploadedInputStream, fileDetail);
				if(workbook == null){
					return Response.status(400).build();
				}
				
				// getting the first work sheet of the excel file 
				Sheet sheet = workbook.getSheetAt(0);
				
				// iterating in each row of the file			
				for (int i = 5, j = sheet.getLastRowNum() + 1; i < j; i++) { // skip first and 2nd row which is expected to be a title and column headers
					// converting each row data into node object
					Row row = sheet.getRow(i);				
					if (row != null) {
						ReportUtil.insertRowToCassy(row,runDate,companyId);	
					}
				}
				System.out.println("File was successfully uploaded.");
				return Response.status(200).build(); // status: request succeeded, TODO, to be replaced by?
				
			} catch (IOException e) {
				return Response.status(500).build();
			}
		}
		else{
			return Response.status(400).build();
		}
	}
	
	*/
/*	@GET
	@Path("/previous-employer/{companyID}/{runDate}/delete")
	@Produces(MediaType.APPLICATION_JSON)
	*//**
	 * Deletes all previous company details for a given company and rundate (year)
	 * @return
	 *//*
	public Response deletePreviousEmployerData(@PathParam("runDate") String runDate, @PathParam("companyID") String companyID) {
		System.out.println("Deleting list of employees with previous employer for company "+companyID+" for the year "+runDate);
		
		try {			
			CassandraConnectionUtility.getUserAccessor().deletePrevEmployerData(runDate, companyID);
			System.out.println("Previous employer data for company "+companyID+" for the year "+runDate+" was successfully deleted");
			return Response.status(200).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(500).build();
		}
	}*/

}
