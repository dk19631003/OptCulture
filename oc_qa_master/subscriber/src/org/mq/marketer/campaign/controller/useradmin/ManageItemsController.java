package org.mq.marketer.campaign.controller.useradmin;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;
import java.net.URLEncoder; 

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OrganizationStores;
import org.mq.marketer.campaign.beans.POSMapping;
import org.mq.marketer.campaign.beans.SkuFile;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.controller.GetUser;
import org.mq.marketer.campaign.custom.MyCalendar;
import org.mq.marketer.campaign.dao.ContactsDao;
import org.mq.marketer.campaign.dao.MessagesDao;
import org.mq.marketer.campaign.dao.MessagesDaoForDML;
import org.mq.marketer.campaign.dao.OrganizationStoresDao;
import org.mq.marketer.campaign.dao.OrganizationStoresDaoForDML;
import org.mq.marketer.campaign.dao.POSMappingDao;
import org.mq.marketer.campaign.dao.SkuFileDao;
import org.mq.marketer.campaign.dao.SkuFileDaoForDML;
import org.mq.marketer.campaign.dao.UsersDao;
import org.mq.marketer.campaign.dao.UsersDomainsDao;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.EncryptDecryptLtyMembshpPwd;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.POSFieldsEnum;
import org.mq.marketer.campaign.general.PageListEnum;
import org.mq.marketer.campaign.general.PageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Redirect;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
// import org.xhtmlrenderer.pdf.ITextRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.PagingEvent;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import au.com.bytecode.opencsv.CSVWriter;

public class ManageItemsController extends GenericForwardComposer {
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private Textbox descTbId,taxTbId,catTbId,deptTbId,sidTbId,classTbId,brandTbId,subclassTbId,priceTbId,sizeTbId,vcodeTbId,dcsTbId,
					udf1TbId,udf2TbId,udf3TbId,udf4TbId,udf5TbId,udf6TbId;
	
	private SkuFileDao skuFileDao;
	private SkuFileDaoForDML skuFileDaoForDML;
	private Paging itemsPagingId; 
	private Session session;
	 private UsersDao usersDao;
	 private UsersDomainsDao userDomaonDao;
	 private Users currentUser;
	 private TimeZone clientTimeZone;
	 private ContactsDao contactsDao;
	 private MessagesDao messagesDao;
	 private MessagesDaoForDML messagesDaoForDML;
	 private final String EMPTY_STRING = "";
	 private Rows itemsRowsId;
	 private Listbox searchLBId;
	 private Textbox searchTBId;
	 private Tabbox itemsTabBoxId;
	 private Button addItemBtnId,cancelItemBtnId,saveItemBtnId,editItemBtnId,backBtnId;
	 private Listbox itemsPerPageLBId;
	 
	 private SkuFile sku;
	 private int searchCriteriaIndex;
	 private String searchFieldStr;
	 
	 private Window custExport;
	 private Div custExport$chkDivId;
	 private Combobox exportCbId;
	 
