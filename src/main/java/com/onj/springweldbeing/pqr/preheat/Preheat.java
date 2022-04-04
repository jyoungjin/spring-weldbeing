package com.onj.springweldbeing.pqr.preheat;

import lombok.Data;

@Data
public class Preheat {
    private int id;
    private int pqrInfoId;
    private double preheatTempMin;
    private double preheatTempMax;
    private double interpassTempMin;
    private double interpassTempMax;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
