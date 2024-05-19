package com.optculture.shared.entities.communication.email;

import java.util.Date;

import com.optculture.shared.entities.communication.CommunicationSent;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bounces")
@Data
public class Bounces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bounce_id")
    private java.lang.Long bounceId;

    @Column(name = "category")
    private String category;

    @Column(name = "message", length = 2000)
    private String message;
//changed for bouncedDate as 2.0 also uses Date for easiness
    @Column(name = "date")
    private Date bouncedDate;

    @Column(name = "cr_id")
    private long crId;

    @Column(name = "status_code", length = 10)
    private String statusCode;
// changed to communicationSent instead of campaignSent.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_id")
    private com.optculture.shared.entities.communication.CommunicationSent sentId;
//required constructor while processing delivery reports.
    
       public Bounces(CommunicationSent sentId,String category,String reason,
							Date bouncedDate,Long crId){
			this.sentId = sentId;
			this.category = category;
			this.message= reason;
			this.bouncedDate = bouncedDate;
			this.crId = crId;
       }
     
}
