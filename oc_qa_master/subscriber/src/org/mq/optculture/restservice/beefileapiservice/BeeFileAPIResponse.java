package org.mq.optculture.restservice.beefileapiservice;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.MessageUtil;
import org.mq.marketer.campaign.general.PropertyUtil;
import org.mq.marketer.campaign.general.Utility;
import org.mq.optculture.model.beefileapi.DataPojo;
import org.mq.optculture.model.beefileapi.DirPojo;
import org.mq.optculture.model.beefileapi.Extra;
import org.mq.optculture.model.beefileapi.FilePojo;
import org.mq.optculture.model.beefileapi.FininalResponse;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.OptCultureUtils;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

// Bee file API Response service
public class BeeFileAPIResponse extends HttpServlet implements org.springframework.web.servlet.mvc.Controller{
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	final static Pattern PATTERN = Pattern.compile("(.*?)(?:\\((\\d+)\\))?(\\.[^.]*)?");
	//controller implements
	@Override
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		String userName = null;
		String responseJson=null;
		try {
			logger.info("Bee File API request getHeaderNames:"+request.getHeaderNames());
			String method = request.getMethod();
			HashMap<String, String> map = new HashMap<String, String>();
			Enumeration<String> headerNames = request.getHeaderNames();
			while(headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
				map.put(key, value);
				if(key.equalsIgnoreCase(OCConstants.BEE_UID_KEY)) userName = value;
				logger.info("value offffffffff "+key+"   :   "+value);
			}
			String contextPath= request.getContextPath();
			logger.info("request.getContextPath()"+contextPath);
			logger.info("getrequesturl"+request.getRequestURL());
			logger.info("request.getRequestURI()"+request.getRequestURI());
			String pathInfo = request.getPathInfo(); 
			String[] pathParts=null;
			String requestedPath=null;
			if(pathInfo!=null){
				pathParts = pathInfo.split("/BeeFileAPI");
				for(int i=0;i<=pathParts.length-1;i++){
					logger.info("request.getPathInfo()"+pathParts[i]);
					requestedPath=pathParts[i];
				}//TODO else for exception if pathparts is null
				requestedPath=requestedPath.replaceAll("/Gallery", "");
			}
			// hard coded the requested path just for local testing
			String requestedPath_User = userName + requestedPath;
			logger.info("userName  :"+userName);
			if(requestedPath.contains("editor_images"))	requestedPath=requestedPath.replace("editor_images", "My Images");
			if(method.equalsIgnoreCase("GET"))
				responseJson=processGetMethod(userName,requestedPath);
			else if(method.equalsIgnoreCase("POST")){
				logger.info("method : "+method);
				String requestedJSON=null;
				String sourceUrl=null;
				JSONObject requestedJsonFileUpload = null;
				//if(requestedPath.contains(".")){
				if(requestedPath.endsWith(".jpeg")||requestedPath.endsWith(".jpg")||requestedPath.endsWith(".gif")||requestedPath.endsWith(".png")||requestedPath.endsWith(".bmp")||requestedPath.endsWith(".JPEG")||requestedPath.endsWith(".JPG")||requestedPath.endsWith(".GIF")||requestedPath.endsWith(".PNG")||requestedPath.endsWith(".BMP")){
					requestedJSON=OptCultureUtils.getParameterJsonValue(request);
					//logger.info("requested JSON..:"+requestedJSON);
					requestedJsonFileUpload = (JSONObject)JSONValue.parse(requestedJSON); 
					sourceUrl = (requestedJsonFileUpload.get("source")).toString();
					logger.info("sourceUrl....:"+sourceUrl);
				}
				responseJson=processPostMethod(userName,requestedPath,sourceUrl);
			}
			else if(method.equalsIgnoreCase("DELETE"))
				responseJson=processDeleteMethod(userName,requestedPath);
			responseJson=responseJson.replaceAll("mimetype", "mime-type");
			responseJson=responseJson.replaceAll("lastmodified", "last-modified");
			responseJson=responseJson.replaceAll("itemcount", "item-count");
			responseJson=responseJson.replaceAll("publicurl", "public-url");
			response.setContentType("application/json");
			try {
				PrintWriter printWriter = response.getWriter();
				printWriter.write(responseJson);
				printWriter.flush();
				printWriter.close();
			} catch (IOException e) {
				logger.error("printWriter = response.getWriter() Exception ::" , e);
			}
		} catch (Exception e) {
			logger.error("BeeFileAPIResponse Exception ::" , e);
		}
		return null;
	}

	private String processGetMethod(String userName, String requestedPath){		
		Gson gson = new Gson(); 
		DataPojo dataObject = prepareDataObject(userName,requestedPath);		         
		FininalResponse fininalResponse = new FininalResponse();         
		fininalResponse.setStatus("success");
		fininalResponse.setData(dataObject);		
		String responseJson = gson.toJson(fininalResponse);		 
		//logger.info("responseJson....:"+responseJson);		
		return responseJson; 
	}


	private DataPojo prepareDataObject(String userName, String requestedPath){   
		Gson gson = new Gson(); 
		DirPojo dirPojoObject = prepareDirPojoObject(userName,requestedPath);	
		List<FilePojo> filePojoObject = prepareFilePojo(userName,requestedPath);	
		String responseJson = gson.toJson(filePojoObject);		 
		//logger.info("filePojoObject responseJson....:"+responseJson);
		DataPojo dataPojoOject = new DataPojo();
		dataPojoOject.setMeta(dirPojoObject);
		dataPojoOject.setItems(filePojoObject);	
		responseJson = gson.toJson(dataPojoOject);		 
		//logger.info("dataPojoOject responseJson....:"+responseJson);
		return dataPojoOject;
	}//prepareDataObject()

	private DirPojo prepareDirPojoObject(String userName, String requestedPath){	   
		String usersParentDirectory=null;
		DirPojo dirObj = new DirPojo();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		String APP_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		logger.debug ("usersParentDirectory: " + usersParentDirectory);
		Gson gson = new Gson();
		logger.debug ("user Name : " + userName);
		try {
			File saveDirectory = new File(usersParentDirectory + "/" +  userName + "/Gallery"+requestedPath );
			if(saveDirectory.isDirectory()){
				dirObj.setMimetype("application/directory");
				if(requestedPath.equalsIgnoreCase("/"))
					dirObj.setName("root");
				else
					dirObj.setName(saveDirectory.getName());
				dirObj.setPath(requestedPath);
				dirObj.setLastmodified(saveDirectory.lastModified());
				dirObj.setSize(0);
				dirObj.setPermissions("rw");
				String[] children = saveDirectory.list(); 
				int directoryCount = children.length;
				dirObj.setItemcount(directoryCount);
				Extra extra = new Extra();
				extra.setFutureExtensions(null);
				dirObj.setExtra(extra);

				String responseJson = gson.toJson(dirObj);		 
				//logger.info("values responseJson....:"+responseJson);
			}else{
				dirObj.setMimetype("image/"+(saveDirectory.getName()).substring(((saveDirectory.getName()).lastIndexOf("."))+1));
				logger.debug("imageeeee");
				dirObj.setName(saveDirectory.getName());
				logger.debug("file.getname.."+saveDirectory.getName());
				dirObj.setPath(requestedPath);
				logger.debug("requestedPath+file.getName().."+requestedPath);
				dirObj.setLastmodified(saveDirectory.lastModified());
				logger.debug("file.lastModified().."+saveDirectory.lastModified());
				File file1 = new File(saveDirectory.getAbsolutePath());
				logger.debug("file.getAbsolutePath().."+saveDirectory.getAbsolutePath());
				dirObj.setSize(file1.length());
				logger.debug("file1.length().."+file1.length());
				dirObj.setPermissions("rw");
				String imagepath=saveDirectory.getAbsolutePath();
				logger.debug("imagepath.."+imagepath);
				if(imagepath.contains(usersParentDirectory))
					imagepath=imagepath.replace(usersParentDirectory, "");
				dirObj.setPublicurl(APP_URL+"UserData"+imagepath);
				dirObj.setThumbnail(APP_URL+"UserData"+imagepath);
				logger.debug("APP_URL+imagepath.."+APP_URL+imagepath);
				Extra extra= new Extra();
				extra.setFutureExtensions(null);
				dirObj.setExtra(extra);
				logger.debug("extra.."+extra);
				String responseJson1  = gson.toJson(dirObj);		 
				//logger.info("filePojoObject file responseJson....:"+responseJson1);
			}
		} 
		catch (Exception e) {
			logger.error("prepareDirPojoObject() Exception ::" , e);

		}
		return dirObj;
	}//prepareDirPojoObject

	private List<FilePojo> prepareFilePojo(String userName, String requestedPath){
		String usersParentDirectory;
		String APP_URL = PropertyUtil.getPropertyValue("ApplicationUrl");	   
		List<FilePojo> filePojoList= null;
		FilePojo filePojo =null;
		String responseJson=null;
		Gson gson = new Gson();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		logger.debug ("usersParentDirectory: " + usersParentDirectory);
		logger.debug("user Name : " + userName);
		try {
			File saveDirectory = new File(usersParentDirectory + "/" +userName + "/Gallery"+requestedPath);
			File[] children = saveDirectory.listFiles(); 	
			filePojoList = new ArrayList<FilePojo>();		
			if(children.length>0){
				for(int i=0;i< children.length;i++){
					logger.debug("gallery name is: " + children[i]);
					filePojo = new FilePojo();
					File file = children[i];
					if(children[i].isDirectory()){
						filePojo.setMimetype("application/directory");	
						filePojo.setName(file.getName());
						filePojo.setPath(requestedPath+file.getName()+"/");
						logger.debug("requestedPath+file.getName()"+requestedPath+file.getName());
						filePojo.setLastmodified(file.lastModified());
						logger.debug("file.lastModified()"+file.lastModified());
						filePojo.setSize(0);
						filePojo.setPermissions("rw");
						File presentdir = new File(file.getAbsolutePath());
						logger.debug("file.getAbsolutePath()"+file.getAbsolutePath());
						String[] subDirSize = presentdir.list();
						filePojo.setItemcount(subDirSize.length);
						Extra extra= new Extra();
						extra.setFutureExtensions(null);
						filePojo.setExtra(extra);
						filePojoList.add(filePojo);	
						responseJson = gson.toJson(filePojoList);		 
						//logger.info("filePojoObject dir responseJson....:"+responseJson);
					}
					else{
						filePojo.setMimetype("image/"+(file.getName()).substring(((file.getName()).lastIndexOf("."))+1));
						logger.debug("imageeeee");
						filePojo.setName(file.getName());
						logger.debug("file.getname.."+file.getName());
						filePojo.setPath(requestedPath+file.getName());
						logger.debug("requestedPath+file.getName().."+requestedPath+file.getName());
						filePojo.setLastmodified(file.lastModified());
						logger.debug("file.lastModified().."+file.lastModified());
						File file1 = new File(file.getAbsolutePath());
						logger.debug("file.getAbsolutePath().."+file.getAbsolutePath());
						filePojo.setSize(file1.length());
						logger.debug("file1.length().."+file1.length());
						filePojo.setPermissions("rw");
						String imagepath=file.getAbsolutePath();
						logger.debug("imagepath.."+imagepath);
						if(imagepath.contains(usersParentDirectory))
							imagepath=imagepath.replace(usersParentDirectory, "");
						filePojo.setPublicurl(APP_URL+"UserData"+imagepath);
						filePojo.setThumbnail(APP_URL+"UserData"+imagepath);
						logger.debug("APP_URL+imagepath.."+APP_URL+imagepath);
						Extra extra= new Extra();
						extra.setFutureExtensions(null);
						filePojo.setExtra(extra);
						logger.debug("extra.."+extra);
						filePojoList.add(filePojo);
						responseJson = gson.toJson(filePojoList);		 
						//logger.info("filePojoObject file responseJson....:"+responseJson);
					}
				}
			}else{
				responseJson = gson.toJson(filePojoList);	
				//logger.info("filePojoObject file responseJson....:"+responseJson);
			}
		} catch (Exception e) {
			logger.error("prepareFilePojo() Exception ::" , e);
		}
		return filePojoList;	   
	}//prepareFilePojo

