package com.onj.springweldbeing.pqr.basemetal;

import com.onj.springweldbeing.pqr.weldingparameter.WeldingParameter;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseMetalService {

    int insertPQRBaseMetal(BaseMetal baseMetal);
    boolean isExistPQRBaseMetal(BaseMetal baseMetal);

}
