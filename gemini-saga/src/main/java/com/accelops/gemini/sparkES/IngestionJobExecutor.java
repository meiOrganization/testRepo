package com.accelops.gemini.sparkES;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;

/**
 * Created by kai.zhang on 1/26/2016.
 */
public class IngestionJobExecutor implements Runnable {

    IngestionJobManager jobManager = null;
    boolean needExit = false;

    IngestionJobExecutor(IngestionJobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Override
    public void run() {

        while(true) {

            if(needExit)
                break;

            IngestionJob job = jobManager.getJob();
            if(job == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                continue;
            }

            execute(job);

        }

    }

    private void execute(IngestionJob job) {

        if(job == null)
            return;

        SparkAppHandle handle;

        try {

            handle = new SparkLauncher()
                    .setAppResource("/home/admin/gemini-saga-1.0.jar")
                    .setMainClass("com.accelops.gemini.sparkES.SparkSeqTestReader")
                    .setMaster("spark://ip-10-0-0-248:7077")
                    .setConf(SparkLauncher.DRIVER_MEMORY, "1g")
                    .addAppArgs("")
                    .startApplication();

            while(!handle.getState().isFinal()) {
                System.out.println("Job running: "  + handle.getState().name());
                Thread.sleep(3000);
            }

            System.out.println("Job finished: " + handle.getState().name());

            Thread.sleep(10000);

            System.out.println("Exit");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
