Please refer the  HL7 parser java code attached in this email. The objective of this servlet program is for the medas developer to understand how to parse the values of a HL7 message  received from PACS. This servlet program will display the following required information at the eclipse console . They are as follows:

HL7 Output from test/shared folder
Patient Demographic Details
MSH Details
PID Details
ORC Details
DG Details
OBR Details
OBX Details

Note:

Download the zip file attached in this email. The zip file contains the servlet code and HL7 file to run the test. The HL7 message is present in 'sample HL7 test'

folder. Paste this folder in C Drive or whichever drive is available at your local PC. Based on the folder path at which it is placed. Modify the path at line 261

(MyParser.java). Run the `MyParser.java` and you will see all the details.