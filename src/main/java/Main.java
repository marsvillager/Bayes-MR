import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.bayesMR.job.sequencefile.SmallFilesToSequenceFileConverter;
import priv.xuyi.bayesMR.utils.Const;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 15:41
 * @description main program
 */
public class Main {
    public static void main(String[] args) throws Exception {
        // print log to .txt file
//        String fileName="log.txt";
//        PrintStream out = new PrintStream(fileName);
//        System.setOut(out);

        // 将训练集多个文件生成一个 sequence_file
        Configuration configuration = new Configuration();
        configuration.set("INPUT_PATH", Const.TRAIN_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TRAIN_DATA_SEQUENCE_FILE_PATH);

        SmallFilesToSequenceFileConverter convert = new SmallFilesToSequenceFileConverter();
        ToolRunner.run(configuration, convert, args);


        // 将测试集多个文件生成一个 sequence_file
        configuration.set("INPUT_PATH", Const.TEST_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TEST_DATA_SEQUENCE_FILE_PATH);
        ToolRunner.run(configuration, convert, args);
    }
}
