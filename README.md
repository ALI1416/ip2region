# High performance snowflake ID generator 高性能雪花ID生成器

## 项目地址
[Github源码](https://github.com/ALI1416/id)
[Gitee源码](https://gitee.com/ALI1416/id)
[![Build Status](https://travis-ci.com/ALI1416/id.svg?branch=master)](https://travis-ci.com/ALI1416/id)

[Github测试](https://github.com/ALI1416/id-test)
[Gitee测试](https://gitee.com/ALI1416/id-test)
[![Build Status](https://travis-ci.com/ALI1416/id-test.svg?branch=master)](https://travis-ci.com/ALI1416/id-test)

### SpringBoot自动配置项目地址
[Github源码](https://github.com/ALI1416/id-spring-boot-autoconfigure)
[Gitee源码](https://gitee.com/ALI1416/id-spring-boot-autoconfigure)
[![Build Status](https://travis-ci.com/ALI1416/id-spring-boot-autoconfigure.svg?branch=master)](https://travis-ci.com/ALI1416/id-spring-boot-autoconfigure)

[Github测试](https://github.com/ALI1416/id-spring-boot-autoconfigure-test)
[Gitee测试](https://gitee.com/ALI1416/id-spring-boot-autoconfigure-test)
[![Build Status](https://travis-ci.com/ALI1416/id-spring-boot-autoconfigure-test.svg?branch=master)](https://travis-ci.com/ALI1416/id-spring-boot-autoconfigure-test)

## 简介
本项目重构的Twitter的雪花ID生成器，并加上了手动设置参数、时钟回拨处理等，以及支持SpringBoot自动配置。

## 依赖导入
最新版本
[![Maven central](https://maven-badges.herokuapp.com/maven-central/cn.404z/id/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cn.404z/id)

maven
```xml
<!-- 必须依赖 -->
<dependency>
    <groupId>cn.404z</groupId>
    <artifactId>id</artifactId>
    <version>2.3.0</version>
</dependency>
<!-- 额外依赖(运行未报错，不需要加) -->
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.32</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>1.2.6</version>
</dependency>
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.2.6</version>
</dependency>
```

gradle
```groovy
// 必须依赖
implementation 'cn.404z:id:2.3.0'
// 额外依赖(运行未报错，不需要加)
implementation 'org.slf4j:slf4j-api:1.7.32'
implementation 'ch.qos.logback:logback-core:1.2.6'
implementation 'ch.qos.logback:logback-classic:1.2.6'
```

## 使用方法
### 直接调用
代码
```java
System.out.println("ID为：" + Id.next());
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
ID为：5483442415337472
```

### 手动初始化
代码
```java
Id.init(0, 8, 14);
System.out.println("ID为：" + Id.next());
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为14
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为16384，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为69年，失效日期为2090-09-07 23:47:35.551
ID为：21934128022683648
```

## 异常处理
### 初始化多次
代码
```java
Id.init(0, 8, 13);
Id.init(0, 8, 15);
System.out.println("ID为：" + Id.next());
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为13
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为8192，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为139年，失效日期为2160-05-15 15:35:11.103
[main] WARN cn.z.id.Id - 已经初始化过了，不可重复初始化！
ID为：10967292061941760
```

### 初始化晚了
代码
```java
System.out.println("ID为：" + Id.next());
Id.init(0, 8, 12);
System.out.println("ID为：" + Id.next());
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] WARN cn.z.id.Id - 已经初始化过了，不可重复初始化！
ID为：5483684734959616
ID为：5483684734959617
```

### 初始化异常
代码
```java
Id.init(1000, 8, 12);
System.out.println("ID为：" + Id.next());
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为1000，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] ERROR cn.z.id.Id - 机器码MACHINE_ID需要>=0并且<=255。当前为1000
java.lang.Exception: 机器码无效
[main] ERROR cn.z.id.Id - 重置初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
ID为：5483719912587264
```

### 阻塞
代码
```java
// 初始化，复现阻塞
Id.init(0, 0, 0);
System.out.println("ID为：" + Id.next());
System.out.println("ID为：" + Id.next());
System.out.println("ID为：" + Id.next());
System.out.println("ID为：" + Id.next());
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
[main] INFO cn.z.id.Id - 手动初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为0，SEQUENCE_BITS为0
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为0，1ms内最多生成Id数量为1，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为292471208年，失效日期为292269004-12-03 00:47:04.191
[main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.469，最大序列号为0
[main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.485，最大序列号为0
ID为：5229847469
ID为：5229847485
ID为：5229847500
[main] WARN cn.z.id.Id - 检测到阻塞，时间为2021-03-02 20:44:07.5，最大序列号为0
ID为：5229847516
```

### 时钟回拨(需要在1分钟内手动回拨时钟)
代码
```java
for (int i = 0; i < 60; i++) {
    System.out.println("ID为：" + Id.next());
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
ID为：5483989976481792
[main] WARN cn.z.id.Id - 监测到系统时钟发生了回拨。回拨时间为2021-03-02 19:45:33.249，上一个生成的时间为2021-03-02 20:45:40.392
ID为：5483989977530368
```

### 重置初始时间戳(需要在1分钟内手动回拨时钟)
代码
```java
for (int i = 0; i < 60; i++) {
    System.out.println("ID为：" + Id.next());
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("总共回拨时间为：" + Id.reset() + "毫秒");
}
```

结果
```txt
[main] INFO cn.z.id.Id - 预初始化...
[main] INFO cn.z.id.Id - 初始化，MACHINE_ID为0，MACHINE_BITS为8，SEQUENCE_BITS为12
[main] INFO cn.z.id.Id - 最大机器码MACHINE_ID为255，1ms内最多生成Id数量为4096，时钟最早回拨到2021-01-01 08:00:00.0，可使用时间大约为278年，失效日期为2299-09-27 23:10:22.207
ID为：23564520900263936
[main] INFO cn.z.id.Id - 重置初始时间戳，时钟总共回拨0毫秒
总共回拨时间为：0毫秒
[main] WARN cn.z.id.Id - 监测到系统时钟发生了回拨。回拨时间为2021-09-18 10:25:55.498，上一个生成的时间为2021-09-18 10:27:58.361
ID为：23564520901312512
[main] INFO cn.z.id.Id - 重置初始时间戳，时钟总共回拨122864毫秒
总共回拨时间为：122864毫秒
ID为：23564393127084032
```

## 性能比较
| 次数   | random.nextLong()耗时 | Id.next()耗时 | UUID.randomUUID()耗时 | 倍数    |
| ------ | --------------------- | ------------- | --------------------- | ------- |
| 100万  | 15毫秒                | 47毫秒        | 1175毫秒              | 25.0倍  |
| 1000万 | 173毫秒               | 227毫秒       | 8853毫秒              | 39.0倍  |
| 1亿    | 793毫秒               | 909毫秒       | 83628毫秒             | 92.0倍  |
| 21亿   | 36886毫秒             | 37871毫秒     | 7915039毫秒           | 209.0倍 |

## 许可证
[![License](https://img.shields.io/badge/license-BSD-brightgreen)](https://opensource.org/licenses/BSD-3-Clause)

## 交流
QQ：1416978277  
微信：1416978277  
支付宝：1416978277@qq.com  
![交流](https://cdn.jsdelivr.net/gh/ALI1416/web/image/contact.png)

## 赞助
![赞助](https://cdn.jsdelivr.net/gh/ALI1416/web/image/donate.png)
