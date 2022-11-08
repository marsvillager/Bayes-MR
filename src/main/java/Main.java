import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ToolRunner;
import priv.xuyi.bayesMR.job.analysis.Evaluation;
import priv.xuyi.bayesMR.job.calculate.GetDocCountFromDocType;
import priv.xuyi.bayesMR.job.calculate.GetNaiveBayesResult;
import priv.xuyi.bayesMR.job.calculate.GetSingleWordCountFromDocType;
import priv.xuyi.bayesMR.job.calculate.GetTotalWordCountFromDocType;
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


        /** 训练集 **/
        Configuration configuration = new Configuration();
        configuration.set("INPUT_PATH", Const.TRAIN_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TRAIN_DATA_SEQUENCE_FILE_PATH);

        // 将训练集多个文件生成一个 sequence_file
        SmallFilesToSequenceFileConverter convert = new SmallFilesToSequenceFileConverter();
        ToolRunner.run(configuration, convert, args);

        // 根据 SmallFilesToSequenceFileConverter 输出的 sequence_file 统计每个文档类别有多少个文档
        GetDocCountFromDocType getDocCountFromDocType = new GetDocCountFromDocType();
        ToolRunner.run(configuration, getDocCountFromDocType, args);

        // 根据 SmallFilesToSequenceFileConverter 输出的 sequence_file 统计每个单词在每个文档类别中出现的次数
        GetSingleWordCountFromDocType getSingleWordCountFromDocType = new GetSingleWordCountFromDocType();
        ToolRunner.run(configuration, getSingleWordCountFromDocType, args);

        // 根据 SmallFilesToSequenceFileConverter 输出的s equence_file 统计每个文档类别的总单词数
        GetTotalWordCountFromDocType getTotalWordCountFromDocTypeJob = new GetTotalWordCountFromDocType();
        ToolRunner.run(configuration, getTotalWordCountFromDocTypeJob, args);

        // 将测试集多个文件生成一个 sequence_file
        configuration.set("INPUT_PATH", Const.TEST_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TEST_DATA_SEQUENCE_FILE_PATH);
        ToolRunner.run(configuration, convert, args);


        /** 测试集 **/
        configuration = new Configuration();
        configuration.set("INPUT_PATH", Const.TEST_DATA_INPUT_PATH);
        configuration.set("OUTPUT_PATH", Const.TEST_DATA_SEQUENCE_FILE_PATH);
        configuration.set("DOC_TYPE_LIST", Const.DOC_TYPE_LIST);

        // 将测试集多个文件生成一个sequence_file
        convert = new SmallFilesToSequenceFileConverter();
        ToolRunner.run(configuration, convert, args);

        // 读取之前所有任务输出的sequence_file到内存并计算训练集的先验概率、条件概率(setup中进行)
        // 读取InitSequenceFileJob生成的测试集的sequence_file计算测试集的每个文档分成每一类的概率
        GetNaiveBayesResult getNaiveBayesResultJob = new GetNaiveBayesResult();
        ToolRunner.run(configuration, getNaiveBayesResultJob, args);


        /** 评估 **/
        Evaluation evaluation = new Evaluation();
        ToolRunner.run(configuration, evaluation, args);
    }
}
