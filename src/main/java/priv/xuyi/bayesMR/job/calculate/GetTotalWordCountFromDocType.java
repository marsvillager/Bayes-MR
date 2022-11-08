package priv.xuyi.bayesMR.job.calculate;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.bayesMR.utils.Const;

import java.io.IOException;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-07 16:35
 * @description 获取每个文档种类的总单词数
 */
public class GetTotalWordCountFromDocType extends Configured implements Tool {
    static class GetTotalWordCountFromDocTypeMapper extends Mapper<Text, IntWritable, Text, IntWritable> {
        private Text docTypeName = new Text();
        // 该文档中每个单词出现的总次数
        private IntWritable wordCount = new IntWritable(0);

        /**
         *
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(Text key, IntWritable value, Context context) throws IOException, InterruptedException {
            // key: CANA@hello
            // value: 13 表示hello在CANA文档类别中出现了13次
            String docTypeName = key.toString().split("@")[0];
            this.docTypeName.set(docTypeName);
            this.wordCount.set(value.get());
            context.write(this.docTypeName, this.wordCount);
        }
    }

    static class GetTotalWordCountFromDocTypeReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        // 每个文档类别的单词总词数
        private IntWritable totalWordCount = new IntWritable(0);

        /**
         *
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            // key: CANA
            // values: [13,1,1,24,3,7....12,3,6]
            // values是中该类别中每个单词出现的次数组成的数组，数组求和即是每个文档类别的单词总词数
            int totalWordCount = 0;
            for (IntWritable wordCount : values) {
                totalWordCount += wordCount.get();
            }
            this.totalWordCount.set(totalWordCount);
            System.out.println(key.toString() + this.totalWordCount);
            context.write(key, this.totalWordCount);
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
        Configuration conf = new Configuration();

        // 如果输出目录存在，则先删除输出目录
        Path outputPath = new Path(Const.GET_TOTAL_WORD_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH);
        FileSystem fs = outputPath.getFileSystem(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(conf, "GetTotalWordCountFromDocTypeJob");

        job.setJarByClass(GetTotalWordCountFromDocType.class);
        job.setMapperClass(GetTotalWordCountFromDocTypeMapper.class);
        job.setCombinerClass(GetTotalWordCountFromDocTypeReducer.class);
        job.setReducerClass(GetTotalWordCountFromDocTypeReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(Const.GET_SINGLE_WORD_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH));
        FileOutputFormat.setOutputPath(job, new Path(Const.GET_TOTAL_WORD_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new GetTotalWordCountFromDocType(), args);
        System.exit(run);
    }
}
