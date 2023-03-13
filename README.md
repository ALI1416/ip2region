# IP Address To Region IP地址转区域

## 项目地址
[Github源码](https://github.com/ALI1416/ip2region)
[Gitee源码](https://gitee.com/ALI1416/ip2region)
[![Build Status](https://travis-ci.com/ALI1416/ip2region.svg?branch=master)](https://app.travis-ci.com/ALI1416/ip2region)

[Github测试](https://github.com/ALI1416/ip2region-test)
[Gitee测试](https://gitee.com/ALI1416/ip2region-test)
[![Build Status](https://travis-ci.com/ALI1416/ip2region-test.svg?branch=master)](https://app.travis-ci.com/ALI1416/ip2region-test)

### SpringBoot自动配置项目地址
[Github源码](https://github.com/ALI1416/ip2region-spring-boot-autoconfigure)
[Gitee源码](https://gitee.com/ALI1416/ip2region-spring-boot-autoconfigure)
[![Build Status](https://travis-ci.com/ALI1416/ip2region-spring-boot-autoconfigure.svg?branch=master)](https://app.travis-ci.com/ALI1416/ip2region-spring-boot-autoconfigure)

[Github测试](https://github.com/ALI1416/ip2region-spring-boot-autoconfigure-test)
[Gitee测试](https://gitee.com/ALI1416/ip2region-spring-boot-autoconfigure-test)
[![Build Status](https://travis-ci.com/ALI1416/ip2region-spring-boot-autoconfigure-test.svg?branch=master)](https://app.travis-ci.com/ALI1416/ip2region-spring-boot-autoconfigure-test)

## 简介
本工具类使用`org.lionsoul:ip2region`工具类作为基础，简化了操作，把方法改写成了静态类，并添加了区域实体，以及支持SpringBoot自动配置。

## 数据文件生成方法

1. 下载数据文件<https://gitee.com/lionsoul/ip2region/blob/master/data/ip.merge.txt>
2. 进行数据转换<https://gitee.com/ALI1416/ip2region-test/blob/master/src/main/java/com/demo/Convert.java>
3. 编译项目并生成jar程序<https://gitee.com/lionsoul/ip2region/tree/master/maker/java>
4. 生成xdb文件`java -jar E:\ip2region-maker-1.0.0.jar --src="E:\ip2region.txt" --dst="E:\ip2region.xdb"`
5. 压缩xdb文件成zip格式(xdb文件位于根目录，文件名任意)
6. 修改zip后缀成zxdb(可以进行cdn加速，zip格式无法加速)

## 依赖导入
最新版本
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cn.404z/ip2region/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.404z/ip2region)

`org.lionsoul:ip2region`最新版本
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.lionsoul/ip2region/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.lionsoul/ip2region)

maven
```xml
<!-- 必须依赖 -->
<dependency>
    <groupId>cn.404z</groupId>
    <artifactId>ip2region</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>ip2region</artifactId>
    <version>2.7.0</version>
</dependency>
<!-- 额外依赖(运行未报错，不需要加) -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.5</version>
</dependency>
```

## 使用方法
### 通过url初始化
代码
```java
Ip2Region.initByUrl("https://cdn.jsdelivr.net/gh/ali1416/ip2region-test/data/ip2region.zxdb");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，URL路径为：https://cdn.jsdelivr.net/gh/ali1416/ip2region-test/data/ip2region.zxdb
[main] INFO cn.z.ip2region.Ip2Region - 加载数据成功！
Region{country='中国', province='北京', city='北京市', isp='联通'}
```

### 通过文件初始化
代码
```java
Ip2Region.initByFile("E:/ip2region.zip");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zip
[main] INFO cn.z.ip2region.Ip2Region - 加载数据成功！
Region{country='中国', province='北京', city='北京市', isp='联通'}
```

### 通过inputStream初始化
代码
```java
try {
    Ip2Region.init(new FileInputStream("E:/ip2region.zip"));
} catch (Exception e) {
    e.printStackTrace();
}
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 加载数据成功！
Region{country='中国', province='北京', city='北京市', isp='联通'}
```

### 初始化多次
代码
```java
Ip2Region.initByFile("E:/ip2region.zip");
Ip2Region.initByFile("E:/ip2region.zip");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zip
[main] INFO cn.z.ip2region.Ip2Region - 加载数据成功！
[main] WARN cn.z.ip2region.Ip2Region - 已经初始化过了，不可重复初始化！
Region{country='中国', province='北京', city='北京市', isp='联通'}
```

### 初始化异常
代码
```java
Ip2Region.initByFile("E:/ip2region");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region
[main] ERROR cn.z.ip2region.Ip2Region - 文件异常！
java.io.FileNotFoundException: E:\ip2region (系统找不到指定的文件。)
[main] ERROR cn.z.ip2region.Ip2Region - 未初始化！
null
```

## 许可证
[![License](https://img.shields.io/badge/license-BSD-brightgreen)](https://opensource.org/licenses/BSD-3-Clause)

## 交流
QQ：1416978277  
微信：1416978277  
支付宝：1416978277@qq.com  
![交流](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/contact.png)

## 赞助
![赞助](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/donate.png)
