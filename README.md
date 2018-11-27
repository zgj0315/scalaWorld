# scala从入门到放弃
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

## 2. spark streaming
### 2.1 监听某一端口数据流
```
org.after90.sparkStreaming.NetworkWordCount

``` 

## 2.2 Kafka
### 2.2.1 本地部署kafka

# 部署ZooKeeper
1. 官网下载
```zookeeper-3.4.5.tar.gz```

2. 解压缩
```/Users/zhaogj/devTools/zookeeper-3.4.5```

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

5. 启动
```
/Users/zhaogj/devTools/zookeeper-3.4.5/bin/zkServer.sh start
/Users/zhaogj/devTools/zookeeper-3.4.5/bin/zkServer.sh stop
/Users/zhaogj/devTools/zookeeper-3.4.5/bin/zkServer.sh status
```

# 部署kafka
1. 官网下载：kafka_2.11-0.10.0.1.tgz
2. 解压缩到：
3. 启动：

```
### 2.2.2 写入kafka
```
todo
```
### 2.2.3 读取kafka
```
todo
```
### 2.2.4 写入kafka+读取kafka
```
todo
```
## 2.3 HBase
### 2.3.1 本地部署hbase
```
todo
```
### 2.3.2 写入hbase
```
todo
```
### 2.3.3 读取hbase
```
todo
```
### 2.3.4 写入hbase+读取hbase
```
todo
```