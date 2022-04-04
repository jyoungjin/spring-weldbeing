package com.onj.springweldbeing.pqr.electricalcharacteristic;

import org.springframework.stereotype.Repository;

@Repository
public interface ElectricalCharacteristicService {

    int insertPQRElectricalCharacteristic(ElectricalCharacteristic electricalCharacteristic);

}
