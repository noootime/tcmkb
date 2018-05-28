package com.kiki.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UrlUtils {

    private static Logger logger = LoggerFactory.getLogger(UrlUtils.class);

    /**
     * 编码URL参数
     */
    public static String encodeUrlParams(String url) {
        if (url == null) {
            return null;
        }
        try {
            if (url.contains("?")) {
                String host = url.substring(0, url.indexOf("?"));
                String paramStr = url.substring(url.indexOf("?") + 1, url.length());
                if (paramStr.isEmpty()) {
                    return url.substring(0, url.length() - 1);
                }
                Map<String, String> map = new HashMap<>();
                if (paramStr.contains("&")) {
                    String[] params = paramStr.split("&");
                    for (String param : params) {
                        String[] kv = param.split("=");
                        if (kv.length >= 2) {
                            map.put(kv[0].trim(), URLEncoder.encode(kv[1].trim(), "UTF-8"));
                        }
                    }
                } else {
                    String[] kv = paramStr.split("=");
                    if (kv.length >= 2) {
                        map.put(kv[0].trim(), URLEncoder.encode(kv[1].trim(), "UTF-8"));
                    }
                }
                StringBuilder sb = new StringBuilder("?");
                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                    sb.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
                }
                url = host + sb.substring(0, sb.length() - 1);
                logger.info(url);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

}
