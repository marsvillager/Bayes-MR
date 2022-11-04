package priv.xuyi.bayesMR.job.sequencefile;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.bayesMR.utils.Const;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 16:31
 * @description convert input files in .txt format to sequence files, key of sequence files: type@file_name
 */
public class ConvertToSequenceFile extends Configured implements Tool {
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
            System.out.println(inputFilePathList[i]);
        }

        Job job = Job.getInstance(configuration, "convert .txt to sequence files"); // 创建 MR job
        job.setJarByClass(ConvertToSequenceFile.class); // 设置启动类, 如果要打包到集群上运行，必须添加该设置
        job.setMapperClass(ConvertToSequenceFileMapper.class); // 设置 Mapper 类

        job.setOutputKeyClass(Text.class); // 设置输出 key 的类型
        job.setOutputValueClass(ByteWritable.class); // 设置输出值的类型
        job.setInputFormatClass(CustomSequenceFileInputFormat.class); // 自定义
        job.setOutputFormatClass(SequenceFileOutputFormat.class); // 原生类

        // 设置输入文件目录：将每个数据集文件夹导入到 CustomSequenceFileInputFormat 中
        for (String path : inputFilePathList) {
            CustomSequenceFileInputFormat.addInputPath(job, new Path(path));
        }
        SequenceFileOutputFormat.setOutputPath(job, outputPath); // 设置输出文件目录

        return job.waitForCompletion(true) ? 0 : 1;
    }

    public static void main(String[] args) throws Exception{
        Configuration configuration = new Configuration();
        configuration.set("INPUT_PATH", Const.TRAIN_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TRAIN_DATA_SEQUENCE_FILE_PATH);
        int run = ToolRunner.run(configuration, new ConvertToSequenceFile(), args);
        System.out.println("ConvertToSequenceFile end");
        System.exit(run);
    }
}
