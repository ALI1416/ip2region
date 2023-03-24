package cn.z.ip2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <h1>IP地址转区域</h1>
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
    private static final Logger log = LoggerFactory.getLogger(Ip2Region.class);
    /**
     * 标记没有实例化
     */
    private static volatile boolean notInstantiated = true;
    /**
     * 数据
     */
    private static ByteBuffer buffer;
    /**
     * 索引偏移量
     */
    private static int indicesOffset;
    /**
     * 索引偏移量最大值
     */
    private static int indicesOffsetMax;

    private Ip2Region() {
    }

    /**
     * 是否已经初始化
     *
     * @since 3.0.0
     */
    public static boolean initialized() {
        return !notInstantiated;
    }

    /**
     * 初始化实例通过File
     *
     * @param path 文件路径
     */
    public static void initByFile(String path) {
        if (notInstantiated) {
            try {
                log.info("初始化，文件路径为：{}", path);
                init(new FileInputStream(path));
            } catch (Exception e) {
                log.error("初始化文件异常！", e);
                throw new Ip2RegionException("初始化文件异常！");
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例通过URL<br>
     * 可以用：<code>https://cdn.jsdelivr.net/gh/ali1416/ip2region@master/data/ip2region.zxdb</code>
     *
     * @param url URL
     */
    public static void initByUrl(String url) {
        if (notInstantiated) {
            try {
                log.info("初始化，URL路径为：{}", url);
                init(new URL(url).openConnection().getInputStream());
            } catch (Exception e) {
                log.error("初始化URL异常！", e);
                throw new Ip2RegionException("初始化URL异常！");
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例
     *
     * @param inputStream 压缩的zxdb输入流
     */
    public static void init(InputStream inputStream) {
        if (notInstantiated) {
            synchronized (Ip2Region.class) {
                if (notInstantiated) {
                    try {
                        if (inputStream == null) {
                            throw new Ip2RegionException("数据文件为空！");
                        }
                        // 解压并提取文件
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            throw new Ip2RegionException("数据文件异常！");
                        }
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2bytes(zipInputStream)) //
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        // log.info("数据加载成功，版本号为：{}", version);
                        notInstantiated = false;
                    } catch (Exception e) {
                        log.error("初始化异常！", e);
                        throw new Ip2RegionException("初始化异常！");
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
     * @return Region
     */
    public static Region parse(String ip) {
        return parse(ip2long(ip));
    }

    /**
     * 解析IP的区域
     *
     * @param ip IP地址(long)
     * @return Region
     */
    public static Region parse(long ip) {
        if (notInstantiated) {
            log.error("未初始化！");
            return null;
        }
        return null;
    }

    /**
     * ip2long
     */
    public static long ip2long(String ip) {
        String[] s = ip.split("\\.");
        if (s.length != 4) {
            throw new Ip2RegionException("IP地址" + ip + "不合法！");
        }
        long address = 0;
        for (int i = 0; i < 4; i++) {
            long v = Long.parseLong(s[i]);
            if (v < 0 || v > 255) {
                throw new Ip2RegionException("IP地址" + ip + "不合法！");
            }
            address |= (v << 8 * (4 - i));
        }
        return address;
    }

    /**
     * long2ip
     */
    public static String long2ip(long ip) {
        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip) & 0xFF);
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
        } catch (Exception e) {
            log.error("转换异常！", e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                log.error("关闭异常！", e);
            }
        }
        return output.toByteArray();
    }

}
