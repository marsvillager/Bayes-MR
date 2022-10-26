整体流程：[IDEA配置Hadoop开发环境&编译运行WordCount程序 - bloglxc - 博客园 (cnblogs.com)](https://www.cnblogs.com/lxc1910/p/11798479.html)

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
hdfs://master:9870/NBCorpus/Country/AUSTR hdfs://master:9870/out
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

A:

```
hdfs dfs -cat /out/part-r-00000
```

注：-ls 查看所有文件



# Token

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
