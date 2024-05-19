package com.optculture.shared.entities.communication;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "communication_sent")
public class CommunicationSent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sent_id")
    private java.lang.Long sentId;


    @Column(name = "recipient", length = 80)
    private String recipient;

    @Column(name = "cr_id")
    private Long communicationReportId;

    @Column(name = "opens")
    private Integer opens;

    @Column(name = "clicks")
    private Integer clicks;

    @Column(name = "contact_id")
    private java.lang.Long contactId;


    @Column(name = "channel_type")
    private String channelType;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "api_msg_id", length = 100)
    private String apiMsgId;

    @Column(name = "contactPhValStr",columnDefinition="MEDIUMTEXT")
    private String contactPhValStr;
   // created for sent object creation 
    public CommunicationSent(String recipient, 
			Long crId,Integer opens,Integer clicks,Long contactId,String channelType,
			String status,String requestId,String apiMsgId,String contactPhValueStr) {
	   this.recipient= recipient; 
		this.communicationReportId = crId;
		this.opens=opens;
		this.clicks = clicks;
		this.contactId = contactId;
		this.channelType = channelType;
		this.status = status;
		this.requestId=requestId;
		this.apiMsgId = apiMsgId;
		this.contactPhValStr= contactPhValueStr;
   }
	

}
