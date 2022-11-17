package priv.xuyi.matrix;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.matrix.utils.Const;

import java.io.IOException;
import java.util.Set;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-16 14:21
 * @description Realization of word co-occurrence matrix based on hadoop
 */
public class CoOccurrenceMatrix extends Configured implements Tool {
    static class StripesOccurrenceMapper extends Mapper<LongWritable, Text, Text, MapWritable> {
        private MapWritable occurrenceMap = new MapWritable();
        private Text word = new Text();

        /**
         *
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            int neighbors = context.getConfiguration().getInt("neighbors", 2);
            String[] tokens = value.toString().split("\\s+");
            if (tokens.length > 1) {
                for (int i = 0; i < tokens.length; i++) {
                    word.set(tokens[i]);
                    occurrenceMap.clear();

                    int start = (i - neighbors < 0) ? 0 : i - neighbors;
                    int end = (i + neighbors >= tokens.length) ? tokens.length - 1 : i + neighbors;
                    for (int j = start; j <= end; j++) {
                        if (j == i) continue;
                        Text neighbor = new Text(tokens[j]);
                        if(occurrenceMap.containsKey(neighbor)){
                            IntWritable count = (IntWritable)occurrenceMap.get(neighbor);
                            count.set(count.get()+1);
                        }else{
                            occurrenceMap.put(neighbor,new IntWritable(1));
                        }
                    }
                    context.write(word, occurrenceMap);
                }
            }
        }
    }

    static class StripesReducer extends Reducer<Text, MapWritable, Text, MapWritable> {
        private MapWritable incrementingMap = new MapWritable();

        /**
         *
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text key, Iterable<MapWritable> values, Context context) throws IOException, InterruptedException {
            incrementingMap.clear();
            for (MapWritable value : values) {
                addAll(value);
            }
            context.write(key, incrementingMap);
        }

        private void addAll(MapWritable mapWritable) {
            Set<Writable> keys = mapWritable.keySet();
            for (Writable key : keys) {
                IntWritable fromCount = (IntWritable) mapWritable.get(key);
                if (incrementingMap.containsKey(key)) {
                    IntWritable count = (IntWritable) incrementingMap.get(key);
                    count.set(count.get() + fromCount.get());
                } else {
                    incrementingMap.put(key, fromCount);
                }
            }
        }
    }

    /**
     *
     * @param strings
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] strings) throws Exception {
        Configuration configuration = new Configuration();

        // 如果输出目录存在，则先删除输出目录
        Path outputPath = new Path(Const.WORD_MATRIX_OUTPUT_PATH);
        FileSystem fs = outputPath.getFileSystem(configuration);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(configuration, "word co-occurrence matrix");

        job.setJarByClass(CoOccurrenceMatrix.class); // 设置启动类, 如果要打包到集群上运行，必须添加该设置
        job.setMapperClass(StripesOccurrenceMapper.class); // 设置 Mapper 类
        job.setCombinerClass(StripesReducer.class);
        job.setReducerClass(StripesReducer.class); // 设置 Reducer 类
        job.setOutputKeyClass(Text.class); // 设置输出 key 的类型
        job.setOutputValueClass(MapWritable.class); // 设置输出值的类型

        FileInputFormat.addInputPath(job, new Path(Const.MITRE_ATTACK_DATA)); // 设置输入文件目录
        FileOutputFormat.setOutputPath(job, new Path(Const.WORD_MATRIX_OUTPUT_PATH)); // 设置输出文件目录

//        FileInputFormat.addInputPath(job, new Path("file:////home/reptile/桌面/Bayes-MR/MITRE_ATTACK_DATA")); // 设置输入文件目录
//        FileOutputFormat.setOutputPath(job, new Path("file:///home/reptile/桌面/Bayes-MR/WORD_MATRIX_OUTPUT_PATH")); // 设置输出文件目录

//        FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/MITRE_ATTACK_DATA")); // 设置输入文件目录
//        FileOutputFormat.setOutputPath(job, new Path("hdfs://master:90000/WORD_MATRIX_OUTPUT_PATH")); // 设置输出文件目录

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new CoOccurrenceMatrix(), args);
        System.exit(run);
    }
}
