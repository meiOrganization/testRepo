package com.accelops.gemini.sparkES;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;

/**
 * Created by kai.zhang on 1/19/2016.
 */
public class SparkReaderLauncher {

    public static void main(final String[] args) {

        if(args.length != 1) {
            System.out.println("Usage: SparkSeqTestReader <filePath>");
            System.exit(-1);
        }

        SparkAppHandle handle;
        try {
            handle = new SparkLauncher()
                    .setAppResource("/home/admin/gemini-saga-1.0.jar")
                    .setMainClass("com.accelops.gemini.sparkES.SparkSeqTestReader")
                    .setMaster("spark://ec2-52-33-244-102.us-west-2.compute.amazonaws.com:7077")
                    .setConf(SparkLauncher.DRIVER_MEMORY, "8g")
                    .setConf(SparkLauncher.EXECUTOR_EXTRA_JAVA_OPTIONS, args[0])
                    .startApplication();

            while(!handle.getState().isFinal()) {
                System.out.println("Job running: "  + handle.getState().name());
                Thread.sleep(10);
            }

            System.out.println("Job finished: " + handle.getState().name());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
