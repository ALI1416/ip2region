# IP Address To Region IP地址转区域

[![License](https://img.shields.io/github/license/ali1416/ip2region?label=License)](https://opensource.org/licenses/BSD-3-Clause)
[![Java Support](https://img.shields.io/badge/Java-8+-green)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/ip2region?label=Maven%20Central)](https://mvnrepository.com/artifact/cn.404z/ip2region)
[![Tag](https://img.shields.io/github/v/tag/ali1416/ip2region?label=Tag)](https://github.com/ALI1416/ip2region/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ali1416/ip2region?label=Repo%20Size&color=success)](https://github.com/ALI1416/ip2region/archive/refs/heads/master.zip)

[![Java CI](https://github.com/ALI1416/ip2region/actions/workflows/ci.yml/badge.svg)](https://github.com/ALI1416/ip2region/actions/workflows/ci.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=coverage)
![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_ip2region)

## 简介

本项目根据[ip2region(lionsoul2014/ip2region)](https://github.com/lionsoul2014/ip2region)重构，并加上了数据文件压缩后从外部导入、静态方法调用等，以及支持[SpringBoot自动配置](https://github.com/ALI1416/ip2region-spring-boot-autoconfigure)

## 数据文件

- 数据文件目录：[点击查看](./data)
- 数据文件生成方法java程序：[点击查看](./src/test/java/cn/z/ip2region/DataGenerationTest.java)

## 依赖导入

```xml
<dependency>
    <groupId>cn.404z</groupId>
    <artifactId>ip2region</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.4.6</version>
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

## 交流

QQ：1416978277  
微信：1416978277  
支付宝：1416978277@qq.com  
![交流](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/contact.png)

## 赞助

![赞助](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/donate.png)
