package cn.z.ip2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
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
     * 标记没有实例化
     */
    private static volatile boolean notInstantiated = true;
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
     * 可以用：<code>https://cdn.jsdelivr.net/gh/ali1416/ip2region@master/data/ip2region.zdb</code>
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
     * @param inputStream 压缩的zdb输入流
     */
    public static void init(InputStream inputStream) {
        if (notInstantiated) {
            synchronized (Ip2Region.class) {
                if (notInstantiated) {
                    try {
                        if (inputStream == null) {
                            throw new Ip2RegionException("数据文件为空！");
                        }
                        // 解压
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            throw new Ip2RegionException("数据文件异常！");
                        }
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2bytes(zipInputStream)) //
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        int crc32OriginValue = buffer.getInt();
                        CRC32 crc32 = new CRC32();
                        crc32.update(buffer);
                        if (crc32OriginValue != (int) crc32.getValue()) {
                            throw new Ip2RegionException("数据文件校验错误！");
                        }
                        buffer.position(4);
                        int version = buffer.getInt();
                        buffer.position(buffer.position() + 4);
                        vector2AreaPtr = buffer.getInt();
                        vectorAreaPtr = buffer.getInt();
                        log.info("数据加载成功，版本号为：{}", version);
                        notInstantiated = false;
                    } catch (Exception e) {
                        log.error("初始化异常！", e);
                        throw new Ip2RegionException("初始化异常！");
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                                log.error("关闭异常！", e);
                            }
                        }
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
        return innerParse(ip2long(ip));
    }

    /**
     * 解析IP的区域
     *
     * @param ip IP地址(long)
     * @return Region
     */
    public static Region parse(long ip) {
        if (ip < 0 || ip > 0xFFFFFFFFL) {
            throw new Ip2RegionException("IP地址" + ip + "不合法！");
        }
        return innerParse(ip);
    }

    /**
     * 解析IP的区域
     *
     * @param ip IP地址(long)
     * @return Region
     */
    private static Region innerParse(long ip) {
        if (notInstantiated) {
            log.error("未初始化！");
            return null;
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
            address |= (v << 8 * (3 - i));
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
