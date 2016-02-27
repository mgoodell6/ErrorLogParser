package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class LogFileChooser extends JFrame {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private File selectedFile;

	private String myTitle;  // title for file open gui
	
	public LogFileChooser() {

		JFileChooser fileChooser = new JFileChooser();
		selectedFile = null;
		
		fileChooser.addActionListener(new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		        System.out.println("Action");

		      }
		    });
		
		 int status = fileChooser.showDialog(null, myTitle);

		    if (status == JFileChooser.APPROVE_OPTION) {
		      selectedFile = fileChooser.getSelectedFile();
		      System.out.println(selectedFile.getParent());
		      System.out.println(selectedFile.getName());
		    } else if (status == JFileChooser.CANCEL_OPTION) {
		      System.out.println("canceled");

		    }
	 }
	
	public File getselectedFile() {
		return selectedFile;
	}
	
	public void setMyTitle( String title) {
		myTitle = title;
	}
	
	
}
