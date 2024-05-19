package org.mq.optculture.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.optculture.exception.BaseServiceException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLUtil {

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public static  boolean validateXMLwithSchema(String xmlFilePath, String xsdFilePath) throws BaseServiceException {
		logger.debug("Entered XMLUtil::: validateXMLwithSchema method ");
		boolean validityFlag = true;
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(true);
			factory.setFeature("http://xml.org/sax/features/validation", true);
			factory.setFeature(
					"http://apache.org/xml/features/validation/schema", true);
			factory
			.setFeature(
					"http://apache.org/xml/features/validation/schema-full-checking",
					true);
			SAXParser parser = factory.newSAXParser();
			parser
			.setProperty(
					"http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
					xsdFilePath);
			FileInputStream fis = new FileInputStream(new File(xmlFilePath));
			//			InputStream xmldoc = new ByteArrayInputStream(fis.getBytes());
			InputSource XmlDocumentUrl = new InputSource(fis);
			parser.parse(XmlDocumentUrl, new DefaultHandler() {
				public void error(SAXParseException saxException)
						throws SAXException {
					throw saxException;
				}
			});
		} catch (SAXParseException e) {
			logger.error("XmlParser::validateXMLwithSchema::SAXParseException"
					+ e);
			validityFlag = false;

		} catch (SAXException e) {
			validityFlag = false;
			logger.error("XmlParser::validateXMLwithSchema::SAXException" + e);
			throw new BaseServiceException(e.getMessage(), e);

		} catch (ParserConfigurationException e) {
			logger
			.error("XmlParser::validateXMLwithSchema::ParserConfigurationException"
					+ e);
			throw new BaseServiceException(
					"ParserConfiguration Exception occured while parsing input string",
					e);
		} catch (FileNotFoundException e) {
			logger.error("XmlParser::validateXMLwithSchema::IOException" + e);
			throw new BaseServiceException(e.getMessage(), e);
		} catch (IOException e) {
			logger
			.error("XmlParser::validateXMLwithSchema::FileNotFoundException"
					+ e);
			throw new BaseServiceException(e.getMessage(), e);
		} catch(Exception e){
			logger.error("XmlParser::validateXMLwithSchema::FileNotFoundException" + e);
			throw new BaseServiceException(e.getMessage(), e);
		}
		logger.debug("Exit from XMLUtil::: validateXMLwithSchema method ");
		return validityFlag;
	}

	public static Object unMarshal(String xmlFilePath, Class xmlClass) throws BaseServiceException{
		logger.debug("Entered XMLUtil::: unMarshal method ");
		Object returnObject = null;
		try{
			FileReader fr = new FileReader(xmlFilePath);
			JAXBContext jc = JAXBContext.newInstance(xmlClass);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLInputFactory xmlif = XMLInputFactory.newInstance();
			XMLStreamReader xmler = xmlif.createXMLStreamReader(fr);
			returnObject = (Object)unmarshaller.unmarshal(xmler);

		}catch(Exception e){
			throw new BaseServiceException(e.getMessage(), e);
		}
		logger.debug("Exit from XMLUtil::: unMarshal method ");
		return returnObject;
	}

	public static void  marshalAndWriteToFileAsXML(String xmlFilePath, Class xmlClass, Object objClass) throws BaseServiceException{
		
		try {
			 String owner = PropertyUtil.getPropertyValueFromDB("owner");
			 String group = PropertyUtil.getPropertyValueFromDB("group");
			
			JAXBContext context = JAXBContext.newInstance(xmlClass);
			Marshaller m = context.createMarshaller();
			//for pretty-print XML in JAXB
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(objClass, new File(xmlFilePath));
			try {
				String chOwnCmd = "chown "+owner+":"+group+" "+xmlFilePath;
				logger.debug("chaging the file permissions =="+ chOwnCmd);
				Process p = Runtime.getRuntime().exec(new String[] {"bash", "-c",chOwnCmd});
                p.waitFor();
				String chmodCmd = "chmod 777 "+xmlFilePath;
				Process p1 = Runtime.getRuntime().exec(new String[] {"bash", "-c",chmodCmd});
                p1.waitFor();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception in while writing failed message.", e);
			}
			
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			logger.error("Error occured while getting or setting the property " , e);
			//e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			logger.error("Error occured while genartating the XML  file " , e);
//			e.printStackTrace();
		}
		
	}

	public static boolean inboxFileExist(String inboxPath) throws Exception {

		File file = new File(inboxPath);
		if(file.exists() && file.isDirectory()){
			if(file.list().length > 0){
				return true;
			}
			else return false;
		}
		return false;
	}
	
	public static boolean renameAndMove(String xmlFile){
		logger.info("Started renaming and moving a file : "+xmlFile);
		try{
			String donePath = PropertyUtil.getPropertyValue("donepromo");
			String renameCmd = "mv "+xmlFile+" "+xmlFile+".txt";
			Runtime.getRuntime().exec(renameCmd);
			
			String moveCmd = "mv "+xmlFile+".txt"+" "+donePath;
			Runtime.getRuntime().exec(moveCmd);
		}catch(Exception e){
			logger.error("Exception in renameandmove enroll xml file" ,e);
			return false;
		}
		logger.info("Completed renaming and moving a file : "+xmlFile);
		return true;
	}

	
	public static boolean checkFileWithXmlExtension(String xmlfile) {
		logger.info("Started checking xml extension of file : "+xmlfile);
		boolean status = false;
		if(xmlfile != null && xmlfile.trim().endsWith(".xml")){
			status = true;
		}
		logger.info("Completed checking xml extension of file : "+xmlfile);
		return status;
	}
	
	
	
	
	
	public static File unzip(String zipFileNamePath, String destDir) {
        File dir = new File(destDir);
        // create output directory if it doesn't exist
        if(!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        //buffer for read and write data to file
        byte[] buffer = new byte[1024];
        File newFile = null;
        try {
            fis = new FileInputStream(zipFileNamePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while(ze != null){
                String fileName = ze.getName();
                newFile = new File(destDir + File.separator + fileName);
                logger.info("Unzipping to "+newFile.getAbsolutePath());
                //create directories for sub directories in zip
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
                }
                fos.close();
                //close this ZipEntry
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            //close last ZipEntry
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
         return newFile;
    }
	
	
	
	static final int BUFFER = 2048;
	public static boolean zipFileCreation(String sourceFileName , String destPathZipFileName){
	   try {
	         BufferedInputStream origin = null;
	         FileOutputStream dest = new FileOutputStream(destPathZipFileName);
	         ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
	         //out.setMethod(ZipOutputStream.DEFLATED);
	         byte data[] = new byte[BUFFER];
	         // get a list of files from current directory
	         
	         File f = new File(sourceFileName);
//	         String files[] = f.list();
	         FileInputStream fi = new FileInputStream(f);
	         origin = new BufferedInputStream(fi, BUFFER);
	         ZipEntry entry = new ZipEntry(f.getName());
	         out.putNextEntry(entry);
	         int count;
	         while((count = origin.read(data, 0, BUFFER)) != -1) {
	        	 out.write(data, 0, count);
	         }
	         origin.close();

//	         for (int i=0; i<files.length; i++) {
//	            System.out.println("Adding: "+files[i]);
//	         }
	         out.close();
	      } catch(Exception e) {
	         e.printStackTrace();
	         return false;
	      }
	   return true;
	}
	

}
