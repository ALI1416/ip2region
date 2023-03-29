# 数据文件

- 当前版本：`20221207`
- 数据文件来源：<https://github.com/lionsoul2014/ip2region>
- 查看最新版本：<https://github.com/lionsoul2014/ip2region/commits/master/data/ip.merge.txt>
- 数据文件下载：<https://gitee.com/lionsoul/ip2region/blob/master/data/ip.merge.txt>
- 数据文件生成方法java程序：[点击查看](../src/test/java/cn/z/ip2region/DataGenerationTest.java)

## 文件列表

- 本项目所使用的数据文件：
  [![ip2region.zdb](https://img.shields.io/github/size/ali1416/ip2region/data/ip2region.zdb?label=ip2region.zdb&color=success)](https://cdn.jsdelivr.net/gh/ali1416/ip2region@master/data/ip2region.zdb)

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
- `CRC32校验和`是除去前`4`字节，对所有数据进行校验

### 记录区

每条记录格式如下(个数根据数据文件而定)：

| 中文名   | 记录值长度        | 记录值      |
| -------- | ----------------- | ----------- |
| 英文名   | recordValueLength | recordValue |
| 长度     | 1                 | 不定长      |
| 数据类型 | byte              | utf8        |

- `记录值长度`不包括自己所占的`1`字节
- `记录值`最大长度为`255`字节

### 二级索引区

每条二级索引格式如下(二级索引个数为`256x256+1`个)：

| 中文名   | 索引指针  |
| -------- | --------- |
| 英文名   | vectorPtr |
| 长度     | 4         |
| 数据类型 | int       |

- 第一条是IP地址前2位为`0.0`的二级索引
- 最后一条是IP地址前2位为`255.255`的二级索引
- 附加一条`索引指针`为`文件总字节数`

### 索引区

每条索引格式如下(个数根据数据文件而定)：

| 中文名   | 起始IP地址 | 结束IP地址 | 记录指针  |
| -------- | ---------- | ---------- | --------- |
| 英文名   | ipStart    | ipEnd      | recordPtr |
| 长度     | 2          | 2          | 4         |
| 数据类型 | short      | short      | int       |

- `起始IP地址`和`结束IP地址`为后2位转为short型

## 数据生成过程

1. 所使用的原始数据每条记录格式为：`起始IP地址|结束IP地址|国家|地区|省份|城市|ISP`
2. 对每条记录通过`|`分隔
   1. 提取区域信息`国家|省份|城市|ISP`，放到`记录区Set`(TreeSet)中
   2. 把`起始IP地址`、`结束IP地址`、`区域信息`，放到`索引区List`(ArrayList)中
3. 确定`记录区指针`
4. 遍历`记录区Set`
   1. 计算`记录区指针`、`记录值长度`
   2. 以`记录值的hash`为key，`记录区指针`、`记录值`、`记录值长度`为value，放到`记录区Map`(LinkedHashMap)中
5. 确定`二级索引区指针`、`索引区指针`
6. 遍历`记录区Map`
   1. 写入`记录值长度`和`记录值`
7. 遍历`索引区List`
   1. 确定`索引区指针List`(ArrayList)
   2. 写入`起始IP地址`和`结束IP地址`
   3. 根据`记录值的hash`从`记录区Map`中找到`记录区指针`并写入
8. 根据`索引区List`、`索引区指针List`计算`二级索引`并写入
9. 写入`头部区`
10. 计算`CRC32校验和`并写入

## 查询过程

1. IP地址`字符串`转为`int`
2. 取`前2字节`，定位到`二级索引区`
3. 根据`索引区指针`定位到`索引区`的起始位置
4. 根据`索引区指针+4`定位到`索引区`的结束位置
5. 根据`起始位置`和`结束位置`对IP地址进行二分查找
6. 根据`记录区指针`定位到`记录区`
7. 读取`第一个字节`，获取记录值长度
8. 根据`记录值长度`获取记录值
9. 返回`记录值`

## 参考

- [ip2region](https://github.com/lionsoul2014/ip2region)
- [phonedata](https://github.com/xluohome/phonedata)
- [phone-number-geo](https://github.com/EeeMt/phone-number-geo)
