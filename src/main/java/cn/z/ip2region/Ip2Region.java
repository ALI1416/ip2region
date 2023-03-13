package cn.z.ip2region;

import org.lionsoul.ip2region.xdb.Searcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

/**
 * <h1>IP地址转区域</h1>
 *
 * <p>
 * 本工具类使用<code>org.lionsoul:ip2region</code>工具类作为基础，简化了操作，把方法改写成了静态类，并添加了区域实体
 * </p>
 *
 * <p>
 * 注意：本工具类所使用的数据对原数据进行了修改，修改方法和修改后的数据请见：https://gitee.com/ALI1416/ip2region-test
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
    private static final Logger log = LoggerFactory.getLogger(Ip2Region.class);
    /**
     * Searcher实例
     */
    private static volatile Searcher searcher = null;

    /**
     * 初始化Searcher实例通过File
     *
     * @param path 文件路径
     * @see FileInputStream
     */
    public static void initByFile(String path) {
        if (searcher == null) {
            try {
                log.info("初始化，文件路径为：" + path);
                init(new FileInputStream(path));
            } catch (Exception e) {
                log.error("文件异常！", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化Searcher实例通过URL<br>
     * 可以用：https://cdn.jsdelivr.net/gh/ali1416/ip2region-test/data/ip2region.zxdb
     *
     * @param url URL
     * @see URL
     */
    public static void initByUrl(String url) {
        if (searcher == null) {
            try {
                log.info("初始化，URL路径为：" + url);
                init(new URL(url).openConnection().getInputStream());
            } catch (Exception e) {
                log.error("URL异常！", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化DbSearcher实例
     *
     * @param inputStream 压缩的xdb输入流
     * @see Searcher#newWithBuffer(byte[])
     */
    public static void init(InputStream inputStream) {
        if (searcher == null) {
            synchronized (Ip2Region.class) {
                if (searcher == null) {
                    try {
                        if (inputStream == null) {
                            log.error("数据为空！");
                            return;
                        }
                        // 解压并提取文件
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        zipInputStream.getNextEntry();
                        searcher = Searcher.newWithBuffer(inputStream2bytes(zipInputStream));
                        log.info("加载数据成功！");
                    } catch (Exception e) {
                        log.error("初始化异常！", e);
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
     * @see Searcher#search(String)
     */
    public static Region parse(String ip) {
        if (searcher == null) {
            log.error("未初始化！");
        } else {
            try {
                return new Region(searcher.search(ip));
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    /**
     * 解析IP的区域
     *
     * @param ip IP地址(long)
     * @return Region
     * @see Searcher#search(String)
     */
    public static Region parse(long ip) {
        if (searcher == null) {
            log.error("未初始化！");
        } else {
            try {
                return new Region(searcher.search(ip));
            } catch (Exception ignore) {
            }
        }
        return null;
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
        } catch (Exception ignore) {
        } finally {
            try {
                inputStream.close();
            } catch (Exception ignore) {
            }
        }
        return output.toByteArray();
    }

}
