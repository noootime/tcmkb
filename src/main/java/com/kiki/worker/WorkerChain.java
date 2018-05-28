package com.kiki.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class WorkerChain {
    private static Logger logger = LoggerFactory.getLogger(WorkerChain.class);

    private List<AbstractWorker> workerList = new ArrayList<>();

    public WorkerChain register(AbstractWorker worker) {
        workerList.add(worker);
        return this;
    }

    public void doChain() {
        long start = System.currentTimeMillis();
        for (AbstractWorker worker : workerList) {
            worker.work();
        }
        long end = System.currentTimeMillis();
        double cost = (end - start) / (1000.00);
        logger.info("完成任务链共耗费:" + cost + "s.");
    }

}
