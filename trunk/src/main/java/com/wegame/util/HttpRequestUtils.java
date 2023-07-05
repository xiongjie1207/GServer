package com.wegame.util;

import com.wegame.framework.core.GameCons;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class HttpRequestUtils {
    public static <R> R request(int module,int pid, String url, HashMap<String, Object> param,
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
        HttpEntity<String> formEntity = new HttpEntity<>(JsonUtils.toJson(param), headers);
        RestTemplate restTemplate = new RestTemplate();
        R result = restTemplate.postForEntity(url, formEntity, clazz).getBody();
        return result;
    }

}
