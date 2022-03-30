package com.onj.springweldbeing.pqr;

import com.onj.springweldbeing.pqr.basemetal.BaseMetal;
import com.onj.springweldbeing.pqr.electricalcharacteristic.ElectricalCharacteristic;
import com.onj.springweldbeing.pqr.fillermetal.FillerMetal;
import com.onj.springweldbeing.pqr.gas.Gas;
import com.onj.springweldbeing.pqr.position.Position;
import com.onj.springweldbeing.pqr.postweldheattreatment.PostWeldHeatTreatment;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfo;
import com.onj.springweldbeing.pqr.preheat.Preheat;
import com.onj.springweldbeing.pqr.technique.Technique;
import com.onj.springweldbeing.pqr.weldingparameter.WeldingParameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PQR {

    private PQRInfo pqrInfo;
    private List<WeldingParameter> weldingParameters = new ArrayList<>();
    private List<BaseMetal> baseMetals = new ArrayList<>();
    private List<FillerMetal> fillerMetals = new ArrayList<>();
    private List<Position> positions = new ArrayList<>();
    private List<Preheat> preheats = new ArrayList<>();
    private List<PostWeldHeatTreatment> postWeldHeatTreatments = new ArrayList<>();
    private List<Gas> gases = new ArrayList<>();
    private List<ElectricalCharacteristic> electricalCharacteristics = new ArrayList<>();
    private List<Technique> techniques = new ArrayList<>();

    private List<String> errors = new ArrayList<>();

}
