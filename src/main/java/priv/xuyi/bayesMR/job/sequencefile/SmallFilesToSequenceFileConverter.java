package priv.xuyi.bayesMR.job.sequencefile;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.bayesMR.utils.Const;

import java.io.IOException;
import java.io.PrintStream;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 16:31
 * @description convert input files in .txt format to sequence files, key of sequence files: type@file_name
 */
public class SmallFilesToSequenceFileConverter extends Configured implements Tool { // 实现 Tool 接口，利用 ToolRunner 来运行这个 MapReduce 程序
    // 利用嵌套类实现 SequenceFileMapper，继承 Mappper 类
    class SequenceFileMapper extends Mapper<NullWritable, ByteWritable, Text, ByteWritable> {
        private Text filenameKey; // 被打包的小文件名作为 key，表示为 Text 对象

        /**
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void setup(Mapper<NullWritable, ByteWritable, Text, ByteWritable>.Context context) throws IOException, InterruptedException {
            System.out.println("start SequenceFileMapper's setup()");
            InputSplit split = context.getInputSplit(); // 从 context 获取 split
            String docType = ((FileSplit) split).getPath().getParent().getName();
            String docName = ((FileSplit) split).getPath().getName();
            filenameKey.set(docType + "@" + docName); //  在 map 之前运行, 将 map 的 key 映射成：文档类型@文件名
        }

        /**
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
            System.out.println("start SequenceFileMapper's map()");
            // map 的输入是由 WholeFileInputFormat 将一个小文件作为一个记录处理生成的 keu-value 对
            context.write(filenameKey, value);
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
        Configuration configuration = getConf(); // 获取设好的配置
        Path inputPath = new Path(configuration.get("INPUT_PATH"));
        Path outputPath = new Path(configuration.get("OUTPUT_PATH"));

        // 如果输出目录存在，则先删除输出目录
        FileSystem fs = outputPath.getFileSystem(configuration);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }

        // 需要进行转换的文件目录（类别）
        fs = inputPath.getFileSystem(configuration);
        FileStatus[] inputFileStatusList = fs.listStatus(inputPath); // 获取输入目录下的目录信息
        String[] inputFilePathList = new String[inputFileStatusList.length]; // 初始化
        for (int i = 0; i < inputFilePathList.length; i++) {
            inputFilePathList[i] = inputFileStatusList[i].getPath().toString();
        }

        Job job = Job.getInstance(configuration, "convert .txt to sequence files"); // 创建 MR job
        job.setJarByClass(SmallFilesToSequenceFileConverter.class); // 设置启动类, 如果要打包到集群上运行，必须添加该设置
        job.setMapperClass(SequenceFileMapper.class); // 设置 Mapper 类

        job.setOutputKeyClass(Text.class); // 设置输出 key 的类型
        job.setOutputValueClass(ByteWritable.class); // 设置输出值的类型
        job.setInputFormatClass(WholeFileInputFormat.class); // 自定义
        job.setOutputFormatClass(SequenceFileOutputFormat.class); // 原生类

        // 设置输入文件目录：将每个数据集文件夹导入到 CustomSequenceFileInputFormat 中
        for (String path : inputFilePathList) {
            WholeFileInputFormat.addInputPath(job, new Path(path));
        }
        SequenceFileOutputFormat.setOutputPath(job, outputPath); // 设置输出文件目录

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception{
        // sout log
        String fileName="log.txt";
        PrintStream out = new PrintStream(fileName);
        System.setOut(out);

        Configuration configuration = new Configuration();
        configuration.set("INPUT_PATH", Const.TRAIN_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TRAIN_DATA_SEQUENCE_FILE_PATH);
        int run = ToolRunner.run(configuration, new SmallFilesToSequenceFileConverter(), args);
        System.out.println("ConvertToSequenceFile end");
        System.exit(run);
    }
}
