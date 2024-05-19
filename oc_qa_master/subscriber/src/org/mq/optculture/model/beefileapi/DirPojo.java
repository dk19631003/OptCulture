package org.mq.optculture.model.beefileapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Array;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.general.Constants;

public class DirPojo {
	
	//mime-type
	private String mimetype;//mime-type
	private String name;
	private String path;
	private long lastmodified;//last-modified
	private long size;
	private String permissions;
	private String publicurl;
	private String thumbnail;
	private int itemcount;//item-count
	private Extra extra;
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		//TODO encoding only for imageName done
				try {
					/*String imageName = thumbnail.substring(thumbnail.lastIndexOf("/")+1);
					String encryptedImageName=URLEncoder.encode(imageName, "UTF-8").replace("+", "%20");
					logger.info("thumbnail before encryption.."+thumbnail);
					thumbnail=thumbnail.replace(imageName, encryptedImageName);
					logger.info("thumbnail after encryption.."+thumbnail);*/
					
					String[] thumbnailParts = thumbnail.split("/");
					String imageName= thumbnailParts[thumbnailParts.length-1];
					String dirName= thumbnailParts[thumbnailParts.length-2];

					String encodedImageName=URLEncoder.encode(imageName, "UTF-8").replace("+", "%20");
					String encodedDirName=URLEncoder.encode(dirName, "UTF-8").replace("+", "%20");

					logger.info("thumbnail before encoding .."+thumbnail);
					thumbnail=thumbnail.replace(imageName, encodedImageName);
					logger.info("thumbnail before encoding imagename.."+thumbnail);
					thumbnail=thumbnail.replace(dirName, encodedDirName);
					logger.info("thumbnail after encoding dirName.."+thumbnail);
					//this.thumbnail = thumbnail2;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					//this.thumbnail = thumbnail;
					logger.info("While encoding thumbnail Error",e);
				}
		this.thumbnail = thumbnail;
	}
	
	public String getPublicurl() {
		return publicurl;
	}
	public void setPublicurl(String publicurl) {
		//TODO encoding only for imageName done
		try {
            //String imageName = publicurl.substring(publicurl.lastIndexOf("/")+1);
			String[] publicurlParts = publicurl.split("/");
			String imageName= publicurlParts[publicurlParts.length-1];
			String dirName= publicurlParts[publicurlParts.length-2];
			String encodedImageName=URLEncoder.encode(imageName, "UTF-8").replace("+", "%20");
			String encodedDirName=URLEncoder.encode(dirName, "UTF-8").replace("+", "%20");
			logger.info("publicUrl before encoding.."+publicurl);
			publicurl=publicurl.replace(imageName, encodedImageName);
			logger.info("publicUrl after encoding imagename.."+publicurl);
			publicurl=publicurl.replace(dirName, encodedDirName);
			logger.info("publicUrl after encoding dirName.."+publicurl);
			//this.publicurl = publicurl2;
			
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//this.publicurl = publicurl;
			logger.info("While encoding publicurl Error",e);
		}

		this.publicurl = publicurl;
	}
      
	
	
	public Extra getExtra() {
		return extra;
	}
	public void setExtra(Extra extra) {
		this.extra = extra;
	}
	public int getItemcount() {
		return itemcount;
	}
	@XmlElement(name = "item-count")
	public void setItemcount(int itemcount) {
		this.itemcount = itemcount;
	}

	
	
	
	public String getMimetype() {
		return mimetype;
	}
	@XmlElement(name = "mime-type")
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public long getLastmodified() {
		return lastmodified;
	}

	@XmlElement(name = "last-modified")
	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	
	public String getPermissions() {
		return permissions;
	}
	public void setPermissions(String permissions) {
		this.permissions = permissions;
	}

	
	

}
