package cn.z.ip2region;

import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.lionsoul.ip2region.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * <h1>IP地址转区域</h1>
 *
 * <p>
 * 本工具类使用org.lionsoul:ip2region工具类作为基础，简化了操作，把方法改写成了静态类，添加了区域实体
 * </p>
 *
 * <p>
 * createDate 2021/09/22 15:43:30
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class Ip2Region {

    /**
     * 日志实例
     */
    private final static Logger log = LoggerFactory.getLogger(Ip2Region.class);
    /**
     * ip2RegionSearcher实例
     */
    private static volatile DbSearcher ip2RegionSearcher = null;

    /**
     * 初始化DbSearcher实例通过File
     *
     * @see #init(byte[])
     * @see Files#readAllBytes(Path)
     * @see File#toPath()
     */
    public static void initByFile(String path) {
        if (ip2RegionSearcher == null) {
            try {
                log.info("初始化，文件路径为" + path);
                init(Files.readAllBytes(new File(path).toPath()));
            } catch (IOException e) {
                log.error("文件读取异常", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化DbSearcher实例通过URL<br>
     * 可以使用以下地址(请不要直接使用gitee链接,下载文件大于1MB需要登录才能下载;也不要使用github链接,经常不能访问)<br>
     * https://cdn.jsdelivr.net/gh/lionsoul2014/ip2region/data/ip2region.db<br>
     * 实际路径为<br>
     * https://gitee.com/lionsoul/ip2region/blob/master/data/ip2region.db
     *
     * @see #init(byte[])
     * @see #inputStream2bytes(InputStream)
     * @see URL
     */
    public static void initByUrl(String path) {
        if (ip2RegionSearcher == null) {
            try {
                log.info("初始化，URL路径为" + path);
                init(inputStream2bytes(new URL(path).openConnection().getInputStream()));
            } catch (IOException e) {
                log.error("文件读取异常", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化DbSearcher实例
     *
     * @see DbSearcher#DbSearcher(DbConfig, byte[])
     * @see DbConfig#DbConfig()
     * @see Files#readAllBytes(Path)
     * @see File#toPath()
     */
    public static void init(byte[] bytes) {
        if (ip2RegionSearcher == null) {
            synchronized (Ip2Region.class) {
                if (ip2RegionSearcher == null) {
                    try {
                        if (bytes == null || bytes.length == 0) {
                            log.error("数据文件为空！");
                            return;
                        }
                        ip2RegionSearcher = new DbSearcher(new DbConfig(), bytes);
                    } catch (Exception e) {
                        log.error("DbSearcher实例初始化异常", e);
                    }
                } else {
                    log.warn("已经初始化过了，不可重复初始化！");
                }
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 解析IP的区域
     *
     * @param ip IP地址(String)
     * @see #parse(long)
     * @see Region
     * @see Util#ip2long(String)
     */
    public static Region parse(String ip) {
        return parse(Util.ip2long(ip));
    }

    /**
     * 解析IP的区域
     *
     * @param ip IP地址(long)
     * @see Region
     * @see DataBlock
     * @see DbSearcher#memorySearch(long)
     */
    public static Region parse(long ip) {
        DataBlock block = null;
        try {
            block = ip2RegionSearcher.memorySearch(ip);
        } catch (Exception e) {
            log.error("memorySearch查询异常", e);
        }
        return new Region(block);
    }

    /**
     * inputStream转byte[]
     */
    public static byte[] inputStream2bytes(InputStream inputStream) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        try {
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            log.error("读取输入流异常", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.error("关闭输入流异常", e);
            }
        }
        return output.toByteArray();
    }

}
