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
    private Double electrodeSize;
    private Double ampMin;
    private Double ampMax;
    private Double voltMin;
    private Double voltMax;
    private Double speedMin;
    private Double speedMax;
    private Double heatInputMin;
    private Double heatInputMax;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