	 private  static String NORMAL_STYLE = "border:1px solid #7F9DB9;";
	 private  static String ERROR_STYLE = "border:1px solid #DD7870;";
	 private static String SELECT_STRING = "--select--";
	 public ManageItemsController(){
			
		 session = Sessions.getCurrent();
		 usersDao = (UsersDao)SpringUtil.getBean("usersDao");
		 contactsDao =(ContactsDao)SpringUtil.getBean("contactsDao");
		 messagesDao = (MessagesDao)SpringUtil.getBean("messagesDao");
		 messagesDaoForDML = (MessagesDaoForDML)SpringUtil.getBean("messagesDaoForDML");
		 userDomaonDao=(UsersDomainsDao) SpringUtil.getBean("usersDomainsDao");
		 skuFileDao=(SkuFileDao) SpringUtil.getBean("skuFileDao");
		 skuFileDaoForDML=(SkuFileDaoForDML) SpringUtil.getBean("skuFileDaoForDML");
		 
		 
	   	 currentUser = GetUser.getUserObj();
		 String style = "font-weight:bold;font-size:15px;color:#313031;font-family:Arial,Helvetica,sans-serif;align:left";
		 PageUtil.setHeader("Manage Items ","",style,true);
		 clientTimeZone = (TimeZone)session.getAttribute("clientTimeZone");
			
	 }
		@SuppressWarnings("rawtypes")
		
	
	public void doAfterCompose(Component comp) throws Exception {
		logger.info("********** doAfterCompose() ************");
		
		try {
			// TODO Auto-generated method stub
			super.doAfterCompose(comp);
			exportCbId.setSelectedIndex(0);
			type = (String)session.removeAttribute("itemType");
			session.removeAttribute("item");
			skuFileDao =	(SkuFileDao)SpringUtil.getBean("skuFileDao");
			skuFileDaoForDML =	(SkuFileDaoForDML)SpringUtil.getBean("skuFileDaoForDML");
			
			setpageTotCount(null);
			setDefaultItems(0,itemsPagingId.getPageSize(),null);
			itemsPagingId.setActivePage(0);
			itemsPagingId.addEventListener("onPaging", this);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
		

	} //  doAfterCompose()
		
	private void setDefaultItems(int startInd, int count, String searchStr) {
		
		List<SkuFile> itemList =skuFileDao.getAllSkuByUserId(currentUser.getUserId(),startInd,count,searchStr);
		
		Components.removeAllChildren(itemsRowsId);
		
		if(itemList == null || itemList.size() == 0) return;
		
		logger.info("itemList.size() = " + itemList.size());
		
		
		
		for (SkuFile item : itemList) {
			Row row = new Row();
			
			row.setParent(itemsRowsId);
			
		    row.appendChild(new Label(item.getItemSid()));
			row.appendChild(new Label(item.getDescription()));
			row.appendChild(new Label(item.getItemCategory()));
			row.appendChild(new Label(item.getListPrice()!=null ? ""+item.getListPrice().doubleValue() : ""));
			row.appendChild(new Label(item.getDepartmentCode()));
			row.appendChild(new Label(item.getDCS()));
			row.appendChild(new Label(item.getClassCode()));
			row.appendChild(new Label(item.getSubClassCode()));
			
			row.appendChild(new Label(item.getUdf7()));//tax
			row.appendChild(new Label(item.getUdf8()));//size
			row.appendChild(new Label(item.getUdf9()));//brand
			
			Hbox hbox = new Hbox();
			
			Image img = new Image("/img/theme/preview_icon.png");
			img.setStyle("margin-right:10px;cursor:pointer;");
			img.setTooltiptext("View");
			img.setAttribute("type", "userView");
			img.addEventListener("onClick",this);
			
			img.setParent(hbox);
			
			
			Image editImg = new Image("/img/email_edit.gif");
			editImg.setTooltiptext("Edit");
			editImg.setStyle("cursor:pointer;margin-right:10px;");
			editImg.addEventListener("onClick", this);
			editImg.setAttribute("type", "userEdit");
			editImg.setParent(hbox);
			
			Image delImg = new Image("/img/action_delete.gif");
			delImg.setStyle("cursor:pointer;margin-right:10px;");
			delImg.setTooltiptext("Delete");
			delImg.addEventListener("onClick", this);
			delImg.setAttribute("type", "userDelete");
			delImg.setParent(hbox);
			
			
			Image genImg = new Image("/img/qrcode.png");
			genImg.setStyle("margin-right:10px;cursor:pointer;");
			genImg.setTooltiptext("Generate QR Codes");
			genImg.addEventListener("onClick", this);
			genImg.setAttribute("type", "generateQR");
			genImg.setParent(hbox);
			
			
			Image genURL = new Image("/img/csv_icon.png");
			genURL.setStyle("cursor:pointer;");
			genURL.setTooltiptext("Export CSV");
			genURL.addEventListener("onClick", this);
			genURL.setAttribute("type", "generateCSV");
			genURL.setParent(hbox);
			
			
			row.setValue(item);
			
			
			hbox.setParent(row);
		
			
			
		}
		
		}
	Window generateQRWinId;
	Button generateQRWinId$backQRBtnId;
	public void generateQR(Image img) {
		try {
			logger.info("Entered the method generate QR");
			generateQRWinId$backQRBtnId.setVisible(false);
			logger.info("check the exception.");
			generateQRWinId.doModal();
		}catch(Exception e){
			logger.error("** Exception caused in generateQR method ** "+e);
		}
	}
	Textbox generateQRWinId$QRCountIdTbId;
	Label generateQRWinId$msgLbId,generateQRWinId$errormsgLblId;
	Button generateQRWinId$generateQRBtnId,generateQRWinId$cancelQRBtnId;
	public void onClick$generateQRBtnId$generateQRWinId() {
	
	
		try {
			String baseQRHTML =   currentUser.getQRCodeHTMLTemplate() != null ? currentUser.getQRCodeHTMLTemplate() : PropertyUtil.getPropertyValueFromDB(OCConstants.PROPS_KEY_QECODE_HTML_TEMPLATE);
			String ProductQRCodeURL = currentUser.getQRCodeToURL() != null ? currentUser.getQRCodeToURL() :  PropertyUtil.getPropertyValueFromDB(OCConstants.PROPS_KEY_QECODE_TO_URL);
			logger.info("baseQRHTML has to be changed to : "+ baseQRHTML);
			logger.info("product URL  :"+ProductQRCodeURL);

			if(baseQRHTML == null || baseQRHTML.isEmpty()) {
				MessageUtil.setMessage("Oops! System error.", "color:blue;");
				return;
			
			}
			String begin = "##BEGIN##" ;
			String end = "##END##";
			int numberOfQRsToBeGenerated = 	Integer.parseInt(generateQRWinId$QRCountIdTbId.getValue());;
			if(numberOfQRsToBeGenerated > 300 || numberOfQRsToBeGenerated < 1) {
				
				logger.info(" numberOfQRsToBeGenerated: "+ numberOfQRsToBeGenerated);

			
				generateQRWinId$msgLbId.setStyle("color:red;font-family:verdana;font-size:15px;");
				generateQRWinId$msgLbId.setValue("Please provide a number between 1 and 300");
				//generateQRWinId$errormsgLblId.setValue("Please provide a number between 1 to 300");
				generateQRWinId$backQRBtnId.setVisible(true);
				generateQRWinId$generateQRBtnId.setVisible(false);
				generateQRWinId$cancelQRBtnId.setVisible(false);
				return; 
			}
		else {
			/*generateQRWinId$errormsgLblId.setValue("Please enter a valid number to generate QR codes.");
		//	generateQRWinId$errormsgLbId.setValue("")
			generateQRWinId$errormsgLblId.setStyle("font-size:15px;");*/
			
			logger.info(" numberOfQRsToBeGenerated: "+ numberOfQRsToBeGenerated);


		}

			String productCode = ((SkuFile)session.getAttribute("item")).getSku();
			String productDescription=((SkuFile)session.getAttribute("item")).getDescription();
					
			
			if(productDescription == null || productDescription.isEmpty()) {
				logger.error("Item Description is Empty.");
				generateQRWinId$msgLbId.setStyle("color:red;font-family:verdana;font-size:15px;");
				generateQRWinId$msgLbId.setValue("Item Description for the chosen item is empty.");
				generateQRWinId$backQRBtnId.setVisible(true);
				generateQRWinId$generateQRBtnId.setVisible(false);
				generateQRWinId$cancelQRBtnId.setVisible(false);
				return;
			}
			
			if(productCode == null || productCode.isEmpty()) {
				logger.error("SKU attribute for the chosen item is empty; please choose another.");
				generateQRWinId$msgLbId.setStyle("color:red;font-family:verdana;font-size:15px;");
				generateQRWinId$msgLbId.setValue("SKU attribute for the chosen item is empty; please choose another.");
				generateQRWinId$backQRBtnId.setVisible(true);
				generateQRWinId$generateQRBtnId.setVisible(false);
				generateQRWinId$cancelQRBtnId.setVisible(false);
				return;
			}
			
			if(baseQRHTML.indexOf(begin) != -1 && baseQRHTML.indexOf(end) != -1) 
			{
				logger.info(" baseQRHtml : "+ baseQRHTML.indexOf(begin)+":: end : "+ baseQRHTML.indexOf(end));

				String loopBlockOne = baseQRHTML.substring(baseQRHTML.indexOf(begin) + begin.length(),
						baseQRHTML.indexOf(end));
				
				int rowcount = 100;
				//int iterCount = numberOfQRsToBeGenerated/(rowcount*3); //numberOfQRsToBeGenerated/rowcount;
				int tempNoOfQRsToBeGenerated = numberOfQRsToBeGenerated;
				//for(int iter = 0; iter<=iterCount ; iter++) {
					
					String baseHTML = baseQRHTML;
					String rowsHTMLBlock = "";
					for (int row=0; row < (tempNoOfQRsToBeGenerated%3 == 0 ? (tempNoOfQRsToBeGenerated/3) : (tempNoOfQRsToBeGenerated/3)+1 ); row++) {	//(int row=0; row< rowcount; row++) {//10 rows per each PDF
						
						int noOfRows = (tempNoOfQRsToBeGenerated%3 == 0 ? (tempNoOfQRsToBeGenerated/3) : (tempNoOfQRsToBeGenerated/3)+1 );
						String loopBlock = "";
						loopBlock =loopBlockOne;
						for(int qrs = 0 ; qrs < 3 ; qrs++) {	//(int qrs = 0 ; qrs<3; qrs++) {
							
							if(qrs >= ( (row == noOfRows-1) ? (tempNoOfQRsToBeGenerated%3==0 ? 3 : tempNoOfQRsToBeGenerated%3) : 3 )) {
								
								String QRCodeOne = currentUser.getOneQRCodeHtml() != null ? currentUser.getOneQRCodeHtml() : PropertyUtil.getPropertyValueFromDB(OCConstants.ONE_QR_CODE_HTML);
								QRCodeOne = QRCodeOne.replace("[QR#]", "[QR"+qrs+"]");
								loopBlock =loopBlock.replace(QRCodeOne, "<td width=\"25%\"></td>");

								continue;
							}
							
							String DocSIDHash = EncryptDecryptLtyMembshpPwd.encryptSessionID(System.currentTimeMillis()+productCode);
							String encodedProductDescription=URLEncoder.encode(productDescription,"UTF-8");
							String customerURL = ProductQRCodeURL.replace("[PRODUCT]", productCode)
												.replace("[DESCRIPTION]", encodedProductDescription)
												.replace("[HASH]", DocSIDHash);
							logger.info("Replaced customerURL :"+customerURL);
							QRCodeWriter qrCodeWriter = new QRCodeWriter();
							BitMatrix bitMatrix = qrCodeWriter.encode(customerURL, BarcodeFormat.QR_CODE, 50, 50);  
							BufferedImage img  = MatrixToImageWriter.toBufferedImage(bitMatrix);
							ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
							//MatrixToImageWriter.writeToStream(bitMatrix,"png", outputStream);

						    ImageIO.write(img, "png", outputStream);
							String base64 = new String(Base64.getEncoder().encodeToString(outputStream.toByteArray()));
							  loopBlock =loopBlock.replace("[QR"+qrs+"]", base64);
							  
							  logger.info("replaced the QR with the Png format image :"+customerURL);
						}
						rowsHTMLBlock += loopBlock;			
					}
					
					baseHTML = baseHTML.replace(loopBlockOne, rowsHTMLBlock);
					  try {
						  Calendar cal=Calendar.getInstance();
						    String pdfFileName="DR_PDF_"+cal.getTimeInMillis()+"_"+productCode+".pdf";
						    String htfileName="DR_HTML_"+cal.getTimeInMillis()+"_"+productCode+".html";
						    
						    logger.info("Entered here to download pdf and html file "+pdfFileName+" :: "+htfileName);

						  final String USER_PARENT_DIR=PropertyUtil.getPropertyValue("usersParentDirectory");
							String PDF_CMD= PropertyUtil.getPropertyValueFromDB("pathToPhantomjs");
							
							String htmlPath= USER_PARENT_DIR+File.separator+GetUser.getUserName()+File.separator+OCConstants.DR+File.separator+OCConstants.HTML+File.separator+htfileName;
						    String pdfPath= USER_PARENT_DIR+File.separator+GetUser.getUserName()+File.separator+OCConstants.DR+File.separator+OCConstants.PDF+File.separator+pdfFileName;
						    File htmlFile =new File(htmlPath);
						    /* BufferedWriter bw = new BufferedWriter(new FileWriter(htmlFile));
						     bw.write(newHtmlContent);
						     bw.close();*/
				            	FileOutputStream fos = new FileOutputStream(htmlFile);
				 		       OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_16);
				 		       BufferedWriter writer = new BufferedWriter(osw);

				 		      writer.write(baseHTML);
				 		      writer.close();
				 		      String cmd = "chmod 777 -R "+htmlPath;
				 		      Runtime.getRuntime().exec(cmd);
				            ProcessBuilder pb = new ProcessBuilder(PDF_CMD+"bin/phantomjs",PDF_CMD+"htmltoImage/htmltoImage.js", htmlPath, pdfPath);
				            Process p = pb.start();
							int exitVal = p.waitFor(); // do a wait here to prevent it running for ever
							if (exitVal != 0) {
								logger.error("EXIT-STATUS - " + p.toString());
							}
							p.destroy();
							
							File pdfFile= new File(pdfPath);
							FileInputStream fileInputStream = new FileInputStream(pdfFile);
							FileOutputStream ouputfile = new FileOutputStream(pdfPath);
							int bytes;
							while ((bytes = fileInputStream.read()) != -1) {
								ouputfile.write(bytes);
								
							}
							/*ITextRenderer renderer = new ITextRenderer();

					        renderer.setDocument(htmlPath);
					        renderer.layout();
					        renderer.createPDF(ouputfile);*/
							/*byte[] buffer = new byte[1024];
						      int readbyte = 0;
						      BufferedInputStream input = new BufferedInputStream(fileInputStream);
						      BufferedOutputStream bufferOut = new BufferedOutputStream(ouputfile, 1024);
						      while((readbyte = input.read(buffer, 0, 1024)) >= 0) {
						        //Writing the content onto the file.
						        bufferOut.write(buffer,0,readbyte);
							
						      }
								 responseOutputStream = response.getOutputStream();
							*/
					        
					        //File pdfFile= new File(pdfPath);
							Filedownload.save(htmlFile, "application/html");
							ouputfile.flush();
							ouputfile.close();
							//fileInputStream.close();
			            }catch (Exception e) {
			            	logger.info("Exception ::",e);
							generateQRWinId$msgLbId.setValue(e.getMessage());

						}
				//}
				//logger.debug("Items HTML is :"+ loopBlockOne);
				
				//String itemloop = getReplacedItemBlock(loopBlock, loopBlock2, DRItemHashTagsSet, prismDRBody);
				generateQRWinId$msgLbId.setValue("Successfully created '"+numberOfQRsToBeGenerated+" QR codes'.");
				logger.info("Completed the generation of QR code");
			}
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			logger.error("===Exception==", e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("===Exception==", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("===Exception==", e);
		}
		
	}
	public void onClick$cancelQRBtnId$generateQRWinId() {
		generateQRWinId.setVisible(false);
		generateQRWinId$generateQRBtnId.setVisible(true);
		generateQRWinId$cancelQRBtnId.setVisible(true);
		generateQRWinId$msgLbId.setValue("");
		//generateQRWinId$errormsgLblId.setValue("");
		generateQRWinId$QRCountIdTbId.setValue("");
	}
	public void onClick$backQRBtnId$generateQRWinId() {
		generateQRWinId.setVisible(false);
		generateQRWinId$generateQRBtnId.setVisible(true);
		generateQRWinId$cancelQRBtnId.setVisible(true);
		generateQRWinId$msgLbId.setValue("");
		//generateQRWinId$errormsgLblId.setValue("");
		generateQRWinId$QRCountIdTbId.setValue("");
	}
	
	Window generateCSVWinId;
	Button generateCSVWinId$backCSVBtnId;
	public void generateCSV(Image img) {
		try {
			logger.info("Entered the method generate CSV");
			generateCSVWinId$backCSVBtnId.setVisible(false);
			logger.info("check the exception.");
			generateCSVWinId.doModal();
		}catch(Exception e){
			logger.error("** Exception caused in generateCSV method ** "+e);
		}
	}
	Textbox generateCSVWinId$CSVCountIdTbId;
	Label generateCSVWinId$msgLbId,generateCSVWinId$errormsgLblId;
	Button generateCSVWinId$generateCSVBtnId,generateCSVWinId$cancelCSVBtnId;
	
	public void onClick$generateCSVBtnId$generateCSVWinId() {
		logger.info("----------------------Entered onClick$generateCSVBtnId$generateCSVWinId-------------");
		try {

			String ProductQRCodeURL = currentUser.getQRCodeToURL() != null ? currentUser.getQRCodeToURL() : PropertyUtil.getPropertyValueFromDB(OCConstants.PROPS_KEY_QECODE_TO_URL);
			logger.info("ProductQR URL has to be changed to : "+ ProductQRCodeURL);
			
			int numberOFURLTobeGenerated = 	Integer.parseInt(generateCSVWinId$CSVCountIdTbId.getValue());
			if(numberOFURLTobeGenerated<=0) {	
				generateCSVWinId$msgLbId.setStyle("color:red;font-family:verdana;font-size:15px;");
				generateCSVWinId$msgLbId.setValue("Please provide a number greater than 0");
				generateCSVWinId$backCSVBtnId.setVisible(true);
				generateCSVWinId$generateCSVBtnId.setVisible(false);
				generateCSVWinId$cancelCSVBtnId.setVisible(false);
				return;
			}
		else {
			
		}

			String productCode = ((SkuFile)session.getAttribute("item")).getSku().trim();
			String productDescription=((SkuFile)session.getAttribute("item")).getDescription().trim();	
			logger.info(productCode);
			logger.info(productDescription);
			if(productDescription == null || productDescription.isEmpty()) {
				logger.error("Item Description is Empty.");
				generateCSVWinId$msgLbId.setStyle("color:red;font-family:verdana;font-size:15px;");
				generateCSVWinId$msgLbId.setValue("Item Description for the chosen item is empty.");
				generateCSVWinId$backCSVBtnId.setVisible(true);
				generateCSVWinId$generateCSVBtnId.setVisible(false);
				generateCSVWinId$cancelCSVBtnId.setVisible(false);
				return;
			}
			
			if(productCode == null || productCode.isEmpty()) {
				logger.error("SKU attribute for the chosen item is empty; please choose another.");
				generateCSVWinId$msgLbId.setStyle("color:red;font-family:verdana;font-size:15px;");
				generateCSVWinId$msgLbId.setValue("SKU attribute for the chosen item is empty; please choose another.");
				generateCSVWinId$backCSVBtnId.setVisible(true);
				generateCSVWinId$generateCSVBtnId.setVisible(false);
				generateCSVWinId$cancelCSVBtnId.setVisible(false);
				return;
			}

			List<String[]> skuList=new ArrayList<>();
			skuList.add(new String[]{"Product Code", "Product Name", "Product URL(QR Code)"});
			for (int i = 1; i <=numberOFURLTobeGenerated; i++) {
				
				String DocSIDHash = EncryptDecryptLtyMembshpPwd.encryptSessionID(System.nanoTime()+productCode).trim();
				String encodedProductDescription=URLEncoder.encode(productDescription,"UTF-8").trim();
				String customerURL = ProductQRCodeURL.replace("[PRODUCT]", productCode)
									.replace("[DESCRIPTION]", encodedProductDescription)
									.replace("[HASH]", DocSIDHash);
	
			logger.info("repolaced customerURL : "+customerURL);
				skuList.add(new String[] {productCode,productDescription,customerURL});
			}

			
			try {
		  	    Calendar cal=Calendar.getInstance();
		  	    String csvFileName="DR_CSV"+cal.getTimeInMillis()+"_"+productCode+".csv";
		  	  
		  	    final String USER_PARENT_DIR=PropertyUtil.getPropertyValue("usersParentDirectory");
		  	    String csvPath=USER_PARENT_DIR+File.separator+GetUser.getUserName()+File.separator+OCConstants.DR+File.separator+csvFileName;
		  	    
		  	    File csvFile = new File(csvPath);	
		  	    FileWriter csvWriter=new FileWriter(csvFile);
				CSVWriter writer = new CSVWriter(csvWriter);
		        writer.writeAll(skuList);
		        Filedownload.save(csvFile, "text/csv");        
		        csvWriter.close();
		        writer.close();       		
		    }catch (Exception e) {
		    	logger.info("Exception ::",e);
		  		generateCSVWinId$msgLbId.setValue(e.getMessage());
  
		    }
		
			generateCSVWinId$msgLbId.setValue("Generated Successfully.");

				logger.info("completed processing");
			
			} catch (WriterException e) {
				// TODO Auto-generated catch block
				logger.error("===Exception==", e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("===Exception==", e);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("===Exception==", e);
			}
			logger.info("----------------------Exit onClick$generateCSVBtnId$generateCSVWinId-------------");
	}
	
	public void onClick$cancelCSVBtnId$generateCSVWinId() {
		generateCSVWinId.setVisible(false);
		generateCSVWinId$generateCSVBtnId.setVisible(true);
		generateCSVWinId$cancelCSVBtnId.setVisible(true);
		generateCSVWinId$msgLbId.setValue("");
		//generateQRWinId$errormsgLblId.setValue("");
		generateCSVWinId$CSVCountIdTbId.setValue("");
	}
	public void onClick$backCSVBtnId$generateCSVWinId() {
		generateCSVWinId.setVisible(false);
		generateCSVWinId$generateCSVBtnId.setVisible(true);
		generateCSVWinId$cancelCSVBtnId.setVisible(true);
		generateCSVWinId$msgLbId.setValue("");
		//generateCSVWinId$errormsgLblId.setValue("");
		generateCSVWinId$CSVCountIdTbId.setValue("");
	}
	public void onClick$addItemBtnId() {
		logger.info("clicked on Add Item button...");
		SkuFile skuFileObj;
		try {
			if(descTbId.getValue().trim().isEmpty() || catTbId.getValue().trim().isEmpty() || sidTbId.getValue().trim().isEmpty() || priceTbId.getValue().trim().isEmpty()){
				MessageUtil.setMessage("Please fill all mandatory fields","color:red","TOP");
				return;
			}
			skuFileObj = getSKUFromOC();
			try {
				if(skuFileObj!=null) processSkuData(currentUser,skuFileObj);
			} catch (Exception e) {
				logger.error("Exception while processing sku data ::", e);
			}
			
			session.removeAttribute("itemType"); 
			//session.removeAttribute("item");
		    Redirect.goTo(PageListEnum.EMPTY);
		    Redirect.goTo(PageListEnum.USERADMIN_MANAGE_ITEMS);
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		
	}//onClick$addItemBtnId
	
	private SkuFile getSKUFromOC()throws BaseServiceException {
		
		try {
			SkuFile skuFileObj = new SkuFile();
			skuFileObj.setDescription(descTbId.getValue().trim());
			skuFileObj.setItemCategory(catTbId.getValue().trim());
			skuFileObj.setItemSid(sidTbId.getValue().trim());
			skuFileObj.setDepartmentCode(deptTbId.getValue().trim());
			if(!(priceTbId.getValue().isEmpty()))skuFileObj.setListPrice(Double.parseDouble(priceTbId.getValue().trim()));
			skuFileObj.setClassCode(classTbId.getValue().trim());
			skuFileObj.setSubClassCode(subclassTbId.getValue().trim());
			skuFileObj.setDCS(dcsTbId.getValue().trim());
			skuFileObj.setVendorCode(vcodeTbId.getValue().trim());
			skuFileObj.setUdf7(taxTbId.getValue().trim());//tax
			skuFileObj.setUdf8(sizeTbId.getValue().trim());//size
			skuFileObj.setUdf9(brandTbId.getValue().trim());//brand
			skuFileObj.setUdf1(udf1TbId.getValue().trim());
			skuFileObj.setUdf2(udf2TbId.getValue().trim());
			skuFileObj.setUdf3(udf3TbId.getValue().trim());
			skuFileObj.setUdf4(udf4TbId.getValue().trim());
			skuFileObj.setUdf5(udf5TbId.getValue().trim());
			skuFileObj.setUdf6(udf6TbId.getValue().trim());
			
			return skuFileObj;
						
		} catch (Exception e) {
			logger.error("Exception while getting data from OC screen ::", e);
			return null;
		}
	}//getSKUFromOC
	
	private void processSkuData(Users user, SkuFile skuFileDataObj) {
		//StatusInfo statusInfo=null;
		try {
			logger.debug("-------entered processSkuData---------");
			POSMappingDao posMappingDao=(POSMappingDao) ServiceLocator.getInstance().getDAOByName(OCConstants.POSMAPPING_DAO);
			SkuFileDao skuFileDao=(SkuFileDao) ServiceLocator.getInstance().getDAOByName(OCConstants.SKU_FILE_DAO);
			SkuFileDaoForDML skuFileDaoForDML=(SkuFileDaoForDML) ServiceLocator.getInstance().getDAOForDMLByName(OCConstants.SKU_FILE_DAO_FOR_DML);
			TreeMap<String, List<String>> prioMap =null;
			prioMap = Utility.getPriorityMap(user.getUserId(), Constants.POS_MAPPING_TYPE_SKU, posMappingDao);
			List<POSMapping> SKUPOSMappingList = null;
			SKUPOSMappingList = posMappingDao.findByType("'"+Constants.POS_MAPPING_TYPE_SKU+"'", user.getUserId());
			logger.info("SKUPOSMappingList size >>"+SKUPOSMappingList.size());
			SkuFile skuFileObj = null;
			SkuFile inputSkuFile=new SkuFile();
			inputSkuFile = prepareSkuObjectData(SKUPOSMappingList,skuFileDataObj);
				
			if(!prioMap.isEmpty()) {
				skuFileObj = skuFileDao.findSKUByPriority(prioMap, inputSkuFile, user.getUserId());
			}
			try {
				 String saveMsg = "";
				int confirm = Messagebox.show("Are you sure you want to save the Item?", "Prompt", 
						 Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {
						
						try {
							if(skuFileObj != null) {
								skuFileObj = Utility.mergeSkuFile(inputSkuFile, skuFileObj);
								skuFileObj.setUserId(user.getUserId());
								skuFileObj.setModifiedDate(Calendar.getInstance());
								skuFileDaoForDML.saveOrUpdate(skuFileObj);
								saveMsg = "updated";
								logger.info("skuFileObj updated...");
							}
							else {
								skuFileObj = new SkuFile(); 
								skuFileObj = Utility.mergeSkuFile(inputSkuFile, skuFileObj);
								skuFileObj.setUserId(user.getUserId());
								skuFileObj.setCreatedDate(Calendar.getInstance());
								skuFileObj.setModifiedDate(Calendar.getInstance());;
								skuFileDaoForDML.saveOrUpdate(skuFileObj);
								saveMsg = "created";
								logger.info("new skuFileObj created...");
							}
							
							MessageUtil.setMessage("Item "+saveMsg +"  successfully.","color:green;");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e);
						}
					}
					//session.removeAttribute("manageItems");
					//				}
			} catch (Exception e) {
				logger.error("Exception ::", e);
			}
			
			logger.debug("-------exit  processSkuData---------");
		}catch (Exception e) {
			logger.error("Exception  ::", e);
		}
	}//processSkuData
	
	private SkuFile prepareSkuObjectData(
			List<POSMapping> sKUPOSMappingList,
			SkuFile skuFileDataObj)throws BaseServiceException {

		logger.debug("-------entered prepareSkuObjectData---------");
		Class strArg[] = new Class[] { String.class };
		Class calArg[] = new Class[] { Calendar.class };
		Class doubleArg[] = new Class[] { Double.class };
		Class dataTypeArg[] = null;
		SkuFile inputSkuFile = null;
		inputSkuFile = new SkuFile();
		String fieldValue = null;
		for(POSMapping posMapping : sKUPOSMappingList){
			
			if(posMapping.getCustomFieldName() == null){
				continue;
			}
			String custFieldAttribute = posMapping.getCustomFieldName();
			//For user defined field name, Json does not contain field value
			fieldValue = getFieldValue(skuFileDataObj,posMapping);
			//logger.info("field value:"+fieldValue);
			if(fieldValue == null || fieldValue.trim().length() <= 0){
				logger.info("field value is null, In SKU data processing>>>>>"+posMapping.getCustomFieldName());
				continue;
			}
			String dataTypeStr = posMapping.getDataType();
			//logger.info("custFieldAttribute:"+custFieldAttribute);
			if(custFieldAttribute.startsWith("UDF") && dataTypeStr.startsWith("Date")){
				String dateValue = getDateFormattedData(posMapping, fieldValue);
				if(dateValue == null) continue;
				fieldValue = dateValue;
			}
			//String dateFormat = null;
			Object[] params = null;
			Method method = null;
			try {
				if (custFieldAttribute.equals(POSFieldsEnum.listPrice.getOcAttr()) && fieldValue.length() > 0 ) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.listPrice.getPojoField(), doubleArg);
					Double listPriceVal = new Double(fieldValue);
					params = new Object[] { listPriceVal };
					//logger.info("parseDouble value: "+Double.parseDouble(fieldValue.trim()));
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.description.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.description.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.sku.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.sku.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.itemCategory.getOcAttr()) && fieldValue.length() > 0) {
				//	logger.info("itemCategory:"+POSFieldsEnum.itemCategory.getPojoField());
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemCategory.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.itemSid.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.itemSid.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.storeNumber.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.storeNumber.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.subsidiaryNumber.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.subsidiaryNumber.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				
				
				else if (custFieldAttribute.equals(POSFieldsEnum.vendorCode.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.vendorCode.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.departMentCode.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.departMentCode.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.classCode.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.classCode.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.Sclass.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.Sclass.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.dcs.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.dcs.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf1.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf1.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf2.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf2.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf3.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf3.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf4.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf4.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf5.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf5.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf6.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf6.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf7.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf7.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf8.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf8.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf9.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf9.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf10.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf10.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf11.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf11.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf12.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf12.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf13.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf13.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf14.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf14.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				else if (custFieldAttribute.equals(POSFieldsEnum.udf15.getOcAttr()) && fieldValue.length() > 0) {
					method = SkuFile.class.getMethod("set" + POSFieldsEnum.udf15.getPojoField(), strArg);
					params = new Object[] { fieldValue };
				}
				if (method != null) {
					try {
						method.invoke(inputSkuFile, params);
					//	logger.info("method name:  "+method.getName()+" field value: "+fieldValue);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Exception ::" , e);
					} 
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Exception ::" , e);
			} 
		}
		logger.debug("-------exit  prepareSkuObjectData---------");
		return inputSkuFile;
	}//prepareSkuObjectData
	
	private String getFieldValue(SkuFile skuFileDataObj,
			POSMapping posMapping)throws BaseServiceException {
		String fieldValue=null;
	//	logger.debug("-------entered getFieldValue---------");
		if(posMapping.getCustomFieldName().equalsIgnoreCase("item sid")) {
			fieldValue=skuFileDataObj.getItemSid();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("store number")) {
			fieldValue=skuFileDataObj.getStoreNumber();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("sku")) {
			fieldValue=skuFileDataObj.getSku();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("item category")) {
			fieldValue=skuFileDataObj.getItemCategory();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("list price") && skuFileDataObj.getListPrice()!=null) {
			fieldValue=skuFileDataObj.getListPrice().toString();
			//logger.info("list price"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("description")) {
			fieldValue=skuFileDataObj.getDescription();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("dcs")) {
			fieldValue=skuFileDataObj.getDCS();
			//logger.info("dcs"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("vendor")) {
			fieldValue=skuFileDataObj.getVendorCode();
			//logger.info("vendor"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("class")) {
			fieldValue=skuFileDataObj.getClassCode();
			//logger.info("class"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("subclass")) {
			fieldValue=skuFileDataObj.getSubClassCode();
			//logger.info("subclass"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("Department")) {
			fieldValue=skuFileDataObj.getDepartmentCode();
			//logger.info("Department"+fieldValue);
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf1")) {
			fieldValue=skuFileDataObj.getUdf1();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf2")) {
			fieldValue=skuFileDataObj.getUdf2();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf3")) {
			fieldValue=skuFileDataObj.getUdf3();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf4")) {
			fieldValue=skuFileDataObj.getUdf4();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf5")) {
			fieldValue=skuFileDataObj.getUdf5();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf6")) {
			fieldValue=skuFileDataObj.getUdf6();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf7")) {
			fieldValue=skuFileDataObj.getUdf7();//tax
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf8")) {
			fieldValue=skuFileDataObj.getUdf8();//size
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf9")) {
			fieldValue=skuFileDataObj.getUdf9();//brand
			return fieldValue;
		}
		/*if(posMapping.getCustomFieldName().equalsIgnoreCase("udf10")) {
			fieldValue=skuFileDataObj.getUdf10();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf11")) {
			fieldValue=skuFileDataObj.getUdf11();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf12")) {
			fieldValue=skuFileDataObj.getUdf12();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf13")) {
			fieldValue=skuFileDataObj.getUdf13();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf14")) {
			fieldValue=skuFileDataObj.getUdf14();
			return fieldValue;
		}
		if(posMapping.getCustomFieldName().equalsIgnoreCase("udf15")) {
			fieldValue=skuFileDataObj.getUdf15();
			return fieldValue;
		}*/
		if(fieldValue==null)logger.debug("fieldValue is null :: "+posMapping.getCustomFieldName());
		logger.debug("-------exit  getFieldValue---------");
		return fieldValue;
	}//getFieldValue
	
	private String getDateFormattedData(POSMapping posMapping, String fieldValue)throws BaseServiceException {
		
		logger.debug("-------entered getDateFormattedData---------");
		String dataTypeStr = posMapping.getDataType();
		String dateFieldValue = null;
		if(posMapping.getDataType().trim().startsWith("Date")) {
			try {
				String dateFormat = dataTypeStr.substring(dataTypeStr.indexOf("(")+1, dataTypeStr.indexOf(")"));

				if(!Utility.validateDate(fieldValue, dateFormat)) {
					return null;
				}
				DateFormat formatter ; 
				Date date ; 
				formatter = new SimpleDateFormat(dateFormat);
				date = (Date)formatter.parse(fieldValue); 
				Calendar cal =  new MyCalendar(Calendar.getInstance(), null, MyCalendar.dateFormatMap.get(dateFormat));
				cal.setTime(date);
				dateFieldValue= MyCalendar.calendarToString(cal, MyCalendar.dateFormatMap.get(dateFormat));
			} catch (Exception e) {
				logger.error("Exception ::" , e);
			}
		}
		logger.debug("-------exit  getDateFormattedData---------");
		return dateFieldValue;
	}//getDateFormattedData
	
	
	public void onClick$filterBtnId() {
		
		//Components.removeAllChildren(itemsRowsId);
		
		searchCriteriaIndex=searchLBId.getSelectedIndex();
		searchFieldStr=searchTBId.getValue().trim();
		
		if(searchLBId.getSelectedIndex() == 0  &&  searchFieldStr.length() > 0 ) {
			MessageUtil.setMessage("Please select the Criteria.", "color:red", "TOP");
			return;
		}
		if(searchLBId.getSelectedIndex() > 0  && (searchFieldStr.length() ==0 || searchFieldStr.startsWith("Search...")) && searchTBId.isVisible()) {
			MessageUtil.setMessage("Please provide text in Textbox.", "color:red", "TOP");
			searchTBId.setStyle(ERROR_STYLE);
			return;
		}
		
		Components.removeAllChildren(itemsRowsId);
		
		if(searchLBId.getSelectedIndex()==1) searchFieldStr="SID :: "+searchFieldStr;
		else if(searchLBId.getSelectedIndex()==2) searchFieldStr="Description :: "+searchFieldStr;
		else if(searchLBId.getSelectedIndex()==3) searchFieldStr="Category :: "+searchFieldStr;
		
		int totalSize = skuFileDao.findItemsCount(currentUser.getUserId(),searchFieldStr); 		
		itemsPagingId.setTotalSize(totalSize);
		itemsPagingId.setActivePage(0);
		itemsPagingId.addEventListener("onPaging", this);
		setDefaultItems(0, itemsPagingId.getPageSize(), searchFieldStr);
			
			
	}//onClick$filterBtnId()
	
	public void onClick$resetSearchCriteriaAnchId() {
		
		try {
			
			searchLBId.setSelectedIndex(0);
			searchTBId.setValue("");
			//setpageTotCount(null);
			
			
			int totalSize = skuFileDao.findItemsCount(currentUser.getUserId(),null); 
			itemsPagingId.setActivePage(0);
			itemsPagingId.addEventListener("onPaging", this);
			itemsPagingId.setTotalSize(totalSize);
			setDefaultItems( 0,itemsPagingId.getPageSize(), null);
							
		} catch (WrongValueException e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
		}
	}//onClick$resetSearchCriteriaAnchId()
	public void setpageTotCount(String key) {
		try {
			
			int totalSize = skuFileDao.findItemsCount(currentUser.getUserId(),key);
			itemsPagingId.setTotalSize(totalSize);
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
		
		
	}//setpageTotCount()
	
	public void onEvent(Event event) throws Exception {
		// TODO Auto-generated method stub
		super.onEvent(event);
		
		if(event.getTarget() instanceof Image){
			
			Image img = (Image)event.getTarget();
			
		
			String evtType = (String)img.getAttribute("type");
		
           if(evtType.equalsIgnoreCase("userView")){
        	   
        	   Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
				SkuFile sku = (SkuFile)tempRow.getValue();
				logger.info("skuId :: "+sku.getSkuId().longValue());
				session.setAttribute("itemType", "view");
				session.setAttribute("item", sku);
				itemsTabBoxId.setSelectedIndex(1);
				skuSettings();
				
			}
			else if(evtType.equalsIgnoreCase("userEdit")) {
				
				Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
				SkuFile sku = (SkuFile)tempRow.getValue();
				logger.info("skuId :: "+sku.getSkuId().longValue());
				session.setAttribute("itemType", "edit");
				session.setAttribute("item", sku);
				itemsTabBoxId.setSelectedIndex(1);
				skuSettings();
			}else if(evtType.equalsIgnoreCase("generateQR")) {
				
				Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
				SkuFile sku = (SkuFile)tempRow.getValue();
				logger.info("skuId :: "+sku.getSkuId().longValue());
				session.setAttribute("itemType", "edit");
				session.setAttribute("item", sku);
				//itemsTabBoxId.setSelectedIndex(1);
				generateQR(img);
			}
			else if(evtType.equalsIgnoreCase("generateCSV")) {
				
				Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
				SkuFile sku = (SkuFile)tempRow.getValue();
				logger.info("skuId :: "+sku.getSkuId().longValue());
				session.setAttribute("itemType", "edit");
				session.setAttribute("item", sku);
				//itemsTabBoxId.setSelectedIndex(1);
			    generateCSV(img);
			}
			else if(img.getAttribute("type").equals("userDelete")) {
				
				Row tempRow = ((Row)((Hbox)img.getParent()).getParent());
				SkuFile sku = (SkuFile)tempRow.getValue();
				logger.info("skuId :: "+sku.getSkuId().longValue());
				try {
					
					int confirm = Messagebox.show("Confirm to delete the selected item.", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
					if(confirm == Messagebox.OK) {
						
						
						try {
							skuFileDaoForDML.delete(sku);
							MessageUtil.setMessage("Item deleted successfully.","color:blue","TOP");
							itemsRowsId.removeChild(tempRow);
							int totalSize = skuFileDao.findItemsCount(currentUser.getUserId(),null); 
							itemsPagingId.setTotalSize(totalSize);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							logger.error("Exception ::", e);
							 MessageUtil.setMessage("Item not deleted, try again", "color:red;");
						}
					}
					}
					
					
				 catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Exception ::", e);
				}
				return;
				
			}
			
		}else if(event.getTarget() instanceof Paging) {
			
			logger.info("clicked on paging...");
			Paging paging = (Paging)event.getTarget();
			
			int desiredPage = paging.getActivePage();
			
			
			
			
			PagingEvent pagingEvent = (PagingEvent) event;
			int pSize = pagingEvent.getPageable().getPageSize();
			int ofs = desiredPage * pSize;
			
			String searchStr = (searchTBId.getValue().trim().equals("") || searchTBId.getValue().trim().equals("Search...")) ? null : searchTBId.getValue().trim() ;
			if(searchLBId.getSelectedIndex()==1) searchStr="SID :: "+searchStr;
			else if(searchLBId.getSelectedIndex()==2) searchStr="Description :: "+searchStr;
			else if(searchLBId.getSelectedIndex()==3) searchStr="Category :: "+searchStr;
			setDefaultItems(ofs, (byte) pagingEvent.getPageable().getPageSize(), searchStr);
			
			
		}
	}//onEvent()
	private String type;
	public void skuSettings() {
		
		if(itemsTabBoxId.getSelectedIndex() == 1) {
			
			type = (String)session.getAttribute("itemType");
			sku = (SkuFile)session.getAttribute("item");
			if(type != null && sku != null) {
				descTbId.setValue(sku.getDescription()!=null ? sku.getDescription() : "");
				//descTbId.setDisabled(true);
				catTbId.setValue(sku.getItemCategory()!=null ? sku.getItemCategory() : "");
				//catTbId.setDisabled(true);
				sidTbId.setValue(sku.getItemSid()!=null ? sku.getItemSid() : "");
				sidTbId.setDisabled(true);
				deptTbId.setValue(sku.getDepartmentCode()!=null ? sku.getDepartmentCode() : "");
				priceTbId.setValue(sku.getListPrice()!=null ? sku.getListPrice().toString() : "");
				//priceTbId.setDisabled(true);
				classTbId.setValue(sku.getClassCode()!=null ? sku.getClassCode() : "");
				subclassTbId.setValue(sku.getSubClassCode()!=null ? sku.getSubClassCode() : "");
				dcsTbId.setValue(sku.getDCS()!=null ? sku.getDCS() : "");
				vcodeTbId.setValue(sku.getVendorCode()!=null ? sku.getVendorCode() : "");
				taxTbId.setValue(sku.getUdf7()!=null ? sku.getUdf7() : "");
				sizeTbId.setValue(sku.getUdf8()!=null ? sku.getUdf8() : "");
				brandTbId.setValue(sku.getUdf9()!=null ? sku.getUdf9() : "");
				udf1TbId.setValue(sku.getUdf1()!=null ? sku.getUdf1() : "");
				udf2TbId.setValue(sku.getUdf2()!=null ? sku.getUdf2() : "");
				udf3TbId.setValue(sku.getUdf3()!=null ? sku.getUdf3() : "");
				udf4TbId.setValue(sku.getUdf4()!=null ? sku.getUdf4() : "");
				udf5TbId.setValue(sku.getUdf5()!=null ? sku.getUdf5() : "");
				udf6TbId.setValue(sku.getUdf6()!=null ? sku.getUdf6() : "");
				
				if(type != null && type.equals("view")) {
					descTbId.setReadonly(true);
					catTbId.setReadonly(true);
					sidTbId.setReadonly(true);
					deptTbId.setReadonly(true);
					priceTbId.setReadonly(true);
					classTbId.setReadonly(true);
					subclassTbId.setReadonly(true);
					dcsTbId.setReadonly(true);
					vcodeTbId.setReadonly(true);
					taxTbId.setReadonly(true);
					sizeTbId.setReadonly(true);
					brandTbId.setReadonly(true);
					udf1TbId.setReadonly(true);
					udf2TbId.setReadonly(true);
					udf3TbId.setReadonly(true);
					udf4TbId.setReadonly(true);
					udf5TbId.setReadonly(true);
					udf6TbId.setReadonly(true);
					
					addItemBtnId.setLabel("Edit");
					addItemBtnId.setVisible(false);
					cancelItemBtnId.setVisible(false);
					//editItemBtnId.setVisible(true);
					backBtnId.setVisible(true);
					
				}else if(type != null && type.equals("edit")) {
					descTbId.setReadonly(false);
					catTbId.setReadonly(false);
					sidTbId.setReadonly(false);
					deptTbId.setReadonly(false);
					priceTbId.setReadonly(false);
					classTbId.setReadonly(false);
					subclassTbId.setReadonly(false);
					dcsTbId.setReadonly(false);
					vcodeTbId.setReadonly(false);
					taxTbId.setReadonly(false);
					sizeTbId.setReadonly(false);
					brandTbId.setReadonly(false);
					udf1TbId.setReadonly(false);
					udf2TbId.setReadonly(false);
					udf3TbId.setReadonly(false);
					udf4TbId.setReadonly(false);
					udf5TbId.setReadonly(false);
					udf6TbId.setReadonly(false);
					
					addItemBtnId.setLabel("Update");
					addItemBtnId.setVisible(true);
					cancelItemBtnId.setVisible(true);
					editItemBtnId.setVisible(false);
					backBtnId.setVisible(false);
					
				}
				
			}
			
		}
	}//skuSettings()
	
	public void onClick$backBtnId() {
		
		itemsTabBoxId.setSelectedIndex(0);
		onSelect$itemsTabBoxId();
	}// onClick$backBtnId()
	
	public void onClick$cancelItemBtnId() {
		
		itemsTabBoxId.setSelectedIndex(0);
		onSelect$itemsTabBoxId();
	}// onClick$cancelItemBtnId()
	
	public void onSelect$itemsTabBoxId() {
		if(itemsTabBoxId.getSelectedIndex()==0) {
			searchLBId.setSelectedIndex(0);
			searchTBId.setValue("");
			setpageTotCount(null);
			setDefaultItems(0,itemsPagingId.getPageSize(),null);
		}else if(itemsTabBoxId.getSelectedIndex()==1) {
			descTbId.setValue("");descTbId.setReadonly(false);//descTbId.setDisabled(false);
			catTbId.setValue("");catTbId.setReadonly(false);//catTbId.setDisabled(false);
			sidTbId.setValue("");sidTbId.setReadonly(false);sidTbId.setDisabled(false);
			deptTbId.setValue("");deptTbId.setReadonly(false);
			priceTbId.setValue("");priceTbId.setReadonly(false);//priceTbId.setDisabled(false);
			classTbId.setValue("");classTbId.setReadonly(false);
			subclassTbId.setValue("");subclassTbId.setReadonly(false);
			dcsTbId.setValue("");dcsTbId.setReadonly(false);
			vcodeTbId.setValue("");vcodeTbId.setReadonly(false);
			taxTbId.setValue("");taxTbId.setReadonly(false);
			sizeTbId.setValue("");sizeTbId.setReadonly(false);
			brandTbId.setValue("");brandTbId.setReadonly(false);
			udf1TbId.setValue("");udf1TbId.setReadonly(false);
			udf2TbId.setValue("");udf2TbId.setReadonly(false);
			udf3TbId.setValue("");udf3TbId.setReadonly(false);
			udf4TbId.setValue("");udf4TbId.setReadonly(false);
			udf5TbId.setValue("");udf5TbId.setReadonly(false);
			udf6TbId.setValue("");udf6TbId.setReadonly(false);
			
			addItemBtnId.setLabel("Add Item");
			addItemBtnId.setVisible(true);
			cancelItemBtnId.setVisible(true);
			editItemBtnId.setVisible(false);
			backBtnId.setVisible(false);

		}
		
	}//onSelect$itemsTabBoxId()
	
	public void onSelect$itemsPerPageLBId() {
		try {
			String searchStr = (searchTBId.getValue().trim().equals("") || searchTBId.getValue().trim().equals("Search...")) ? null : searchTBId.getValue().trim() ;
			if(searchLBId.getSelectedIndex()==1) searchStr="SID :: "+searchStr;
			else if(searchLBId.getSelectedIndex()==2) searchStr="Description :: "+searchStr;
			else if(searchLBId.getSelectedIndex()==3) searchStr="Category :: "+searchStr;
			int n =Integer.parseInt(itemsPerPageLBId.getSelectedItem().getLabel().trim());
			itemsPagingId.setPageSize(n);
			//searchTBId.setText("");
			setpageTotCount(searchStr);
			setDefaultItems(0, n, searchStr);
			
			
		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//onSelect$itemsPerPageLBId()
	
	//for Export button
	public void onClick$exportBtnId() {
			logger.info("clicked on Export Button...");
			createWindow();
			custExport.setVisible(true);
			custExport.doHighlighted();
			
	}//onClick$exportBtnId()
	
	 public void onClick$selectAllAnchr$custExport() {
		 anchorEvent(true);
	 }

	 public void onClick$clearAllAnchr$custExport() {
		anchorEvent(false);
	 }

	 public void anchorEvent(boolean flag) {
		List<Component> chkList = custExport$chkDivId.getChildren();
		 Checkbox tempChk = null;
		 for (int i = 0; i < chkList.size(); i++) {
			 if(!(chkList.get(i) instanceof Checkbox)) continue;

			 tempChk = (Checkbox)chkList.get(i);
			 tempChk.setChecked(flag);

		 } // for
	 }
	
	
	public void createWindow()	{

		try {
			Components.removeAllChildren(custExport$chkDivId);
			Checkbox tempChk2 = new Checkbox("Item Description");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);		
			
			tempChk2 = new Checkbox("Item Category");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
            
			tempChk2 = new Checkbox("Department");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
            
		    tempChk2 = new Checkbox("Item SID");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);


			tempChk2 = new Checkbox("Class");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("Sub Class");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("List Price");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
			tempChk2 = new Checkbox("Vendor Code");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);

			tempChk2 = new Checkbox("DCS");
			tempChk2.setSclass("custCheck");
			tempChk2.setParent(custExport$chkDivId);
			tempChk2.setChecked(true);
			
//			tempChk2 = new Checkbox("Tax");
//			tempChk2.setSclass("custCheck");
//			tempChk2.setParent(custExport$chkDivId);
//			tempChk2.setChecked(true);
//			
//			tempChk2 = new Checkbox("Size");
//			tempChk2.setSclass("custCheck");
//			tempChk2.setParent(custExport$chkDivId);
//			tempChk2.setChecked(true);
//
//			tempChk2 = new Checkbox("Brand");
//			tempChk2.setSclass("custCheck");
//			tempChk2.setParent(custExport$chkDivId);
//			tempChk2.setChecked(true);
			
			
			
			

		} catch (Exception e) {
			logger.error("Exception ::", e);
		}
	}//createWindow()
	
	//To export data to file
	public void onClick$selectFieldBtnId$custExport() {

		 custExport.setVisible(false);
		 List<Component> chkList = custExport$chkDivId.getChildren();

		 int indexes[]=new int[chkList.size()];
		 
		 boolean checked=false;

		 for(int i=0;i<chkList.size();i++) {
			 indexes[i]=-1;
		 } // for

		 Checkbox tempChk = null;

		 for (int i = 0; i < chkList.size(); i++) {
			 if(!(chkList.get(i) instanceof Checkbox)) continue;

			 tempChk = (Checkbox)chkList.get(i);

			 if(tempChk.isChecked()) {
				 indexes[i]=0;
				 checked=true;
			 }else{
					indexes[i]=-1;
				}

		 } // for


		 if( ((Checkbox)custExport$chkDivId.getLastChild()).isChecked()) {

			 checked=true;
		 }

		 if(checked) {

			 int confirm=Messagebox.show("Do you want to export with selected fields ?", "Confirm",Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			 if(confirm==1){
				 try{
					 String type = exportCbId.getSelectedItem().getValue();
					 logger.info("FileType---- >"+type);
					 if(type.contains("csv"))
						 exportCSV((String)exportCbId.getSelectedItem().getValue(),indexes);
					 exportCSV(type.toString(),indexes);
					 //if(type.contains("xls"))
							 //exportExcel((String)exportCbId.getSelectedItem().getValue(),indexes);

				 }catch(Exception e){
					 logger.error("Exception caught :: ",e);
				 }
			 }
			 else{
				 custExport.setVisible(true);
			 }

		 }
		 else {

			 MessageUtil.setMessage("Please select atleast one field", "red");
			 custExport.setVisible(false);
		 }

	 }//onClick$selectFieldBtnId$custExport()
	
	private void exportCSV(String value, int[] indexes) {
	 	logger.debug("-- just entered into exportCSV --");
		String type = exportCbId.getSelectedItem().getValue();
		logger.info("FileType---- >"+type);
		StringBuffer sb = null;
		String userName = GetUser.getUserName();
		String usersParentDirectory = (String)PropertyUtil.getPropertyValue("usersParentDirectory");
		String exportDir = usersParentDirectory + "/" + userName + "/Export/" ;
		File downloadDir = new File(exportDir);
		if(downloadDir.exists()){
			try {
				FileUtils.deleteDirectory(downloadDir);
				logger.debug(downloadDir.getName() + " is deleted");
			} catch (Exception e) {
				logger.error("Exception ::" , e);
				
				logger.debug(downloadDir.getName() + " is not deleted");
			}
		}
		if(!downloadDir.exists()){
			downloadDir.mkdirs();
		}
		
			
			String filePath = exportDir +  "Item_Reports_" +
				MyCalendar.calendarToString(Calendar.getInstance(), MyCalendar.FORMAT_YEARTOSEC, clientTimeZone);
				try {
						filePath = filePath + "_ItemReports"+"."+type;
						logger.debug("Download File path : " + filePath);
						File file = new File(filePath);
						logger.info("file---->"+file);
						BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
						
						String searchStr = (searchTBId.getValue().trim().equals("Search...") || 
								searchTBId.getValue().trim().equals("")) ? null : searchTBId.getValue().trim() ;
						if(searchLBId.getSelectedIndex()==1) searchStr="SID :: "+searchStr;
						else if(searchLBId.getSelectedIndex()==2) searchStr="Description :: "+searchStr;
						else if(searchLBId.getSelectedIndex()==3) searchStr="Category :: "+searchStr;
						int count = skuFileDao.findItemsCount(currentUser.getUserId(),searchStr);
						//int count = itemsPagingId.getPageSize();
						if(count == 0) {
							Messagebox.show("No items reports found.","Info", Messagebox.OK,Messagebox.INFORMATION);
							return;
						}
						
						
						 String udfFldsLabel= "";
						 if(indexes[0]==0) {
							 udfFldsLabel += "\""+"Item Description"+"\""+",";
						 }
						 if(indexes[1]==0) {
							 udfFldsLabel += "\""+"Item Category"+"\""+",";
						 }
						 if(indexes[2]==0) {
							 udfFldsLabel += "\""+"Department"+"\""+",";
						 }
						
						 if(indexes[3]==0) {
							 udfFldsLabel += "\""+"Item SID"+"\""+",";
						 }
						 if(indexes[4]==0) {
							 udfFldsLabel += "\""+"Class"+"\""+",";
						 }
						 if(indexes[5]==0) {
							 udfFldsLabel += "\""+"Sub Class"+"\""+",";
						 }
						 if(indexes[6]==0) {
							 udfFldsLabel += "\""+"List Price"+"\""+",";
						 }	
						 if(indexes[7]==0) {
							 udfFldsLabel += "\""+"Vendor Code"+"\""+",";
						 }	
						 if(indexes[8]==0) {

							 udfFldsLabel += "\""+"DCS"+"\""+",";
						 }
						 sb = new StringBuffer();
						 sb.append(udfFldsLabel);
						 sb.append("\r\n");
						 bw.write(sb.toString());
						 //System.gc();
						
						int size = 1000;
						List<SkuFile> itemList = null;
						itemList = skuFileDao.getAllSkuByUserId(currentUser.getUserId(),0,count,searchStr);
						logger.info("itemlist size is"+itemList.size());
						for (int i = 0; i < count; i+=size) {
							sb = new StringBuffer();
							if(itemList != null) {
							if(itemList.size()>0){
								for (SkuFile item : itemList) {
									if(indexes[0]==0) {
										sb.append("\"");sb.append(item.getDescription()); sb.append("\",");
									}
									if(indexes[1]==0) {
										sb.append("\"");sb.append(item.getItemCategory()); sb.append("\",");
									}
									if(indexes[2]==0) {
										sb.append("\"");sb.append(item.getDepartmentCode()); sb.append("\",");
									}
									if(indexes[3]==0) {
										sb.append("\"");sb.append(item.getItemSid()); sb.append("\",");
									}
									if(indexes[4]==0) {
										sb.append("\"");sb.append(item.getClassCode()); sb.append("\",");
									}
									if(indexes[5]==0) {
										sb.append("\"");sb.append(item.getSubClassCode()); sb.append("\",");
									}
									if(indexes[6]==0) {
										sb.append("\"");sb.append(item.getListPrice()!=null ? ""+item.getListPrice().doubleValue() : ""); sb.append("\",");
									}
									if(indexes[7]==0) {
										sb.append("\"");sb.append(item.getVendorCode()); sb.append("\",");
									}
									if(indexes[8]==0) {
									sb.append("\"");sb.append(item.getDCS()); sb.append("\",");
									}
									sb.append("\r\n");
								}
								
							}
							}
							
							bw.write(sb.toString());
							itemList = null;
							sb = null;
							//System.gc();
						}
						bw.flush();
						bw.close();
						Filedownload.save(file, "text/plain");
						
						
					}catch (IOException e) {
							logger.error("Exception ::",e);
					}
					logger.debug("-- exit --");
	
	}//exportCSV()

}
