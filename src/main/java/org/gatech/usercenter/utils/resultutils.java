package org.gatech.usercenter.utils;

import ch.qos.logback.core.spi.ErrorCodes;
import org.gatech.usercenter.common.errorCode;
import org.gatech.usercenter.common.generalRespon;

public class resultutils {

    public static <T> generalRespon<T> success(T data){
        return new generalRespon<T>(0,data,"success","");
    }

    public static generalRespon error(errorCode errorCode){
        return new generalRespon(errorCode);
    }

    public static generalRespon error(int code,String msg,String description){
        return new generalRespon(code,msg,description);
    }
}
