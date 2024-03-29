package com.wegame.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wegame.framework.core.GameCons;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class HttpRequestUtils {
    @SneakyThrows
    public static <R> R request(int module, int pid, String url, HashMap<String, Object> param,
                                Class<R> clazz) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add(GameCons.MODULE, String.valueOf(module));
        headers.add(GameCons.PID, String.valueOf(pid));
        if (param == null) {
            param = new HashMap<>();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        HttpEntity<String> formEntity = new HttpEntity<>(objectMapper.writeValueAsString(param), headers);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForEntity(url, formEntity, clazz).getBody();
    }

}
