package com.optculture.app.util;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;
import com.optculture.app.config.GetLoggedInUser;
import com.optculture.app.dto.campaign.template.TemplateMapper;
import com.optculture.app.repositories.CommunicationTemplateRepository;
import com.optculture.shared.entities.communication.CommunicationTemplate;
import com.optculture.shared.entities.org.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TemplateImporter {
    @Autowired
    GetLoggedInUser getLoggedInUser;
    @Autowired
    CommunicationTemplateRepository commTemplateRepository;
    @Value("${spring.datasource.url}")
    private  String connectionURL;
    @Value("${spring.datasource.username}")
    private  String username;
    @Value("${spring.datasource.password}")
    private String password;

    private  String INSERT_STATEMENT=" INSERT INTO communication_templates (channel_type,created_date,msg_content,org_id,sender_id,status,template_name,template_registered_id,user_id) VALUES (?,?,?,?,?,?,?,?,?)  ON DUPLICATE KEY UPDATE sender_id = ? ,template_registered_id = ?";
    Logger logger= LoggerFactory.getLogger(TemplateImporter.class);
    public ResponseEntity processFile(File file) {

        try {
            User user= getLoggedInUser.getLoggedInUser();
            FileReader filereader = new FileReader(file);
            Reader reader = new BufferedReader(new FileReader(file));
             Map<String, String> columnMappings=new HashMap<>();
            //All providers headers combo

            columnMappings.put("Template Name", "templateName");
            columnMappings.put( "TEMPLATE NAME", "templateName");
            columnMappings.put("TEMPLATE_NAME", "templateName");

            columnMappings.put("Content Registered","msgContent");
            columnMappings.put("TEMPLATE_CONTENT","msgContent");
            columnMappings.put("TEMPLATE MESSAGE","msgContent");
            columnMappings.put("Template Content","msgContent");

            columnMappings.put("Template ID", "templateRegisteredId");
            columnMappings.put("TEMPLATE ID", "templateRegisteredId");
            columnMappings.put("Template DLT ID","templateRegisteredId");
            columnMappings.put("TEMPLATE_ID", "templateRegisteredId");

            columnMappings.put("HEADER", "senderId");
            columnMappings.put("Header/CLI associated", "senderId");
//            columnMappings.put("Header")
            columnMappings.put("REQUESTED ON", "createdDate");
            columnMappings.put("VERIFICATION_DATE", "createdDate");
            columnMappings.put("Date of Creation", "createdDate");

            columnMappings.put("APPROVAL STATUS", "status");
            columnMappings.put("Operator Status", "status");
            columnMappings.put("VERIFICATION_STATUS", "status");
            columnMappings.put("Template Registered On Airtel (Y/N)","status"); // Y or N

            HeaderColumnNameTranslateMappingStrategy mappingStrategy =
                    new HeaderColumnNameTranslateMappingStrategy();
            mappingStrategy.setColumnMapping(columnMappings);
            mappingStrategy.setType(TemplateMapper.class);

            CsvToBean<TemplateMapper> csvReader = new CsvToBeanBuilder(reader)
                    .withType(TemplateMapper.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withMappingStrategy(mappingStrategy)
                    .build();

            List<TemplateMapper> templateMappers=csvReader.parse();
            List<CommunicationTemplate> communicationTemplates= new ArrayList<>();
            Connection connection = null;
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(
                        connectionURL,
                        username, password);

                PreparedStatement statement;
                 statement= connection.prepareStatement(INSERT_STATEMENT);

                for(TemplateMapper templateMapper:templateMappers){
                //multiple senderId for one template
                    List<String> senderIds= Arrays.asList(templateMapper.getSenderId().split(","));

                    for(String senderId:senderIds) {
                    String templateId=templateMapper.getTemplateRegisteredId(); // '100939884343734'
                        templateId=templateId.replaceAll("'","");

                    String status=templateMapper.getStatus();
                    if(status==null || status.equalsIgnoreCase("True") ||
                        status.equalsIgnoreCase("Approved") || status.equalsIgnoreCase("Y")) {

                        status = "Approved";
                    }
                    else status="Disapproved";
                        //prepare statement
                        statement.setString(1,"SMS");
                        statement.setTimestamp(2,Timestamp.valueOf(LocalDateTime.now()));
                        statement.setString(3,templateMapper.getMsgContent());
                        statement.setLong(4,user.getUserOrganization().getUserOrgId());
                        statement.setString(5,senderId);
                        statement.setString(6,status);
                        statement.setString(7,templateMapper.getTemplateName());
                        statement.setString(8,templateId);
                        statement.setLong(9,user.getUserId());
                        statement.setString(10,senderId);
                        statement.setString(11,templateId);
                        if(status.equalsIgnoreCase("Approved"))
                            statement.addBatch();
                }

                }
                int[] insertCount = statement.executeBatch();
                logger.info("insertCount :"+Arrays.toString(insertCount));
                return new ResponseEntity<>(insertCount.length==0?"No templates added .":insertCount.length+" templates added successfully !",HttpStatus.OK);
            }
            catch (ClassNotFoundException e) {
                logger.info("Exception while connecting to Db",e);
                return new ResponseEntity<>("Templates importing failed",HttpStatus.OK);
            }
        }
        catch (Exception e) {
            logger.info("Exception while extracting data from file");
            logger.info("Exception :"+e);
        }
        return new ResponseEntity<>("Templates importing failed",HttpStatus.OK);
    }

}
