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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import priv.xuyi.bayesMR.utils.Const;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-07 16:23
 * @description 获取每个文档类型中每个单词出现的次数
 */
public class GetSingleWordCountFromDocType extends Configured implements Tool {
    public static final Logger log = LoggerFactory.getLogger(GetSingleWordCountFromDocType.class);

    static class GetSingleWordCountFromDocTypeMapper extends Mapper<Text, BytesWritable, Text, IntWritable> {
        // 单词
        private Text word = new Text();
        // 单词出现次数
        private IntWritable singleWordCountInEachDoc = new IntWritable(1);
        private static final Pattern ENGLISH_WORD_REGEX = Pattern.compile("^[A-Za-z]{2,}$");

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
            String docTypeName = key.toString().split("@")[0];
            // 将sequence_file中的bytes读成字符串
            String content = new String(value.getBytes());

            String[] wordList = content.split("\\s+");
            for (String word : wordList) {
                if (ENGLISH_WORD_REGEX.matcher(word).find() && !Const.STOP_WORDS_LIST.contains(word)) {
                    this.word.set(docTypeName + "@" + word);
                    context.write(this.word, this.singleWordCountInEachDoc);
                }
                // 处理训练集中出现的特殊字符对单词的影响
                else if (word.contains(".")) {
                    for (String maybeWord : word.split(".")) {
                        if (ENGLISH_WORD_REGEX.matcher(word).find()) {
                            this.word.set(docTypeName + "@" + maybeWord);
                            context.write(this.word, this.singleWordCountInEachDoc);
                        }
                    }
                } else if (word.contains("-")) {
                    for (String maybeWord : word.split("-")) {
                        if (ENGLISH_WORD_REGEX.matcher(word).find()) {
                            this.word.set(docTypeName + "@" + maybeWord);
                            context.write(this.word, this.singleWordCountInEachDoc);
                        }
                    }
                }
                else {
                    log.debug("过滤无用词：" + word);
                }
            }
        }
    }

    static class GetSingleWordCountFromDocTypeJobReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        // 单词出现总次数
        private IntWritable singleWordCountInEachDocType = new IntWritable(1);

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
            // key: CANA@hello
            // value: [1,1,1,1,1....,1,1,1]
            int totalWordCount = 0;
            for (IntWritable wordCount : values) {
                totalWordCount += wordCount.get();
            }
            this.singleWordCountInEachDocType.set(totalWordCount);
            context.write(key, this.singleWordCountInEachDocType);
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
        Path outputPath = new Path(Const.GET_SINGLE_WORD_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH);
        FileSystem fs = outputPath.getFileSystem(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        Job job = Job.getInstance(conf, "GetSingleWordCountFromDocTypeJob");

        job.setJarByClass(GetSingleWordCountFromDocType.class);
        job.setMapperClass(GetSingleWordCountFromDocTypeMapper.class);
        job.setCombinerClass(GetSingleWordCountFromDocTypeJobReducer.class);
        job.setReducerClass(GetSingleWordCountFromDocTypeJobReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(SequenceFileInputFormat.class);
        job.setOutputFormatClass(SequenceFileOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(Const.TRAIN_DATA_SEQUENCE_FILE_PATH));
        FileOutputFormat.setOutputPath(job, new Path(Const.GET_SINGLE_WORD_COUNT_FROM_DOC_TYPE_JOB_OUTPUT_PATH));

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new GetSingleWordCountFromDocType(), args);
        System.exit(run);
    }
}
