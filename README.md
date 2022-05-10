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
本工具类使用org.lionsoul:ip2region工具类作为基础，简化了操作，把方法改写成了静态类，添加了区域实体，以及支持SpringBoot自动配置。

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
    <version>1.1.0</version>
</dependency>
<dependency>
    <groupId>org.lionsoul</groupId>
    <artifactId>ip2region</artifactId>
    <version>1.7.2</version>
</dependency>
<!-- 额外依赖(运行未报错，不需要加) -->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.11</version>
</dependency>
```

gradle
```groovy
// 必须依赖
implementation 'cn.404z:ip2region:1.1.0'
implementation 'org.lionsoul:ip2region:1.7.2'
// 额外依赖(运行未报错，不需要加)
implementation 'ch.qos.logback:logback-classic:1.2.11'
```

## 使用方法
### 通过url初始化
代码
```java
Ip2Region.initByUrl("https://cdn.jsdelivr.net/gh/lionsoul2014/ip2region/data/ip2region.db");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，URL路径为https://cdn.jsdelivr.net/gh/lionsoul2014/ip2region/data/ip2region.db
[main] INFO cn.z.ip2region.Ip2Region - 加载数据文件成功，总共8.93MB
Region{country='中国', province='北京', city='北京', area='', isp='联通'}
```

### 通过文件初始化
代码
```java
Ip2Region.initByFile("/file/ip2region/data.db");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为/file/ip2region/data.db
[main] INFO cn.z.ip2region.Ip2Region - 加载数据文件成功，总共8.93MB
Region{country='中国', province='北京', city='北京', area='', isp='联通'}
```

### 通过bytes初始化
代码
```java
try {
    Ip2Region.init(Files.readAllBytes((new File("/file/ip2region/data.db")).toPath()));
} catch (IOException e) {
    e.printStackTrace();
}
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 加载数据文件成功，总共8.93MB
Region{country='中国', province='北京', city='北京', area='', isp='联通'}
```

### 初始化多次
代码
```java
Ip2Region.initByFile("/file/ip2region/data.db");
Ip2Region.initByFile("/file/ip2region/data.db");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为/file/ip2region/data.db
[main] INFO cn.z.ip2region.Ip2Region - 加载数据文件成功，总共8.93MB
[main] WARN cn.z.ip2region.Ip2Region - 已经初始化过了，不可重复初始化！
Region{country='中国', province='北京', city='北京', area='', isp='联通'}
```

### 初始化异常
代码
```java
Ip2Region.initByFile("/file/ip2region/data");
System.out.print(Ip2Region.parse("202.108.22.5"));
```

结果
```txt
[main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为/file/ip2region/data
[main] ERROR cn.z.ip2region.Ip2Region - 文件读取异常
java.nio.file.NoSuchFileException: \file\ip2region\data
[main] ERROR cn.z.ip2region.Ip2Region - memorySearch查询异常
java.lang.NullPointerException: null
Region{country='', province='', city='', area='', isp=''}
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
