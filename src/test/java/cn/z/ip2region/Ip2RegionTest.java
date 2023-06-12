package cn.z.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;

/**
 * <h1>IP地址转区域测试</h1>
 *
 * <p>
 * createDate 2023/03/24 10:31:35
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class Ip2RegionTest {

    final String url = "https://www.404z.cn/files/ip2region/v3.0.0/data/ip2region.zdb";
    final String zdbPath = "E:/ip2region.zdb";
    final String txtPath = "E:/ip.merge.txt";
    final String errorPath = "E:/ip2region.error.txt";
    final String ip = "123.132.0.0";

    /**
     * 通过url初始化
     */
    @Test
    void test00InitByUrl() {
        log.info("是否已经初始化：{}", Ip2Region.initialized());
        Ip2Region.initByUrl(url);
        log.info(String.valueOf(Ip2Region.initialized()));
        log.info("是否已经初始化：{}", Ip2Region.initialized());
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 是否已经初始化：false
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，URL路径为：https://www.404z.cn/files/ip2region/v3.0.0/data/ip2region.zdb
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 是否已经初始化：true
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 通过文件初始化
     */
    // @Test
    void test01InitByFile() {
        Ip2Region.initByFile(zdbPath);
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zdb
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 通过inputStream初始化
     */
    // @Test
    void test02InitByInputStream() {
        try {
            Ip2Region.init(new FileInputStream(zdbPath));
        } catch (Exception ignore) {
        }
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 初始化多次
     */
    // @Test
    void test03InitMore() {
        Ip2Region.initByFile(zdbPath);
        Ip2Region.initByFile(zdbPath);
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zdb
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // [main] WARN cn.z.ip2region.Ip2Region - 已经初始化过了，不可重复初始化！
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Ip2Region.initByFile("A:/1.txt");
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // [main]  INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：A:/1.txt
        // [main] ERROR cn.z.ip2region.Ip2Region - 初始化文件异常！
        // java.io.FileNotFoundException: A:/1.txt (系统找不到指定的文件。)
        // cn.z.ip2region.Ip2RegionException: 初始化文件异常！
    }

    /**
     * 数据错误
     */
    // @Test
    void test05Error() {
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
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zdb
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // cn.z.ip2region.Ip2RegionException: IP地址 0.0.0.300 不合法！
        // cn.z.ip2region.Ip2RegionException: IP地址 -1 不合法！
    }

    /**
     * 性能测试
     */
    // @Test
    void test06PerformanceTest() {
        Ip2Region.initByFile(zdbPath);
        log.info(String.valueOf(Ip2Region.parse(ip)));
        long startTime = System.currentTimeMillis();
        for (long i = 0; i < 0x100000000L; i++) {
            Ip2Region.parse(i);
        }
        long endTime = System.currentTimeMillis();
        log.info("查询 {} 条数据，用时 {} 毫秒", 0x100000000L, endTime - startTime);
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zdb
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 查询 4294967296 条数据，用时 562161 毫秒
    }

    /**
     * 完整性测试
     */
    // @Test
    void test07IntegrityTest() throws Exception {
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
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zdb
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：20221207，校验码为：68EDD841
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 解析记录`0.0.0.0|0.255.255.255|0|0|0|内网IP|内网IP`，共 16777216 条
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 解析记录`1.0.0.0|1.0.0.255|澳大利亚|0|0|0|0`，共 256 条
        // ...
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 解析记录`223.255.255.0|223.255.255.255|澳大利亚|0|0|0|0`，共 256 条
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 解析记录`224.0.0.0|255.255.255.255|0|0|0|内网IP|内网IP`，共 536870912 条
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 解析 4294967296 条数据，错误 0 条，用时 869132 毫秒
    }

}
