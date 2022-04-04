package com.onj.springweldbeing.pqr.gas;

import org.springframework.stereotype.Repository;

@Repository
public interface GasService {

    int insertPQRGas(Gas gas);

}
