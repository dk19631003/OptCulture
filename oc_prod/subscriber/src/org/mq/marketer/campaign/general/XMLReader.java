package org.mq.marketer.campaign.general;

import java.io.ByteArrayInputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	public static HashMap<String, String> read(String xml,String tag) {
		HashMap<String, String> hashmapMessages = new HashMap<String, String>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));
			doc.getDocumentElement().normalize();
			NodeList nodeLst = doc.getElementsByTagName(tag);

			Node fstNode = nodeLst.item(0);
			Element fstElmnt = (Element) fstNode;

			NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("*");

			for (int i = 0; i < fstNmElmntLst.getLength(); i++) {
				Element fstNmElmnt = (Element) fstNmElmntLst.item(i);
				NodeList fstNm = fstNmElmnt.getChildNodes();
				String strNodeName = fstNmElmnt.getNodeName();
				String strNodeValue = (fstNm.item(0)).getNodeValue();
				if (strNodeValue != null) {
					hashmapMessages.put(strNodeName, strNodeValue);
				}
			}
			
		} catch (Exception e) {
			logger.info("Exception : " + e);
		}
		return hashmapMessages;

	}
}
