hadoop: 18068110320

## 1.log4j

Q:

```
log4j:WARN No appenders could be found for logger (org.apache.http.client.protocol.RequestAddCookies). log4j:WARN Please initialize the log4j system properly. log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
```

A:

[(131条消息) 解决报错显示：log4j:WARN No appenders could be found for logger ._⚆Pearl的博客-CSDN博客](https://blog.csdn.net/weixin_58330979/article/details/123576662)

把v log4j.properties 文件加入工程内就可以了，一般存放于 target/classes 的文件夹下。

## 2.hadoop

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
