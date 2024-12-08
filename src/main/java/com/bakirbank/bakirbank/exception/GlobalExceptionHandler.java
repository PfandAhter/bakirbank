package com.bakirbank.bakirbank.exception;


import com.bakirbank.bakirbank.api.response.BaseResponse;
import com.bakirbank.bakirbank.dto.ErrorCodesDTO;
import com.bakirbank.bakirbank.lib.ResponseStatus;
import com.bakirbank.bakirbank.model.ErrorCodes;
import com.bakirbank.bakirbank.rest.service.ICacheService;
import com.bakirbank.bakirbank.rest.service.IMapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j

public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ICacheService cacheService;

    private final IMapperService mapperService;


    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseResponse> handleException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createFailResponse(e.getMessage()));
    }




    private BaseResponse createFailResponse(String exceptionMessage){
        ErrorCodesDTO errorCodesDTO = findErrorCode(exceptionMessage);
        return new BaseResponse(errorCodesDTO.getId(),errorCodesDTO.getError(),errorCodesDTO.getDescription());
    }

    private ErrorCodesDTO findErrorCode(String errorKey) {
        ErrorCodes errorCodes = cacheService.getErrorCodesList().get(errorKey);
        if (errorCodes == null) {
            errorCodes = new ErrorCodes();
            errorCodes.setId(ResponseStatus.FAILED);
            errorCodes.setError(errorKey);
            errorCodes.setDescription(errorKey);
        }
        return mapperService.map(errorCodes, ErrorCodesDTO.class);
    }
}