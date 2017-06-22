package com.svi.payroll.reports.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.svi.payroll.reports.object.PreviousEmployer;


public class JsonReader {

	
	/**
	 * Parse json file to previous employer object 
	 * 
	 * @param filePath
	 * 			Json file path
	 * @return
	 * @throws JsonParseException
	 * @throws IOException	
	 */
	public static PreviousEmployer readJsonFile(String filePath) throws JsonParseException, IOException {
		//			Convert file to string													//
		String text = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
		
		//			Retrieve prevEmployerDetails from the converted string					//
		Gson gson = new GsonBuilder().setPrettyPrinting().create();	
		JsonObject obj = gson.fromJson (text, JsonObject.class);
		JsonElement elem = obj.get("prevEmployerDetails");
		
		//			Parse the prevEmployerDetails data to the PreviousEmployer object		//
		PreviousEmployer prevEmp = gson.fromJson(elem, PreviousEmployer.class);
		
		return prevEmp;
		
	}
	

}
