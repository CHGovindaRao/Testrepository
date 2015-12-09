package com.qtpselenium.test;

//import java.io.File;
import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Properties;

//import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.qtpselenium.util.ReportUtil;
//import com.qtpselenium.util.TestUtil;
//import com.qtpselenium.util.TestUtil;
import com.qtpselenium.xls.read.Xls_Reader;

public class DriverScript {

	private static Logger APP_LOGS;
	//suite.xlsx
	private Xls_Reader suiteXLS;
	private int currentSuiteID;
	public String currentTestSuite;
	
	
	// current test suite
	protected static Xls_Reader currentTestSuiteXLS;
		
	private static int currentTestCaseID;
	protected static String currentTestCaseName;
	private static int currentTestStepID;
	private static String currentKeyword;
	protected static int currentTestDataSetID=2;
	private static Method method[];
	private static Method capturescreenShot_method;
	

	private static Keywords keywords;
	private static String keyword_execution_result;
	private static ArrayList<String> resultSet;
	private static String data;
	private static String object;
	
	// properties
	protected static Properties CONFIG;
	protected static Properties OR;
	private static boolean isInitalized=false;

	public DriverScript() throws NoSuchMethodException, SecurityException{
		keywords = new Keywords();
		method = keywords.getClass().getMethods();
		capturescreenShot_method =keywords.getClass().getMethod("captureScreenshot",String.class,String.class);
	}
	
	@BeforeClass 
	public void Intialize() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException, NoSuchMethodException, SecurityException 
	
	{
		if(!isInitalized){
		FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//com//qtpselenium//config//config.properties");
		CONFIG= new Properties();
		CONFIG.load(fs);
		
		fs = new FileInputStream(System.getProperty("user.dir")+"//src//com//qtpselenium//config//or.properties");
		OR= new Properties();
		OR.load(fs);
		
		APP_LOGS = Logger.getLogger("devpinoyLogger");
		APP_LOGS.debug("Hello");
		APP_LOGS.debug("Properties loaded. Starting testing");
		// 1) check the runmode of test Suite
		// 2) Runmode of the test case in test suite
	    // 3) Execute keywords of the test case serially
		// 4) Execute Keywords as many times as
		// number of data sets - set to Y
		APP_LOGS.debug("Intialize Suite xlsx");
		suiteXLS = new Xls_Reader(System.getProperty("user.dir")+"//src//com//qtpselenium//xls//Suite.xlsx");
		isInitalized=true;
		}
		//System.out.println(CONFIG.getProperty("testsiteURL"));
		//System.out.println(OR.getProperty("name"));
		
	}
	
