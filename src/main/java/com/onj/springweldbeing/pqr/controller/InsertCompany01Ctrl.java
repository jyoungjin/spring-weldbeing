package com.onj.springweldbeing.pqr.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onj.springweldbeing.config.KeyData;
import com.onj.springweldbeing.pqr.PQR;
import com.onj.springweldbeing.pqr.basemetal.BaseMetal;
import com.onj.springweldbeing.pqr.basemetal.BaseMetalService;
import com.onj.springweldbeing.pqr.fillermetal.FillerMetal;
import com.onj.springweldbeing.pqr.fillermetal.FillerMetalService;
import com.onj.springweldbeing.pqr.position.Position;
import com.onj.springweldbeing.pqr.position.PositionService;
import com.onj.springweldbeing.pqr.postweldheattreatment.PostWeldHeatTreatment;
import com.onj.springweldbeing.pqr.postweldheattreatment.PostWeldHeatTreatmentService;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfo;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfoSevice;
import com.onj.springweldbeing.pqr.preheat.Preheat;
import com.onj.springweldbeing.pqr.preheat.PreheatService;
import com.onj.springweldbeing.pqr.weldingparameter.WeldingParameter;
import com.onj.springweldbeing.pqr.weldingparameter.WeldingParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;

@RestController
@RequestMapping("/insert")
@RequiredArgsConstructor
public class InsertCompany01Ctrl {
    private final KeyData keyData;

    @Autowired
    PQRInfoSevice pqrInfoSevice;
    @Autowired
    WeldingParameterService weldingParameterService;
    @Autowired
    BaseMetalService baseMetalService;
    @Autowired
    FillerMetalService fillerMetalService;
    @Autowired
    PositionService positionService;
    @Autowired
    PreheatService preheatService;
    @Autowired
    PostWeldHeatTreatmentService postWeldHeatTreatmentService;

    @GetMapping("/company01")
    @Transactional
    public ArrayList<PQR> getJsonFiles() throws IOException, JSONException, IllegalAccessException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String pattern =  "json/01. Spraying Systems/*.json";

        ArrayList<PQR> pqrList = new ArrayList<>();

        Resource[] resources = null;

        try {
            resources = resourcePatternResolver.getResources(pattern);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Resource resource : resources) {
            File file = resource.getFile();
            String jsonString = new String(Files.readAllBytes(file.toPath()));
            JSONObject pqrJson = new JSONObject(jsonString);

            PQR pqr = new PQR();

            // pqrInfo 섹션 전처리
            for (String key : keyData.pqrKey.get("pqrInfo")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setPqrInfo(makePQRInfo(pqrJson.getJSONObject(key), pqr));
                    break;
                }
            }

            // weldingProcess 개수 파악
            ArrayList<String> weldingProcesses = new ArrayList<>();
            if (pqr.getPqrInfo().getWeldingProcess1() != null) {
                weldingProcesses.add(pqr.getPqrInfo().getWeldingProcess1());
            }
            if(pqr.getPqrInfo().getWeldingProcess2() != null){
                weldingProcesses.add(pqr.getPqrInfo().getWeldingProcess2());
            }
            if(pqr.getPqrInfo().getWeldingProcess3() != null){
                weldingProcesses.add(pqr.getPqrInfo().getWeldingProcess3());
            }

