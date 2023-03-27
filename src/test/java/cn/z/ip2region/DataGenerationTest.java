package cn.z.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h1>数据文件生成测试</h1>
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
class DataGenerationTest {

    final String txtPath = "E:/ip.merge.txt";
    final String dbPath = "E:/ip2region.db";
    final String zdbPath = "E:/ip2region.zdb";
    final int version = 20221207;

    /**
     * 数据文件生成
     */
    // @Test
    void test00DataGeneration() throws Exception {
        test01Txt2Db();
        test02Compress();
    }

    /**
     * txt文件转db文件
     */
    // @Test
    void test01Txt2Db() throws Exception {
        log.info("---------- txt文件转db文件 ---------- 开始");
        // 头部区 版本号 指针
        final int headerVersionPtr = 4;
        // 头部区 记录区指针 值
        final int headerRecordAreaPtrValue = 16;
        // 头部区 二级索引区指针 值
        int headerVector2AreaPtrValue;
        // 二级索引 个数
        final int vector2Size = 256 * 256 + 1;

        // 记录区Set
        Set<String> recordSet = new TreeSet<>((o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1, o2));
        // 记录区Map<记录值hash,Record>
        Map<Integer, Record> recordMap = new LinkedHashMap<>();
        // 索引区List[{起始IP地址,结束IP地址,国家|省份|城市|ISP}]
        List<String[]> vectorList = new ArrayList<>();
        // 索引区指针List<索引指针>
        List<Integer> vectorPtrMap = new ArrayList<>();

        /* 读取文件 */
        BufferedReader bufferedReader = new BufferedReader(new FileReader(txtPath));
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            // 起始IP地址|结束IP地址|国家|地区|省份|城市|ISP
            String[] s = line.split("\\|");
            String region = ("0".equals(s[2]) ? "" : s[2]) + "|" // 国家
                    + ("0".equals(s[4]) ? "" : s[4]) + "|" // 省份
                    + ("0".equals(s[5]) ? "" : s[5]) + "|" // 城市
                    + ("0".equals(s[6]) ? "" : s[6]); // ISP
            recordSet.add(region);
            vectorList.add(new String[]{s[0], s[1], region});
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        log.info("记录区数据 {} 条", recordSet.size());
        log.info("索引区数据 {} 条", vectorList.size());

        /* 计算文件大小 */
        // 头部区
        int size = headerRecordAreaPtrValue;
        // 记录区
        for (String s : recordSet) {
            // java的String为UTF16LE是变长4字节，而UTF8是变长3字节
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            if (length > 256) {
                throw new Exception("记录值`" + s + "`为" + length + "字节，超出最大限制255字节！");
            }
            recordMap.put(s.hashCode(), new Record(size, bytes, length));
            size += (length + 1);
        }
        // 头部区 二级索引区指针 值
        headerVector2AreaPtrValue = size;
        // 二级索引区
        size += vector2Size * 4;
        // 索引区
        size += vectorList.size() * 8;
        log.info("文件容量 {} 字节", size);

        /* 创建二进制文件 */
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);

        // 记录区
        buffer.position(headerRecordAreaPtrValue);
        for (Record r : recordMap.values()) {
            buffer.put((byte) r.byteLength);
            buffer.put(r.getBytes());
        }

        // 索引区
        buffer.position(headerVector2AreaPtrValue + vector2Size * 4);
        for (String[] s : vectorList) {
            vectorPtrMap.add(buffer.position());
            // 起始IP地址后2段、结束IP地址后2段
            buffer.put(last2SegmentsIp2Bytes(s[0], s[1]));
            // 记录指针
            buffer.putInt(recordMap.get(s[2].hashCode()).getOffset());
        }

        // 二级索引区
        buffer.position(headerVector2AreaPtrValue);
        // 第一条
        int previousValue = 0;
        {
            int value = top2SegmentsIp2Int(vectorList.get(1)[0]);
            for (int j = 0; j < value - previousValue; j++) {
                buffer.putInt(vectorPtrMap.get(0));
            }
            previousValue = value;
        }
        for (int i = 2; i < vectorList.size(); i++) {
            int value = top2SegmentsIp2Int(vectorList.get(i)[0]);
            for (int j = 0; j < value - previousValue; j++) {
                buffer.putInt(vectorPtrMap.get(i - 1));
            }
            previousValue = value;
        }
        // 倒数第二条
        {
            for (int j = 0; j < 0xFFFF - previousValue; j++) {
                buffer.putInt(vectorPtrMap.get(vectorPtrMap.size() - 1));
            }
        }
        // 最后一条
        buffer.putInt(buffer.capacity());

        // 头部区
        // 版本号
        buffer.position(headerVersionPtr);
        buffer.putInt(version);
        // 记录区指针
        buffer.putInt(headerRecordAreaPtrValue);
        // 二级索引区指针
        buffer.putInt(headerVector2AreaPtrValue);
        // CRC32校验和
        buffer.position(4);
        CRC32 crc32 = new CRC32();
        crc32.update(buffer);
        buffer.position(0);
        buffer.putInt((int) crc32.getValue());

        /* 导出文件 */
        FileOutputStream fileOutputStream = new FileOutputStream(dbPath);
        fileOutputStream.write(buffer.array());
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("写入文件完成");
        log.info("---------- txt文件转db文件 ---------- 结束");
    }

    /**
     * IP地址后2段转byte[]
     */
    static byte[] last2SegmentsIp2Bytes(String ip, String ip2) {
        String[] s = ip.split("\\.");
        String[] s2 = ip2.split("\\.");
        return new byte[]{(byte) Integer.parseInt(s[3]), (byte) Integer.parseInt(s[2]),
                (byte) Integer.parseInt(s2[3]), (byte) Integer.parseInt(s2[2])};
    }

    /**
     * IP地址前2段转int
     */
    static int top2SegmentsIp2Int(String ip) {
        String[] s = ip.split("\\.");
        return Integer.parseInt(s[0]) * 256 + Integer.parseInt(s[1]);
    }

    /**
     * 记录
     */
    static class Record {
        /**
         * 偏移
         */
        private int offset;
        /**
         * byte[]
         */
        private byte[] bytes;
        /**
         * 长度
         */
        private int byteLength;

        public Record() {
        }

        public Record(int offset, byte[] bytes, int byteLength) {
            this.offset = offset;
            this.bytes = bytes;
            this.byteLength = byteLength;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public int getByteLength() {
            return byteLength;
        }

        public void setByteLength(int byteLength) {
            this.byteLength = byteLength;
        }

        @Override
        public String toString() {
            return "Record{" + "offset=" + offset + ", bytes=" + new String(bytes) + ", byteLength=" + byteLength + '}';
        }
    }

    /**
     * 压缩
     */
    // @Test
    void test02Compress() throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zdbPath));
        zipOutputStream.putNextEntry(new ZipEntry(new File(dbPath).getName()));
        FileInputStream fileInputStream = new FileInputStream(dbPath);
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = fileInputStream.read(buffer))) {
            zipOutputStream.write(buffer, 0, n);
        }
        zipOutputStream.flush();
        fileInputStream.close();
        zipOutputStream.closeEntry();
        zipOutputStream.close();
    }

}
