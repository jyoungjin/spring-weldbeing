package com.onj.springweldbeing.pqr.pqrinfo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PQRInfo {
    private int id;
    private String company;
    private String pqrNo;
    private LocalDate pqrDate;
    private String wpsNo;
    private int wpsRevNo;
    private String weldingProcess1;
    private String weldingProcess2;
    private String weldingProcess3;
    private String weldingType1;
    private String weldingType2;
    private String weldingType3;
    private String originFileName;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
