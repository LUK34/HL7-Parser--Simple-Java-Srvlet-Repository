package hl7.parse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MyParser  {

	String message;
	private Long totalLines = (long) 0;
	private Long MSHCount = (long) 0;
	private Long PIDCount = (long) 0;
	private Long ORCCount = (long) 0;
	private Long totalDGs = (long) 0;
	private Long OBRCounter = (long) 0;
	private Long OBXCounter = (long) 0;
	private String MSH;
	private String PID;
	private String ORC;
	private Map<String, String> element = new HashMap<String, String>();
	private String currentOBR;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		onParse();
	}
	
	public Long getTotalDGs() {
		return totalDGs;
	}

	public Long getTotalOBRs() {
		return OBRCounter;
	}

	public Long getORCCount() {
		return ORCCount;
	}

	public String getORC() {
		return ORC;
	}

	public String getMSH() {
		return MSH;
	}

	public String getPID() {
		return PID;
	}

	public Long getPIDCount() {
		return PIDCount;
	}

	public Long getMSHCount() {
		return MSHCount;
	}

	public Long getTotalLines() {
		return totalLines;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MyParser(String message) {
		this.message = message;
	}

	

	

	public String getValue(String key) {
		return element.get(key);
	}

	public void parseIt() {
		String delimiter = "\\r";
		String[] temp;
		temp = message.split(delimiter);
		for (int i = 0; i < temp.length; i++) {
			// Store the lines if developer wants it
			element.put("Segment-" + i, temp[i]);
			parseSegment(temp[i]);
			totalLines++;
		}

		// Write the final one
		// Store the total OBX Count -- OBR[1]OBX-count
		if (this.currentOBR != null) {
			String key = "OBR[" + OBRCounter + "]" + "OBX-count";
			element.put(key, String.valueOf(OBXCounter));

		}
	}

	private void parseSegment(String line) {
		String segment;
		segment = line.substring(0, 3);
		if (segment.equalsIgnoreCase("MSH"))
			parseMSH(line);

		if (segment.equalsIgnoreCase("PID"))
			parsePID(line);

		if (segment.equalsIgnoreCase("ORC"))
			parseORC(line);

		if (segment.equalsIgnoreCase("DG1") || segment.equalsIgnoreCase("DG2")
				|| segment.equalsIgnoreCase("DG3")
				|| segment.equalsIgnoreCase("DG4"))
			parseDGs(line);

		if (segment.equalsIgnoreCase("OBR"))
			parseOBRs(line);

		if (segment.equalsIgnoreCase("OBX"))
			parseOBXs(line);

	}

	private void parseDGs(String line) {

		String delimiter = "\\|";
		String[] temp;
		temp = line.split(delimiter);
		totalDGs++;
		element.put("DG-" + totalDGs, line);
		element.put("DG-" + totalDGs + "-count", String.valueOf(temp.length));
		for (int i = 0; i < temp.length; i++) {
			element.put("DG[" + totalDGs + "]-" + i, temp[i]);
		}

	}

	private void parseOBRs(String line) {

		String delimiter = "\\|";
		String[] temp;
		temp = line.split(delimiter);

		// Store the total OBX Count -- OBR[1]OBX-count
		if (this.currentOBR != null) {
			String key = "OBR[" + OBRCounter + "]" + "OBX-count";
			element.put(key, String.valueOf(OBXCounter));
		}
		OBRCounter++;
		element.put("OBR[" + OBRCounter + "]", line);
		element.put("OBR[" + OBRCounter + "]" + "-count",
				String.valueOf(temp.length));
		this.currentOBR = "OBR[" + OBRCounter + "]";
		// Reset the OBX counter
		OBXCounter = (long) 0;
		for (int i = 0; i < temp.length; i++) {
			element.put("OBR[" + OBRCounter + "]-" + i, temp[i]);
			if (i == 4 || i == 16) //
			{
				parseSubComponent(temp[i], "OBR[" + OBRCounter + "]-" + i);
			}
		}

	}

	private void parseOBXs(String line) {

		// Here we need to carefull, OBX are under OBR, so we need to attach to
		// the correct OBR
		String delimiter = "\\|";
		String[] temp;
		String key;
		temp = line.split(delimiter);
		OBXCounter++;
		key = this.currentOBR + "OBX[" + OBXCounter + "]";
		// Very first thing is, We need to Print the OBX Line under OBR as
		// OBR[1]OBX[1]
		element.put(key, line);
		// OBR[1]OBX[1]-count
		element.put(key + "-count", String.valueOf(temp.length));
		// OBR[1]OBX[1]-1, // OBR[1]OBX[1]-2,etc
		for (int i = 0; i < temp.length; i++) {
			element.put(key + "-" + i, temp[i]);
		}
	}

	private void parseMSH(String line) {

		String delimiter = "\\|";
		String[] temp;
		temp = line.split(delimiter);
		this.MSH = line;
		for (int i = 0; i < temp.length; i++) {
			element.put("MSH-" + i, temp[i]);
			MSHCount++;
		}

	}

	private void parseORC(String line) {

		String delimiter = "\\|";
		String[] temp;
		temp = line.split(delimiter);
		this.ORC = line;
		for (int i = 0; i < temp.length; i++) {
			element.put("ORC-" + i, temp[i]);
			if (i == 12 || i == 17 || i == 18) //
			{
				parseSubComponent(temp[i], "ORC-" + i);
			}
			ORCCount++;
		}

	}

	private void parsePID(String line) {

		String delimiter = "\\|";
		String[] temp;
		temp = line.split(delimiter);
		this.PID = line;
		for (int i = 0; i < temp.length; i++) {

			element.put("PID-" + i, temp[i]);
			if (i == 5 || i == 11) // Let us parse patient Name
			{
				parseSubComponent(temp[i], "PID-" + i);
			}
			PIDCount++;
		}

	}

	private void parseSubComponent(String line, String mapKey) {

		String delimiter = "\\^";
		String[] temp;
		temp = line.split(delimiter);
		int j;
		for (int i = 0; i < temp.length; i++) {
			j = i + 1;
			element.put(mapKey + "-" + j, temp[i]);
		}

	}




public static void onParse() {
	String element;
	String value;
	String hl7Message = getStringFromInputStream("c:\\temp\\two.hl7");
	System.out.println("HL7 Content is " + hl7Message);
	MyParser myParser = new MyParser(hl7Message);
	myParser.parseIt();

	element = "Receiving Facility";
	value = myParser.getValue("MSH-5");
	System.out.println(element + " : " + value);

	element = "Patient No";
	value = myParser.getValue("PID-2");
	System.out.println(element + " : " + value);

	element = "Lab Accession Number";
	value = myParser.getValue("PID-4");
	System.out.println(element + " : " + value);

	element = "Patient Last Name";
	value = myParser.getValue("PID-5-1");
	System.out.println(element + " : " + value);

	element = "Patient First  Name";
	value = myParser.getValue("PID-5-2");
	System.out.println(element + " : " + value);

	element = "Patient Middle  Name";
	value = myParser.getValue("PID-5-3");
	System.out.println(element + " : " + value);

	element = "Patient DOB";
	value = myParser.getValue("PID-7");
	System.out.println(element + " : " + value);

	element = "Patient Sex";
	value = myParser.getValue("PID-8");
	System.out.println(element + " : " + value);

	element = "Provider NPI";
	value = myParser.getValue("ORC-12-1");
	System.out.println(element + " : " + value);

	element = "Provider Last Name";
	value = myParser.getValue("ORC-12-2");
	System.out.println(element + " : " + value);

	element = "Provider First Name";
	value = myParser.getValue("ORC-12-3");
	System.out.println(element + " : " + value);

	element = "Provider Initial";
	value = myParser.getValue("ORC-12-5");
	System.out.println(element + " : " + value);

	element = "Account No";
	value = myParser.getValue("ORC-17-1");
	System.out.println(element + " : " + value);

	element = "Account Name";
	value = myParser.getValue("ORC-17-2");
	System.out.println(element + " : " + value);

	element = "Account Address";
	value = myParser.getValue("ORC-18-1");
	System.out.println(element + " : " + value);

	element = "Account City";
	value = myParser.getValue("ORC-18-2");
	System.out.println(element + " : " + value);

	element = "Account State";
	value = myParser.getValue("ORC-18-3");
	System.out.println(element + " : " + value);

	element = "Account Zip";
	value = myParser.getValue("ORC-18-4");
	System.out.println(element + " : " + value);

	element = "Account Phone";
	value = myParser.getValue("ORC-18-5");
	System.out.println(element + " : " + value);

	System.out
			.println("*********************************ALL ABOUT MSH ***********************");
	// Let us print the MSH Values
	for (int i = 0; i < myParser.getMSHCount(); i++) {
		System.out.println("MSH-" + i + ": "
				+ myParser.getValue("MSH-" + i));
	}

	// Ofcouse If you want to you get particular value also as follows
	System.out.println("Please give me MSH[3] "
			+ myParser.getValue("MSH-3"));

	// Ofcourse You can also print MSH String
	System.out.println("Please give me MSH Line..." + myParser.getMSH());

	System.out
			.println("*********************************ALL ABOUT PID ***********************");
	// Ofcouse If you want to you get particular value also as follows
	System.out.println("Please give me PID[3] "
			+ myParser.getValue("PID-3"));

	// Ofcourse You can also print PID String
	System.out.println("Please give me PID Line..." + myParser.getPID());

	// Component 1 [ST] family name (last name)
	// Component 2 [ST] given name (first name)
	// Component 3 [ST] middle initial or name
	// Component 4 [ST] suffix (jr, sr, etc)
	// Component 5 [ST] prefix (Mr, Mrs, Dr etc)
	// Component 6 [ST] degree (MD, PHD etc)
	// Component 7 [ID] name type code

	// Ofcouse You can print Patient Last Name as follows
	System.out.println("Please give me Patient Last Name "
			+ myParser.getValue("PID-5-1"));

	// Ofcouse You can print Patient First Name as follows
	System.out.println("Please give me Patient First Name "
			+ myParser.getValue("PID-5-2"));

	// Ofcouse You can print Patient Address as follows
	System.out.println("Please give me Patient Address "
			+ myParser.getValue("PID-11-1"));

	// Ofcouse You can print Patient Address as follows
	System.out.println("Please give me Patient City "
			+ myParser.getValue("PID-11-2"));

	// Ofcouse You can print Patient Address as follows
	System.out.println("Please give me Patient State "
			+ myParser.getValue("PID-11-3"));

	// Ofcouse You can print Patient Address as follows
	System.out.println("Please give me Patient Zip "
			+ myParser.getValue("PID-11-4"));

	// Let us print the PID Values
	for (int i = 0; i < myParser.getPIDCount(); i++) {
		System.out.println("PID-" + i + ": "
				+ myParser.getValue("PID-" + i));
	}

	System.out
			.println("*********************************ALL ABOUT ORC***********************");
	// Let us print the ORC Values
	for (int i = 0; i < myParser.getORCCount(); i++) {
		System.out.println("ORC-" + i + ": "
				+ myParser.getValue("ORC-" + i));
	}
	// Ofcouse If you want to you get particular value also as follows
	System.out.println("Please give me ORC[2] "
			+ myParser.getValue("ORC-2"));

	// Ofcourse You can also print ORC String
	System.out.println("Please give me ORC Line..." + myParser.getORC());
	
	System.out
	.println("*********************************ALL ABOUT DG***********************");
	// Total Number of DGs
	System.out.println("Please give me Total DGs " + myParser.getTotalDGs());
	// Let us print the DGs
	for (int i = 1; i <= myParser.getTotalDGs(); i++) {
		System.out.println("DG-" + i + ": " + myParser.getValue("DG-" + i));
	}

	for (int i = 1; i <= myParser.getTotalDGs(); i++) {
		int total = Integer.parseInt(myParser
				.getValue("DG-" + i + "-count"));
		for (int j = 1; j < total; j++) {
			System.out.println("DG[" + i + "]-" + j + " : "
					+ myParser.getValue("DG[" + i + "]-" + j));
		}

	}
	
	System.out
	.println("*********************************ALL ABOUT OBR***********************");
	// Total Number of OBRs
	System.out.println("Please give me Total OBR " + myParser.getTotalOBRs());
	// Let us print the OBRs
	String key;
	for (int i = 1; i <= myParser.getTotalOBRs(); i++) {
		key = "OBR[" + i + "]";
		System.out.println(key + " : " + myParser.getValue(key));
		// We can also retieve the total elements in each OBR
		System.out.println("Total Elements in " + key + " is "
				+ myParser.getValue(key + "-count"));
	}

	for (int i = 1; i <= myParser.getTotalOBRs(); i++) {
		key = "OBR[" + i + "]-count";
		int total = Integer.parseInt(myParser.getValue(key));
		for (int j = 1; j < total; j++) {
			key = "OBR[" + i + "]-" + j;
			System.out.println(key + " : " + myParser.getValue(key));
		}

	}
	
	System.out
	.println("*********************************ALL ABOUT OBX***********************");
 
	key = "OBR[1]OBX-count";
	// For given OBR, You can find how many OBXs are there as follows
	System.out.println("Please give me Total OBXs in OBR[1] "
			+ myParser.getValue(key));

	key = "OBR[2]OBX-count";
	System.out.println("Please give me Total OBXs in OBR[2] "
			+ myParser.getValue(key));

	key = "OBR[1]OBX[1]";
	// You can also Print the OBX Line in each OBR as follows
	System.out.println("Please give me OBX[1] in OBR[1] " + myParser.getValue(key));
	key = "OBR[1]OBX-count";
	int total = Integer.parseInt(myParser.getValue(key));

	// You can also Print all OBXs in OBR[1]
	for (int i = 1; i <= total; i++) {
		key = "OBR[1]OBX[" + i + "]";
		System.out.println("OBR[1]OBX[" + i + "] => " + myParser.getValue(key));
	}

	// Dont know how many OBRs and how many OBXs in each OBRs, no problem,
	// we can put them in the loop
	for (int i = 1; i <= myParser.getTotalOBRs(); i++) {
		key = "OBR[" + i + "]";
		System.out.println(key + " : " + myParser.getValue(key));
		key = key + "OBX-count";
		total = Integer.parseInt(myParser.getValue(key));
		for (int j = 1; j <= total; j++) {
			key = "OBR[" + i + "]" + "OBX[" + j + "]";
			System.out.println("----->" + key + " : " + myParser.getValue(key));
		}
	}
}

// convert InputStream to String
private static String getStringFromInputStream(String fileName) {

	BufferedReader br = null;
	StringBuilder sb = new StringBuilder();

	String line;
	try {
		br = new BufferedReader(new FileReader(fileName));
		while ((line = br.readLine()) != null) {
			sb.append(line + "\r");
		}

	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		if (br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	return sb.toString();

}

}