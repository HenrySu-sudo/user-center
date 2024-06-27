package org.gatech.usercenter.exception;

import lombok.Data;
import org.gatech.usercenter.common.errorCode;

@Data
public class businessException extends RuntimeException{

    private int code;

    private String description;

    public businessException(errorCode errorCode) {
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
        this.description=errorCode.getDescription();
    }

    public businessException(errorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
        this.description=description;
    }
}
