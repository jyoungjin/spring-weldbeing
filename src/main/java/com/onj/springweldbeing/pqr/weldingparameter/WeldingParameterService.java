package com.onj.springweldbeing.pqr.weldingparameter;

import com.onj.springweldbeing.pqr.pqrinfo.PQRInfo;
import org.springframework.stereotype.Repository;

@Repository
public interface WeldingParameterService {

    int insertPQRWeldingParameter(WeldingParameter weldingParameter);

}
