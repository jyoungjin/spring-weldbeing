package com.onj.springweldbeing.pqr.fillermetal;

import lombok.Data;

@Data
public class FillerMetal {
    private int id;
    private int pqrInfoId;
    private String process;
    private String sfaNo;
    private String awsClass;
    private String fNo;
    private String aNo;
    private String fillerProductForm;
    private Double sizeOfElectrode1;
    private Double sizeOfElectrode2;
    private Double sizeOfElectrode3;
    private Double depositWeldMetalThickness;
    private String wireFluxClass;
    private String fluxTradeName;
    private String brandName;
    private String supplemental;
    private String alloyElements;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
