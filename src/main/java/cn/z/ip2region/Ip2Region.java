package cn.z.ip2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.CRC32;
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
     * 已初始化
     */
    private static volatile boolean isInit = false;
    /**
     * 数据
     */
    private static ByteBuffer buffer;
    /**
     * 二级索引区指针
     */
    private static int vector2AreaPtr;
    /**
     * 索引区指针
     */
    private static int vectorAreaPtr;

    private Ip2Region() {
    }

    /**
     * 是否已经初始化
     *
     * @return 是否已经初始化
     * @since 3.0.0
     */
    public static boolean initialized() {
        return isInit;
    }

    /**
     * 通过URL初始化实例<br>
     * 例如：<code>https://www.404z.cn/files/ip2region/v3.0.0/data/ip2region.zdb</code>
     *
     * @param url URL
     */
    public static void initByUrl(String url) {
        if (!isInit) {
            log.info("IP地址转区域初始化：URL路径URL_PATH {}", url);
            try (InputStream inputStream = new URI(url).toURL().openConnection().getInputStream()) {
                init(inputStream);
            } catch (Exception e) {
                throw new Ip2RegionException("初始化URL异常！", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例通过File
     *
     * @param path 文件路径
     */
    public static void initByFile(String path) {
        if (!isInit) {
            log.info("IP地址转区域初始化：文件路径LOCAL_PATH {}", path);
            try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
                init(inputStream);
            } catch (Exception e) {
                throw new Ip2RegionException("初始化文件异常！", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例
     *
     * @param inputStream 压缩的zdb输入流
     */
    public static void init(InputStream inputStream) {
        if (!isInit) {
            synchronized (Ip2Region.class) {
                if (!isInit) {
                    if (inputStream == null) {
                        throw new Ip2RegionException("数据文件为空！");
                    }
                    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                        // 解压
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            throw new Ip2RegionException("数据文件为空！");
                        }
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2Bytes(zipInputStream))
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        int crc32OriginValue = buffer.getInt();
                        CRC32 crc32 = new CRC32();
                        crc32.update(buffer);
                        if (crc32OriginValue != (int) crc32.getValue()) {
                            throw new Ip2RegionException("数据文件校验错误！");
                        }
                        buffer.position(4);
                        int version = buffer.getInt();
                        buffer.position(12);
                        vector2AreaPtr = buffer.getInt();
                        vectorAreaPtr = buffer.getInt();
                        log.info("数据加载成功：版本号VERSION {} ，校验码CRC32 {}", version,
                                String.format("%08X", crc32OriginValue));
                        isInit = true;
                    } catch (Exception e) {
                        throw new Ip2RegionException("初始化异常！", e);
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
     * 解析IP地址的区域
     *
     * @param ip IP地址
     * @return Region
     */
    public static Region parse(String ip) {
        return innerParse(ip2long(ip));
    }

    /**
     * 解析IP地址的区域
     *
     * @param ip long型IP地址
     * @return Region
     */
    public static Region parse(long ip) {
        if (ip < 0 || ip > 0xFFFFFFFFL) {
            throw new Ip2RegionException("long型IP地址 " + ip + " 不合法！");
        }
        return innerParse(ip);
    }

    /**
     * 解析IP地址的区域
     *
     * @param ip long型IP地址
     * @return Region
     */
    private static Region innerParse(long ip) {
        if (!isInit) {
            throw new Ip2RegionException("未初始化！");
        }

        // 二级索引区
        buffer.position(vector2AreaPtr + (((int) (ip >>> 16)) << 2));
        int left = buffer.getInt();
        int right = buffer.getInt();

        // 索引区
        if (left == right || left == right - 8) {
            buffer.position(left + 4);
        } else {
            right -= 8;
            // 二分查找
            int ipSegments = (int) ip & 0xFFFF;
            while (left <= right) {
                int mid = align((left + right) / 2);
                // 查找是否匹配到
                buffer.position(mid);
                int startAndEnd = buffer.getInt();
                int ipSegmentsStart = startAndEnd & 0xFFFF;
                int ipSegmentsEnd = startAndEnd >>> 16;
                if (ipSegments < ipSegmentsStart) {
                    right = mid - 8;
                } else if (ipSegments > ipSegmentsEnd) {
                    left = mid + 8;
                } else {
                    break;
                }
            }
        }

        // 记录区
        buffer.position(buffer.getInt());
        byte[] recordValue = new byte[buffer.get() & 0xFF];
        buffer.get(recordValue);
        return new Region(new String(recordValue, StandardCharsets.UTF_8));
    }

    /**
     * 字节对齐
     *
     * @param pos 位置
     * @return 对齐后的位置
     */
    private static int align(int pos) {
        int remain = (pos - vectorAreaPtr) % 8;
        if (pos - vectorAreaPtr < 8) {
            return pos - remain;
        } else if (remain != 0) {
            return pos + 8 - remain;
        } else {
            return pos;
        }
    }

    /**
     * IP地址转long
     *
     * @param ip IP地址
     * @return long型IP地址
     */
    public static long ip2long(String ip) {
        if (ip == null || ip.isEmpty()) {
            throw new Ip2RegionException("IP地址不能为空！");
        }
        String[] s = ip.split("\\.");
        if (s.length != 4) {
            throw new Ip2RegionException("IP地址 " + ip + " 不合法！");
        }
        long address = 0;
        for (int i = 0; i < 4; i++) {
            long v = Long.parseLong(s[i]);
            if (v < 0 || v > 255) {
                throw new Ip2RegionException("IP地址 " + ip + " 不合法！");
            }
            address |= (v << 8 * (3 - i));
        }
        return address;
    }

    /**
     * long转IP地址
     *
     * @param ip long型IP地址
     * @return IP地址
     */
    public static String long2ip(long ip) {
        if (ip < 0 || ip > 0xFFFFFFFFL) {
            throw new Ip2RegionException("long型IP地址 " + ip + " 不合法！");
        }
        return ((ip >> 24) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip) & 0xFF);
    }

    /**
     * 是合法的IP地址
     *
     * @param ip IP地址
     * @return 是否合法
     * @since 3.1.2
     */
    public static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        String[] s = ip.split("\\.");
        if (s.length != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            int v = Integer.parseInt(s[i]);
            if (v < 0 || v > 255) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是合法的IP地址
     *
     * @param ip long型IP地址
     * @return 是否合法
     * @since 3.1.2
     */
    public static boolean isValidIp(long ip) {
        return ip >= 0 && ip <= 0xFFFFFFFFL;
    }

    /**
     * InputStream转byte[]
     *
     * @param inputStream InputStream
     * @return byte[]
     */
    public static byte[] inputStream2Bytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, n);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

}
