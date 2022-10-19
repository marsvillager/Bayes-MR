package com.hadoop.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.StringTokenizer;

/**
 * public class Mapper<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
 * 四个泛型：KEYIN, VALUEIN, KEYOUT, VALUEOUT, <KEYIN, VALUEIN> ==> <KEYOUT, VALUEOUT>
 *     String ==> Text
 *     int ==> IntWritable
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
    public void map(Object key, Text value, Mapper<Object, Text, Text, IntWritable>.Context context)
            throws IOException, InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString()); // 将字符串分成一个个的单词
        while (itr.hasMoreTokens()) {
            word.set(itr.nextToken()); // 将 token 写入 word
            context.write(word, one); // token 出现一次, 就将 <token, 1> 写入 context, MR 将键值交给 Reducer 处理
        }
    }
}
