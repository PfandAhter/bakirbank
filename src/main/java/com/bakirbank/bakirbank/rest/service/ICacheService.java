package com.bakirbank.bakirbank.rest.service;

import com.bakirbank.bakirbank.model.ErrorCodes;

import java.util.HashMap;

public interface ICacheService {
    void getErrorCodes();

    HashMap<String, ErrorCodes> getErrorCodesList();
}
