package com.bakirbank.bakirbank.rest.aspect;


import com.bakirbank.bakirbank.rest.service.ICacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
@Slf4j
@RequiredArgsConstructor

public class MicroServiceStartUp {

    private final ICacheService cacheService;

    @EventListener(ApplicationReadyEvent.class)
    public void logToDataBaseServiceReady() {
        cacheService.getErrorCodes();
    }
}
