package job.wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date 2022-11-01 16:06
 * @description Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT>
 *     四个泛型：KEYIN, VALUEIN, KEYOUT, VALUEOUT, 相同 key 合并，value 形成一个集合
 *     String ==> Text
 *     int ==> IntWritable
 */
public class IntSumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    private IntWritable result = new IntWritable();

    /**
     *
     * @param key   input
     * @param values    input, 集合类型, map 输出通过 context 交到 reduce 前，要把相同 key 的 value 都合并到一个集合里
     * @param context   用户代码与 MR 系统交互的上下文
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Reducer<Text, IntWritable, Text,
            IntWritable>.Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable value : values) {
            sum += value.get(); // 每个 value = 1, 进行累加
        }
        result.set(sum); // 得到 token 的词频
        context.write(key, result); // 将 <token, 词频> 写入 context
    }
}
