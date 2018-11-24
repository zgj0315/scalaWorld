# scala从入门到放弃
## sbt
项目有sbt构建
### sbt安装步骤
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
