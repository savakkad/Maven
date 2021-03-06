package com.shell.markethub.base.util;

import org.apache.log4j.Logger;
import  org.apache.log4j.PropertyConfigurator;

import com.shell.markethub.base.util.config.BaseDataConstants;


public class BaseLogManager extends Logger {	
	
	public BaseLogManager(String name){
		super(name);		
		PropertyConfigurator.configure(BaseDataConstants.PROJECT_ROOT_LOCATION + 
				BaseDataConstants.LOG_PROPERTIES_FILE);			
	}

}
