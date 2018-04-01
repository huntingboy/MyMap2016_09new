package com.nomad.unity;

/**
 * Created by nomad on 17-2-23.
 */
public class User {
    private String Uname;
    private String Upassword;
    //private String Ufriend;

    public User(String uname, String upassword) {
        Uname = uname;
        Upassword = upassword;
    }

    public String getUname() {
        return Uname;
    }

    public String getUpassword() {
        return Upassword;
    }

    public void setUname(String uname) {
        Uname = uname;
    }

    public void setUpassword(String upassword) {
        Upassword = upassword;
    }
}
