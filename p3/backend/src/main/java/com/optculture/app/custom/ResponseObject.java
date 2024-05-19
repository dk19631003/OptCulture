package com.optculture.app.custom;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseObject<T> {
    Long totalItems=0L;
    Long totalPages=0L;
    T object;
}
