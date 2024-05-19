package com.optculture.app.services;

import com.optculture.app.repositories.UrlShortCodeMappingRepository;
import com.optculture.shared.entities.communication.UrlShortCodeMapping;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UrlShortCodeMappingService {
    @Autowired
    UrlShortCodeMappingRepository urlShortCodeMapRepository;
    @Value("${APPLICATIONSHORTURL}")
    private String APPLICATION_SHORT_URL;
    Logger logger = LoggerFactory.getLogger(UrlShortCodeMappingService.class);
    public ResponseEntity getURLShortCodes(List<String> urls, Long userId) {
        List<String> generatedShortCodes= new ArrayList<>();
        for(String enteredURL:urls){
            if(!(enteredURL.startsWith("http://")||enteredURL.startsWith( "https://"))) {
                enteredURL = "http://"+enteredURL;
            }
            String shortCode= getShortCodeByUrl(enteredURL,userId);
            if(shortCode==null) shortCode="";
            logger.info("url :"+enteredURL+"  =>  shortCode :"+shortCode);
            generatedShortCodes.add(APPLICATION_SHORT_URL+shortCode);
        }
        return new ResponseEntity<>(generatedShortCodes, HttpStatus.OK);
    }

    private String getShortCodeByUrl(String enteredURL, Long userId) {

        Optional<UrlShortCodeMapping> existedshortCode=urlShortCodeMapRepository.findByUrlContentAndUserId(enteredURL,userId);
        if(existedshortCode.isPresent()){
            logger.info("shortCode already computed ,returning existed code");
            return existedshortCode.get().getShortCode();

        }
        else{
            String finalshortCode=null;
            List<String> shortCodes=getFourDigitShortCodes(enteredURL+userId.toString());
            UrlShortCodeMapping urlShortCodeMapping= new UrlShortCodeMapping();
            for(String shortCode:shortCodes){
                try{
                    String urlShortCode = shortCode;
                    urlShortCodeMapping.setShortCode(urlShortCode);
                    urlShortCodeMapping.setUrlContent(enteredURL);
                    urlShortCodeMapping.setUrlType("");
                    urlShortCodeMapping.setUserId(userId);
                    urlShortCodeMapRepository.save(urlShortCodeMapping);
                    finalshortCode=urlShortCode;
                    break;
                }
                catch (ConstraintViolationException e){
                    logger.info("Exception while saving urlShortCode (shortcode already exists)");
                    continue;
                }
                catch (Exception e){
                    logger.info("Exception while saving urlShortCode ");
                    continue;
                }
            }
            return  finalshortCode;
        }
    }

    private List<String> getFourDigitShortCodes(String enteredURL) {
        List<String> fourDigitShortCodes=new ArrayList<>();

        char symbols[] =
            {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '-', '.', '_', '~', '(', ')', '\'', '!', '*', ':', '@', ',', ';'
    };
        String Md5result = MD5Algo(enteredURL);
        if(Md5result == null) {
            logger.debug(" :: generated  MD5 algo is null");
            return null;
        }
        logger.debug("MD5 result string is ===>"+Md5result);
        int lenthofInt = Md5result.length();
        int subHexLenInt = lenthofInt / 8;
        int maxLength=8;

        for(int i=0; i < subHexLenInt; i++) {
            String subHexStr = Md5result.substring(i*8, (i*8)+maxLength);

            long hexaValue = 0x3FFFFFFF & Long.parseLong(subHexStr, 16);

            StringBuffer outSb = new StringBuffer();

            for (int j = 0; j < 4; j++) { // for 4 digit
                long val = 0x0000001F & hexaValue;
                outSb.append(symbols[(int)val]);
                hexaValue = hexaValue >>> 5;
            } //inner for
    //	    logger.debug("== "+outSb);
            if(outSb !=  null)
                fourDigitShortCodes.add(outSb.toString());
        } // outer for
        return fourDigitShortCodes;
    }
    private String MD5Algo(String inputUrl) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(inputUrl.getBytes());

//			 logger.debug("md5 lengtyh ==="+messageDigest.length);
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {

                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (Exception e) {
            logger.error("Exception ::error occured while generating the MD5 algoitham",e);
            return null;
        }
    } // MD5Algo
}
