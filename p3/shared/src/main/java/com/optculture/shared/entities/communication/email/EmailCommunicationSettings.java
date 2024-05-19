package com.optculture.shared.entities.communication.email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "email_communication_settings")
public class EmailCommunicationSettings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private java.lang.Long settingId;
	/*
	 * stores display from org level (org, stores) include before text field (org
	 * name or org unit) google analytics by default campaign name or able to enter
	 * name web version json {text,limk text, alignment} permission remainder on/off
	 * text editing include before text filed, (orgname,org unit )
	 */

	/*
	 * @user/homestore/ store level
	 */
	@Column(name = "address_type_flag")
	private String addressTypeFlag;

	/*
	 * @stored value of User ,address taken --> from user table stored value of Home
	 * store, merge tag of store -- > ${homeStore.addressStr} store value of the
	 * store address. --> Store|124
	 */
	@Column(name = "address_type_value")
	private String addressTypeValue;
	/*
	 * @will provide a text block and store the value here. to show
	 */
	@Column(name = "include_before_address")
	private String includeBeforeAddress;

	/*
	 * @enable google analytics flag, will append communication name(source) and
	 * medium to the click links.
	 */

	@Column(name = "enable_analytics")
	private Boolean enableAnalytics;
	/*
	 * @to enables the web page view.
	 * 
	 */
	@Column(name = "web_link_flag")
	private Boolean webLinkFlag;
	/*
	 * web link showing text
	 */
	@Column(name = "web_link_text")
	private String webLinkText;

	/*
	 * web link url text
	 */
	@Column(name = "web_link_url_text")
	private String webLinkURLText;

	/*
	 * alignment flag is used for both permission reminder & web page Text
	 */
	@Column(name = "alignment")
	private String alignment;

	/*
	 * permission reminder flag
	 */
	@Column(name = "permission_reminder_flag")
	private Boolean permissionReminderFlag;

	/*
	 * permission reminder text
	 */
	@Column(name = "premission_reminder_text")
	private String premissionReminderText;

	@Column(name = "personalized_to_flag")
	private Boolean personalizedToFlag;

	@Column(name = "personalized_to_value")
	private String personalizedToValue;

	/*
	 * userId
	 */
	@Column(name = "user_id")
	private java.lang.Long userId;

}
