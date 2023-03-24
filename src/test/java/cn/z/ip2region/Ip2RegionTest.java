package cn.z.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.FileInputStream;

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

    /**
     * 通过url初始化
     */
    // @Test
    void test00InitByUrl() {
        log.info(String.valueOf(Ip2Region.initialized()));
        Ip2Region.initByUrl("https://cdn.jsdelivr.net/gh/ali1416/ip2region@master/data/ip2region.zdat");
        log.info(String.valueOf(Ip2Region.initialized()));
        log.info(String.valueOf(Ip2Region.parse("123.132.0.0")));
        // [main] INFO cn.z.ip2region.Ip2Region - false
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，URL路径为：https://cdn.jsdelivr.net/gh/ali1416/ip2region@master/data/ip2region.zdat
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.ip2region.Ip2Region - true
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 通过文件初始化
     */
    // @Test
    void test01InitByFile() {
        Ip2Region.initByFile("E:/ip2region.zip");
        log.info(String.valueOf(Ip2Region.parse("18754710000")));
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zip
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 通过inputStream初始化
     */
    // @Test
    void test02InitByInputStream() {
        try {
            Ip2Region.init(new FileInputStream("E:/ip2region.zip"));
        } catch (Exception ignore) {
        }
        log.info(String.valueOf(Ip2Region.parse("18754710000")));
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 初始化多次
     */
    // @Test
    void test03InitMore() {
        Ip2Region.initByFile("E:/ip2region.zip");
        Ip2Region.initByFile("E:/ip2region.zip");
        log.info(String.valueOf(Ip2Region.parse("18754710000")));
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zip
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：2302
        // [main] WARN cn.z.ip2region.Ip2Region - 已经初始化过了，不可重复初始化！
        // [main] INFO cn.z.ip2region.Ip2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Ip2Region.initByFile("E:/ip2region");
        log.info(String.valueOf(Ip2Region.parse("18754710000")));
        // [main]  INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region
        // [main] ERROR cn.z.ip2region.Ip2Region - 初始化文件异常！
        // java.io.FileNotFoundException: E:\ip2region (系统找不到指定的文件。)
    }

    /**
     * 覆盖测试(2302版本497191条数据)
     */
    // @Test
    void test05CoverageTest() {
        Ip2Region.initByFile("E:/ip2region.zip");
        long startTime = System.currentTimeMillis();
        int count = 0;
        for (int i = 1300000; i < 2000000; i++) {
            if (Ip2Region.parse(String.valueOf(i)) != null) {
                count++;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("查询700000条数据，{}条有效数据，用时{}毫秒", count, endTime - startTime);
        // [main] INFO cn.z.ip2region.Ip2Region - 初始化，文件路径为：E:/ip2region.zip
        // [main] INFO cn.z.ip2region.Ip2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.ip2region.Ip2RegionTest - 查询700000条数据，497191条有效数据，用时322毫秒
    }

}