            // weldingParameters 섹션 전처리
            for (String key : keyData.pqrKey.get("weldingParameters")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setWeldingParameters(makeWeldingParameters(pqrJson.getJSONArray(key), pqr));
                    break;
                }
            }

            // baseMetals 섹션 전처리
            for (String key : keyData.pqrKey.get("baseMetals")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setBaseMetals(makeBaseMetals(pqrJson.getJSONObject(key), pqr));
                    break;
                }
            }

            // fillerMetals 섹션 전처리
            for (String key : keyData.pqrKey.get("fillerMetals")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setFillerMetals(makeFillerMetals(pqrJson.getJSONObject(key), weldingProcesses, pqr));
                    break;
                }
            }

            // positions 섹션 전처리
            for (String key : keyData.pqrKey.get("positions")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setPositions(makePositions(pqrJson.getJSONObject(key), weldingProcesses, pqr));
                    break;
                }
            }

            // preheat 섹션 전처리
            for (String key : keyData.pqrKey.get("preheats")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setPreheats(makePreheats(pqrJson.getJSONObject(key), weldingProcesses, pqr));
                    break;
                }
            }

            // postWeldHeatTreatments 섹션 전처리
            for (String key : keyData.pqrKey.get("postWeldHeatTreatments")) {
                if (!pqrJson.isNull(key)) {
                    pqr.setPostWeldHeatTreatments(makePostWeldHeatTreatments(pqrJson.getJSONObject(key), weldingProcesses, pqr));
                    break;
                }
            }

            pqrList.add(pqr);

            insertPQR(pqr);

        }
        return pqrList;
    }


    // pqrInfo data 전처리
    private PQRInfo makePQRInfo(JSONObject pqrInfoJson, PQR pqr) throws JSONException, IllegalAccessException {
        PQRInfo pqrInfo = new PQRInfo();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
//        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(pqrInfoJson.getString("date").trim(), formatter);
        String[] types = pqrInfoJson.getString("type").split("\\+");

        pqrInfo.setPqrDate(date);
        pqrInfo.setWeldingProcess1(pqrInfoJson.getString("welding_process1"));
        if(!pqrInfoJson.getString("welding_process2").isEmpty()){
            pqrInfo.setWeldingProcess2(pqrInfoJson.getString("welding_process2"));
        }
        if(!pqrInfoJson.getString("welding_process3").isEmpty()){
            pqrInfo.setWeldingProcess3(pqrInfoJson.getString("welding_process3"));
        }

        for (int i = 0; i < types.length; i++) {
            if (i == 0) {
                pqrInfo.setWeldingType1(types[0]);
            }else if(i==1){
                pqrInfo.setWeldingType2(types[1]);
            }
            else if(i==2){
                pqrInfo.setWeldingType3(types[2]);
            }
        }

        pqrInfoJson.remove("date");
        pqrInfoJson.remove("welding_process1");
        pqrInfoJson.remove("welding_process2");
        pqrInfoJson.remove("welding_process3");
        pqrInfoJson.remove("type");

        for (String key : keyData.pqrKey.getOrDefault("pqrNo", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setPqrNo(pqrInfoJson.getString(key));
                pqrInfoJson.remove(key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("wpsNo", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setWpsNo(pqrInfoJson.getString(key));
                pqrInfoJson.remove(key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("company", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setCompany(pqrInfoJson.getString(key));
                pqrInfoJson.remove(key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("originalFileName", new HashSet<>())) {
            if (!pqrInfoJson.isNull(key)) {
                pqrInfo.setOriginFileName(pqrInfoJson.getString(key));
                pqrInfoJson.remove(key);
            }
        }

        pqrInfo.setOther(pqrInfoJson.toString());

        return pqrInfo;
    }


    // weldingParameter data 전처리
    private ArrayList<WeldingParameter> makeWeldingParameters(JSONArray weldingParametersJson, PQR pqr) throws JSONException, IllegalAccessException {
        ArrayList<WeldingParameter> weldingParameters = new ArrayList<>();

        for (int i = 0; i < weldingParametersJson.length(); i++) {
            JSONObject weldingParameterJson = weldingParametersJson.getJSONObject(i);
            WeldingParameter weldingParameter = new WeldingParameter();

            if (weldingParameterJson.has("action")) {
                weldingParameter.setAction(weldingParameterJson.getString("action"));
            }else{
                for (String key : keyData.pqrKey.getOrDefault("process", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        weldingParameter.setProcess(weldingParameterJson.getString(key));
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("beadNo", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        weldingParameter.setBeadNo(weldingParameterJson.getString(key));
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("electrode", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        weldingParameter.setElectrode(weldingParameterJson.getString(key));
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("electrodeSize", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String electrodeSize = weldingParameterJson.getString(key).replaceAll("ø","");
                        if(electrodeSize.contains(",")){
                            electrodeSize = electrodeSize.split(",")[0];
                        }
                        weldingParameter.setElectrodeSize(Double.valueOf(electrodeSize));
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("amps", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String amps = weldingParameterJson.getString(key);
                        Double ampMin;
                        Double ampMax;
                        if(amps.contains("~")){
                            String[] ampsArr = amps.split("~");
                            ampMin = Double.valueOf(ampsArr[0]);
                            ampMax = Double.valueOf(ampsArr[1]);
                        }else{
                            ampMin = Double.valueOf(amps);
                            ampMax = Double.valueOf(amps);
                        }
                        weldingParameter.setAmpMin(ampMin);
                        weldingParameter.setAmpMax(ampMax);
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("volt", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String amps = weldingParameterJson.getString(key);
                        Double voltMin;
                        Double voltMax;
                        if(amps.contains("~")){
                            String[] ampsArr = amps.split("~");
                            voltMin = Double.valueOf(ampsArr[0]);
                            voltMax = Double.valueOf(ampsArr[1]);
                        }else{
                            voltMin = Double.valueOf(amps);
                            voltMax = Double.valueOf(amps);
                        }
                        weldingParameter.setVoltMin(voltMin);
                        weldingParameter.setVoltMax(voltMax);
                        weldingParameterJson.remove(key);
                    }
                }

                for (String key : keyData.pqrKey.getOrDefault("speed", new HashSet<>())) {
                    if (!weldingParameterJson.isNull(key)) {
                        String speed = weldingParameterJson.getString(key);
                        Double speedMin;
                        Double speedMax;
                        if(!speed.trim().isEmpty()){

                            if(speed.contains("~")){
                                String[] speedArr = speed.split("~");
                                speedMin = Double.valueOf(speedArr[0]);
                                speedMax = Double.valueOf(speedArr[1]);
                            }else{
                                speedMin = Double.valueOf(speed);
                                speedMax = Double.valueOf(speed);
                            }

                            if(key.toLowerCase().contains("cm/min")){
                                speedMin = cmminTommmin(speedMin);
                                speedMax = cmminTommmin(speedMax);
                            }else if(key.toLowerCase().contains("im/min")){
                                speedMin = inminTommmin(speedMin);
                                speedMax = inminTommmin(speedMax);
                            }

                            weldingParameter.setSpeedMin(speedMin);
                            weldingParameter.setSpeedMax(speedMax);
                        }
                        weldingParameterJson.remove(key);
                    }
                }

                for(String key : keyData.pqrKey.getOrDefault("heatInput", new HashSet<>())){
                    if (!weldingParameterJson.isNull(key)) {
                        String heatInput = weldingParameterJson.getString(key);
                        Double heatInputMin;
                        Double heatInputMax;
                        if(!heatInput.trim().isEmpty()){
                            if(heatInput.contains("~")){
                                String[] heatInputArr = heatInput.split("~");
                                heatInputMin = Double.valueOf(heatInputArr[0]);
                                heatInputMax = Double.valueOf(heatInputArr[1]);
                            }else{
                                heatInputMin = Double.valueOf(heatInput);
                                heatInputMax = Double.valueOf(heatInput);
                            }

                            if(key.toLowerCase().contains("kj/cm")){
                                heatInputMin = kjcmTokjmm(heatInputMin);
                                heatInputMax = kjcmTokjmm(heatInputMax);
                            }
                            weldingParameter.setHeatInputMin(heatInputMin);
                            weldingParameter.setHeatInputMax(heatInputMax);
                        }
                        weldingParameterJson.remove(key);
                    }
                }
                weldingParameter.setOther(weldingParameterJson.toString());

            }

            weldingParameters.add(weldingParameter);
        }

        return weldingParameters;
    }


    // base metal 전처리
    private ArrayList<BaseMetal> makeBaseMetals(JSONObject baseMetalsJson, PQR pqr) throws JSONException, IllegalAccessException {
        ArrayList<BaseMetal> baseMetals = new ArrayList<>();

        BaseMetal baseMetal1 = new BaseMetal();
        BaseMetal baseMetal2 = new BaseMetal();

        for (String key : keyData.pqrKey.getOrDefault("pNo", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setPNo(baseMetalsJson.getString(key));
                baseMetal2.setPNo(baseMetalsJson.getString("to_"+key));
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("grNo", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setGrNo(baseMetalsJson.getString(key));
                baseMetal2.setGrNo(baseMetalsJson.getString("to_"+key));
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("typeAndGrade", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setTypeAndGrade(baseMetalsJson.getString(key));
                baseMetal2.setTypeAndGrade(baseMetalsJson.getString("to_"+key));
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("thickness", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setThickness(thicknessChange(baseMetalsJson.getString(key)));
                baseMetal2.setThickness(thicknessChange(baseMetalsJson.getString("to_"+key)));
                baseMetalsJson.remove(key);
                baseMetalsJson.remove("to_" + key);
            }
        }

        for (String key : keyData.pqrKey.getOrDefault("diameter", new HashSet<>())) {
            if (!baseMetalsJson.isNull(key)) {
                baseMetal1.setDiameter(thicknessChange(baseMetalsJson.getString(key)));
                baseMetal2.setDiameter(thicknessChange(baseMetalsJson.getString(key)));
                baseMetalsJson.remove(key);
            }
        }

        baseMetal1.setOther(baseMetalsJson.toString());
        baseMetal2.setOther(baseMetalsJson.toString());

        baseMetals.add(baseMetal1);
        baseMetals.add(baseMetal2);

        return baseMetals;
    }

    // filler metal 전처리
    private ArrayList<FillerMetal> makeFillerMetals(JSONObject fillerMetalsJson, ArrayList<String> weldingProcesses, PQR pqr) throws JSONException, IllegalAccessException {
        ArrayList<FillerMetal> fillerMetals = new ArrayList<>();

        for (String process : weldingProcesses) {

            FillerMetal fillerMetal = new FillerMetal();
            fillerMetal.setProcess(process);

            for (String key : keyData.pqrKey.getOrDefault("sfaNo", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setSfaNo(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setSfaNo(fillerMetalsJson.getString(key));
                    }
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("awsClass", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setAwsClass(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setAwsClass(fillerMetalsJson.getString(key));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("fNo", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setFNo(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setFNo(fillerMetalsJson.getString(key));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("aNo", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setANo(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setANo(fillerMetalsJson.getString(key));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("fillerProductForm", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setFillerProductForm(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setFillerProductForm(fillerMetalsJson.getString(key));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("sizeOfElectrode", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setSizeOfElectrode1(thicknessChange(fillerMetalsJson.getJSONObject(key).getString(process)));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setSizeOfElectrode1(thicknessChange(fillerMetalsJson.getString(key)));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("depositWeldMetalThickness", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setDepositWeldMetalThickness(thicknessChange(fillerMetalsJson.getJSONObject(key).getString(process)));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setDepositWeldMetalThickness(thicknessChange(fillerMetalsJson.getString(key)));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("wireFluxClass", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setWireFluxClass(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setWireFluxClass(fillerMetalsJson.getString(key));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("supplemental", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setSupplemental(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setSupplemental(fillerMetalsJson.getString(key));
                    }
                }
            }
            for (String key : keyData.pqrKey.getOrDefault("alloyElements", new HashSet<>())) {
                if (!fillerMetalsJson.isNull(key)) {
                    try {
                        fillerMetalsJson.getJSONObject(key);
                        fillerMetal.setAlloyElements(fillerMetalsJson.getJSONObject(key).getString(process));
                        fillerMetalsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        fillerMetal.setAlloyElements(fillerMetalsJson.getString(key));
                    }
                }
            }
            fillerMetal.setOther(fillerMetalsJson.toString());
            fillerMetals.add(fillerMetal);
        }

        return fillerMetals;
    }

    // position 전처리
    private ArrayList<Position> makePositions(JSONObject positionsJson, ArrayList<String> weldingProcesses, PQR pqr) throws JSONException, IllegalAccessException {
        ArrayList<Position> positions = new ArrayList<>();

        for (String process : weldingProcesses) {

            Position position = new Position();

            for (String key : keyData.pqrKey.getOrDefault("positionOfGroove", new HashSet<>())) {
                if (!positionsJson.isNull(key)) {
                    try {
                        positionsJson.getJSONObject(key);
                        position.setPositionOfGroove(positionsJson.getJSONObject(key).getString(process));
                        positionsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        position.setPositionOfGroove(positionsJson.getString(key));
                    }
                }
            }

            for (String key : keyData.pqrKey.getOrDefault("progression", new HashSet<>())) {
                if (!positionsJson.isNull(key)) {
                    try {
                        positionsJson.getJSONObject(key);
                        position.setProgression(positionsJson.getJSONObject(key).getString(process));
                        positionsJson.getJSONObject(key).remove(process);
                    } catch (JSONException e) {
                        position.setProgression(positionsJson.getString(key));
                    }
                }
            }

            position.setOther(positionsJson.toString());
            positions.add(position);

        }
        return positions;
    }

    // preheat 전처리
    private ArrayList<Preheat> makePreheats(JSONObject preheatJson, ArrayList<String> weldingProcesses, PQR pqr) throws JSONException, IllegalAccessException {
        ArrayList<Preheat> preheats = new ArrayList<>();

        Preheat preheat = new Preheat();

        for (String key : keyData.pqrKey.getOrDefault("preheatTemp", new HashSet<>())) {
            if (!preheatJson.isNull(key)) {
                String str = preheatJson.getString(key).trim().replaceAll("°C","");
                if (isNone(str)) {
                    preheatJson.remove(key);
                    break;
                }
                double preheatTempMin;
                double preheatTempMax;
                if (str.contains("~")) {
                    preheatTempMin = Double.parseDouble(str.split("~")[0]);
                    preheatTempMax = Double.parseDouble(str.split("~")[1]);
                }else{
                    preheatTempMin = Double.parseDouble(str);
                    preheatTempMax = Double.parseDouble(str);
                }

                preheat.setPreheatTempMin(preheatTempMin);
                preheat.setPreheatTempMax(preheatTempMax);

                preheatJson.remove(key);
            }

        }

        for (String key : keyData.pqrKey.getOrDefault("interPassTemp", new HashSet<>())) {
            if (!preheatJson.isNull(key)) {
                String str = preheatJson.getString(key).trim().replaceAll("°C","");
                if (isNone(str)) {
                    preheatJson.remove(key);
                    break;
                }
                double interpassTempMin;
                double interpassTempMax;
                if (str.contains("~")) {
                    interpassTempMin = Double.parseDouble(str.split("~")[0]);
                    interpassTempMax = Double.parseDouble(str.split("~")[1]);
                }else{
                    interpassTempMin = Double.parseDouble(str);
                    interpassTempMax = Double.parseDouble(str);
                }
                preheat.setInterpassTempMin(interpassTempMin);
                preheat.setInterpassTempMax(interpassTempMax);

                preheatJson.remove(key);
            }
        }

        preheat.setOther(preheatJson.toString());
        preheats.add(preheat);

        return preheats;
    }

    // postWeldHeatTreatment 전처리
    private ArrayList<PostWeldHeatTreatment> makePostWeldHeatTreatments(JSONObject postWeldHeatTreatmentJson, ArrayList<String> weldingProcesses, PQR pqr) throws JSONException, IllegalAccessException {
        ArrayList<PostWeldHeatTreatment> postWeldHeatTreatments = new ArrayList<>();

        PostWeldHeatTreatment postWeldHeatTreatment = new PostWeldHeatTreatment();

        for (String key : keyData.pqrKey.getOrDefault("temperature", new HashSet<>())) {
            if (!postWeldHeatTreatmentJson.isNull(key)) {
                String str = postWeldHeatTreatmentJson.getString(key).trim().replaceAll("°C","");
                if (isNone(str)) {
                    postWeldHeatTreatmentJson.remove(key);
                    break;
                }
                double temperatureMin;
                double temperatureMax;
                if (str.contains("~")) {
                    temperatureMin = Double.parseDouble(str.split("~")[0]);
                    temperatureMax = Double.parseDouble(str.split("~")[1]);
                }else{
                    temperatureMin = Double.parseDouble(str);
                    temperatureMax = Double.parseDouble(str);
                }

                postWeldHeatTreatment.setTemperatureMin(temperatureMin);
                postWeldHeatTreatment.setTemperatureMax(temperatureMax);

                postWeldHeatTreatmentJson.remove(key);
            }

        }

        for (String key : keyData.pqrKey.getOrDefault("holdingTime", new HashSet<>())) {

            if (!postWeldHeatTreatmentJson.isNull(key)) {

                postWeldHeatTreatment.setHoldingTime(postWeldHeatTreatmentJson.getString(key).trim());
                postWeldHeatTreatmentJson.remove(key);
            }
        }

        postWeldHeatTreatment.setOther(postWeldHeatTreatmentJson.toString());
        postWeldHeatTreatments.add(postWeldHeatTreatment);

        return postWeldHeatTreatments;
    }

    // PQR Info 데이터가 이미 존재 하는 Data 인지 확인
    private boolean isExistPQRInfoData(PQRInfo pqrInfo){
        boolean isExist = false;

        if(pqrInfoSevice.isExistPQR(pqrInfo)){
           isExist = true;
        };

        return isExist;
    }

    private Double cmminTommmin(Double x){
        return x*10;
    }

    private Double inminTommmin(Double x){
        return x*25.4;
    }

    private Double kjcmTokjmm(Double x){
        return x*10;
    }

    private Double thicknessChange(String str){

        str = str.trim().toLowerCase().replaceAll("ø","");

        if (str.equals("") || str.equals("-") || str.equals("n/a") || str.equals("none")) {
            return null;
        }

        if (str.contains("mm")) {
            return Double.parseDouble(str.replaceAll("mm",""));
        } else if (str.contains("cm")) {
            return Double.parseDouble(str.replaceAll("cm","")) * 10;
        } else if (str.contains("in")) {
            return Double.parseDouble(str.replaceAll("in","")) * 25.4;
        } else {
            return Double.valueOf(str);
        }

    }

    private boolean isNone(String str){
        str = str.toLowerCase();
        if (str.equals("") || str.equals("-") || str.equals("n/a") || str.equals("none")) {
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void insertPQR(PQR pqr) throws RuntimeException {

        try {
            // 이미 입력된 PQR 데이터인지 확인
            if (!isExistPQRInfoData(pqr.getPqrInfo())) {
                pqrInfoSevice.insertPQRInfo(pqr.getPqrInfo());
                System.out.print("return key: " + pqr.getPqrInfo().getId()+", ");
            } else {
                pqr.getPqrInfo().setId(pqrInfoSevice.getPqrInfo(pqr.getPqrInfo()).getId());
            }

            for (WeldingParameter weldingParameter : pqr.getWeldingParameters()) {
                 weldingParameter.setPqrInfoId(pqr.getPqrInfo().getId());
                 weldingParameterService.insertPQRWeldingParameter(weldingParameter);
            }

            for (BaseMetal baseMetal : pqr.getBaseMetals()) {
                baseMetal.setPqrInfoId(pqr.getPqrInfo().getId());
                baseMetalService.insertPQRBaseMetal(baseMetal);
            }

            for (FillerMetal fillerMetal : pqr.getFillerMetals()) {
                fillerMetal.setPqrInfoId(pqr.getPqrInfo().getId());
                fillerMetalService.insertPQRFillerMetal(fillerMetal);
            }

            for (Position position : pqr.getPositions()) {
                position.setPqrInfoId(pqr.getPqrInfo().getId());
                positionService.insertPQRPosition(position);
            }

            for (Preheat preheat : pqr.getPreheats()) {
                preheat.setPqrInfoId(pqr.getPqrInfo().getId());
                preheatService.insertPQRPreheat(preheat);
            }

            for (PostWeldHeatTreatment postWeldHeatTreatment : pqr.getPostWeldHeatTreatments()) {
                postWeldHeatTreatment.setPqrInfoId(pqr.getPqrInfo().getId());
                postWeldHeatTreatmentService.insertPQRPostWeldHeatTreatment(postWeldHeatTreatment);
            }

        } catch (Exception e) {
            System.out.println("PQR Insert 오류 - PQR No: " + pqr.getPqrInfo().getPqrNo());
            throw new RuntimeException(e);
        }

    }

}
