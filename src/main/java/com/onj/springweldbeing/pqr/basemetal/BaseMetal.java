package com.onj.springweldbeing.pqr.basemetal;

import lombok.Data;

@Data
public class BaseMetal {
    private int id;
    private int pqrInfoId;
    private String materialSpec;
    private String typeAndGrade;
    private String pNo;
    private String grNo;
    private Double thickness;
    private Double diameter;
    private Double depositWeldMetalThickness;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
