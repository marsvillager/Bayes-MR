package priv.xuyi.bayesMR.job.sequencefile;

import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 16:38
 * @description key of sequence files: type@file_name
 */
public class ConvertToSequenceFileMapper extends Mapper<NullWritable, ByteWritable, Text, ByteWritable> {
    private Text docKey = new Text();

    /**
     *
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Mapper<NullWritable, ByteWritable, Text, ByteWritable>.Context context) throws IOException, InterruptedException {
        System.out.println("start InitSequenceFileMapper's setup()");
        InputSplit split = context.getInputSplit();
        String docType = ((FileSplit)split).getPath().getParent().getName();
        String docName = ((FileSplit)split).getPath().getName();
        docKey.set(docType + "@" + docName); //  在 map 之前运行, 将 map 的 key 映射成：文档类型@文件名
    }

    /**
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(NullWritable key, ByteWritable value, Mapper<NullWritable, ByteWritable, Text, ByteWritable>.Context context) throws IOException, InterruptedException {
        // key: CANA@487557newsML.txt
        // value: 487557newsML.txt的文件内容
        System.out.println("start InitSequenceFileMapper's map()");
        context.write(this.docKey, value);
    }
}
