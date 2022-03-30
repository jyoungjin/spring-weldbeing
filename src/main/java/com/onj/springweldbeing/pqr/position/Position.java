package com.onj.springweldbeing.pqr.position;

import lombok.Data;

@Data
public class Position {
    private int id;
    private int pqrInfoId;
    private String process;
    private String positionOfGroove;
    private String progression;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
