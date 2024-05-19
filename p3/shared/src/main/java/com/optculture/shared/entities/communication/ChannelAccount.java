package com.optculture.shared.entities.communication;

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
@Table(name = "channel_account")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChannelAccount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private java.lang.Long id;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_setting_id")
	private com.optculture.shared.entities.communication.ChannelSetting channelSettings;

	@Column(name = "api_key", length = 100)
	private String apiKey;

	@Column(name = "account_name", length = 120)
	private String accountName;

	@Column(name = "account_pwd", length = 20)
	private String accountPwd;

	@Column(name = "country_name", length = 20)
	private String countryName;

	@Column(name = "account_type", length = 10)
	private String accountType;

	@Column(name = "status", length = 20)
	private String status;

}
