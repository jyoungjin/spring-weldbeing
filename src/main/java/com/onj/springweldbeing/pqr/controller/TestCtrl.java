package com.onj.springweldbeing.pqr.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onj.springweldbeing.config.KeyData;
import com.onj.springweldbeing.pqr.PQR;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfo;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfoSevice;
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
public class TestCtrl {
    private final KeyData keyData;

    @Autowired
    PQRInfoSevice pqrInfoSevice;

    @Autowired
    WeldingParameterService weldingParameterService;


//    @GetMapping("/getPqrInfoList")
//    public List<PQRInfo> getPqrInfoList(){
//        return pqrInfoSevice.getPqrInfos();
//    }

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


    // PQR Info 데이터가 이미 존재 하는 Data 인지 확인
    private boolean isExistPQRInfoData(PQRInfo pqrInfo){
        boolean isExist = false;

        if(pqrInfoSevice.isExistPQR(pqrInfo)){
           isExist = true;
        };

        return isExist;
    }

    // PQR WeldingParameter 데이터가 이미 존재 하는 Data 인지 확인
    private boolean isExistPQRWeldingParameterData(WeldingParameter weldingParameter){
        boolean isExist = false;

        if(weldingParameterService.isExistPQRWeldingParameter(weldingParameter)){
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


    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void insertPQR(PQR pqr) throws RuntimeException {

        try {
            if (!isExistPQRInfoData(pqr.getPqrInfo())) {
                pqrInfoSevice.insertPQRInfo(pqr.getPqrInfo());
                System.out.println("return key: " + pqr.getPqrInfo().getId());
            } else {
                System.out.println("이미 존재하는 PQR Info Data 입니다!");
                pqr.getPqrInfo().setId(pqrInfoSevice.getPqrInfo(pqr.getPqrInfo()).getId());
            }

            for (WeldingParameter weldingParameter : pqr.getWeldingParameters()) {
//                 weldingParameter.setPqrInfoId(pqr.getPqrInfo().getId());
                if (!isExistPQRWeldingParameterData(weldingParameter)) {
                    weldingParameterService.insertPQRWeldingParameter(weldingParameter);
                } else {
                    System.out.println("이미 존재하는 WeldingParameter Data 입니다!");
                }
            }
        } catch (Exception e) {
            System.out.println("PQR Insert 오류 - PQR No: " + pqr.getPqrInfo().getPqrNo());
            throw new RuntimeException(e);
        }

    }

}
