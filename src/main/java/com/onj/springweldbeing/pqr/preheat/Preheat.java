package com.onj.springweldbeing.pqr.preheat;

import lombok.Data;

@Data
public class Preheat {
    private int id;
    private int pqrInfoId;
    private Double preheatTempMin;
    private Double preheatTempMax;
    private Double interpassTempMin;
    private Double interpassTempMax;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