	@Test 
	public void testApp() throws Exception{
		
		for(currentSuiteID=2;currentSuiteID<=suiteXLS.getRowCount(Constants.TEST_SUITE_SHEET);currentSuiteID++){
			
			APP_LOGS.debug(suiteXLS.getCellData(Constants.TEST_SUITE_SHEET, Constants.Test_Suite_ID, currentSuiteID)+" -- "+  suiteXLS.getCellData("Test Suite", "Runmode", currentSuiteID));
			// test suite name = test suite xls file having tes cases
			currentTestSuite=suiteXLS.getCellData(Constants.TEST_SUITE_SHEET, Constants.Test_Suite_ID, currentSuiteID);
			if(suiteXLS.getCellData(Constants.TEST_SUITE_SHEET, Constants.RUNMODE, currentSuiteID).equals(Constants.RUNMODE_YES)){
				// execute the test cases in the suite
				APP_LOGS.debug("******Executing the Suite******"+suiteXLS.getCellData(Constants.TEST_SUITE_SHEET, Constants.Test_Suite_ID, currentSuiteID));
				currentTestSuiteXLS=new Xls_Reader(System.getProperty("user.dir")+"//src//com//qtpselenium//xls//"+currentTestSuite+".xlsx");
				// iterate through all the test cases in the suite
				for(currentTestCaseID=2;currentTestCaseID<=currentTestSuiteXLS.getRowCount("Test Cases");currentTestCaseID++){				
					APP_LOGS.debug(currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, Constants.TCID, currentTestCaseID)+" -- "+currentTestSuiteXLS.getCellData("Test Cases", "Runmode", currentTestCaseID));
					currentTestCaseName=currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, Constants.TCID, currentTestCaseID);
									
					if(currentTestSuiteXLS.getCellData(Constants.TEST_CASES_SHEET, Constants.RUNMODE, currentTestCaseID).equals(Constants.RUNMODE_YES)){
						APP_LOGS.debug("Executing the test case -> "+currentTestCaseName);
					 if(currentTestSuiteXLS.isSheetExist(currentTestCaseName)){
					  	// RUN as many times as number of test data sets with runmode Y
					  for(currentTestDataSetID=2;currentTestDataSetID<=currentTestSuiteXLS.getRowCount(currentTestCaseName);currentTestDataSetID++)	
					  {
						resultSet = new ArrayList<String>();
						APP_LOGS.debug("Iteration number "+(currentTestDataSetID-1));
						// checking the runmode for the current data set
					   if(currentTestSuiteXLS.getCellData(currentTestCaseName, Constants.RUNMODE, currentTestDataSetID).equals(Constants.RUNMODE_YES)){
						
					    // iterating through all keywords	
						   executeKeywords(); // multiple sets of data
					   }
					   createXLSReport();
					   ReportUtil.Report();
					  
					   
					   }
					 }else{
						// iterating through all keywords	
						 resultSet= new ArrayList<String>();
						 executeKeywords();// no data with the test
						 createXLSReport();
					 }
					 
					 
					}
				}
			}

		}	
	}
	
	
	public void executeKeywords() throws Exception {
		// iterating through all keywords	
		for(currentTestStepID=2;currentTestStepID<=currentTestSuiteXLS.getRowCount(Constants.TEST_STEPS_SHEET);currentTestStepID++){
			// checking TCID
		  if(currentTestCaseName.equals(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.TCID, currentTestStepID))){
						
			data=currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.DATA,currentTestStepID  );
			if(data.startsWith(Constants.DATA_START_COL)){
				// read actual data value from the corresponding column				
				data=currentTestSuiteXLS.getCellData(currentTestCaseName, data.split(Constants.DATA_SPLIT)[1] ,currentTestDataSetID );
			}else if(data.startsWith(Constants.CONFIG)){
				//read actual data value from config.properties		
				data=CONFIG.getProperty(data.split(Constants.DATA_SPLIT)[1]);
			}else{
				//by default read actual data value from or.properties
				data=OR.getProperty(data);
			}
			object=currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.OBJECT,currentTestStepID  );
				currentKeyword=currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.KEYWORD, currentTestStepID);
				APP_LOGS.debug(currentKeyword);
				// code to execute the keywords as well
			    // reflection API
				
				for(int i=0;i<method.length;i++){
					
					if(method[i].getName().equals(currentKeyword)){
					keyword_execution_result=(String)method[i].invoke(keywords,object,data);
					APP_LOGS.debug(keyword_execution_result);
					resultSet.add(keyword_execution_result);
					
					if(keyword_execution_result.startsWith(Constants.KEYWORD_FAIL)){
					capturescreenShot_method.invoke(keywords,
							currentTestSuite+"_"+currentTestCaseName+"_TS"+currentTestStepID+"_"+(currentTestDataSetID-1),
							keyword_execution_result);
										
					}							
					
					}
					
					
				}
				
				 
			}
		  
		}
		
		
		 
	}
	
	
	
	
	
	public void createXLSReport() throws Exception{
		
		
		
		String colName=Constants.RESULT +(currentTestDataSetID-1);
		boolean isColExist=false;
		
		for(int c=0;c<currentTestSuiteXLS.getColumnCount(Constants.TEST_STEPS_SHEET);c++){
			if(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET,c , 1).equals(colName)){
				isColExist=true;
				break;
			}
		}
		
		if(!isColExist)
			currentTestSuiteXLS.addColumn(Constants.TEST_STEPS_SHEET, colName);
		int index=0;
		for(int i=2;i<=currentTestSuiteXLS.getRowCount(Constants.TEST_STEPS_SHEET);i++){
			
			if(currentTestCaseName.equals(currentTestSuiteXLS.getCellData(Constants.TEST_STEPS_SHEET, Constants.TCID, i))){
				if(resultSet.size()==0)
					currentTestSuiteXLS.setCellData(Constants.TEST_STEPS_SHEET, colName, i, Constants.KEYWORD_SKIP);
				else	
					currentTestSuiteXLS.setCellData(Constants.TEST_STEPS_SHEET, colName, i, resultSet.get(index));
				index++;
			}
			
			
		}
		
		if(resultSet.size()==0){
			// skip
			currentTestSuiteXLS.setCellData(currentTestCaseName, Constants.RESULT, currentTestDataSetID, Constants.KEYWORD_SKIP);
			return;
		}else{
			for(int i=0;i<resultSet.size();i++){
				if(!resultSet.get(i).equals(Constants.KEYWORD_PASS)){
					currentTestSuiteXLS.setCellData(currentTestCaseName, Constants.RESULT, currentTestDataSetID, resultSet.get(i));
					return;
				}
			}
		}
		
		currentTestSuiteXLS.setCellData(currentTestCaseName, Constants.RESULT, currentTestDataSetID, Constants.KEYWORD_PASS);
	//	if(!currentTestSuiteXLS.getCellData(currentTestCaseName, "Runmode",currentTestDataSetID).equals("Y")){}
	
		ReportUtil.Report();
	
	}
	
	
	
}


	

