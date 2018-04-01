package com.nomad.unity;

import java.util.Date;

/**
 * Created by nomad on 17-2-23.
 */
public class Info {
    private Date Udate;
    private double Ulatitude;
    private double Ulongitude;
    private double Uradius;
    private String Uaddress;

    public Info(Date udate, double ulatitude, double ulongitude, double uradius, String uaddress) {
        Udate = udate;
        Ulatitude = ulatitude;
        Ulongitude = ulongitude;
        Uradius = uradius;
        Uaddress = uaddress;
    }

    public double getUradius() {
        return Uradius;
    }

    public void setUradius(double uradius) {
        Uradius = uradius;
    }

    public Date getUdate() {
        return Udate;
    }

    public double getUlatitude() {
        return Ulatitude;
    }

    public double getUlongitude() {
        return Ulongitude;
    }

    public String getUaddress() {
        return Uaddress;
    }

    public void setUaddress(String uaddress) {
        Uaddress = uaddress;
    }

    public void setUdate(Date udate) {
        Udate = udate;
    }

    public void setUlatitude(double ulatitude) {
        Ulatitude = ulatitude;
    }

    public void setUlongitude(double ulongitude) {
        Ulongitude = ulongitude;
    }
}
