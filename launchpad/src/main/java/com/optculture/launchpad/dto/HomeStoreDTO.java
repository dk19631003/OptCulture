package com.optculture.launchpad.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeStoreDTO {
	
	private String addressStr;
	private String phone;
	private String emailId;
	private String street;
	private String city;
	private String state;
	private String zip;
	
	
	
	/*
	 * <html><body>Hello, fullName =${contact.firstName ! 'N/A'} ${contact.lastName ! 'N/A'}
Email id = ${contact.emailId !'example@example.com'}
🤩 🥳 😏 😒
my date ${.now?date}
&#34;this is text to check entity 	&#34;
&quot; this is to demostrate quotes &quot;
symbol and other characters : Ω Α ©
&amp; & symbol
hexa decimal code : &#128512
Phone = ${contact.mobilePhone !'N/A'}\n"  "Street = ${contact.addressOne ! 'Local'}
AddressTwo = ${contact.addressTwo ! 'N/A'}\n"  "City =${contact.city ! 'N/A'}
State =${contact.state ! 'N/A'}
My home address ${homeStore.storeAddress}
My home phone ${homeStore.storePhone}
My Home street ${homeStore.storeStreet}
My Home City ${homeStore.storeCity}
My Home state ${homeStore.storeState}
My Home Zip ${homeStore.storeZip}
Country =${contact.country ! 'N/A'}=?UTF-8?Q?=E2=98=82?=</body></html>

	 */
	
		
}
