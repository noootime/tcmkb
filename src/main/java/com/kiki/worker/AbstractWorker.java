package com.kiki.worker;

import org.apache.http.impl.client.CloseableHttpClient;

public abstract class AbstractWorker {

    protected CloseableHttpClient client;

    /**
     * 每个worker必须有一个HttpClient才能进行工作
     */
    public AbstractWorker(CloseableHttpClient client) {
        this.client = client;
    }

    public abstract void work();
}
