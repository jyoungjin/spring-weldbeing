package com.onj.springweldbeing.pqr.weldingparameter;

import lombok.Data;

@Data
public class WeldingParameter {
    private int id;
    private int pqrInfoId;
    private String process;
    private String action;
    private String beadNo;
    private String electrode;
    private double electrodeSize;
    private double ampMin;
    private double ampMax;
    private double voltMin;
    private double voltMax;
    private double speedMin;
    private double speedMax;
    private double heatInputMin;
    private double heatInputMax;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
