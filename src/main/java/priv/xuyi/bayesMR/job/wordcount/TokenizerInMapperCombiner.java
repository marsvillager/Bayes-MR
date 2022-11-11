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
 * @date 2022-11-11 17:07
 * @description add combiner in mapper
 */

public class TokenizerInMapperCombiner extends Mapper<Object, Text, Text, IntWritable> {
    private static Map<Text, Integer> inMapperCombiner = null;
    //    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    /**
     *
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Mapper<Object, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        inMapperCombiner = new HashMap<>();
    }

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
        StringTokenizer itr = new StringTokenizer(value.toString()); // 将字符串分成一个个的单词
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken()); // 将 token 写入 word
            if(inMapperCombiner.containsKey(word)) { // 键值对已存在，则值自增 1
                int tmp = inMapperCombiner.get(word);
                inMapperCombiner.put(word, tmp + 1);
            }
            else inMapperCombiner.put(word, 1); // 键值对不存在，则插入
//            context.write(word, one); // token 出现一次, 就将 <token, 1> 写入 context, MR 将键值交给 Reducer 处理
        }
    }

    /**
     *
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void cleanup(Mapper<Object, Text, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        for (Text text : inMapperCombiner.keySet()) {
            context.write(text, new IntWritable(inMapperCombiner.get(text))); // 统一写入
        }
    }
}
