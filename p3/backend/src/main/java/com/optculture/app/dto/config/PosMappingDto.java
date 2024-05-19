package com.optculture.app.dto.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PosMappingDto {
    String custFieldName;
    String displayLabel;
    String dataType;
}
