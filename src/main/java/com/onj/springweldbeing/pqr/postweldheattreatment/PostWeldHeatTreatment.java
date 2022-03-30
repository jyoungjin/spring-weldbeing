package com.onj.springweldbeing.pqr.postweldheattreatment;

import lombok.Data;

@Data
public class PostWeldHeatTreatment {
    private int id;
    private int pqrInfoId;
    private double temperatureMin;
    private double temperatureMax;
    private int holdingTime;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
