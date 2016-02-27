package src;

public class ErrorInfo {

	private String errorType;
	private String errorClass;
	private String errorTime;
	public int count;  // number of times this error occurs
	public int hourCount[] = new int[50];;  // counts number of errors per hour
	
	private void ErrorInfo() {
		errorType = null;
		errorClass = null;
		errorTime = null;
		for (int i = 0; i< 50; i++ )
			hourCount[i] = 0;
	}  // end constructor

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getErrorClass() {
		return errorClass;
	}

	public void setErrorClass(String errorClass) {
		this.errorClass = errorClass;
	}
	
	public void setErrorTime(String errorTime) {
		this.errorTime = errorTime;
	}
	
	public void setCount(int Count) {
		this.count = Count;
	}
	
	public int getCount() {
		return count;
	}
	
}
