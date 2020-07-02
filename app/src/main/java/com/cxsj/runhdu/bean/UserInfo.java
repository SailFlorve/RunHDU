package com.cxsj.runhdu.bean;

import org.litepal.crud.DataSupport;

public class UserInfo extends DataSupport {
    private String userName;
    private String passwordMD5;

    public UserInfo(String userName, String passwordMD5) {
        this.userName = userName;
        this.passwordMD5 = passwordMD5;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordMD5() {
        return passwordMD5;
    }

    public void setPasswordMD5(String passwordMD5) {
        this.passwordMD5 = passwordMD5;
    }
}
