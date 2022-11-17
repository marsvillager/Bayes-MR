package priv.xuyi.matrix.utils;

import java.util.Arrays;
import java.util.List;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 15:43
 * @description constant
 */
public class Const {
    private static String[] stopWordsArray = {"A", "a", "the", "an", "in",
            "on", "and", "The", "As", "as", "AND"};

    /**
     * 目录前缀
     */
    public static final String BASE_PATH = "hdfs://master:9000";

    /**
     * 数据集
     */
    public static final String MITRE_ATTACK_DATA = BASE_PATH + "/MITRE_ATTACK_DATA";

    /**
     * word co-occurrence matrix 输出目录
     */
    public static final String WORD_MATRIX_OUTPUT_PATH = BASE_PATH + "/WORD_MATRIX_JOB_OUTPUT";

    /**
     * Hadoop生成的文件名 因为本实验处理的文件大小都小于block_size所以只有一个part文件
     */
    public static final String HADOOP_DEFAULT_OUTPUT_FILE_NAME = "/part-r-00000";

    public static final List<String> STOP_WORDS_LIST = Arrays.asList(stopWordsArray);
}
