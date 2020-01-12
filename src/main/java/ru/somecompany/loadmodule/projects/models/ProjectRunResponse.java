package ru.somecompany.loadmodule.projects.models;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ProjectRunResponse {

    private String name;
    private Long channelId;
    private Map<String, String> attributes = new HashMap<>();

    public void addAttribute(String key, String value){
        attributes.put(key, value);

    }

}


