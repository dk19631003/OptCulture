package com.optculture.shared.entities.communication;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "channel_setting")
@Data
public class ChannelSetting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private java.lang.Long id;

	@Column(name = "gateway_name", length = 120)
	private String gatewayName;

	@Column(name = "channel_type")
	private String channelType;

	@Column(name = "end_point")
	private String endPoint;
	
	@Column(name = "port")
	private String port;
	


}
