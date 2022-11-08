package priv.xuyi.bayesMR.job.calculate;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
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
 * @date 2022-11-07 15:59
 * @description 获取各文档数
 */
public class GetDocCountFromDocType extends Configured implements Tool {
    static class GetDocCountMapper extends Mapper<Text, BytesWritable, Text, IntWritable> {
        private Text docTypeName = new Text();
        private IntWritable docCount = new IntWritable(1);

        /**
         *
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void map(Text key, BytesWritable value, Context context) throws IOException, InterruptedException {
            // key: CANA@487557newsML.txt
            // value: 487557newsML.txt的文件内容
            // 这里只取key的信息用来计算每个文档种类有多少个文档，value不用管
            String[] keyName = key.toString().split("@"); // 以@为分隔符
            this.docTypeName.set(keyName[0]);
            this.docCount.set(1);
            context.write(this.docTypeName, docCount);
        }
    }

    static class GetDocCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private Text docTypeName = new Text();
        private IntWritable totalDocCount = new IntWritable(1);

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
            // values: [1,1,1,1,1.....,1,1,1]
            // 因为设置了job.setCombinerClass(GetDocCountReducer.class);
            // 相同的key的value会合并成一个数组，数组的和就是改文档种类对应的文档总数
            int totalDocCount = 0;
            for (IntWritable docCount : values) {
                totalDocCount += docCount.get();
            }
            this.docTypeName.set(key);
            this.totalDocCount.set(totalDocCount);
            context.write(this.docTypeName, this.totalDocCount);
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
        Path outputPath = new Path(Const.GET_DOC_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH);
        FileSystem fs = outputPath.getFileSystem(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(conf, "GetDocCountFromDocTypeJob");

        job.setJarByClass(GetDocCountFromDocType.class);
        job.setMapperClass(GetDocCountMapper.class);
        job.setCombinerClass(GetDocCountReducer.class);
        job.setReducerClass(GetDocCountReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(Const.TRAIN_DATA_SEQUENCE_FILE_PATH));
        FileOutputFormat.setOutputPath(job, new Path(Const.GET_DOC_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new GetDocCountFromDocType(), args);
        System.exit(run);
    }
}
