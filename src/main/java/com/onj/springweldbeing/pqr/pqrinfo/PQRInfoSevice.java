package com.onj.springweldbeing.pqr.pqrinfo;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PQRInfoSevice {

    PQRInfo getPqrInfo(PQRInfo pqrInfo);

    ArrayList<PQRInfo> getPQRInfoList();

    int insertPQRInfo(PQRInfo pqrInfo);

    boolean isExistPQR(PQRInfo pqrInfo);

}
