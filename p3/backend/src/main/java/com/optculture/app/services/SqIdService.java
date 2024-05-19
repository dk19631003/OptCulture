package com.optculture.app.services;

import com.optculture.app.util.Sqids;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SqIdService {
    Sqids sqids=Sqids.builder().build();
    public String encodeId(List<Long> idList){
        String encodedId=sqids.encode(idList);
        return  encodedId;
    }
    public  List<Long> decodeId(String id){
        List<Long> idList=sqids.decode(id);
        return idList;
    }
}

