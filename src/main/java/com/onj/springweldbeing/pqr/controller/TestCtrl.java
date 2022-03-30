package com.onj.springweldbeing.pqr.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.onj.springweldbeing.config.KeyData;
import com.onj.springweldbeing.pqr.PQR;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfo;
import com.onj.springweldbeing.pqr.pqrinfo.PQRInfoSevice;
import com.onj.springweldbeing.pqr.weldingparameter.WeldingParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestCtrl {
    private final KeyData keyData;

    @Autowired
    PQRInfoSevice pqrInfoSevice;

    @GetMapping("/getPqrInfoList")
    public List<PQRInfo> getPqrInfoList(){
        return pqrInfoSevice.getPqrInfos();
    }

    @GetMapping("/getJsonFiles")
    public void getJsonFiles() throws IOException, JSONException, IllegalAccessException {

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        String pattern =  "json/01. Spraying Systems/*.json";

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

            // DATA insert
//            if(!isExistData(pqr.getPqrInfo())){
//                pqrInfoSevice.insertPQRInfo(pqr.getPqrInfo());
//                System.out.println("return key: "+pqr.getPqrInfo().getId());
//            }else{
//                System.out.println("이미 존재하는 Data입니다!");
//            }

        }
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
    

        }

        return weldingParameters;
    }


    // PQR 데이터가 이미 존재 하는 Data 인지 확인
    private boolean isExistData(PQRInfo pqrInfo){
        boolean isExist = false;

        if(pqrInfoSevice.isExistPQR(pqrInfo)){
           isExist = true;
        };

        return isExist;
    }


}
