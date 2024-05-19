package org.mq.optculture.controller;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mq.optculture.business.common.BaseService;
import org.mq.optculture.model.BaseResponseObject;
import org.mq.optculture.model.OCMediaRequestObject;
import org.mq.optculture.model.UrlShortCodeResponseObject;
import org.mq.optculture.utils.OCConstants;
import org.mq.optculture.utils.ServiceLocator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

public class UrlShortCodeService extends AbstractController {

	
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response)  {
		try {
			
			OCMediaRequestObject ocMediaRequestObject = new OCMediaRequestObject();
			ocMediaRequestObject.setAction(OCConstants.REQUEST_PARAM_ACTION_SHORTCODE);
			ocMediaRequestObject.setShortCode(request.getParameter(OCConstants.REQUEST_PARAM_URLSHORTCODE));
			
			BaseService baseService = ServiceLocator.getInstance().getServiceByName(OCConstants.OCMEDIA_SERVICE);
			
			BaseResponseObject baseResponseObject = baseService.processRequest(ocMediaRequestObject);
			UrlShortCodeResponseObject responseObj = (UrlShortCodeResponseObject)baseResponseObject;
			
			if(responseObj.getResponseObject() != null)	{
				PrintWriter pout = response.getWriter();
				
				try {
					pout.println(responseObj.getResponseObject().toString());
				} catch (Exception e) {
					logger.error("Exception sms url short code service", e);
				}finally{
					
					pout.close();
				}
			
			}
			else if(responseObj.getRedirectTo() != null ) {
				
				response.sendRedirect(responseObj.getRedirectTo());
				
			}else if(responseObj.getBitMatrix() != null) {
				
				BitMatrix bitMatrix = responseObj.getBitMatrix();
				String format = responseObj.getImageFormat() ;
				format = format== null ? OCConstants.IMAGE_FORMAT_PNG : format;
				
				response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
				response.setHeader("Pragma", "no-cache"); // HTTP 1.0
				response.setDateHeader("Expires", 0); // prevents caching at the
														// proxy server
				response.setContentType("image/"+format);
				
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				MatrixToImageWriter.writeToStream(bitMatrix, format, baos);
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			
				BufferedOutputStream bout = new BufferedOutputStream(response.getOutputStream());
			
				try {
					byte b[] = new byte[8];
					int count;
					while((count=bais.read(b)) != -1) {
						bout.write(b,0,count);
					}
				
					
				} catch (Exception e) {
					logger.error("Exception in SMS barcode generation", e);
				}finally {
					bout.flush();
					bout.close();
					bais.close();
				}

				
			}
			
		} catch (Exception e) {
			logger.error("Exception in url short code service", e);
		}
		return null;
	}
	
}
