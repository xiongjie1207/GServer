package com.wegame.utils;

import com.wegame.core.GameCons;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class HttpRequestUtil {
    public static <R> R request(int pid, String url, HashMap<String, Object> param, Class<R> clazz) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add(GameCons.PID, String.valueOf(pid));
        if(param==null){
            param = new HashMap<>();
        }
        HttpEntity<String> formEntity = new HttpEntity<>(JsonUtil.toJson(param), headers);
        RestTemplate restTemplate = new RestTemplate();
        R result = restTemplate.postForEntity(url, formEntity, clazz).getBody();
        return result;
    }
    public static  <R> void Asyncrequest(int pid, String url, HashMap<String, Object> param, Class<R> clazz,IAsyncRequestCallback<R> callback) {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        headers.add(GameCons.PID, String.valueOf(pid));
        if(param==null){
            param = new HashMap<>();
        }
        HttpEntity<String> formEntity = new HttpEntity<>(JsonUtil.toJson(param), headers);
        AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        ListenableFuture<ResponseEntity<R>> entity = restTemplate.postForEntity(url, formEntity, clazz);
        entity.addCallback(callback, callback);
    }
}
