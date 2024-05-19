package com.optculture.app.dto.referral;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
public class UserDetailsDto {
    @JsonProperty
    private String USERNAME;
    @JsonProperty
    private String ORGID;
    @JsonProperty
    private String TOKEN;
}
