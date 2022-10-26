[新手在Ubuntu上安装 Hadoop 3详细过程(验证+总结) - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/503707617)

# 1、SSH

## （1）安装 ssh 和 ssh 服务器

```
sudo apt-get install openssh-server
```

确认 `ssh` 服务器启动：

```
 ps -e|grep ssh 
```

看到 `sshd` 就说明 `ssh-server` 已经启动了，否则 `sudo /etc/init.d/ssh start` 启动服务

## （2）（可选）安装和配置 pdsh

pdsh 是一个并行分布式运维工具

- pdsh 是一个多线程远程 shell 客户机，它在多个远程主机上并行执行命令
- pdsh 可以使用几种不同的远程 shell 服务，包括标准的 rsh、Kerberos IV 和 ssh
- 在使用 pdsh之前，必须保证本地主机和要管理远程主机之间的单向信任
- pdsh 还附带了 pdcp 命令，该命令可以将本地文件批量复制到远程的多台主机上，这在大规模的文件分发环境下非常有用

```
 sudo apt-get install pdsh
```

**编辑用户目录下的 bash 命令行配置文件 .bashrc** ，在配置文件后面中增加以下内容

```text
 export PDSH_RCMD_TYPE=ssh
```

只是变量？

## （3）配置 ssh 密钥

```
 ssh-keygen -t rsa -P ""
```

将生成的密钥拷贝到授权文件中

```
 cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
```

## （4）测试 ssh 服务器是否正常运行

```
ssh localhost
```

# 2、Java 8

## （1）安装

```
 sudo apt-get install openjdk-8-jdk
```

## （2）检查

```
java -version
```

检查 Java 的 home 目录

```
ls /usr/lib/jvm/java-1.8.0-openjdk-amd64
```

# 3、Hadoop

## （1）下载 Hadoop tar 安装文件

