package com.sx.kakou.models;

import java.util.Map;

/**
 * Created by mglory on 2015/9/7.
 */
public class CxfxInfo {
    private  Map<String,String> hpys;
    private  Map<String,String> hpzl;
    private  Map<String,String> csys;
    private  Map<String,String> cllx;
    private  Map<String,String> fxbh;
    private  Map<String,String> place;
    private  Map<String,String> ppdm;

    public Map<String, String> getHpys() {
        return hpys;
    }

    public void setHpys(Map<String, String> hpys) {
        this.hpys = hpys;
    }

    public Map<String, String> getHpzl() {
        return hpzl;
    }

    public void setHpzl(Map<String, String> hpzl) {
        this.hpzl = hpzl;
    }

    public Map<String, String> getCsys() {
        return csys;
    }

    public void setCsys(Map<String, String> csys) {
        this.csys = csys;
    }

    public Map<String, String> getFxbh() {
        return fxbh;
    }

    public void setFxbh(Map<String, String> fxbh) {
        this.fxbh = fxbh;
    }

    public Map<String, String> getCllx() {
        return cllx;
    }

    public void setCllx(Map<String, String> cllx) {
        this.cllx = cllx;
    }

    public Map<String, String> getPlace() {
        return place;
    }

    public void setPlace(Map<String, String> place) {
        this.place = place;
    }

    public Map<String, String> getPpdm() {
        return ppdm;
    }

    public void setPpdm(Map<String, String> ppdm) {
        this.ppdm = ppdm;
    }
}
