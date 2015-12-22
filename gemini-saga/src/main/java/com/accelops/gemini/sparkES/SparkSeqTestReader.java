package com.accelops.gemini.sparkES;

import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.util.List;

/**
 * Created by kai.zhang on 12/21/2015.
 */
public class SparkSeqTestReader {
    public static void main(final String[] args) {

        if(args.length != 1) {
            System.out.println("Usage: SparkSeqTestReader <filePath>");
            System.exit(-1);
        }

        //String filePath = args[0];

        SparkConf sparkConf = new SparkConf().setAppName("TestReader");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaPairRDD<LongWritable, BytesWritable> timeEventRDD = sc.sequenceFile(args[0], LongWritable.class, BytesWritable.class);
        List<String> bytesWritables = timeEventRDD.values().map(new Function<BytesWritable, String>() {
            public String call(BytesWritable bytesWritable) {
                return bytesWritable.toString();
            }
        }).take(10);

        for(String bw : bytesWritables) {
            System.out.println(bw);
        }


    }
}
