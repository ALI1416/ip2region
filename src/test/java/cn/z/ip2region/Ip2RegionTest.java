package cn.z.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.nio.file.Files;
import java.nio.file.Paths;

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
    final String ip = "123.132.0.0";

    /**
     * 通过url初始化
     */
    @Test
    void test00InitByUrl() {
        log.info("是否已经初始化：{}", Ip2Region.initialized());
        Ip2Region.initByUrl(url);
        log.info("是否已经初始化：{}", Ip2Region.initialized());
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // INFO cn.z.ip2region.Ip2RegionTest - 是否已经初始化：false
        // INFO cn.z.ip2region.Ip2Region - IP地址转区域初始化：URL路径URL_PATH https://www.404z.cn/files/ip2region/v3.0.0/data/ip2region.zdb
        // INFO cn.z.ip2region.Ip2Region - 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
        // INFO cn.z.ip2region.Ip2RegionTest - 是否已经初始化：true
        // INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 通过文件初始化
     */
    // @Test
    void test01InitByFile() {
        Ip2Region.initByFile(zdbPath);
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // INFO cn.z.ip2region.Ip2Region - IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
        // INFO cn.z.ip2region.Ip2Region - 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
        // INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 通过inputStream初始化
     */
    // @Test
    void test02InitByInputStream() throws Exception {
        Ip2Region.init(Files.newInputStream(Paths.get(zdbPath)));
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // INFO cn.z.ip2region.Ip2Region - 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
        // INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 初始化多次
     */
    @Test
    void test03InitMore() {
        Ip2Region.initByFile(zdbPath);
        Ip2Region.initByFile(zdbPath);
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
        // INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
        // WARN cn.z.ip2region.Ip2Region -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.ip2region.Ip2RegionTest -- Region{country='中国', province='山东省', city='济宁市', isp='联通'}
    }

    /**
     * 初始化异常
     */
    @Test
    void test04InitException() {
        try {
            Ip2Region.initByFile("A:/1.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(String.valueOf(Ip2Region.parse(ip)));
        // INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH A:/1.txt
        // ERROR cn.z.ip2region.Ip2Region -- 初始化文件异常！
        // java.io.FileNotFoundException: A:\1.txt (系统找不到指定的路径。)
        // cn.z.ip2region.Ip2RegionException: 未初始化！
    }

    /**
     * 数据错误
     */
    @Test
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
        // INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
        // INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
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
        // INFO cn.z.ip2region.Ip2Region -- IP地址转区域初始化：文件路径LOCAL_PATH E:/ip2region.zdb
        // INFO cn.z.ip2region.Ip2Region -- 数据加载成功：版本号VERSION 20221207 ，校验码CRC32 68EDD841
        // INFO cn.z.ip2region.Ip2RegionTest - Region{country='中国', province='山东省', city='济宁市', isp='联通'}
        // INFO cn.z.ip2region.Ip2RegionTest - 查询 4294967296 条数据，用时 562161 毫秒
    }

    /**
     * 工具测试
     */
    @Test
    void test07UtilTest() {
        String ip = "123.45.67.89";
        log.info("IP地址 {} 是否合法 {}", ip, Ip2Region.isValidIp(ip));
        long ip2 = 123456789L;
        log.info("long型IP地址 {} 是否合法 {}", ip, Ip2Region.isValidIp(ip2));
        // INFO cn.z.ip2region.Ip2RegionTest -- IP地址 123.45.67.89 是否合法 true
        // INFO cn.z.ip2region.Ip2RegionTest -- long型IP地址 123.45.67.89 是否合法 true
    }

}
