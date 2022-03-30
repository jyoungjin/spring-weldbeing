package com.onj.springweldbeing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Component
@ConfigurationProperties(prefix = "map")
public class KeyData {
    public Map<String, Set<String>> pqrKey = new HashMap<>();

    public List<String> folderNames;
}
