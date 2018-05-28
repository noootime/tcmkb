package com.kiki.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WorkerChain {
    private static Logger logger = LoggerFactory.getLogger(WorkerChain.class);

    private List<AbstractWorker> workerList = new LinkedList<>();

    private int pos = 0;

    public WorkerChain register(AbstractWorker worker) {
        if (worker != null) {
            workerList.add(worker);
        }
        return this;
    }

    public void doFilter() {
        if (pos >= 0 && pos < workerList.size()) {
            workerList.get(pos++).work(this);
        }
    }

}
