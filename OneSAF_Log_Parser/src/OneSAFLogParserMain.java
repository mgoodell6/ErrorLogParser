package src;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JFileChooser;

public class OneSAFLogParserMain {

public static void main(String[] args) throws IOException {
		
		// Initialize variables and classes
		
		LogFileChooser frame1 = new LogFileChooser();   // for error list
		LogFileChooser frame2 = new LogFileChooser();   // for log file
		File file = null;
		
		LogErrorParser logger = new LogErrorParser();
		
		// Open File Chooser Dialog and provide path/name to file with error list
		frame1.setMyTitle("Open Error List File");
		frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    frame1.pack();
	    frame1.setVisible(true);

		file = frame1.getselectedFile();
		System.out.println("Got the Error List file " + file);
		frame1.dispose();
		
		logger.setErrorFileName(file);  // provide filename for error list to LogErrorParser
		logger.readErrorFile();
		
		// Open File Chooser Dialog and provide path/name to log file
		
		frame2.setMyTitle("Open Log File");
		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    frame2.pack();
	    frame2.setVisible(true);

		file = frame2.getselectedFile();
		System.out.println("Got the Log file " + file);
		frame2.dispose();

		logger.setLogFileName(file);  // provide filename for OneSAF log to LogErrorParser
		logger.readLogFile();
		

	}
	
	
}
