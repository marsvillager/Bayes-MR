package priv.xuyi.bayesMR.job.sequencefile;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

import java.io.IOException;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 17:41
 * @description custom sequence file record reader
 */
public class WholeFileRecordReader extends RecordReader<NullWritable, BytesWritable> {
    private FileSplit fileSplit; // 保存输入的分片，它将被转换成一条(key, value)记录
    private Configuration configuration; // 配置对象
    private BytesWritable value = new BytesWritable(); // value 对象，内容为空
    private boolean processed = false; // 布尔变量记录记录是否被处理过

    /**
     *
     * @param inputSplit
     * @param taskAttemptContext
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        this.fileSplit = (FileSplit) inputSplit; // 将输入分片强制转换成 FileSplit
        this.configuration = taskAttemptContext.getConfiguration(); // 从 context 获取配置信息
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if(!processed) { // 如果记录没有被处理过
            byte[] contents = new byte[(int)fileSplit.getLength()]; // 从 FileSplit 对象获取 split 的字节数， 创建 byte 数组 contents
            Path filePath = fileSplit.getPath(); // 从 FileSplit 对象获取输入文件路径
            FileSystem fs = filePath.getFileSystem(configuration); // 获取文件系统对象
            FSDataInputStream stream = null; // 定义文件输入流对象
            try {
                stream = fs.open(filePath); // 打开文件，返回文件输入流对象
                IOUtils.readFully(stream, contents,0, contents.length); // 从输入流读取所有字节到 contents
                value.set(contents, 0, contents.length); // 将 contents 内容设置到 value 对象中
            } finally {
                IOUtils.closeStream(stream); // 关闭输入流
            }
            this.processed = true; // 将是否处理标志设为 true，下次调用该方法会返回 false
            return true;
        }
        return false; // 如果记录处理过，返回 false，表示 split 处理完毕
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        return NullWritable.get();
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    /**
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public float getProgress() throws IOException, InterruptedException {
        return processed ? 1.0f : 0.0f; // 返回一个浮点数，表示已经处理的数据占所有要处理数据的百分比，由于是将整个文件作为一个记录读取，因此要么是 0.0，要么是 1.0
    }

    /**
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        // do nothing，由于前面已经关闭文件输入流，所以这里不要做任何事
    }
}
