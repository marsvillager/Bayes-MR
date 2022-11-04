整体流程：[IDEA配置Hadoop开发环境&编译运行WordCount程序 - bloglxc - 博客园 (cnblogs.com)](https://www.cnblogs.com/lxc1910/p/11798479.html)

# 0、格式化

**（1）class**

[Idea 设置自动注释，并解决自动注释的@param和@return为null的问题 - 码农教程 (manongjc.com)](http://www.manongjc.com/detail/26-hzdqrswgsogmgsi.html)

设置 -> 编辑器 -> 文件和代码模板 -> 文件 -> class

```
/**
 * @author XuYi
 * @email 1968643693@qq.com
 * @date ${YEAR}-${MONTH}-${DAY} ${HOUR}:${MINUTE}
 * @description	${description}
 */
```

**（2）method**

[idea中Java文件方法的Javadoc注释（param去括号，return全类名） - 灰信网（软件开发博客聚合） (freesion.com)](https://www.freesion.com/article/28471511726/)

设置 -> 编辑器 -> 实时模板（live template）-> MyJavaDoc（没找到）

设置 -> 编辑器 -> 文件和代码模板 -> 代码 -> JavaDoc Method

**（3）package-info**

右键新建 pakcage-info.java

**（4）包的命名规范**

[(133条消息) Java包名的命名规则_我顶得了的博客-CSDN博客_java包名命名规则](https://blog.csdn.net/qq_38534524/article/details/89441607)

priv ： 独自完成，非公开，版权属于个人

```
    包名为priv.个人名.项目名.模块名
```

[(133条消息) java命名大小写规则,Java命名规范_卖星巴克杯子的人的博客-CSDN博客](https://blog.csdn.net/weixin_36302331/article/details/115390117)

- 项目名全部小写
- 包名全部小写
- 类名首字母大写，如果类名由多个单词组成，每个单词的首字母都要大写
- 变量名、方法名首字母小写，如果名称由多个单词组成，每个单词的首字母都要大写
- 常量名全部大写

# 1、log4j（closed）

Q:

```
log4j:WARN No appenders could be found for logger (org.apache.http.client.protocol.RequestAddCookies). log4j:WARN Please initialize the log4j system properly. log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
```

A:

[(131条消息) 解决报错显示：log4j:WARN No appenders could be found for logger ._⚆Pearl的博客-CSDN博客](https://blog.csdn.net/weixin_58330979/article/details/123576662)

把v log4j.properties 文件加入工程内就可以了，一般存放于 target/classes 的文件夹下。

# 2、windows 下 hadoop（closed）

Q:

```
java.io.FileNotFoundException: java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset. -see https://wiki.apache.org/hadoop/WindowsProblems
```

A:

[关于IDEA出现报错： java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset.、_小小程序员呀~的博客-CSDN博客](https://blog.csdn.net/whs0329/article/details/121878162)

没有设置  HADOOP_HOME 和 hadoop.home.dir 两项。而这两项就是配置在本地环境变量中的 Hadoop 地址，也就是需要我们在本地搭建Hadoop环境。

法一、如果是远程连接 Linux 上的 Hadoop 集群，是不需要在本地再下载 hadoop，只要下载 winutils 文件，然后配置环境变量，最后再把 hadoop.dll 文件放到 C:/windows/system32 下就可以了

法二、hadoop 运行在 windows 系统上的，也是要下载 winutils 文件,然后配置环境变量，比上面多出一步就是，需要把你下的 winutils 文件下你需要的 Hadoop 版本的 bin 目录文件去替换你 windows 系统之前使用的 Hadoop 版本的 bin 目录文件，最后同样是把 hadoop.dll 文件放 C:/windows/system32 下就可以了

### winutils

[cdarlint/winutils: winutils.exe hadoop.dll and hdfs.dll binaries for hadoop windows (github.com)](https://github.com/cdarlint/winutils)

# 3、linux 下 hadoop（closed）

Q:

```
util.Shell: Failed to detect a valid hadoop home directory
java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset.
```

`echo $HADOOP_HOME` 发现没问题

# 4、Type mismatch（closed）

Q:

```
java.io.IOException: Type mismatch in value from map: expected org.apache.hadoop.io.Text, received org.apache.hadoop.io.IntWritable
```

A:

输出 key 的类型写错，不该是 Text，而是 IntWritable

```
job.setOutputKeyClass(Text.class); // 设置输出 key 的类型
job.setOutputValueClass(IntWritable.class); // 设置输出值的类型
```

# 5、权限（closed）

Q:

```
DEBUG security.UserGroupInformation: PrivilegedAction [as: reptile (auth:SIMPLE)][action: org.apache.hadoop.mapreduce.Job$1@fa49800]
```

A:

（1）安全模式？

[为什么启动HDFS之后一直处于安全模式 - 简书 (jianshu.com)](https://www.jianshu.com/p/d02e046f936a)

`hdfs dfsadmin -safemode get` 查看

输出 `Safe mode is OFF`

（2）`Permission denied: user=dr.who, access=WRITE, inode="/":reptile:supergroup:drwxr-xr-x`

[(132条消息) Hdfs页面操作文件出现 Permission denied: user=dr.who, access=WRITE, inode=“/“:hadoop:supergroup:drwxr-xr问题解决_biuubi的博客-CSDN博客_permission denied: user=dr.who](https://blog.csdn.net/biuubi/article/details/117230219)

法① 通过修改 core-site.xml，配置为当前用户

```
<property>
    <name>hadoop.http.staticuser.user</name>
    <value>reptile</value>
</property>
```

法② hdfs-site.xml 中默认是开启权限检查的

```
# 配置 HDFS 权限
<property>
    <name>dfs.permissions.enabled</name>
    <value>false</value>
</property>
# 配置 HDFS 超级用户
<property>
　　<name>dfs.permissions.superusergroup</name>
　　<value>reptile</value>
　　<description>配置超级用户组</description>
</property>
```

法③ 直接修改 /tmp 目录的权限设置

```
hdfs dfs -chmod -R 755 /文件  
```

（3）hdfs 上没有 root 用户，也没有对应的文件夹 /user/root

hdfs 默认以 root 身份去将作业写入 hdfs 文件系统中，对应的也就是 /user/root

[(132条消息) 使用hive客户端的hdfs权限认证org.apache.hadoop.security.AccessControlException: Permission denied: user=root..._jzy3711的博客-CSDN博客_at org.apache.hadoop.security.usergroupinformation](https://blog.csdn.net/jzy3711/article/details/85003606)

# 配置

[(132条消息) Hadoop学习之idea开发wordcount实例_wangyangmingtian的博客-CSDN博客](https://blog.csdn.net/yangmingtia/article/details/84021223)

## （1）上传文件到 hdfs

### ① 可视化上传

hdfs 网址：

```
master:9870
```

Utilities 中选择 Browse the file system，里面可以上传文件

问题：只能上传文件，不能上传文件夹

### ② 代码上传

批量上传脚本：[(132条消息) hadoop 上传本地文件夹到hdfs - CSDN](https://www.csdn.net/tags/NtDaAgzsNTU5Mi1ibG9n.html)

windows 运行脚本问题：`urllib3.exceptions.NewConnectionError: <urllib3.connection.HTTPConnection object at 0x00000299E93195E0>: Failed to establish a new connection: [Errno 11001] getaddrinfo failed`

尝试 hadoop 所在的本机 linux：<font color="red">**成功**</font>

## （2）配置参数：文件输入路径和输出路径

### ① 本地文件

```java
FileInputFormat.addInputPath(job, new Path("file:////home/reptile/桌面/Bayes-MR/NBCorpus/Country/AUSTR")); // 设置输入文件目录
FileOutputFormat.setOutputPath(job, new Path("file:///home/reptile/out")); // 设置输出文件目录
```

### ② hdfs 路径

```java
FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/NBCorpus/Country/AUSTR")); // 设置输入文件目录
FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/out")); // 设置输出文件目录
```

### ③ 设置实参

在**运行/调试配置**里修改即可（框框下有一行灰色小字：应用程序的 CLI 实参）

```
hdfs://master:9000/NBCorpus/Country/AUSTR hdfs://master:9000/out
```

# 6、RPC 连接失败（closed）

Q:

第一种情况：端口号为 9870

```java
FileInputFormat.addInputPath(job, new Path("hdfs://master:9870/NBCorpus/Country/AUSTR")); // 设置输入文件目录
FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9870/out")); // 设置输出文件目录
```

```
Exception in thread "main" org.apache.hadoop.ipc.RpcException: RPC response exceeds maximum data length
```

第二种情况：端口号为 9000

```java
FileInputFormat.addInputPath(job, new Path("hdfs://master:9000/NBCorpus/Country/AUSTR")); // 设置输入文件目录
FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9000/out")); // 设置输出文件目录
```

```
Call From master/192.168.73.169 to master:9000 failed on connection exception: java.net.ConnectException: 拒绝连接
```

A:

[(132条消息) Call From Master/192.168.47.100 to localhost:9000 failed on connection exception: java.net. 报错解决方法_SmarTongs的博客-CSDN博客](https://blog.csdn.net/weixin_44080131/article/details/120909028)

[Hadoop之常用端口号_61%的博客-CSDN博客_hadoop端口号](https://blog.csdn.net/weixin_44484668/article/details/123238351)

	hadoop3.x
	HDFS	NameNode	内部通常端口：8020、9000、9820
	HDFS	NameNode	对用户的查询端口：9870
	Yarn查看任务运行情况的端口：8088
	历史服务器：19888

① 浏览器访问 localhost:9870 发现 Overview 中节点的名称是 master:9000（改名称在 core-site.xml 的 fs.defaultFS 字段中）

② 输入文件和输出文件的目录中 hdfs 的 ip 改为 master，端口则是 9000（不是可视化查询界面的 9870）

注：第二种情况的错误就是因为 ① 中 fs.defaultFS 的字段设置为了 localhost，主机名却是 master，不匹配

# 7、查看 wordcount 输出文件

## （1）hdfs 查看

A:

```
hdfs dfs -cat /out/part-r-00000
```

注：-ls 查看所有文件

## （2）文件逻辑位置查看

[(132条消息) Hadoop中HDFS的文件到底存储在集群节点本地文件系统哪里_YuanOo。的博客-CSDN博客_hdfs文件存储在哪里](https://blog.csdn.net/weixin_43114954/article/details/115571939)

**① 根据 hdfs 的可视化界面查看文件的 Block Information**

Utilities 中的 Browse the file system

```
Block Information: Block x
Block ID: 1073742399
Block Pool ID: BP-2129610265-192.168.73.169-1666601721284 
Generation Stamp: 1575
Size: 3884
Availability:
	slave1
	master
```

发现 namenode 和 datanode 都有该文件

附：block 的作用举例

```
Block Information的下拉框中可以发信啊文件被分为几块，比如：
发现该文件被分成了block0，block1两个块。
原因是该文件大小大于128M，所以要被切割分为2个块。然后备份3份。

备份在该Block x选项下的Availability中显示
block0备份存储共3份在Slave1,Slave3以及Slave4 这3个datanode机器上。
block1备份存储共3份在Slave2,Slave4以及Slave3 这3个datanode机器上。
```

**② 根据 hdfs-site.xml 文件确定 datanode 中文件的目录**

```xml
	<property>
 		<name>dfs.replication</name>
 		<value>3</value>
 	</property>
	<property>
		<name>dfs.datanode.data.dir</name>
		<value>/home/reptile/BayesMR/hdfs/data</value>
	</property>
```

**③ 找到该目录下的 Block Pool ID 文件夹: BP-2129610265-192.168.73.169-1666601721284 **

```
drwxrwxr-x 4 reptile reptile 4096 10月 26 18:06 current
-rw-rw-r-- 1 reptile reptile  166 10月 26 16:28 scanner.cursor
drwxrwxr-x 2 reptile reptile 4096 10月 27 18:16 tmp
```

current 目录下有：

```
-rw-rw-r-- 1 reptile reptile   21 10月 26 18:06 dfsUsed
drwxrwxr-x 3 reptile reptile 4096 10月 26 16:28 finalized
drwxrwxr-x 2 reptile reptile 4096 10月 26 18:00 rbw
-rw-rw-r-- 1 reptile reptile  146 10月 27 18:16 VERSION
```

finalized 目录下有 subdir0

subdir0 的目录下有 subdir0, subdir1, subdir2

三个目录下都是以 blk_ + 一串数字结尾的文件，这一串数字就是 ① 中的 Block ID: 1073742399

最终在 subdir2 中找到了该文件：blk_1073742399

# 8、小文件过多

## （1）转化为 Sequence File

setup 在 map 之前运行, 将 map 的 key 映射成：文档类型@文件名

MR 处理 job：

- key: CANA@487557newsML.txt
- value: 487557newsML.txt的文件内容

输入格式：自定义

输出格式：`job.setOutputFormatClass(SequenceFileOutputFormat.class); // 原生类`

## （2）多文件

### ① 训练集

hdfs://master:9000/TRAIN_DATA_FILE 其下有两类：AUSTR 和 CANA

```mermaid
flowchart TB
	A(hdfs://master:9000) --> B(TRAIN_DATA_FILE)
	B --> C(AUSTR)
	B --> D(CANA)
```

### ② 测试集

hdfs://master:9000/TEST_DATA_FILE

## （3）类的实例化

Q:

由于 log 太长，控制台没有全部显示出来，而且信息太多，查看或者检索很不方便，所以输出到文件中：

```java
        String fileName="log.txt";
        PrintStream out = new PrintStream(fileName);
        System.setOut(out);
```

搜索 SequenceFileMapper，发现：

```java
22/11/04 17:06:44 WARN mapred.LocalJobRunner: job_local377893897_0001
java.lang.Exception: java.lang.RuntimeException: java.lang.NoSuchMethodException: priv.xuyi.bayesMR.job.sequencefile.SmallFilesToSequenceFileConverter$SequenceFileMapper.<init>()
	at org.apache.hadoop.mapred.LocalJobRunner$Job.runTasks(LocalJobRunner.java:492)
	at org.apache.hadoop.mapred.LocalJobRunner$Job.run(LocalJobRunner.java:552)
Caused by: java.lang.RuntimeException: java.lang.NoSuchMethodException: priv.xuyi.bayesMR.job.sequencefile.SmallFilesToSequenceFileConverter$SequenceFileMapper.<init>()
	at org.apache.hadoop.util.ReflectionUtils.newInstance(ReflectionUtils.java:137)
	at org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:760)
	at org.apache.hadoop.mapred.MapTask.run(MapTask.java:348)
	at org.apache.hadoop.mapred.LocalJobRunner$Job$MapTaskRunnable.run(LocalJobRunner.java:271)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:750)
Caused by: java.lang.NoSuchMethodException: priv.xuyi.bayesMR.job.sequencefile.SmallFilesToSequenceFileConverter$SequenceFileMapper.<init>()
	at java.lang.Class.getConstructor0(Class.java:3082)
	at java.lang.Class.getDeclaredConstructor(Class.java:2178)
	at org.apache.hadoop.util.ReflectionUtils.newInstance(ReflectionUtils.java:131)
	... 8 more
```

A:[MapReduce错误之Error: java.lang.RuntimeException: java.lang.NoSuchMethodException的解决方法-百度网盘下载-Java自学者论坛 - Powered by Discuz! (javazxz.com)](https://www.javazxz.com/thread-5343-1-1.html)

<font color=red>因为在 main 函数中是按照类名来调用方法的，所以要将 Map 内部类申明为静态的，即原因在于 SequenceFileMapper 并没有申明为 static，所以报错</font>

## （4）FileSplit

Q:

搜索 WholeFileInputFormat，发现：

```
22/11/04 17:18:28 WARN mapred.LocalJobRunner: job_local705361015_0001
java.lang.Exception: java.lang.ClassCastException: org.apache.hadoop.mapreduce.lib.input.FileSplit cannot be cast to org.apache.hadoop.mapred.FileSplit
	at org.apache.hadoop.mapred.LocalJobRunner$Job.runTasks(LocalJobRunner.java:492)
	at org.apache.hadoop.mapred.LocalJobRunner$Job.run(LocalJobRunner.java:552)
Caused by: java.lang.ClassCastException: org.apache.hadoop.mapreduce.lib.input.FileSplit cannot be cast to org.apache.hadoop.mapred.FileSplit
	at priv.xuyi.bayesMR.job.sequencefile.WholeFileRecordReader.initialize(WholeFileRecordReader.java:38)
	at priv.xuyi.bayesMR.job.sequencefile.WholeFileInputFormat.createRecordReader(WholeFileInputFormat.java:47)
	at org.apache.hadoop.mapred.MapTask$NewTrackingRecordReader.<init>(MapTask.java:528)
	at org.apache.hadoop.mapred.MapTask.runNewMapper(MapTask.java:771)
	at org.apache.hadoop.mapred.MapTask.run(MapTask.java:348)
	at org.apache.hadoop.mapred.LocalJobRunner$Job$MapTaskRunnable.run(LocalJobRunner.java:271)
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	at java.util.concurrent.FutureTask.run(FutureTask.java:266)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:750)
```

A: 导入的包错了

[(134条消息) 报错org.apache.hadoop.mapreduce.lib.input.FileSplit cannot be cast to org.apache.hadoop.mapred.FileSpl_我是玄离大人的博客-CSDN博客](https://blog.csdn.net/qq_32599217/article/details/100029800)

## （5）输出格式问题

Q: 创建了输出文件夹，但里面没有内容

```
22/11/04 16:53:25 DEBUG mapred.LocalJobRunner: Starting mapper thread pool executor.
22/11/04 16:53:25 DEBUG mapred.LocalJobRunner: Max local threads: 1
22/11/04 16:53:25 DEBUG mapred.LocalJobRunner: Map tasks to process: 400
22/11/04 16:53:25 INFO mapred.LocalJobRunner: Waiting for map tasks
22/11/04 16:53:25 DEBUG concurrent.HadoopThreadPoolExecutor: beforeExecute in thread: LocalJobRunner Map Task Executor #0, runnable type: java.util.concurrent.FutureTask
22/11/04 16:53:25 INFO mapred.LocalJobRunner: Starting task: attempt_local1916219546_0001_m_000000_0
22/11/04 16:53:25 DEBUG mapred.SortedRanges: currentIndex 0   0:0
22/11/04 16:53:25 DEBUG mapred.LocalJobRunner: mapreduce.cluster.local.dir for child : /tmp/hadoop-reptile/mapred/local/localRunner//reptile/jobcache/job_local1916219546_0001/attempt_local1916219546_0001_m_000000_0
22/11/04 16:53:25 DEBUG mapred.Task: using new api for output committer
22/11/04 16:53:25 DEBUG output.PathOutputCommitterFactory: Looking for committer factory for path hdfs://master:9000/TRAIN_DATA_SEQUENCE_FILE
22/11/04 16:53:25 DEBUG output.PathOutputCommitterFactory: No scheme-specific factory defined in mapreduce.outputcommitter.factory.scheme.hdfs
22/11/04 16:53:25 DEBUG output.PathOutputCommitterFactory: No output committer factory defined, defaulting to FileOutputCommitterFactory
22/11/04 16:53:25 DEBUG output.PathOutputCommitterFactory: Creating FileOutputCommitter for path hdfs://master:9000/TRAIN_DATA_SEQUENCE_FILE and context TaskAttemptContextImpl{JobContextImpl{jobId=job_local1916219546_0001}; taskId=attempt_local1916219546_0001_m_000000_0, status=''}
22/11/04 16:53:25 DEBUG output.PathOutputCommitter: Instantiating committer FileOutputCommitter{PathOutputCommitter{context=TaskAttemptContextImpl{JobContextImpl{jobId=job_local1916219546_0001}; taskId=attempt_local1916219546_0001_m_000000_0, status=''}; org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter@449ab020}; outputPath=null, workPath=null, algorithmVersion=0, skipCleanup=false, ignoreCleanupFailures=false} with output path hdfs://master:9000/TRAIN_DATA_SEQUENCE_FILE and job context TaskAttemptContextImpl{JobContextImpl{jobId=job_local1916219546_0001}; taskId=attempt_local1916219546_0001_m_000000_0, status=''}
22/11/04 16:53:25 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 2
22/11/04 16:53:25 INFO output.FileOutputCommitter: FileOutputCommitter skip cleanup _temporary folders under output directory:false, ignore cleanup failures: false
```



```
22/11/04 15:39:06 INFO mapred.Task:  Using ResourceCalculatorProcessTree : [ ]
22/11/04 15:39:06 INFO mapred.MapTask: Processing split: hdfs://master:9000/TRAIN_DATA_FILE/CANA/486838newsML.txt:0+1538
22/11/04 15:39:06 DEBUG concurrent.ExecutorHelper: afterExecute in thread: LocalJobRunner Map Task Executor #0, runnable type: java.util.concurrent.FutureTask
22/11/04 15:39:06 DEBUG concurrent.HadoopThreadPoolExecutor: beforeExecute in thread: LocalJobRunner Map Task Executor #0, runnable type: java.util.concurrent.FutureTask
22/11/04 15:39:06 INFO mapred.LocalJobRunner: Starting task: attempt_local103077956_0001_m_000149_0
22/11/04 15:39:06 DEBUG mapred.SortedRanges: currentIndex 0   0:0
22/11/04 15:39:06 DEBUG mapred.LocalJobRunner: mapreduce.cluster.local.dir for child : /tmp/hadoop-reptile/mapred/local/localRunner//reptile/jobcache/job_local103077956_0001/attempt_local103077956_0001_m_000149_0
22/11/04 15:39:06 DEBUG mapred.Task: using new api for output committer
22/11/04 15:39:06 DEBUG output.PathOutputCommitterFactory: Looking for committer factory for path hdfs://master:9000/TRAIN_DATA_SEQUENCE_FILE
22/11/04 15:39:06 DEBUG output.PathOutputCommitterFactory: No scheme-specific factory defined in mapreduce.outputcommitter.factory.scheme.hdfs
22/11/04 15:39:06 DEBUG output.PathOutputCommitterFactory: No output committer factory defined, defaulting to FileOutputCommitterFactory
22/11/04 15:39:06 DEBUG output.PathOutputCommitterFactory: Creating FileOutputCommitter for path hdfs://master:9000/TRAIN_DATA_SEQUENCE_FILE and context TaskAttemptContextImpl{JobContextImpl{jobId=job_local103077956_0001}; taskId=attempt_local103077956_0001_m_000149_0, status=''}
22/11/04 15:39:06 DEBUG output.PathOutputCommitter: Instantiating committer FileOutputCommitter{PathOutputCommitter{context=TaskAttemptContextImpl{JobContextImpl{jobId=job_local103077956_0001}; taskId=attempt_local103077956_0001_m_000149_0, status=''}; org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter@475f04f7}; outputPath=null, workPath=null, algorithmVersion=0, skipCleanup=false, ignoreCleanupFailures=false} with output path hdfs://master:9000/TRAIN_DATA_SEQUENCE_FILE and job context TaskAttemptContextImpl{JobContextImpl{jobId=job_local103077956_0001}; taskId=attempt_local103077956_0001_m_000149_0, status=''}
22/11/04 15:39:06 INFO output.FileOutputCommitter: File Output Committer Algorithm version is 2
22/11/04 15:39:06 INFO output.FileOutputCommitter: FileOutputCommitter skip cleanup _temporary folders under output directory:false, ignore cleanup failures: false
```

# 9、常量

```mermaid
flowchart TB
	A(hdfs://master:9000) --> B(TRAIN_DATA_FILE)
	B --> C(AUSTR)
	B --> D(CANA)
	A --> E(TEST_DATA_FILE)
	A --> F(TRAIN_DATA_SEQUENCE_FILE)
	A --> G(TEST_DATA_SEQUENCE_FILE)
```

|             常量              |                 具体值                  |               含义                |
| :---------------------------: | :-------------------------------------: | :-------------------------------: |
|           BASE_PATH           |           hdfs://master:9000            |              根目录               |
|    WORD_COUNT_OUTPUT_PATH     |  BASE_PATH + "/WORD_COUNT_JOB_OUTPUT"   |          word count 结果          |
|     TRAIN_DATA_INPUT_PATH     |     BASE_PATH + "/TRAIN_DATA_FILE"      |        训练集原始输入目录         |
|     TEST_DATA_INPUT_PATH      |      BASE_PATH + "/TEST_DATA_FILE"      |        测试集原始输入目录         |
| TRAIN_DATA_SEQUENCE_FILE_PATH | BASE_PATH + "/TRAIN_DATA_SEQUENCE_FILE" | 训练集整合为sequence file后的目录 |
| TEST_DATA_SEQUENCE_FILE_PATH  | BASE_PATH + "/TEST_DATA_SEQUENCE_FILE"  | 测试集整合为sequence file后的目录 |
|                               |                                         |                                   |
|                               |                                         |                                   |

## （1）word count

```java
FileInputFormat.addInputPath(job, new Path(Const.TRAIN_DATA_INPUT_PATH + "/CANA")); // 设置输入文件目录
FileOutputFormat.setOutputPath(job, new Path(Const.WORD_COUNT_OUTPUT_PATH)); // 设置输出文件目录
```

## （2）convert to sequence file

### ① 训练集

```java
    configuration.set("INPUT_PATH", Const.TRAIN_DATA_INPUT_PATH);
    configuration.set("OUTPUT_PATH", Const.TRAIN_DATA_SEQUENCE_FILE_PATH);
```

### ② 测试集



# 10、数据集

|        | AUSTR | CANA | 总计 |
| :----: | :---: | :--: | :--: |
| 训练集 |  200  | 200  | 400  |
| 测试集 |  105  |  63  | 168  |
|  总计  |  305  | 263  | 568  |

# 11、Local Aggregation

## （1）官方 Combiner

```java
class Mapper
	method Map(docid a, doc d)
        for all term t ∈ doc d do
        Emit(term t, count 1)

class Combiner
	method Combine(term t, counts [c1, c2, . . .])
        sum ← 0
        for all count c ∈ counts [c1, c2, . . .] do
        sum ← sum + c
        Emit(term t, count sum)

class Reducer
	method Reduce(term t, counts [c1, c2, . . .])
        sum ← 0
        for all count c ∈ counts [c1, c2, . . .] do
        sum ← sum + c
        Emit(term t, count sum)
```

Combiner 是利用 MapReduce API 提供的 Hook(钩子）。程序员只需要实现 Combiner 类的 Combine 方法，MapReduce 框架会自动调用自定义的 Combiner 类的 Combine 方法。

缺点：

- 无法控制Combiner的执行。例如，Hadoop 不保证 Combiner 被应用了多少次，甚至根本不保证它会被应用。在很多场合，这种不确定性是无法接受的
- Combiner 减少了在网络中 shuffled 的中间数据量，但实际上并没有减少 Mapper 首先发出的键值对的数量

## （2）In-Mapper Combining 设计模式

```java
class Mapper
	method Map(docid a, doc d)
        H ← new AssociativeArray // AssociativeArray可以是一个Java Map，里面存放是键值对，key是单词（term），value是单词出现次数
        for all term t ∈ doc d do
        H{t} ← H{t} + 1 // 统计整个文档d范围内，t出现的次数
        for all term t ∈ H do
        Emit(term t, count H{t})
```

Mapper 的输出是<t, t 在 d 中出现的次数>，注意：这时 map 输出的对比之前的 Mapper 输出的对大大减少了，因为此时的 t 是不重复的，考虑到一些频繁出现的单词如 the，In-Mapper  Combiner 大大减少了 Mapper 输出的键值对数量

## （3）In-Mapper Combiner（进一步改进）

```java
class Mapper
	method Initialize
            H ← new AssociativeArray // 现在是在Initialize方法里初始化AssociativeArray Initialize方法就是setup方法，后面不再特别说明。这样可以在Map方法的多次调用间保存状态
        method Map(docid a, doc d)
        for all term t ∈ doc d do
        H{t} ← H{t} + 1 // 由于每次Map调用传进来整个文档，因此现在是跨文档进行单词出现次数统计
        method Close
        for all term t ∈ H do
        Emit(term t, count H{t}) // 现在是在Close方法里输出键值对，Close方法就是cleanup方法，后面不再特别说明。t是跨多个文档的不重复的t，因此In-Mapper Combiner进一步大大减少了Mapper输出的键值对数量
```

[(132条消息) MapReduce设计模式之In-mapper Combining_weixin_34345560的博客-CSDN博客](https://blog.csdn.net/weixin_34345560/article/details/93881621)

## origin(no SequenceFile)

```java
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
```

## imporve

```

```

# Token

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

