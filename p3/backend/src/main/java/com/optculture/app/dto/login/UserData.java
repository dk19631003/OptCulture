package com.optculture.app.dto.login;

import com.optculture.app.dto.config.PosMappingDto;
import com.optculture.shared.entities.org.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserData {

    private String userName;
    private String emailId;
    private String companyName;
    private String currencyInfo;
    private String completeAddress;
    private String role;
    private List<PosMappingDto> posMappingList;
    private List<String> valueCodes;


    public String getAddress(User user) {
        StringBuilder address=new StringBuilder();
        if(user.getAddressOne()!=null) {
            address.append(user.getAddressOne());
//            address.append(", ");
        }
        if(user.getAddressTwo()!=null) {
            address.append(", ");
            address.append(user.getAddressTwo());

        }
        if(user.getCity()!=null){
            address.append(", ");
            address.append(user.getCity());

        }
        if(user.getState()!=null){
            address.append(", ");
            address.append(user.getState());

        }
        if(user.getCountry()!=null){
            address.append(", ");
            address.append(user.getCountry());
        }
        if(user.getPinCode()!=null){
            address.append(", ");
            address.append(user.getPinCode());
        }
        if(user.getPhone()!=null) {
            address.append(", ");
            address.append(user.getPhone());
        }
        return address.toString();
    }

}
