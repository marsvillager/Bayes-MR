package priv.xuyi.bayesMR.job.analysis;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-07 17:15
 * @description 对各文档的贝叶斯分类结果进行评估，计算各文档FP、TP、FN、TN、Precision、Recall、F1以及整体的宏平均、微平均
 */
public class Evaluation extends Configured implements Tool {


    /**
     *
     * @param strings
     * @return
     * @throws Exception
     */
    @Override
    public int run(String[] strings) throws Exception {
        return 0;
    }
}
