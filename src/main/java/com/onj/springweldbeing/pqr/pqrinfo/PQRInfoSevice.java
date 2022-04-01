package com.onj.springweldbeing.pqr.pqrinfo;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PQRInfoSevice {

    PQRInfo getPqrInfo(PQRInfo pqrInfo);

    int insertPQRInfo(PQRInfo pqrInfo);

    boolean isExistPQR(PQRInfo pqrInfo);

}
