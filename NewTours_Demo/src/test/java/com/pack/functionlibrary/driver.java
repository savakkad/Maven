package com.pack.functionlibrary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.pack.utils.Listeners.TestListener;

import org.testng.TestNG;
import org.testng.annotations.AfterSuite;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class driver {

	FunctionLibrary globalfunctions = new FunctionLibrary();
	Properties p = new Properties();
	Properties cfg = new Properties();

	@BeforeSuite
	public void runcase() throws Exception {

		// Setting value for configuration file
		System.setProperty("pathConfig", System.getProperty("user.dir") + "/src/test/java/com/pack/config");
		FileInputStream in = new FileInputStream(System.getProperty("pathConfig") + "/config.properties");
		Properties props = new Properties();
		props.load(in);
		in.close();

		FileOutputStream out = new FileOutputStream(System.getProperty("pathConfig") + "/config.properties");
		props.setProperty("userdir", System.getProperty("user.dir"));
		props.setProperty("TotalTestCase", "0");
		props.setProperty("TestCasePassed", "0");
		props.store(out, null);
		out.close();

		// Load Config parameter
		cfg = globalfunctions.getObjectRepository(System.getProperty("pathConfig") + "/config.properties");
		System.setProperty("pathOR", System.getProperty("user.dir") + cfg.getProperty("pathObjectRepo"));
		System.setProperty("excel", System.getProperty("user.dir") + cfg.getProperty("pathDataSheet"));
		System.setProperty("resources", System.getProperty("user.dir") + "/resources");
		System.setProperty("ss", System.getProperty("user.dir") + cfg.getProperty("pathScreenShot"));
		System.setProperty("log", System.getProperty("user.dir") + cfg.getProperty("pathLog"));
		System.setProperty("appURL", cfg.getProperty("appURL"));
		Sheet excelSheetTest = globalfunctions.readExcel(System.getProperty("excel"), "TestData.xlsx", "NewTours_Demo");
		globalfunctions.loadColumnName(excelSheetTest, "Test");

		// variable Declaration
		int threadCount = Integer.parseInt(cfg.getProperty("ThreadCount"));
		int testmaxnum;
		int moduleMax;
		int deviceRow;
		int varTestControl;
		int testRownum = 0;
		int varTestNumModule;
		int loopControl;
		double deviceCnt, testCnt;

		String strFrameWorkRunMode = cfg.getProperty("FrameWorkRunMode");
		String runtimeXML = "<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n";
		String varSuiteName = "New Tours Demo";
		String strTestClass = null;
		String varPackage = cfg.getProperty("PackageName");
		String varPack;
		String varModule;
		String varTCName;
		String browserType;
		String varModuleNxt;

		List<Object> rownumbers = new ArrayList<>();
		rownumbers.clear();
		List<Object> testNumbers = new ArrayList<>();
		testNumbers.clear();
		List<Object> moduleCnt = new ArrayList<>();
		moduleCnt.clear();
		
		if(strFrameWorkRunMode.equalsIgnoreCase("all") || strFrameWorkRunMode.equalsIgnoreCase("quick")){
			runtimeXML = runtimeXML + "<suite name=\"" + varSuiteName + "\" parallel=\"" + "tests" + "\" thread-count=\""
					+ "10" + "\">\n";
		}else{
			runtimeXML = runtimeXML + "<suite name=\"" + varSuiteName + "\">\n";
		}		
		// runtimeXML = runtimeXML + "<suite name=\""+varSuiteName+"\">\n";
		runtimeXML = runtimeXML + "<listeners> <listener class-name=\"" + "com.pack.utils.Listeners.TestListener"
				+ "\"/> </listeners>\n";

		try {
			// variable for Suite and Module name in reporting
			Sheet excelSheetDevice = globalfunctions.readExcel(System.getProperty("excel"), "BrowserData.xlsx",
					"BrowserList");
			globalfunctions.loadColumnName(excelSheetDevice, "Browser");

			int rowCountTest = excelSheetTest.getLastRowNum() - excelSheetTest.getFirstRowNum();
			int rowCountDevice = excelSheetDevice.getLastRowNum() - excelSheetDevice.getFirstRowNum();

			// Loop to find out number of device to be executed
			for (int i = 1; i <= rowCountDevice; i++) {
				String varRunmode = globalfunctions.getCellValue(excelSheetDevice, "Browser", "Run_Flag", i).toString();
				if (varRunmode.equalsIgnoreCase("yes")) {
					rownumbers.add(i);
				}
			}
			// Loop to find out number of Test case to be executed to be
			// executed
			for (int i = 1; i <= rowCountTest; i++) {
				String varRunmode = globalfunctions.getCellValue(excelSheetTest, "Test", "Run_Flag", i).toString();
				if (varRunmode.equalsIgnoreCase("yes")) {
					testNumbers.add(i);
				}
			}
			deviceCnt = rownumbers.size();
			testCnt = testNumbers.size();
			testmaxnum = (int) Math.ceil(testCnt / deviceCnt);
			strFrameWorkRunMode = strFrameWorkRunMode.toLowerCase();
			strTestClass = getTestCaseList(excelSheetTest, testNumbers, varPackage);
		
			if(strFrameWorkRunMode.equalsIgnoreCase("quick")) {
				//Dividing the test case based on number of device
				for (int i=0;i<deviceCnt;i++){				
					//setting test name
					deviceRow = (int)rownumbers.get(i);
					//getDeviceManuf = globalfunctions.getCellValue(excelSheetDevice, "Device", "Manufacture", deviceRow).toString();
					//getDeviceModel = globalfunctions.getCellValue(excelSheetDevice, "Device", "Model", deviceRow).toString();
					//runtimeXML = runtimeXML + "\n<test name=\""+getDeviceManuf+"_"+getDeviceModel+"\">\n";
					browserType = globalfunctions.getCellValue(excelSheetDevice, "Browser", "Browser", deviceRow).toString();
					runtimeXML = runtimeXML + "\n<test name=\"" + browserType + "\">\n";
					// setting parameter name
					runtimeXML = runtimeXML + "<parameter name=\"browserType\" value=\"" + browserType + "\"/>\n";
					runtimeXML = runtimeXML + "<parameter name=\"appURL\" value=\"" + System.getProperty("appURL")
							+ "\"/>\n<classes>\n";
					
					//setting parameter name
					/*
					 * getDeviceID = globalfunctions.getCellValue(excelSheetDevice, "Device",
					 * "Device_ID", deviceRow).toString(); runtimeXML = runtimeXML +
					 * "<parameter name=\"deviceID\" value=\""+getDeviceID+"\"/>\n"; runtimeXML =
					 * runtimeXML +
					 * "<parameter name=\"deviceNum\" value=\""+deviceRow+"\"/>\n<classes>\n";
					 */
					
					for(int j=0;j<testmaxnum;j++){
						testRownum = (i*testmaxnum)+j;
						if(testRownum <testNumbers.size()){
							varTestControl = (int)testNumbers.get(testRownum);
							varPack = globalfunctions.getCellValue(excelSheetTest, "Test", "Package", varTestControl).toString();
							varModule = globalfunctions.getCellValue(excelSheetTest, "Test", "Module", varTestControl).toString();
							varTCName = globalfunctions.getCellValue(excelSheetTest, "Test", "TC_Name", varTestControl).toString();
							if (varPack.isEmpty()){
								strTestClass = "<class name=\""+varPackage+"."+varModule+"\">\n<methods>\n";
								strTestClass = strTestClass+"<include name=\""+varTCName+"\"/>\n";
								strTestClass = strTestClass +"</methods>\n</class> <!-- "+varPackage+"."+varModule+" -->\n";
								runtimeXML = runtimeXML+strTestClass;
							}else{
								strTestClass = "<class name=\""+varPackage+"."+varPack+"."+varModule+"\">\n<methods>\n";
								strTestClass = strTestClass+"<include name=\""+varTCName+"\"/>\n";
								strTestClass = strTestClass +"</methods>\n</class> <!-- "+varPackage+"."+varPack+"."+varModule+" -->\n";
								runtimeXML = runtimeXML+strTestClass;
							}
						}
					}
					//runtimeXML= runtimeXML+"</classes>\n</test> <!-- "+getDeviceManuf+"_"+getDeviceModel+"-->\n";
					runtimeXML = runtimeXML + "</classes>\n</test> <!-- " + browserType + "-->\n";
					if(testRownum == testNumbers.size()-1){
						break;
					}
				}
			}else if(strFrameWorkRunMode.equalsIgnoreCase("all")) {
				for (int i = 0; i < deviceCnt; i++) {
					// setting test name
					deviceRow = (int) rownumbers.get(i);
					browserType = globalfunctions.getCellValue(excelSheetDevice, "Browser", "Browser", deviceRow)
							.toString();
					runtimeXML = runtimeXML + "\n<test name=\"" + browserType + "\">\n";

					// setting parameter name
					runtimeXML = runtimeXML + "<parameter name=\"browserType\" value=\"" + browserType + "\"/>\n";
					runtimeXML = runtimeXML + "<parameter name=\"appURL\" value=\"" + System.getProperty("appURL")
							+ "\"/>\n<classes>\n";
					runtimeXML = runtimeXML + strTestClass;
					runtimeXML = runtimeXML + "</classes>\n</test> <!-- " + browserType + "-->\n";
				}
			}else if(strFrameWorkRunMode.equalsIgnoreCase("module")) {
				for (int i = 0; i < deviceCnt; i++) {
					// setting test name
					deviceRow = (int) rownumbers.get(i);
					browserType = globalfunctions.getCellValue(excelSheetDevice, "Browser", "Browser", deviceRow)
							.toString();
					runtimeXML = runtimeXML + "\n<test name=\"" + browserType + "\">\n";

					// setting parameter name
					runtimeXML = runtimeXML + "<parameter name=\"browserType\" value=\"" + browserType + "\"/>\n";
					runtimeXML = runtimeXML + "<parameter name=\"appURL\" value=\"" + System.getProperty("appURL")
							+ "\"/>\n<classes>\n";
					runtimeXML = runtimeXML + strTestClass;
					runtimeXML = runtimeXML + "</classes>\n</test> <!-- " + browserType + "-->\n";
				}
			}
			 
			runtimeXML = runtimeXML + "</suite> <!-- " + varSuiteName + " -->";
			// creating new xml files
			File f = null;
			f = new File(System.getProperty("resources") + "/test.xml");
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();

			// writing values in to file
			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(runtimeXML);
			bw.close();
			// running the testng suite files
			List<String> testFilesList = new ArrayList<String>();
			testFilesList.add(System.getProperty("resources") + "/test.xml");
			TestNG tng = new TestNG();
			tng.setTestSuites(testFilesList);
			tng.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterSuite
	public void TestStatus() throws Exception {	}

	// Get list of all the test cases to be executed
	public String getTestCaseList(Sheet excelSheetTest, List<Object> testNumbers, String varPackage) {
		// Variable declaration
		int tstCnt = (int) testNumbers.size();
		int varTestControl;
		int loopControlK = 0;
		String varPack;
		String varModule;
		String varTCName;
		String strTestClass = "";
		String varVerifyModule;

		for (int i = 0; i < tstCnt; i++) {
			varTestControl = (int) testNumbers.get(i);
			varPack = globalfunctions.getCellValue(excelSheetTest, "Test", "Package", varTestControl).toString();
			varModule = globalfunctions.getCellValue(excelSheetTest, "Test", "Module", varTestControl).toString();
			if (varPack.isEmpty()) {
				strTestClass = strTestClass + "<class name=\"" + varPackage + "." + varModule + "\">\n<methods>\n";
			} else {
				strTestClass = strTestClass + "<class name=\"" + varPackage + "." + varPack + "." + varModule
						+ "\">\n<methods>\n";
			}

			for (int j = i; j < tstCnt; j++) {
				varTestControl = (int) testNumbers.get(j);
				varVerifyModule = globalfunctions.getCellValue(excelSheetTest, "Test", "Module", varTestControl)
						.toString();
				varTCName = globalfunctions.getCellValue(excelSheetTest, "Test", "TC_Name", varTestControl).toString();
				if (!(varVerifyModule.equalsIgnoreCase(varModule))) {
					loopControlK = j - 1;
					break;
				} else {
					loopControlK = j;
				}
				strTestClass = strTestClass + "<include name=\"" + varTCName + "\"/>\n";
			}
			i = loopControlK;
			if (varPack.isEmpty()) {
				strTestClass = strTestClass + "</methods>\n</class> <!-- " + varPackage + "." + varModule + " -->\n";
			} else {
				strTestClass = strTestClass + "</methods>\n</class> <!-- " + varPackage + "." + varPack + "."
						+ varModule + " -->\n";
			}

		}
		return strTestClass;
	}
}
