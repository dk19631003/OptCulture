package com.optculture.app.dto.campaign;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
@RequiredArgsConstructor
public class CampaignFilterReqDto {
    int pageNumber=0;
    int pageSize=10;
    List<String> channelTypes;
    String criteria;
    String searchValue;
}
