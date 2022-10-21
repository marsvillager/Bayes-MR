package com.hadoop.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.FileOutputStream;

public class WordCount {
    public static void main(String[] args) throws Exception{
        Configuration configuration = new Configuration(); // 读取 hadoop 配置信息
//        String[] otherArgs = new GenericOptionsParser(configuration, args).getRemainingArgs();
//        if (otherArgs.length != 2) {
//            System.err.println("Usage: word count<int><out>");
//            System.exit(2);
//        }

        Job job = Job.getInstance(configuration, "word count"); // 创建 MR job
        job.setJarByClass(WordCount.class); // 设置启动类, 如果要打包到集群上运行，必须添加该设置
        job.setMapperClass(TokenizerMapper.class); // 设置 Mapper 类
        job.setReducerClass(IntSumReducer.class); // 设置 Reducer 类
        job.setOutputKeyClass(Text.class); // 设置输出 key 的类型
        job.setOutputValueClass(Text.class); // 设置输出值的类型

//        FileInputFormat.addInputPath(job, new Path(otherArgs[0])); // 设置输入文件目录
//        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1])); // 设置输出文件目录
        FileInputFormat.addInputPath(job, new Path("file:///D:\\Bayes-MR\\NBCorpus\\NBCorpus\\Country\\ABDBI")); // 设置输入文件目录
        FileOutputFormat.setOutputPath(job, new Path("file:///D:\\Bayes-MR\\out")); // 设置输出文件目录

        System.exit(job.waitForCompletion(true)?0:1);
    }
}
