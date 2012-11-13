package com.poly.nlp.filekommander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class FileKommander {
	
	private static final Logger log = Logger.getLogger(FileKommander.class);
	  
	public static void main(String[] args) {
	
		// Load properties file 
		log.info("Reading Properties File");
		
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File("FileKommander.properties")));
		} catch (FileNotFoundException e) {
		    log.error("Properties file was not found", e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Error reading properties file", e);
		}
		
		
		
	}

}
