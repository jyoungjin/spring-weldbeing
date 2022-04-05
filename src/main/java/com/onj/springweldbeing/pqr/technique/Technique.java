package com.onj.springweldbeing.pqr.technique;

import lombok.Data;

@Data
public class Technique {
    private int id;
    private int pqrInfoId;
    private String process;
    private String stringerOrWeaveBead;
    private String oscillation;
    private String multiPassOrSinglePass;
    private String closedToOutChamber;
    private String useOfThermalProcesses;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
