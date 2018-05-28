package com.kiki.http;

import com.kiki.utils.UrlUtils;

public class HttpGet extends org.apache.http.client.methods.HttpGet {
    public HttpGet(String url) {
        super(UrlUtils.encodeUrlParams(url));
    }
}
