package com.optculture.app.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {
    String userName;
    String companyName;
    public UserDto(String userName,String companyName){
        this.companyName=companyName;
        this.userName=userName;
    }
}
