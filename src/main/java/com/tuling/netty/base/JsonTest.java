package com.tuling.netty.base;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wbw
 * @date 2022/2/14 9:31
 */
public class JsonTest {
    public static void main(String[] args) {
        JsonTest jsonTest = new JsonTest();
        jsonTest.setRadarConfig("192.168.8.10", "7000","192.168.1.1","192.168.8.44","1000");
    }
    public void setRadarConfig(String staticIp,String staticPort,String staticGateway, String descIp,String descPort){
        //step1:拼接目标需要的字符串,后序可能问题：就是需要添加指定的Sn去修改静态IP
        Map<String, Object> result = new HashMap<>(16);
        result.put("url", "http://"+descIp+":"+descPort);
        result.put("staticIp",staticIp);
        result.put("staticGateway", staticGateway);
        JSONObject jsonResult = new JSONObject(result);
        System.out.println(jsonResult.toJSONString());
    }
}
