package com.onj.springweldbeing.pqr.weldingparameter;

import lombok.Data;

@Data
public class WeldingParameter {
    int id;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
