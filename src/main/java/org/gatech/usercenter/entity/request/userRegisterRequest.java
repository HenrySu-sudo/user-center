package org.gatech.usercenter.entity.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class userRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private String userName;

    private String userPassword;

    private String checkedPassword;
}
