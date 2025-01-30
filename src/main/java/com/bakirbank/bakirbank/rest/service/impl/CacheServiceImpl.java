package com.bakirbank.bakirbank.rest.service.impl;


import com.bakirbank.bakirbank.model.ErrorCodes;
import com.bakirbank.bakirbank.repository.ErrorCodeRepository;
import com.bakirbank.bakirbank.rest.service.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class CacheServiceImpl implements ICacheService {

    private static final HashMap<String,ErrorCodes> errorCodeList = new HashMap<>();

    private final ErrorCodeRepository errorCodeRepository;

    @Override
    public void getErrorCodes() {
        try {
            List<ErrorCodes> errorCodes = errorCodeRepository.findAll();
            if (errorCodes != null) {
                errorCodes.forEach(errorCode -> errorCodeList.put(errorCode.getId(),errorCode));
            }
        } catch (Exception e) {
            log.error("Error in getting error codes from database");
        }
    }

    @Override
    public HashMap<String,ErrorCodes> getErrorCodesList() {
        return errorCodeList;
    }

}