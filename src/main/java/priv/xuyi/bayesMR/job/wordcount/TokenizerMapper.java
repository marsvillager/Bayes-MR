package priv.xuyi.bayesMR.job.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 15:59
 * @description tokenizer the text and mapper
 */
public class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    /**
     *
     * @param key   input
     * @param value input
     * @param context   用户代码与 MR 系统交互的上下文
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {

        // print log to .txt file
//        String fileName="log.txt";
//        PrintStream out = new PrintStream(fileName);
//        System.setOut(out);

        StringTokenizer itr = new StringTokenizer(value.toString()); // 将字符串分成一个个的单词
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken()); // 将 token 写入 word
            context.write(word, one); // token 出现一次, 就将 <token, 1> 写入 context, MR 将键值交给 Reducer 处理
        }
    }
}
