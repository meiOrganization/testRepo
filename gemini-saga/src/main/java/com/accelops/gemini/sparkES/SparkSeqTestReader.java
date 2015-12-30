package com.accelops.gemini.sparkES;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;

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
        sparkConf.set("es.index.auto.create", "true");
        sparkConf.set("es.nodes", "10.0.0.138:9200,10.0.0.139:9200");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);
        JavaPairRDD<LongWritable, BytesWritable> timeEventRDD = sc.sequenceFile(args[0], LongWritable.class, BytesWritable.class);
        JavaRDD<String> stringRDD = timeEventRDD.values().map(new Function<BytesWritable, String>() {
            public String call(BytesWritable bytesWritable) {
                return new String(bytesWritable.getBytes(), 0, bytesWritable.getLength());
            }
        });

        JavaEsSpark.saveJsonToEs(stringRDD, "accelops-2015.12.22");


    }
}
