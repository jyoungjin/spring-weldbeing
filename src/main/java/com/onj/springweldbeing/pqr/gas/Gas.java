package com.onj.springweldbeing.pqr.gas;

import lombok.Data;

@Data
public class Gas {
    private int id;
    private int pqrInfoId;
    private String process;
    private String shieldingGas;
    private String shieldingGasComposition;
    private Double shieldingGasFlowMin;
    private Double shieldingGasFlowMax;
    private String backingGas;
    private String backingGasComposition;
    private Double backingGasFlowMin;
    private Double backingGasFlowMax;
    private String trailingGas;
    private String trailingGasComposition;
    private Double trailingGasFlowMin;
    private Double trailingGasFlowMax;
    private String other;
    private String createTime;
    private int createUser;
    private String modifyTime;
    private int modifyUser;
}
