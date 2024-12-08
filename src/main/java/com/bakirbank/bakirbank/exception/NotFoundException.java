package com.bakirbank.bakirbank.exception;

import lombok.Getter;

public class NotFoundException extends Exception{

    @Getter
    private String message;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message){
        super();
        this.message = message;
    }
}
