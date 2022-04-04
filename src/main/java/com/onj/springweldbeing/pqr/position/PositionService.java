package com.onj.springweldbeing.pqr.position;

import org.springframework.stereotype.Repository;

@Repository
public interface PositionService {

    int insertPQRPosition(Position position);

}
