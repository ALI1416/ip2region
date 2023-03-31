# 数据文件

## 文件列表

### 标签：`3.0.0`

- 文件名：`ip2region.zdb`
  - 版本号：`20221207`
  - 校验码：`68EDD841`
  - 点击下载：[![ip2region.zdb](https://img.shields.io/github/size/ali1416/ip2region/data/ip2region.zdb?label=ip2region.zdb&color=success&branch=v3.0.0)](https://cdn.jsdelivr.net/gh/ali1416/ip2region@3.0.0/data/ip2region.zdb)
  - 链接地址：`https://cdn.jsdelivr.net/gh/ali1416/ip2region@3.0.0/data/ip2region.zdb`

## 数据来源

- 数据文件来源：<https://gitee.com/lionsoul/ip2region>
- 查看最新版本：<https://gitee.com/lionsoul/ip2region/commits/master/data/ip.merge.txt>
- 数据文件下载：<https://gitee.com/lionsoul/ip2region/blob/master/data/ip.merge.txt>
- 数据文件生成方法java程序：[点击查看](../src/test/java/cn/z/ip2region/DataGenerationTest.java)

## 数据文件设计

### 整体结构

| 中文名   | 头部区 | 记录区     | 二级索引区 | 索引区   |
| -------- | ------ | ---------- | ---------- | -------- |
| 英文名   | header | record     | vector2    | vector   |
| 长度     | 不定长 | 不定长     | 定长       | 不定长   |
| 数据类型 | 不限   | 不定长数组 | 定长数组   | 定长数组 |

### 头部区

| 中文名   | CRC32校验和 | 版本号  | 记录区指针    | 二级索引区指针 | 索引区指针    | 拓展 |
| -------- | ----------- | ------- | ------------- | -------------- | ------------- | ---- |
| 英文名   | crc32       | version | recordAreaPtr | vector2AreaPtr | vectorAreaPtr | ...  |
| 长度     | 4           | 4       | 4             | 4              | 4             | ...  |
| 数据类型 | int         | int     | int           | int            | int           | ...  |

- `头部区`可以进行拓展
- `CRC32校验和`是除去前`4`字节，对剩下的所有数据进行校验

### 记录区

每条记录格式如下：

| 中文名   | 记录值长度        | 记录值      |
| -------- | ----------------- | ----------- |
| 英文名   | recordValueLength | recordValue |
| 长度     | 1                 | 不定长      |
| 数据类型 | byte              | utf8        |

- `记录值长度`不包括自己所占的`1`字节
- `记录值`最大长度为`255`字节

### 二级索引区

每条二级索引格式如下(`256x256+1`块)：

| 中文名   | 索引指针  |
| -------- | --------- |
| 英文名   | vectorPtr |
| 长度     | 4         |
| 数据类型 | int       |

- 附加一块的值为`文件总字节数`

### 索引区

每条索引格式如下：

| 中文名   | 起始IP地址 | 结束IP地址 | 记录指针  |
| -------- | ---------- | ---------- | --------- |
| 英文名   | ipStart    | ipEnd      | recordPtr |
| 长度     | 2          | 2          | 4         |
| 数据类型 | short      | short      | int       |

- `起始IP地址`和`结束IP地址`为后2位转为short型
- 部分需要拆分，详见代码

## 参考

- [lionsoul2014/ip2region](https://github.com/lionsoul2014/ip2region)
- [xluohome/phonedata](https://github.com/xluohome/phonedata)
- [EeeMt/phone-number-geo](https://github.com/EeeMt/phone-number-geo)
