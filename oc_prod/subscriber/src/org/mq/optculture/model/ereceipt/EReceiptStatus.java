package org.mq.optculture.model.ereceipt;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.codehaus.jackson.map.ObjectMapper;
//import org.eclipse.persistence.jaxb.JAXBContextFactory;
//import org.eclipse.persistence.jaxb.MarshallerProperties;
//import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;

public class EReceiptStatus {
	
	private String errorCode;
	private String message;
	private String status;
	
	
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	/*public static void main(String[] args) throws Exception{
		EReceiptReport report = new EReceiptReport();	       
	 
	        // Create a JaxBContext
	       // JAXBContext jc = JAXBContext.newInstance(EReceiptReport.class);
	 
		
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");
		
		JAXBContext jaxbContext = JAXBContextFactory.createContext(new Class[]{EReceiptReport.class, ObjectFactory.class}, properties);

		
	        // Create the Marshaller Object using the JaxB Context
	        Marshaller marshaller = jaxbContext.createMarshaller();
	         
	        // Set the Marshaller media type to JSON or XML
	        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE,
	        		"application/json" );
	         
	        // Set it to true if you need to include the JSON root element in the JSON output
	        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
	         
	        // Set it to true if you need the JSON output to formatted
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		
	        marshaller.marshal(report, new File("/home/pravendra/staff.json"));

		
		
		EReceiptReport report = new EReceiptReport();
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			System.out.println("Start");
			mapper.
			//writing in file json data from java obj
			mapper.writeValue(new File("/home/pravendra/staff.json"), report);	
			
	   System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	*/
}
