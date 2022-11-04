import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.bayesMR.job.sequencefile.ConvertToSequenceFile;
import priv.xuyi.bayesMR.utils.Const;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 15:41
 * @description main program
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        configuration.set("INPUT_PATH", Const.TRAIN_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TRAIN_DATA_SEQUENCE_FILE_PATH);

        ConvertToSequenceFile convert = new ConvertToSequenceFile();
        ToolRunner.run(configuration, convert, args);
    }
}