二进制文件：[Apache Hadoop](https://hadoop.apache.org/releases.html)

## （2）解压缩 Hadoop 压缩文件

建议将下载的文件拷贝到用户目录

```
 tar xzf hadoop-3.3.4.tar.gz
```

## （3）将 hadooop 目录改名

以方便后续使用和引用目录名，这个目录 **/home/<用户名>/Hadoop** 就是 Hadoop 安装目录

```
 mv hadoop-3.3.4 hadoop
```

## （4）配置 Hadoop

### ① hadoop-env.sh

设置 Java 的 Home 路径（/usr/lib/jvm/java-1.8.0-openjdk-amd64）

```
 export JAVA_HOME=<path-of-your-Java-installation>
```

### ② core-site.xml

配置通用属性，例如 HDFS 和 MapReduce 常用的 I/O 设置等

准备一个 Hadoop 可读写目录，未来用于 Hadoop 数据存储目录（临时目录），本次安装设定在 ***/home/<用户名>/BayesMR/hdata*** 在配置文件中增加如下内容：

```
 <configuration>
 	<property>
 		<name>fs.defaultFS</name>
 		<value>hdfs://master:9000</value>
 	</property>
 	<property>
 		<name>hadoop.tmp.dir</name>
 		<value>/home/reptile/BayesMR/tmpdata</value>
 	</property>
 </configuration>
```

### ③ hdfs-site.xml

Hadoop 守护进程配置，包括 namenode、辅助 namenode和 datanode 等

- dfs.replication：默认 datanode 备份文件个数
- dfs.name.dir：指定 Hadoop 数据 namenode 文件存储目录
- dfs.data.dir：指定 Hadoop 数据 datanode 文件存储目录

```
 <configuration>
 	<property>
 		<name>dfs.replication</name>
 		<value>3</value>
 	</property>
 	<property>
 		<name>dfs.namenode.name.dir</name>
		<value>/home/reptile/BayesMR/hdfs/name</value>
	</property>
	<property>
		<name>dfs.datanode.data.dir</name>
		<value>/home/reptile/BayesMR/hdfs/data</value>
	</property>
 </configuration>
```

### ④ mapred-site.xml

MapReduce 守护进程配置

\<value> 是hadoop 的安装目录

```\
 <configuration>
 	<property>
 		<name>mapreduce.framework.name</name>
 		<value>yarn</value>
 	</property>
 	<property>
 		<name>yarn.app.mapreduce.am.env</name>
 		<value>HADOOP_MAPRED_HOME=/home/reptile/hadoop</value>
 	</property>
 	<property>
		 <name>mapreduce.map.env</name>
		 <value>HADOOP_MAPRED_HOME=/home/reptile/hadoop</value>
 	</property>
	<property>
 		<name>mapreduce.reduce.env</name>
 		<value>HADOOP_MAPRED_HOME=/home/reptile/hadoop</value>
 	</property>
 </configuration>
```

### ⑤ yarn-site.xml

资源调度相关配置

```\
 <configuration>
 	<property>
 		<name>yarn.nodemanager.aux-services</name>
 		<value>mapreduce_shuffle</value>
 	</property>
 	<property>
		 <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
 		<value>org.apache.hadoop.mapred.ShuffleHandler</value>
 	</property>  
 </configuration>
```

### ⑥ 在用户目录编辑 .bashrc 配置文件

```
 export HADOOP_HOME="/home/reptile/hadoop"
 export PATH=$PATH:$HADOOP_HOME/bin
 export PATH=$PATH:$HADOOP_HOME/sbin  
 export HADOOP_MAPRED_HOME=${HADOOP_HOME}
 export HADOOP_COMMON_HOME=${HADOOP_HOME}
 export HADOOP_HDFS_HOME=${HADOOP_HOME}
 export YARN_HOME=${HADOOP_HOME}
```

变更完激活参数

```text
 source ~/.bashrc
```

测试

```
echo $HADOOP_MAPRED_HOME
```

## （5）格式化 HDFS 文件系统

```
 hdfs namenode -format
```

### ① 在 hadoop 安装目录启动 HDFS 服务

```
start-dfs.sh
```

### ② 检查运行情况

- 用 jps 命令检查 Hadoop 进程是否正常运行
  - Jps
  - **DataNode**
  - **NameNode**
  - **SecondaryNameNode**
- 浏览器查看：` localhost:9870`

## （6）启动 yarn 服务

### ① 在 hadoop 安装目录启动 yarn 服务

```
start-yarn.sh
```

### ② 检查运行情况

- 用 jps 命令检查Hadoop 进程是否正常运行
  - Jps
  - DataNode
  - NameNode
  - SecondaryNameNode
  - **NodeManager**
  - **ResourceManager**
- 用浏览器访问 yarn web console 服务：` localhost:8088`

# 4、Hadoop 集群

[(131条消息) 【hadoop】ubuntu 安装hadoop3.2.1分布式集群和配置启动_weixin_42332638的博客-CSDN博客_ubuntu怎么启动hadoop](https://blog.csdn.net/weixin_42332638/article/details/123302494)

## （1）修改 hostname

分别为 master、slave1、slave2

```
sudo gedit /etc/hostname
```

## （2）修改节点 IP 映射

```
sudo gedit /etc/hosts  
```

```
IP1 master
IP2 slave1
IP3 slave2
```

## （3）ssh 免密登录

```
sudo ufw disable   #关闭防火墙     
```

将 1 中生成的公钥传输到各 slave 节点

```
scp ~/.ssh/id_rsa.pub reptile@slave1:/home/reptile
```

在 slave1 节点上，把公钥加入授权

```
mkdir ~/.ssh        # 如果不存在该文件夹需先创建，若已存在则忽略       
cat ~/id_rsa.pub >> ~/.ssh/authorized_keys                       
rm ~/id_rsa.pub    # 用完就可以删掉了
```

## （4）配置分布式集群环境

### ① workers

```
master
slave1
```

### ②  复制到 slave 集群

打包、发送、slave 集群解压

```
tar -zcvf hadoop.tar.gz /home/reptile/hadoop

scp hadoop.tar.gz reptile@slave1:~/

tar -zxvf ~/hadoop.tar.gz /home/reptile/
```

### ③ 检查

slave 集群 jps，有

- Jps
- DataNode
- NodeManager

# 5、运行和关闭

## （1）运行

```
 hdfs namenode -format # 初始化，不然虽然能启动，但打不开网页
 start-all.sh
```

hdfs: http://localhost:8088

yarn: http://localhost:9870

## （2）关闭

```
 stop-all.sh
```

# appendix

## 常用端口

[Hadoop之常用端口号_61%的博客-CSDN博客_hadoop端口号](https://blog.csdn.net/weixin_44484668/article/details/123238351)

	hadoop3.x
	HDFS	NameNode	内部通常端口：8020、9000、9820
	HDFS	NameNode	对用户的查询端口：9870
	Yarn查看任务运行情况的端口：8088
	历史服务器：19888

nmap 粗略扫描

```
PORT     STATE SERVICE
22/tcp   open  ssh
8031/tcp open  unknown
8042/tcp open  fs-agent
8088/tcp open  radan-http
```

## 可视化界面

### （1）HDFS

```
master:9870
```

### （2）YARN

#### ① Resource Manager

```
master:8088
```

#### ② Node Manafer

```
master:8042
```

