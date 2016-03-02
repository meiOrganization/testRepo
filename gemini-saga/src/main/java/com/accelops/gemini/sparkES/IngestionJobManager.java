package com.accelops.gemini.sparkES;

import com.accelops.libra.event.EventTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kai.zhang on 1/22/2016.
 */
public class IngestionJobManager {

    Log log  = LogFactory.getLog(getClass());

    Queue<IngestionJob> jobQueue = new LinkedList<>();
    Map<Integer, IngestionRequest> requestStatus = new TreeMap<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();
    Thread thread = new Thread(new IngestionJobExecutor(this));

    public boolean initialize() {
        thread.start();
        return true;
    }

    public IngestionJob getJob() {

        IngestionJob job;
        lock.writeLock().lock();
        job = jobQueue.poll();
        lock.writeLock().unlock();

        return job;

    }

    public void getStatus(String requestCmd, ByteBuffer resultBuffer) {

        int requestId;
        try {
            requestId = Integer.parseInt(requestCmd);
        } catch (NumberFormatException e) {
            //TODO-Kai: add failure resultBuffer
            return;
        }

        try {
            lock.readLock().lock();
            IngestionRequest request = requestStatus.get(requestId);
            if (request == null) {
                //TODO-Kai: add failure resultBuffer
                return;
            }
            request.getStatus(resultBuffer);
        } finally {
            lock.readLock().unlock();
        }

    }

    public void addRequest(String requestCmd, ByteBuffer resultBuffer) {

        if(requestCmd == null) {
            resultBuffer.clear();
            resultBuffer.putInt(StatusCode.UNKNOWN_REQUEST.getCode());
            return;
        }

        try {
            IngestionRequest request = IngestionRequestParser.buildFromXml(requestCmd);
            addRequest(request);
        } catch (IngestionRequestParsingException e) {
            log.error("Failed to parse query: " + e.getMessage() + ", request: " + requestCmd);
            resultBuffer.clear();
            resultBuffer.putInt(StatusCode.REQUEST_XML_SYNTAX_ERROR.getCode());
        } catch (Exception e) {
            log.error("Failed to parse query: " + e.getMessage() + ", request: " + requestCmd);
            resultBuffer.clear();
            resultBuffer.putInt(StatusCode.INTERNAL_ERR.getCode());
        }

    }

    private void addRequest(IngestionRequest request) {

        request.evaluateLoad();
        try {
            lock.writeLock().lock();
            requestStatus.put(request.getRequestId(), request);
            request.addJobs(jobQueue, this);
        } finally {
            lock.writeLock().unlock();
        }

    }

    public IngestionJobStatus getJobStatus(EventTable eventTable, int day) {
        return IngestionJobStatus.Unknown;
    }
}
