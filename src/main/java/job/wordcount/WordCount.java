package job.wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import utils.Const;

public class WordCount extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        Configuration configuration = new Configuration(); // 读取 hadoop 配置信息

        // 如果输出目录存在，则先删除输出目录
        Path outputPath = new Path(Const.WORD_COUNT_OUTPUT_PATH);
        FileSystem fs = outputPath.getFileSystem(configuration);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(configuration, "word count"); // 创建 MR job
        job.setJarByClass(WordCount.class); // 设置启动类, 如果要打包到集群上运行，必须添加该设置
        job.setMapperClass(TokenizerMapper.class); // 设置 Mapper 类
        job.setReducerClass(IntSumReducer.class); // 设置 Reducer 类
        job.setOutputKeyClass(Text.class); // 设置输出 key 的类型
        job.setOutputValueClass(IntWritable.class); // 设置输出值的类型

        FileInputFormat.addInputPath(job, new Path(Const.TRAIN_DATA_INPUT_PATH)); // 设置输入文件目录
        FileOutputFormat.setOutputPath(job, new Path(Const.WORD_COUNT_OUTPUT_PATH)); // 设置输出文件目录

//        FileInputFormat.addInputPath(job, new Path("file:////home/reptile/桌面/Bayes-MR/NBCorpus/Country/AUSTR")); // 设置输入文件目录
//        FileOutputFormat.setOutputPath(job, new Path("file:///home/reptile/桌面/Bayes-MR/out")); // 设置输出文件目录

//        FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/NBCorpus/Country/AUSTR")); // 设置输入文件目录
//        FileOutputFormat.setOutputPath(job, new Path("hdfs://master:90000/out")); // 设置输出文件目录

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception{
        //提交run方法之后，得到一个程序的退出状态码
        int run = ToolRunner.run(new Configuration(), new WordCount(), args);
        //根据程序的退出状态码，退出整个进程
        System.exit(run);
    }
}
