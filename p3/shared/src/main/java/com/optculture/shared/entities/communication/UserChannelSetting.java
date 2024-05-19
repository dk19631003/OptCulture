package com.optculture.shared.entities.communication;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_channel_setting")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserChannelSetting {
	
		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "id")
	    private java.lang.Long id;
	
		@ManyToOne(fetch=FetchType.LAZY)
	    @JoinColumn(name = "channel_account_id")
	    private ChannelAccount channelAccount;
		
		@Column(name = "user_id")
		private java.lang.Long userId;
	
	 	@Column(name = "created_date")
	    private LocalDateTime createdDate;

	    @Column(name = "modified_date")
	    private LocalDateTime modifiedDate;
	
	    @Column(name = "sender_id", length = 30)
	    private String senderId;
	    
// added to differentiate between types in userlevel
	    @Column(name = "channel_type")
	    private String channelType;

		
		

}
