# scala入门
## 1. sbt
项目由sbt构建
### 1.1 sbt安装步骤
1. 官网下载sbt
```
https://www.scala-sbt.org/download.html
sbt-1.2.6.tgz
```

2. 解压
```
# 将压缩包解压，mv到开发工具目录
/Users/zhaogj/devTools/sbt-1.2.6
```
注：我习惯将所有开发工具都放在devTools目录中

3. 配置环境变量
```
# 文件：.bash_profile
export SBT_HOME=/Users/zhaogj/devTools/sbt-1.2.6
export PATH=$PATH:$SBT_HOME/bin
```

4. 验证sbt环境
```
rm -rf /Users/zhaogj/tmp/sbtTest
mkdir -p /Users/zhaogj/tmp/sbtTest
cd /Users/zhaogj/tmp/sbtTest
sbt
[warn] No sbt.version set in project/build.properties, base directory: /Users/zhaogj/tmp/sbtTest
[info] Set current project to sbttest (in build file:/Users/zhaogj/tmp/sbtTest/)
[info] sbt server started at local:///Users/zhaogj/.sbt/1.0/server/f5b33b8a2711dfc976e0/sock
sbt:sbttest> sbtVersion
[info] 1.2.6
```

### 1.2 构建一个sbt工程
```
直接参看官方文档，写的很详细
https://www.scala-sbt.org/1.x/docs/sbt-by-example.html
```

## 2. 本地部署ZooKeeper
1. 官网下载```zookeeper-3.4.5.tar.gz```
2. 解压缩```/Users/zhaogj/devTools/zookeeper-3.4.5```
3. 修改配置文件
```
# /Users/zhaogj/devTools/zookeeper-3.4.5/conf/zoo.cfg
tickTime=2000
dataDir=/Users/zhaogj/devTools/zookeeper-3.4.5/data
clientPort=2181
```
4. 创建数据目录
```
mkdir /Users/zhaogj/devTools/zookeeper-3.4.5/data
```
5. 命令
```
bin/zkServer.sh start
bin/zkServer.sh stop
bin/zkServer.sh status
bin/zkCli.sh -server 127.0.0.1:2181
```

## 3. 本地部署Kafka
1. 官网下载```kafka_2.11-0.10.0.1.tgz```
2. 解压缩```/Users/zhaogj/devTools/kafka_2.11-0.10.0.1```
3. 修改配置文件
```
na
```
4. 创建数据目录
```
na
```
5. 命令
```
bin/kafka-server-start.sh config/server.properties
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
bin/kafka-topics.sh --list --zookeeper localhost:2181
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
bin/kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning

```

## 4. 本地部署HBase

## 5. spark streaming
### 5.1 监听某一端口数据流
```
org.after90.sparkStreaming.NetworkWordCount

``` 


## 5.2 Kafka
仔细阅读spark源码中的demo
### 5.2.1 写入kafka
```
todo
```
### 5.2.2 读取kafka
```
todo
```
### 5.2.3 写入kafka+读取kafka
```
todo
```
## 5.3 HBase

### 5.3.1 写入hbase
```
todo
```
### 5.3.2 读取hbase
```
todo
```
### 5.3.3 写入hbase+读取hbase
```
todo
```
