package src;


import java.io.File;
import javax.swing.JOptionPane;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class LogErrorParser {
final static Charset ENCODING = StandardCharsets.UTF_8;  // Identify character encoding format
	
	private ErrorInfo[] errorList;  // array of ErrorInfo
	private String[] errorTypes;  // reads list of common error and critical information types from csv file
	private int errorTotalCount;
	private int errorCount;
	private int errorCriticalCount;
	private int uniqueErrorCount;
	private File errorFileName;  //path and filename for list of common error types to be used when sorting
	private File logFileName;   // path and filename for log file to be parsed
	private long nLines = 0;  // counts number of lines in the log
	private int nErrorTypes = 0;  // number of error types read from error types data file
	private int nHours = 0;   // holds number of hours covered in the log
	public int startHour = -1;  // Holds hour from first line in the log
	
	private final int MAX_UNIQUE_ERRORS = 10000;
	
	
	public LogErrorParser() {   // Constructor
		errorList = new ErrorInfo[MAX_UNIQUE_ERRORS];  // initialize array
		errorTypes = new String[50];   // error type then string with error description
		errorTotalCount = 0;
		errorCount = 0;
		errorCriticalCount = 0;
		uniqueErrorCount =0;
		logFileName = null;
	}  // end LogErrorParser constructor
	
	
	public void readErrorFile() throws IOException {
		
		String myLine = null;  // used when reading each line from error type
		
		try (Scanner scanner = new Scanner(errorFileName,ENCODING.name())){
			while (scanner.hasNextLine()){   // routine for reading individual logfile lines
				myLine = scanner.nextLine();
			    errorTypes[nErrorTypes++] = myLine;

			}
		}
	}
	
	
	public void readLogFile() throws IOException {
		
		String myLine = null;  // used to evaluate individual lines of text from logfile
		int errorType = -1;  // used to track error type found in lines

		try (Scanner scanner = new Scanner(logFileName,ENCODING.name())){
			while (scanner.hasNextLine()){   // routine for reading individual logfile lines
				nLines++;
				myLine = scanner.nextLine();
				
				if (startHour == -1 && myLine.length() > 10)  
					startHour = Integer.valueOf(myLine.substring(6, 8));
				
				// Check for error in scanned line
				errorType = detectError(myLine);
				
				switch (errorType)  {
					case 1:      // CRITICAL_INFO
						errorCriticalCount++;
						errorTotalCount++;
					
						parseError(myLine,"CRITICAL_INFO");
						
						break;
					case 2:      // ERROR
						errorCount++;
						errorTotalCount++;
						
						parseError(myLine,"ERROR");
						
						break;
					
				}
				

			}
			
			sortArray();
			
			printArray();
			
		}
		errorTotalCount = errorCriticalCount + errorCount;
		System.out.println("Lines in file Parsed - total number of lines = " + nLines);
		System.out.println("Total Errors = " + errorTotalCount + " Critical_Info = " + errorCriticalCount + " Errors = " + errorCount);
		System.out.println("Number of Unique Error types = " + uniqueErrorCount);
		JOptionPane.showMessageDialog(null, "Parsing complete. Total lines read = " + nLines, 
				"InfoBox: Completion Summary", JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	
	private int detectError(String myLine) {
		
		int errorType = 0;    // used to return error type detected
		  // Type 1 - CRITICAL_INFO
		  // Type 2 - ERROR
		
		if (myLine.contains("CRITICAL_INFO")) {
			errorType = 1;
		}
		else if (myLine.contains("ERROR")){
			errorType = 2;
		}
		
		return errorType;
	}
	
	public void parseError(String myLine, String eCat ) {
		
		int errorLocation;
		int stringPointer;  // used as temporary pointer for extracting substrings from scanned line
		int stringPointer2;  // another temporary string pointer
		int count; 

		String eType = null;
		String eClass = null;
		String eTime = null;
 
		for (int i=0; i < nErrorTypes; i++) {
			if (myLine.contains(errorTypes[i]))
				eType = eCat + " - " + errorTypes[i];
		}
		
		

			
			/*
			// General messages
			if (myLine.contains("Received DYING packet from")) 
				eType = "CRITICAL_INFO - SYNC: Waiting for nodes to sync. ";
			if (myLine.contains("NodeList: Removing node:")) 
				eType = "CRITICAL_INFO - NodeList: Removing node: ";
			if (myLine.contains("removing app node:")) 
				eType = "CRITICAL_INFO - removing app node: ";
			
			// SIMCORE common message types
			if (myLine.contains("Waiting for nodes to sync.")) 
				eType = "CRITICAL_INFO - Received DYING packet from ";
			
			// OSS Server message types
			if (myLine.contains("Sending big message")) 
				eType = "CRITICAL_INFO - Received DYING packet from ";
			if (myLine.contains("NodeManager.nodeGone() removing node IP")) 
				eType = "CRITICAL_INFO - NodeManager.nodeGone() removing node IP";
			*/

			
			/* General message types
			if (myLine.contains("Ignoring packet from 'deceased' node:")) 
				eType = "ERROR - Ignoring packet from 'deceased' node:";
			
			// SIMCORE common message types
			if (myLine.contains("Failed to send requestedLogisticsReport message")) 
				eType = "ERROR - Failed to send requestedLogisticsReport message";
			if (myLine.contains("already has supplemental data of type net.onesaf.core.services.data.dm.rdm.org.ERFActorData ignoring")) 
				eType = "ERROR - already has supplemental data of type net.onesaf.core.services.data.dm.rdm.org.ERFActorData ignoring";
			if (myLine.contains("Characteristic Indirect Maximum Range Data Not Found:")) 
				eType = "ERROR - Characteristic Indirect Maximum Range Data Not Found:";
			if (myLine.contains("Unable to link communication capability Comm Device with a comm device in the scenario file for entity")) 
				eType = "ERROR - Unable to link communication capability Comm Device with a comm device in the scenario file for entity";
			if (myLine.contains("Muzzle Velocity Data Not Found:")) 
				eType = "ERROR - Muzzle Velocity Data Not Found:";
			if (myLine.contains("Cannot determine type of entity based on EntityTypeEnum")) 
				eType = "ERROR - ConstructiveModelSelectorWrapper.getEntityCategory() ERROR Cannot determine type of entity based on EntityTypeEnum data!";
			
			// MCT common message types
			if (myLine.contains("Characteristic Indirect Range Data Not Found:")  || myLine.contains("Characteristic Indirect Range Data Not Found:" )) 
				eType = "ERROR - Characteristic Indirect Range Data Not Found:";
			if (myLine.contains("Cannot determine type of entity based on EntityTypeEnum data!")) 
				eType = "ERROR - Cannot determine type of entity based on EntityTypeEnum data!";
			if (myLine.contains("No data for key: System:")) 
				eType = "ERROR - No data for key: System:";
			if (myLine.contains("Received ReapedNode heartbeat from")) 
				eType = "ERROR - Received ReapedNode heartbeat from ......";
			if (myLine.contains("has no commander!")) 
				eType = "ERROR - ..... <unit> has no commander!";
			if (myLine.contains("has a Commander  TacticalRel with null parent")) 
				eType = "ERROR - ..... <unit> has a Commander  TacticalRel with null parent";
			if (myLine.contains("Thread starvation on: NodeListener")) 
				eType = "ERROR - Thread starvation on: NodeListener";
			
			// OSS Server common message types
			if (myLine.contains("Task(net.onesaf.core.services.data.dm.rdm.c2.RDMTask")) 
				eType = "ERROR - Task(net.onesaf.core.services.data.dm.rdm.c2.RDMTask";
			
			*/
			

		// Extract substrings for error type, class, and time
		
		errorLocation = myLine.indexOf(eCat);
		stringPointer = myLine.indexOf(" ", errorLocation + eCat.length()+1);
		eClass = myLine.substring(errorLocation+eCat.length(), stringPointer );
		stringPointer2 = myLine.length();
		if ( eType == null) {   // if error type not previously assigned
			eType = eCat + myLine.substring(stringPointer, stringPointer2);
		}
		eTime = myLine.substring(1, myLine.indexOf(" ", 1));  // extract time that error occurred
			

		// Check for previous occurrences of the same error
		if ( CheckUniqueError(eType, myLine) && uniqueErrorCount < MAX_UNIQUE_ERRORS-1) {   // if unique error  and not exceeding max limit then create new error entry
			
			errorList[++uniqueErrorCount] = new ErrorInfo();   // Initialize new errorList element
			errorList[uniqueErrorCount].setErrorTime(eTime);
			errorList[uniqueErrorCount].setErrorClass(eClass);
			errorList[uniqueErrorCount].setErrorType(eType);
			count = errorList[uniqueErrorCount].getCount();
			errorList[uniqueErrorCount].setCount(count+1);
			int eHour = Integer.valueOf(myLine.substring(6, 8));
			int deltaHour = eHour - startHour;
			if (deltaHour < 0)        // adjust for rollover past midnight
				deltaHour = 24 - startHour + eHour;
			if (deltaHour > nHours )
				nHours = deltaHour;
			errorList[uniqueErrorCount].hourCount[deltaHour]++;
		}  // end if
		

		
	}  // end method parseError
	
	private boolean CheckUniqueError(String eType, String myLine) {
		
		String sTemp = null;   // temp string holder
		
		if (uniqueErrorCount == 0 )
			return true;
		else {
			
			for (int x=1; x<= uniqueErrorCount; x++ ) {
				sTemp = errorList[x].getErrorType();
				if (sTemp.equals(eType))   {   // Compare to see if error types are the same
					int count = errorList[x].getCount();
					errorList[x].setCount(count+1);
					int eHour = Integer.valueOf(myLine.substring(6, 8));
					int deltaHour = eHour - startHour;
					if (deltaHour < 0)        // adjust for rollover past midnight
						deltaHour = 24 - startHour + eHour;
					if (deltaHour > nHours )
						nHours = deltaHour;
					errorList[x].hourCount[deltaHour]++;
					return false;
				}  // end if
					
			}  // end for loop
			
			return true;
			
		}  // end if else statement
	}  // end method CheckUniqueError
	
	public void sortArray() {
	
		int maxValue = 0;   // stores max value for each pass
		int maxPosition = 0;  // stores position of max value
		
		for(int x =1; x < uniqueErrorCount; x++ )  {
			for (int y=x; y< uniqueErrorCount; y++ )  {
				if (errorList[y].count > maxValue )  {
					maxValue = errorList[y].count;
					maxPosition = y;
				}  // end if
					
			}  // next y
			if (maxPosition > 0)
				 swapData(x,maxPosition);
			maxValue = 0;
			maxPosition = 0;
		}  // next x
	
	}  // end method sortArray
	
	public void printArray()  {
		
		int temp=-1;
		String myPath = null;
		String tPath1;
		String tPath2;
		String tPath3;
		
		myPath = logFileName.getPath();  // use same file path as input file
		tPath1 = logFileName.getPath();
		tPath2 = logFileName.getAbsolutePath();

		for (int i=0; i<myPath.length(); i++ )  {
			char charValue = myPath.charAt(i);
			if (charValue == '\\')
				temp = i;
		}
		
		myPath = myPath.substring(0, temp+1 ) + "output.csv";
		try{ 
			PrintStream out = new PrintStream(new FileOutputStream(myPath));
			
			errorTotalCount = errorCriticalCount + errorCount;
			out.println("Number of simulation hours in log = " + nHours );
			out.println("Lines in file Parsed - total number of lines = " + nLines);
			out.println("Total Errors = " + errorTotalCount + " Critical_Info = " + errorCriticalCount + " Errors = " + errorCount);
			out.println("Number of Unique Error types = " + uniqueErrorCount);
			out.println("   ");   
			out.print("Number of Occurrences ,");
			for (int h = 1; h < 10; h++ ) {   // Print Simulation Hour for column header
				int eHour = startHour + h - 1;
				if (eHour > 24)
					eHour = eHour - 24;
				out.print(" Hr - " + eHour + " , "); 
			}  // end for loop
			out.println(" , OneSAF Class , Error Description");
			for (int i = 1; i <= uniqueErrorCount; i++ ) {
				out.print( errorList[i].getCount() + ", ");
				for (int j = 0; j < 10; j++ )
					out.print( errorList[i].hourCount[j] + " , ");
				out.println(errorList[i].getErrorClass() + ", " + 
						errorList[i].getErrorType() + ", " );
			}  // end for loop
			
			out.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
		}  // end catch
	}  // end method printArray
	
	// swap positions in the errorList array - move max count to top of the stack
	public void swapData(int x1, int x2 )  {
		ErrorInfo tempError;
		tempError = new ErrorInfo();
		
		tempError = errorList[x1];
		errorList[x1] = errorList[x2];
		errorList[x2] = tempError;
		
	}
	
	public void setLogFileName(File nameFile) {
		logFileName = nameFile;
	}
	
	public void setErrorFileName(File nameFile) {
		errorFileName = nameFile;
	}
	
	
	
}
