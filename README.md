# IP Address To Region IP地址转区域

[![License](https://img.shields.io/github/license/ALI1416/ip2region?label=License)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Java Support](https://img.shields.io/badge/Java-8+-green)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/ip2region?label=Maven%20Central)](https://mvnrepository.com/artifact/cn.404z/ip2region)
[![Tag](https://img.shields.io/github/v/tag/ALI1416/ip2region?label=Tag)](https://github.com/ALI1416/ip2region/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ALI1416/ip2region?label=Repo%20Size&color=success)](https://github.com/ALI1416/ip2region/archive/refs/heads/master.zip)

[![Java CI](https://github.com/ALI1416/ip2region/actions/workflows/ci.yml/badge.svg)](https://github.com/ALI1416/ip2region/actions/workflows/ci.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=coverage)
![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_ip2region&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_ip2region)

## 简介

本项目根据[lionsoul2014/ip2region](https://github.com/lionsoul2014/ip2region)重构，并加上了数据文件压缩后从外部导入、静态方法调用等，以及支持[SpringBoot自动配置](https://github.com/ALI1416/ip2region-spring-boot-autoconfigure)

## 数据文件

- 数据文件目录：[点击查看](./data)

### 其他语言项目

- `JavaScript` : [ALI1416/ip2region-js](https://github.com/ALI1416/ip2region-js)

## 依赖导入

```xml
<dependency>
  <groupId>cn.404z</groupId>
  <artifactId>ip2region</artifactId>
  <version>3.2.0</version>
</dependency>
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.4.11</version>
</dependency>
```

## 使用方法

### 定义常量

```java
final String url = "https://www.404z.cn/files/ip2region/v3.0.0/data/ip2region.zdb";
final String zdbPath = "E:/ip2region.zdb";
final String txtPath = "E:/ip.merge.txt";
final String errorPath = "E:/ip2region.error.txt";
final String ip = "123.132.0.0";
```

### 通过url初始化

```java
log.info("是否已经初始化：{}", Ip2Region.initialized());
Ip2Region.initByUrl(url);
log.info(String.valueOf(Ip2Region.initialized()));
log.info("是否已经初始化：{}", Ip2Region.initialized());
log.info(String.valueOf(Ip2Region.parse(ip)));
// INFO cn.z.ip2region.Ip2RegionTest - 是否已经初始化：false
// INFO cn.z.ip2region.Ip2Region - IP地址转区域初始化：URL路径URL_PATH https://www.404z.cn/files/ip2region/v3.0.0/data/ip2region.zdb
// INFO cn.z.ip2region.Ip2Region - 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// INFO cn.z.ip2region.Ip2RegionTest - 是否已经初始化：true
// INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
```

### 通过文件初始化

```java
Ip2Region.initByFile(zdbPath);
log.info(String.valueOf(Ip2Region.parse(ip)));
// INFO cn.z.ip2region.Ip2Region - IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
// INFO cn.z.ip2region.Ip2Region - 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
```

### 通过inputStream初始化

```java
Ip2Region.init(new FileInputStream(zdbPath));
log.info(String.valueOf(Ip2Region.parse(ip)));
// INFO cn.z.ip2region.Ip2Region - 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
```

### 初始化多次

```java
Ip2Region.initByFile(zdbPath);
Ip2Region.initByFile(zdbPath);
log.info(String.valueOf(Ip2Region.parse(ip)));
// INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
// INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// WARN cn.z.ip2region.Ip2Region -- 已经初始化过了，不可重复初始化！
// INFO cn.z.ip2region.Ip2RegionTest -- Region{country='中国', province='山东省', city='济宁市', isp='联通'}
```

### 初始化异常

```java
try {
    Ip2Region.initByFile("A:/1.txt");
} catch (Exception e) {
    e.printStackTrace();
}
log.info(String.valueOf(Ip2Region.parse(ip)));
// INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH A:/1.txt
// ERROR cn.z.ip2region.Ip2Region -- 初始化文件异常！
// java.io.FileNotFoundException: A:\1.txt (系统找不到指定的路径。)
// cn.z.ip2region.Ip2RegionException: 初始化文件异常！
// cn.z.ip2region.Ip2RegionException: 未初始化！
```

### 数据错误

```java
Ip2Region.initByFile(zdbPath);
try {
    Ip2Region.parse("0.0.0.300");
} catch (Exception e) {
    e.printStackTrace();
}
try {
    Ip2Region.parse(-1L);
} catch (Exception e) {
    e.printStackTrace();
}
// INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
// INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// cn.z.ip2region.Ip2RegionException: IP地址 0.0.0.300 不合法！
// cn.z.ip2region.Ip2RegionException: IP地址 -1 不合法！
```

### 性能测试

```java
Ip2Region.initByFile(zdbPath);
log.info(String.valueOf(Ip2Region.parse(ip)));
long startTime = System.currentTimeMillis();
for (long i = 0; i < 0x100000000L; i++) {
    Ip2Region.parse(i);
}
long endTime = System.currentTimeMillis();
log.info("查询 {} 条数据，用时 {} 毫秒", 0x100000000L, endTime - startTime);
// INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
// INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
// INFO cn.z.ip2region.Ip2RegionTest - 查询 4294967296 条数据，用时 562161 毫秒
```

### 完整性测试

```java
Ip2Region.initByFile(zdbPath);
log.info(String.valueOf(Ip2Region.parse(ip)));
long startTime = System.currentTimeMillis();
int errorCount = 0;
BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(errorPath));
BufferedReader bufferedReader = new BufferedReader(new FileReader(txtPath));
String line = bufferedReader.readLine();
while (line != null && !line.isEmpty()) {
    // 起始IP地址|结束IP地址|国家|地区|省份|城市|ISP
    String[] s = line.split("\\|");
    String region = ("0".equals(s[2]) ? "" : s[2]) + "|" // 国家
            + ("0".equals(s[4]) ? "" : s[4]) + "|" // 省份
            + ("0".equals(s[5]) ? "" : s[5]) + "|" // 城市
            + ("0".equals(s[6]) ? "" : s[6]); // ISP
    int hash = (new Region(region)).toString().hashCode();
    long ipStart = Ip2Region.ip2long(s[0]);
    long ipEnd = Ip2Region.ip2long(s[1]) + 1;
    for (long i = ipStart; i < ipEnd; i++) {
        if (hash != Ip2Region.parse(i).toString().hashCode()) {
            String error = "解析记录`" + line + "`时发现IP地址`" + Ip2Region.long2ip(i) //
                    + "`解析错误，实际为`" + Ip2Region.parse(i) + "`";
            errorCount++;
            log.error(error);
            bufferedWriter.write(error + "\n");
        }
    }
    log.info("解析记录`{}`，共 {} 条", line, ipEnd - ipStart);
    line = bufferedReader.readLine();
}
bufferedReader.close();
bufferedWriter.flush();
bufferedWriter.close();
long endTime = System.currentTimeMillis();
log.info("解析 {} 条数据，错误 {} 条，用时 {} 毫秒", 0x100000000L, errorCount, endTime - startTime);
// INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
// INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
// INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
// INFO cn.z.ip2region.Ip2RegionTest - 解析记录`0.0.0.0|0.255.255.255|0|0|0|内网IP|内网IP`，共 16777216 条
// INFO cn.z.ip2region.Ip2RegionTest - 解析记录`1.0.0.0|1.0.0.255|澳大利亚|0|0|0|0`，共 256 条
// ...
// INFO cn.z.ip2region.Ip2RegionTest - 解析记录`223.255.255.0|223.255.255.255|澳大利亚|0|0|0|0`，共 256 条
// INFO cn.z.ip2region.Ip2RegionTest - 解析记录`224.0.0.0|255.255.255.255|0|0|0|内网IP|内网IP`，共 536870912 条
// INFO cn.z.ip2region.Ip2RegionTest - 解析 4294967296 条数据，错误 0 条，用时 869132 毫秒
```

### 工具测试

```java
String ip = "123.45.67.89";
log.info("IP地址 {} 是否合法 {}", ip, Ip2Region.isValidIp(ip));
long ip2 = 123456789L;
log.info("long型IP地址 {} 是否合法 {}", ip, Ip2Region.isValidIp(ip2));
// INFO cn.z.ip2region.Ip2RegionTest -- IP地址 123.45.67.89 是否合法 true
// INFO cn.z.ip2region.Ip2RegionTest -- long型IP地址 123.45.67.89 是否合法 true
```

更多请见[测试](./src/test)

## 更新日志

[点击查看](./CHANGELOG.md)

## 关于

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://www.404z.cn/images/about.dark.svg">
  <img alt="About" src="https://www.404z.cn/images/about.light.svg">
</picture>
