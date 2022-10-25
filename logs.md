hadoop: 18068110320

# 1.log4j（closed）

Q:

```
log4j:WARN No appenders could be found for logger (org.apache.http.client.protocol.RequestAddCookies). log4j:WARN Please initialize the log4j system properly. log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
```

A:

[(131条消息) 解决报错显示：log4j:WARN No appenders could be found for logger ._⚆Pearl的博客-CSDN博客](https://blog.csdn.net/weixin_58330979/article/details/123576662)

把v log4j.properties 文件加入工程内就可以了，一般存放于 target/classes 的文件夹下。

# 2.windows 下 hadoop（closed）

Q:

```
java.io.FileNotFoundException: java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset. -see https://wiki.apache.org/hadoop/WindowsProblems
```

A:

[关于IDEA出现报错： java.io.FileNotFoundException: HADOOP_HOME and hadoop.home.dir are unset.、_小小程序员呀~的博客-CSDN博客](https://blog.csdn.net/whs0329/article/details/121878162)

没有设置  HADOOP_HOME 和 hadoop.home.dir 两项。而这两项就是配置在本地环境变量中的 Hadoop 地址，也就是需要我们在本地搭建Hadoop环境。

法一、如果是远程连接 Linux 上的 Hadoop 集群，是不需要在本地再下载 hadoop，只要下载 winutils 文件，然后配置环境变量，最后再把 hadoop.dll 文件放到 C:/windows/system32 下就可以了

法二、hadoop 运行在 windows 系统上的，也是要下载 winutils 文件,然后配置环境变量，比上面多出一步就是，需要把你下的 winutils 文件下你需要的 Hadoop 版本的 bin 目录文件去替换你 windows 系统之前使用的 Hadoop 版本的 bin 目录文件，最后同样是把 hadoop.dll 文件放 C:/windows/system32 下就可以

### winutils

[cdarlint/winutils: winutils.exe hadoop.dll and hdfs.dll binaries for hadoop windows (github.com)](https://github.com/cdarlint/winutils)

# 3、linux 下 hadoop

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
job.setOutputKeyClass(IntWritable.class); // 设置输出 key 的类型
job.setOutputValueClass(Text.class); // 设置输出值的类型
```

# 5、权限

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

法② hdfs-default.xml 中默认是开启权限检查的

```
<property>
    <name>dfs.permissions.enabled</name>
    <value>false</value>
</property>
```

法③ 直接修改 /tmp 目录的权限设置

```
$hadoop位置/hdfs dfs -chmod -R 755 /
```

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
FileInputFormat.addInputPath(job, new Path("hdfs://master:9870/NBCorpus/Country/AUSTR")); // 设置输入文件目录
FileOutputFormat.setOutputPath(job, new Path("hdfs://master:9870/out")); // 设置输出文件目录
```

### ③ 设置实参

在**运行/调试配置**里修改即可（框框下有一行灰色小字：应用程序的 CLI 实参）

```
hdfs://master:9870/NBCorpus/Country/AUSTR hdfs://master:9870/out
```

# 6、超出长度

Q:

```
Exception in thread "main" org.apache.hadoop.ipc.RpcException: RPC response exceeds maximum data length
```

A:
