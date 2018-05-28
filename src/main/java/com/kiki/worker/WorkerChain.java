package com.kiki.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class WorkerChain {
    private static Logger logger = LoggerFactory.getLogger(WorkerChain.class);

    private Queue<AbstractWorker> workerQueue = new LinkedList<>();

    public WorkerChain register(AbstractWorker worker) {
        if (worker != null) {
            workerQueue.add(worker);
        }
        return this;
    }

    public void doFilter() {
        if (!workerQueue.isEmpty()) {
            workerQueue.poll().work(this);
        }
    }

}
