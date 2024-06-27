package org.gatech.usercenter.common;

import lombok.Data;

import java.io.Serializable;

@Data

public class generalRespon<T> implements Serializable {
    private int code;

    private T data;

    private String message;

    private String description;

    public generalRespon(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public generalRespon(errorCode errorCode){
        this.code=errorCode.getCode();
        this.message=errorCode.getMessage();
        this.description=errorCode.getDescription();
    }

    public generalRespon(int code, String message, String description) {
        this.code = code;
        this.data = null;
        this.message = message;
        this.description = description;
    }

}
