package com.onj.springweldbeing.pqr.preheat;

import org.springframework.stereotype.Repository;

@Repository
public interface PreheatService {

    int insertPQRPreheat(Preheat preheat);

}
