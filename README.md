# 联邦型RDF数据库系统上的多查询优化

**1，查询分解与数据源选择**\
**2，查询重写**\
**3，查询处理**\
**4，查询结果连接**



## 算法框架

![image](https://github.com/QiGe57/FMQO/blob/master/resources/fmqo_framework.png?raw=true)

## 运行
### 运行环境

Linux / win all \
java 1.8

### Demo
基于FMQO算法实现的简单演示网站
[点击查看](http://39.98.70.144:8088/FMQO)


### 快速开始
1. 下载 `FMQO_ask.jar`、`conf`文件夹(配置文件)、`lib.zip`（需要的jar包）

2. 运行 FMQO_ask.jar 

举例：
```
java -jar FMQO_ask.jar ./conf/configure.txt 10 50 2000
```

运行参数说明
```
Usage:java -jar xx.jar <configureFile> <windowSize> <queryNum> <joinNum>
<queryNum> : the number of queries
<joinNum> : the number of intermediate result
```

---

configure.txt 文件说明

| 参数 | 说明  |
| :------------ | :------------ |
| DataServerFile  |  服务器的配置 |
|  optimizaType |  优化类型；可填 values（默认） 、 Filter、optional 或者 none（不用任何 Join 优化） |
|outputPath | 日志和结果的输出位置 |
| WorkloadPath| 查询文件的位置 |
|DataType | 如 watdiv100M |
|STGFileStr |数据库拓扑关系文件的位置 |
|Data | RDF数据谓词的位置 |
 

---

## 论文
1. [TKDE 2019] Optimizing Multi-Query Evaluation in Federated RDF Systems

2. [APWeb-WAIM 2019] FMQO: A Federated RDF System Supporting Multi-Query Optimization  , [查看](https://link.springer.com/chapter/10.1007/978-3-030-26075-0_30) , **Best Demo Award** .
