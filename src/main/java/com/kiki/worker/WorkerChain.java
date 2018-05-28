package com.kiki.worker;

import java.util.ArrayList;
import java.util.List;

public class WorkerChain {
    private List<AbstractWorker> workerList = new ArrayList<>();

    public WorkerChain register(AbstractWorker worker) {
        workerList.add(worker);
        return this;
    }

    public void doChain() {
        for (AbstractWorker worker : workerList) {
            worker.work();
        }
    }

}
