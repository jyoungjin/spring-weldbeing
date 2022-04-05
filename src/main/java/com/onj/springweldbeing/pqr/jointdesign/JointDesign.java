package com.onj.springweldbeing.pqr.jointdesign;

import lombok.Data;

@Data
public class JointDesign {

    private int id;
    private int pqrInfoId;
    private Double rootFaceMin;
    private Double rootFaceMax;
    private Double rootOpeningMin;
    private Double rootOpeningMax;
    private Double grooveAngleMin;
    private Double grooveAngleMax;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;

}