private String processDeleteMethod(String userName,String requestedPath){  
		Gson gson = new Gson();
		String usersParentDirectory = null;
		String responseJson=null;
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		//TODO change to generic for image and dir path
		File delFile = new File(usersParentDirectory+"/"+userName+"/Gallery"+requestedPath);
		if(delFile.exists()&delFile.isDirectory()){
			logger.info("folder exits..");
			delFile.delete();
			responseJson="{\"status\": \"success\",\"data\": null}";
			//logger.info("responseJson....:"+responseJson);
		}
		else if(delFile.exists()&delFile.isFile()){
			logger.info("Image Doesnot exits..");
			delFile.delete();
			responseJson="{\"status\": \"success\",\"data\": null}";
		}
		else{
			if(requestedPath.endsWith(".jpeg")||requestedPath.endsWith(".jpg")||requestedPath.endsWith(".gif")||requestedPath.endsWith(".png")||requestedPath.endsWith(".bmp"))
				responseJson="{\"status\": \"error\",\"message\": \"Image does not Exists\"}";
			else
				responseJson="{\"status\": \"error\",\"message\": \"Folder does not Exists\"}";
		}
		return responseJson; 
	} 
    static final String BEE_FREE_HTTPS_TOKEN = "https://";
    static final String BEE_FREE_HTTP_TOKEN = "http://";
	private String processPostMethod(String userName, String requestedPath,String sourceUrl){
		String usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		String responseJson=null;
		logger.info("sourceUrl before replaceing https"+sourceUrl);
		if(sourceUrl!=null && sourceUrl.contains(BEE_FREE_HTTPS_TOKEN)){
			sourceUrl=sourceUrl.replace(BEE_FREE_HTTPS_TOKEN, BEE_FREE_HTTP_TOKEN);
			logger.info("sourceUrl after replaceing https with http"+sourceUrl);
		} 
		if(requestedPath.contains("/My Images/")){
			File createEditorImagesDir = new File(usersParentDirectory + "/" + userName+"/Gallery/My Images");
			if(!createEditorImagesDir.exists())
			createEditorImagesDir.mkdir();
		}
		File newFile = new File(usersParentDirectory + "/" + userName+"/Gallery"+requestedPath);
		String[] pathParts2 = requestedPath.split("/");
		logger.info("requestedPath   :"+requestedPath+"   pathParts size   :"+pathParts2.length);
		if(pathParts2.length==2 && (requestedPath.endsWith(".jpeg")||requestedPath.endsWith(".jpg")||requestedPath.endsWith(".gif")||requestedPath.endsWith(".png")||requestedPath.endsWith(".bmp")||requestedPath.endsWith(".JPEG")||requestedPath.endsWith(".JPG")||requestedPath.endsWith(".GIF")||requestedPath.endsWith(".PNG")||requestedPath.endsWith(".BMP"))){
			File newFileFirstLevel = new File(usersParentDirectory + "/" + userName+"/Gallery"+"/My Images");
			if(!newFileFirstLevel.exists()) newFileFirstLevel.mkdir();
			requestedPath="/My Images"+requestedPath;
		}
		if(!newFile.exists() && !(requestedPath.endsWith(".jpeg")||requestedPath.endsWith(".jpg")||requestedPath.endsWith(".gif")||requestedPath.endsWith(".png")||requestedPath.endsWith(".bmp")||requestedPath.endsWith(".JPEG")||requestedPath.endsWith(".JPG")||requestedPath.endsWith(".GIF")||requestedPath.endsWith(".PNG")||requestedPath.endsWith(".BMP"))){
			try{
				String[] pathParts = requestedPath.split("/");
				logger.info("requestedPath   :"+requestedPath+"   pathParts size   :"+pathParts.length);
				if(pathParts.length>2){
					logger.info("Error :Only one level of folders can be created under Gallery.");
					return responseJson="{\"status\": \"error\",\"message\": \"Only one level of folders can be created under Gallery.\"}";
				}
				else{
					logger.info("folder name :"+pathParts[1]);
					String dirName = pathParts[1];
					boolean isAnySpecChar = isAnySpecialCharWithOutExtentsion(dirName);
					if(isAnySpecChar){
						//return responseJson="{\"code\": \"3907\",\"HTTP Status\": \"400\",\"message\": \"Allowed characters in folder name are: A-Z, a-z, 0-9, _ and space.\",\"Details\":\"Due to special characters in folder name. folder cannot be created.\"}";
						/** APP-1340
						 **/
				return responseJson="{\"code\": \"3907\",\"HTTP Status\": \"400\",\"message\": \"Unable to create the folder. Please ensure that your folder's name has only the allowed characters (A-Z, a-z, 0-9, &, +, -, =, @, _ and space) and try again.\",\"Details\":\"Due to special characters in folder name. folder cannot be created.\"}";
					}
					logger.info("File existed and make di"+newFile.mkdir());
					newFile.mkdir();
					Gson gson = new Gson(); 
					DataPojo dataObject = prepareDataObjectPostMethod(userName,requestedPath);		         
					FininalResponse fininalResponse = new FininalResponse();         
					fininalResponse.setStatus("success");
					fininalResponse.setData(dataObject);		
					responseJson = gson.toJson(fininalResponse);		 
				}
			}
			catch (Exception e) {
				MessageUtil.setMessage("Problem experienced while creating the new folder.", "color:red", "TOP");
			}
		}
		else if(((requestedPath.endsWith(".jpeg")||requestedPath.endsWith(".jpg")||requestedPath.endsWith(".gif")||requestedPath.endsWith(".png")||requestedPath.endsWith(".bmp"))||(requestedPath.endsWith(".JPEG")||requestedPath.endsWith(".JPG")||requestedPath.endsWith(".GIF")||requestedPath.endsWith(".PNG")||requestedPath.endsWith(".BMP")))){
			try {
				File imageNamepath = new File(usersParentDirectory+"/"+userName+"/Gallery"+requestedPath);
				/*if(imageNamepath.exists()){
					File createDuplicateImagesDir = new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/");
					if(!createDuplicateImagesDir.exists()){
						File createdonotdeleteimagesImagesDir = new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/duplicateimages_1");
						createdonotdeleteimagesImagesDir.mkdirs();
					}
					String[] children=createDuplicateImagesDir.list();
					String newimageNameExtention=children[0].substring(16);
					int newimageNameExtentionIncrement= Integer.parseInt(newimageNameExtention);
					String imageNameAfterRename= requestedPath.substring(requestedPath.lastIndexOf("/")+1,requestedPath.lastIndexOf("."))+newimageNameExtention+requestedPath.substring(requestedPath.lastIndexOf("."));
					String requestedPathAfterRename=requestedPath.substring(0, requestedPath.lastIndexOf("/")+1);
					File createdonotdeleteimagesImagesDir = new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/"+children[0]);
					try {
						FileUtils.copyURLToFile(new URL(sourceUrl), new File(usersParentDirectory + "/" + userName+"/Gallery"+requestedPathAfterRename+imageNameAfterRename));
						createdonotdeleteimagesImagesDir.renameTo(new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/duplicateimages_"+(newimageNameExtentionIncrement+1)));
					} catch (MalformedURLException e) {
						logger.error("MalformedURLException"+e);	
						e.printStackTrace();
					} catch (IOException e) {
						logger.error("IOException"+e);
						e.printStackTrace();
					}
					Gson gson = new Gson(); 
					DataPojo dataObject = prepareDataObjectPostMethod(userName,requestedPathAfterRename+imageNameAfterRename);		         
					FininalResponse fininalResponse = new FininalResponse();         
					fininalResponse.setStatus("success");
					fininalResponse.setData(dataObject);		
					responseJson = gson.toJson(fininalResponse);		 
					logger.info("processPostMethod for image....:"+responseJson);
				}*/
				if(imageNamepath.exists()){
					//File createDuplicateImagesDir = new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/");
					//if(!createDuplicateImagesDir.exists()){
					//	File createdonotdeleteimagesImagesDir = new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/duplicateimages_1");
					//	createdonotdeleteimagesImagesDir.mkdirs();
					//}
					//String[] children=createDuplicateImagesDir.list();
					//String newimageNameExtention=children[0].substring(16);
					//int newimageNameExtentionIncrement= Integer.parseInt(newimageNameExtention);
					//String imageNameAfterRename= requestedPath.substring(requestedPath.lastIndexOf("/")+1,requestedPath.lastIndexOf("."))+newimageNameExtention+requestedPath.substring(requestedPath.lastIndexOf("."));
					//String requestedPathAfterRename=requestedPath.substring(0, requestedPath.lastIndexOf("/")+1);
					//File createdonotdeleteimagesImagesDir = new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/"+children[0]);
					String imageName = requestedPath.substring(requestedPath.lastIndexOf("/")+1);
					boolean isAnySpecChar = isAnySpecialCharWithExtentsion(imageName);
					if(isAnySpecChar){
						//return responseJson="{\"code\": \"3907\",\"HTTP Status\": \"400\",\"message\": \"Allowed characters in image name are: A-Z, a-z, 0-9, &, +, -, =, @,  _ and space.\",\"Details\":\"Due to special characters in image name. Image cannot be uploaded.\"}";
						/** APP-1340
						 **/
						return responseJson="{\"code\": \"3907\",\"HTTP Status\": \"400\",\"message\": \"Unable to upload the image. Please ensure that your image's name has only the allowed characters (A-Z, a-z, 0-9, &, +, -, =, @, _ and space) and try again.\",\"Details\":\"Due to special characters in image name. Image cannot be uploaded.\"}";

					}
					String requestedPathAfterRename = requestedPath.substring(0, requestedPath.lastIndexOf("/")+1);
					String dirPathOfImage = usersParentDirectory+"/"+userName+"/Gallery"+requestedPath.substring(0, requestedPath.lastIndexOf("/"));
					String imageNameAfterRename = getNewName(imageName,dirPathOfImage);
					try {
						FileUtils.copyURLToFile(new URL(sourceUrl), new File(dirPathOfImage+"/"+imageNameAfterRename));
						//FileUtils.copyURLToFile(new URL(sourceUrl), new File(usersParentDirectory + "/" + userName+"/Gallery"+requestedPathAfterRename+imageNameAfterRename));
						//createdonotdeleteimagesImagesDir.renameTo(new File(usersParentDirectory + "/" + userName+"/DonotDeleteThisFolder/duplicateimages_"+(newimageNameExtentionIncrement+1)));
					} catch (MalformedURLException e) {
						logger.error("MalformedURLException"+e);	
						e.printStackTrace();
					} catch (IOException e) {
						logger.error("IOException"+e);
						e.printStackTrace();
					}
					Gson gson = new Gson(); 
					DataPojo dataObject = prepareDataObjectPostMethod(userName,requestedPathAfterRename+imageNameAfterRename);		         
					FininalResponse fininalResponse = new FininalResponse();         
					fininalResponse.setStatus("success");
					fininalResponse.setData(dataObject);		
					responseJson = gson.toJson(fininalResponse);		 
					//logger.info("processPostMethod for image....:"+responseJson);
				}else{
					try{
						String[] pathParts = requestedPath.split("/");
						String imageName=pathParts[pathParts.length-1];
						
						boolean isAnySpecChar = isAnySpecialCharWithExtentsion(imageName);
						if(isAnySpecChar){
							//return responseJson="{\"code\": \"3906\",\"HTTP Status\": \"400\",\"message\": \"Allowed characters in image name are: A-Z, a-z, 0-9, &, +, -, =, @,  _ and space.\",\"Details\":\"Due to special characters in image name. Image cannot be uploaded.\"}";
							/** APP-1340
							 **/
							return responseJson="{\"code\": \"3906\",\"HTTP Status\": \"400\",\"message\": \"Unable to upload the image. Please ensure that your image's name has only the allowed characters (A-Z, a-z, 0-9, &, +, -, =, @, _ and space) and try again.\",\"Details\":\"Due to special characters in image name. Image cannot be uploaded.\"}";

						}
						
						String imageNameAfterRename= (imageName.substring(0, imageName.lastIndexOf("."))).trim();
						String requestedPathAfterRename = requestedPath.replaceAll(imageName, imageNameAfterRename+imageName.substring(imageName.lastIndexOf(".")));
						logger.info("sourceUrl...:"+sourceUrl);
						logger.info("usersParentDirectory + / + userName+/Gallery+requestedPathAfterRename..:"+usersParentDirectory + "/" + userName+"/Gallery"+requestedPathAfterRename);
						FileUtils.copyURLToFile(new URL(sourceUrl), new File(usersParentDirectory + "/" + userName+"/Gallery"+requestedPathAfterRename));
					}catch (MalformedURLException e) {
						logger.error("copyURLToFile MalformedURLException Exception:",e);
						return responseJson="{\"status\": \"error\",\"message\": \"Erro occured while uploading\"}";
					} catch (IOException e) {
						logger.error("copyURLToFile IOException Exception:",e);
						return responseJson="{\"status\": \"error\",\"message\": \"Erro occured while uploading\"}";
					}
					Gson gson = new Gson(); 
					DataPojo dataObject = prepareDataObjectPostMethod(userName,requestedPath);		         
					FininalResponse fininalResponse = new FininalResponse();         
					fininalResponse.setStatus("success");
					fininalResponse.setData(dataObject);		
					responseJson = gson.toJson(fininalResponse);		 
					//logger.info("processPostMethod for image....:"+responseJson);
				}
			}catch (Exception e) {
				logger.error("Image U Exception:",e);
			}
		}else{
			responseJson="{\"status\": \"error\",\"message\": \"FolderName Already Exists\"}";
		}
		return responseJson;
	}     

	private DataPojo prepareDataObjectPostMethod(String userName, String requestedPath){   
		Gson gson = new Gson(); 
		DirPojo dirPojoObject = prepareDirPojoObjectPostMethod(userName, requestedPath);	
		DataPojo dataPojoOject = new DataPojo();
		dataPojoOject.setMeta(dirPojoObject);
		String responseJson = gson.toJson(dataPojoOject);		 
		//logger.info("prepareDataObjectPostMethod responseJson....:"+responseJson);
		return dataPojoOject;
	}//prepareDataObjectPostMethod()
	
	private DataPojo prepareDataObjectPostMethodImageUpload(String userName, String requestedPath){   
		Gson gson = new Gson(); 
		DirPojo dirPojoObject = prepareDirPojoObjectPostMethod(userName, requestedPath);	
		DataPojo dataPojoOject = new DataPojo();
		dataPojoOject.setMeta(dirPojoObject);
		String responseJson = gson.toJson(dataPojoOject);		 
		//logger.info("prepareDataObjectPostMethodImageUpload responseJson....:"+responseJson);
		return dataPojoOject;
	}//prepareDataObjectPostMethod()prepareFilePojo

	private DirPojo prepareDirPojoObjectPostMethod(String userName, String requestedPath){	   
		String usersParentDirectory;
		String APP_URL = PropertyUtil.getPropertyValue("ApplicationUrl");
		DirPojo dirobj = new DirPojo();
		usersParentDirectory = PropertyUtil.getPropertyValue("usersParentDirectory");
		logger.debug ("usersParentDirectory: " + usersParentDirectory);
		try {
			File saveDirectory = new File(usersParentDirectory + "/" +  userName+"/Gallery"+requestedPath);
			if(saveDirectory.isDirectory()){
				dirobj.setMimetype("application/directory");
				dirobj.setName(saveDirectory.getName());
				if(requestedPath.endsWith("/")){
					requestedPath =requestedPath.substring(0, (requestedPath.lastIndexOf("/"))-1);
				}
				dirobj.setPath(requestedPath);
				dirobj.setLastmodified(saveDirectory.lastModified());
				dirobj.setSize(0);
				dirobj.setPermissions("rw");
				dirobj.setItemcount(0);
				Extra extra= new Extra();
				extra.setFutureExtensions(null);
				dirobj.setExtra(extra);
			}else if(saveDirectory.isFile()){
				dirobj.setMimetype("image/png");
				logger.debug("imageeeee");
				String[] pathParts = requestedPath.split("/");
				String imageName=pathParts[pathParts.length-1];
				dirobj.setName(imageName);
				logger.debug("imageName.."+imageName);
				dirobj.setPath(requestedPath);
				logger.debug("requestedPath.."+requestedPath);
				dirobj.setLastmodified(saveDirectory.lastModified());
				logger.debug("saveDirectory.lastModified().."+saveDirectory.lastModified());
				File file1 = new File(saveDirectory.getAbsolutePath());
				logger.debug("file.getAbsolutePath().."+saveDirectory.getAbsolutePath());
				dirobj.setSize(saveDirectory.length());
				logger.debug("file1.length().."+file1.length());
				dirobj.setPermissions("rw");
				String imagepath=saveDirectory.getAbsolutePath();
				logger.debug("imagepath.."+imagepath);
				if(imagepath.contains(usersParentDirectory))
					imagepath=imagepath.replace(usersParentDirectory, "");
				dirobj.setPublicurl(APP_URL+"UserData"+imagepath);
				dirobj.setThumbnail(APP_URL+"UserData"+imagepath);
				logger.debug("APP_URL+imagepath.."+APP_URL+imagepath);
				Extra extra= new Extra();
				extra.setFutureExtensions(null);
				dirobj.setExtra(extra);
				logger.debug("extra.."+extra);
			}
			Gson gson = new Gson(); 
			String responseJson = gson.toJson(dirobj);		 
			//logger.info("dirobj responseJson....:"+responseJson);
		} 
		catch (Exception e) {
			logger.error("prepareDirPojoObject() Exception ::" , e);
		}
		return dirobj;
	}//prepareDirPojoObjectPostMethod
	private String getNewName(String filename,String filePath) {
	    try {
			if (fileExists(filename,filePath)) {
			    Matcher m = PATTERN.matcher(filename);
			    if (m.matches()) {
			        String prefix = m.group(1);
			        String last = m.group(2);
			        String suffix = m.group(3);
			        if (suffix == null) suffix = "";

			        int count = last != null ? Integer.parseInt(last) : 0;

			        do {
			            count++;
			            filename = prefix + "(" + count + ")" + suffix;
			            
			        } while (fileExists(filename,filePath));
			    }
			}
			return filename;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			logger.info("getNewName  :",e);
			return filename;
		}
	    catch (Exception e) {
	    	logger.info("getNewName  :",e);
			return filename;
	    }
	}
	private boolean fileExists(String fileName,String filePath){
		
		try {
			File imageNamepath = new File(filePath+"/"+fileName);
			if(imageNamepath.exists()) return true;
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("while checking the file path exists or not",e);
			return false;
		}

	}
	
	
	private boolean isAnySpecialCharWithExtentsion(String fileName){
		logger.info("+++++++++++++++++"+fileName);
		try {
			if(!Utility.validateUploadFilName(fileName) || fileName.contains("'"))
				return true;
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	} //isAnySpecialChar()
	
	private boolean isAnySpecialCharWithOutExtentsion(String fileName){
		logger.info("+++++++++++++++++"+fileName);
		try {
			if(!Utility.validateName(fileName) || fileName.contains("'"))
				return true;
			else return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	} //isAnySpecialChar()
	
	
	/*public static void main(String[] args) {
		String requestedpath="/img.png";
		String[] pathParts2 = requestedpath.split("/");
		System.out.println("pathParts2.length...:"+pathParts2.length);

		if(pathParts2.length!=3){
			System.out.println("pathParts2.length...:"+pathParts2.length);
			//logger.info("Error :Images cannot be uploaded directly under Gallery.");
			//return responseJson="{\"status\": \"error\",\"message\": \"Images cannot be uploaded directly underr Gallery.\"}";
		}
		List<String> fileExtensioList=new ArrayList<String>();
        fileExtensioList.add(".png");
        fileExtensioList.add(".PNG");
        fileExtensioList.add(".jpeg");
        fileExtensioList.add(".JPEG");
        fileExtensioList.add(".jpg");
        fileExtensioList.add(".JPG");
        fileExtensioList.add(".gif");
        fileExtensioList.add(".GIF");
        fileExtensioList.add(".bmp");
        fileExtensioList.add(".BMP");
        String fileExtention=null;
        if(requestedpath.contains("."))
        	fileExtention = requestedpath.substring(requestedpath.lastIndexOf("."));
        if(fileExtensioList.contains(fileExtention))
        {
        	System.out.println(requestedpath+"ends with"+fileExtention);
        }
        System.out.println(requestedpath+"ends with"+fileExtention);
        
	}*/
	/*public static void main(String[] args) {
		
		
		
		String 	fileName="etr+";
			if(fileName == null && fileName.length() == 0) {
				logger.debug("upload filename is null ..");
				System.out.println("False");
			}
			String pattern = "(([&]|[=]|[@]|[a-z]|[A-Z]|[0-9]|[ ]|[-]|[_]|[//+])*+$)";
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(fileName);
			System.out.println("Result :"+m.matches());
		
		
		
		String sourceUrl="https://pro-bee-user-content-eu-west-1.s3.amazonaws.com/public/upload/users/Integrators/268870de-d888-4dae-8edd-cb5b5c07a/mahesh__org__ocqa/myimage.png";
		//String destinationUrl="http://dev.optculture.com/subscriber/UserData/mahesh__org__ocqa/Gallery/My Images/salute1.png";
		String destinationUrl="/home/motupallivenkat/Eclipse_work_spaces/qa_2017_06_23_workspace/subscriber/WebContent/UserData/mahesh__org__ocqa/Gallery/My Images/myimagefromhotspotwithHTTPS.png";
		try {
			System.out.println("before FileUtils.copyURLToFile");
			FileUtils.copyURLToFile(new URL(sourceUrl), new File(destinationUrl));
			//FileUtils.copyURLToFile(sourceUrl, destinationUrl, connectionTimeout, readTimeout)
			System.out.println("after FileUtils.copyURLToFile");
		} catch (MalformedURLException e) {
			logger.error("MalformedURLException"+e);	
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException"+e);
			e.printStackTrace();
		}
		System.out.println("uploaded sucessfully");
	}*/
}