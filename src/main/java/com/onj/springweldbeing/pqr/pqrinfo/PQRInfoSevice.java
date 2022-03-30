package com.onj.springweldbeing.pqr.pqrinfo;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PQRInfoSevice {

    List<PQRInfo> getPqrInfos();

    int insertPQRInfo(PQRInfo pqrInfo);

    boolean isExistPQR(PQRInfo pqrInfo);

}
