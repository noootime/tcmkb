package com.kiki.http;

public class LoginUser {

    /**
     * 爬取数据专用，没有积分
     */
    public static final LoginUser ACCOUNT_FOR_DATA = new LoginUser("tcmkb1", "123456");

    /**
     * 更新爬取源专用，有积分
     */
    public static final LoginUser ACCOUNT_FOR_ORIGIN = new LoginUser("tcmkb2", "123456");

    private String username;
    private String password;

    @Override
    public String toString() {
        return "LoginUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
