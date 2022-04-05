package com.onj.springweldbeing.pqr.electricalcharacteristic;

import lombok.Data;

@Data
public class ElectricalCharacteristic {
    private int id;
    private int pqrInfoId;
    private String process;
    private String current;
    private String polarity;
    private String transferMode;
    private String tungstenElectrodeType;
    private Double tungstenElectrodeSize1;
    private Double tungstenElectrodeSize2;
    private Double tungstenElectrodeSize3;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
