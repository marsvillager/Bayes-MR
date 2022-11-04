package priv.xuyi.bayesMR.job.sequencefile;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 17:32
 * @description customize sequence files input format
 */
public class WholeFileInputFormat extends FileInputFormat<NullWritable, BytesWritable> {
    static {
        System.out.println("start WholeFileInputFormat");
    }

    /**
     *
     * @param context
     * @param filename
     * @return
     */
    @Override
    protected boolean isSplitable(JobContext context, Path filename) {
        return false; // 不对输入文件分片
    }

    /**
     *
     * @param inputSplit
     * @param taskAttemptContext
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public RecordReader<NullWritable, BytesWritable> createRecordReader(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        WholeFileRecordReader reader = new WholeFileRecordReader(); // 实例化定制的 RecordReader 并返回
        reader.initialize(inputSplit, taskAttemptContext);
        return reader;
    }
}
