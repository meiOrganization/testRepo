package com.accelops.gemini.sparkES;

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
    Map<Integer, IngestionJob> jobStatus = new TreeMap<>();
    ReadWriteLock lock = new ReentrantReadWriteLock();
    Thread thread = new Thread(new IngestionJobExecutor());

    public boolean initialize() {
        thread.start();
        return true;
    }

    public void addJob(String request, ByteBuffer resultBuffer) {

        if(request == null) {
            resultBuffer.clear();
            resultBuffer.putInt(StatusCode.UNKNOWN_REQUEST.getCode());
            return;
        }

        try {
            addNewJob(new IngestionJob(IngestionRequestParser.buildFromXml(request)));
        } catch (IngestionRequestParsingException e) {
            log.error("Failed to parse query: " + e.getMessage() + ", request: " + request);
            resultBuffer.clear();
            resultBuffer.putInt(StatusCode.REQUEST_XML_SYNTAX_ERROR.getCode());
        } catch (Exception e) {
            log.error("Failed to parse query: " + e.getMessage() + ", request: " + request);
            resultBuffer.clear();
            resultBuffer.putInt(StatusCode.INTERNAL_ERR.getCode());
        }

    }

    public void addNewJob(IngestionJob job) {

        lock.writeLock().lock();
        jobQueue.add(job);
        jobStatus.put(job.getRequestId(), job);
        lock.writeLock().unlock();

    }

    public IngestionJob getJob() {

        IngestionJob job;
        lock.writeLock().lock();
        job = jobQueue.poll();
        lock.writeLock().unlock();

        return job;

    }

    public void getStatus(String request, ByteBuffer resultBuffer) {

        int requestId = Integer.parseInt(request);
        lock.readLock().lock();
        jobStatus.get(requestId).getStatus(resultBuffer);
        lock.readLock().unlock();

    }

}
