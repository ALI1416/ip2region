package cn.z.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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

    final String txtPath = "D:/ip.merge.txt";
    final String dbPath = "D:/ip2region1.db";
    final String zdbPath = "D:/ip2region1.zdb";
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
        final int headerRecordAreaPtrValue = 20;
        // 头部区 二级索引区指针 值
        int headerVector2AreaPtrValue;
        // 头部区 一级索引区指针 值
        int headerVectorAreaPtrValue;
        // 二级索引 个数
        final int vector2Size = 256 * 256 + 1;

        // 记录区Set
        Set<String> recordSet = new TreeSet<>((o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1, o2));
        // 记录区Map<记录值hash,Record>
        Map<Integer, Record> recordMap = new LinkedHashMap<>();
        // 一级索引区List<Vector>
        List<Vector> vectorList = new ArrayList<>();

        /* 读取文件 */
        BufferedReader bufferedReader = new BufferedReader(new FileReader(txtPath));
        String line = bufferedReader.readLine();
        // 一级索引附加个数
        int vectorAddition = 0;
        while (line != null && !line.isEmpty()) {
            // 起始IP地址|结束IP地址|国家|地区|省份|城市|ISP
            String[] s = line.split("\\|");
            String record = ("0".equals(s[2]) ? "" : s[2]) + "|" // 国家
                    + ("0".equals(s[4]) ? "" : s[4]) + "|" // 省份
                    + ("0".equals(s[5]) ? "" : s[5]) + "|" // 城市
                    + ("0".equals(s[6]) ? "" : s[6]); // ISP
            recordSet.add(record);
            Vector vector = new Vector(s[0], s[1], record.hashCode());
            Vector vector2 = vector.separation();
            vectorList.add(vector);
            if (vector2 != null) {
                vectorAddition++;
                vectorList.add(vector2);
                log.info("拆分 {}", line);
            }
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        log.info("记录区数据 {} 条", recordSet.size());
        log.info("一级索引区数据 {} 条，其中附加 {} 条", vectorList.size(), vectorAddition);

        /* 计算文件大小 */
        // 头部区
        int size = headerRecordAreaPtrValue;
        // 记录区
        for (String record : recordSet) {
            // java的String为UTF16LE是变长4字节，而UTF8是变长3字节
            byte[] bytes = record.getBytes(StandardCharsets.UTF_8);
            int length = bytes.length;
            if (length > 256) {
                throw new Exception("记录值`" + record + "`为" + length + "字节，超出最大限制255字节！");
            }
            recordMap.put(record.hashCode(), new Record(size, bytes));
            size += (length + 1);
        }
        // 头部区 二级索引区指针 值
        headerVector2AreaPtrValue = size;
        // 头部区 一级索引区指针 值
        headerVectorAreaPtrValue = headerVector2AreaPtrValue + vector2Size * 4;
        // 二级索引区
        size += vector2Size * 4;
        // 一级索引区
        size += vectorList.size() * 8;
        log.info("文件容量 {} 字节", size);

        /* 创建二进制文件 */
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        // 记录区
        buffer.position(headerRecordAreaPtrValue);
        for (Record r : recordMap.values()) {
            // 记录值长度
            buffer.put((byte) r.getBytes().length);
            // 记录值
            buffer.put(r.getBytes());
        }

        // 一级索引区
        buffer.position(headerVectorAreaPtrValue);
        for (Vector vector : vectorList) {
            vector.setPrt(buffer.position());
            // IP地址后2段
            buffer.put(vector.getIpLastBytes());
            // 记录指针
            buffer.putInt(recordMap.get(vector.getRecordHash()).getPrt());
        }

        // 二级索引区
        buffer.position(headerVector2AreaPtrValue);
        for (Vector vector : vectorList) {
            // x.x.0.0|x.x.x.x
            if (vector.getIpStartLast() == 0) {
                // x.x.x.x|0.0.x.x - 0.0.x.x|x.x.x.x
                int count = vector.getIpEndFirst() - vector.getIpStartFirst() + 1;
                int prt = vector.getPrt();
                for (int i = 0; i < count; i++) {
                    buffer.putInt(prt);
                }
            }
        }
        // 附加一条
        buffer.putInt(buffer.capacity());

        // 头部区
        // 版本号
        buffer.position(headerVersionPtr);
        buffer.putInt(version);
        // 记录区指针
        buffer.putInt(headerRecordAreaPtrValue);
        // 二级索引区指针
        buffer.putInt(headerVector2AreaPtrValue);
        // 一级索引区指针
        buffer.putInt(headerVectorAreaPtrValue);
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
     * 压缩
     */
    // @Test
    void test02Compress() throws Exception {
        log.info("---------- 压缩 ---------- 开始");
        ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(Paths.get(zdbPath)));
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
        log.info("---------- 压缩 ---------- 结束");
    }

    /**
     * 记录
     */
    static class Record {
        /**
         * 指针
         */
        private int prt;
        /**
         * 记录值
         */
        private byte[] bytes;

        public Record() {
        }

        /**
         * 构造记录
         *
         * @param prt   指针
         * @param bytes 记录值
         */
        public Record(int prt, byte[] bytes) {
            this.prt = prt;
            this.bytes = bytes;
        }

        public int getPrt() {
            return prt;
        }

        public void setPrt(int prt) {
            this.prt = prt;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public String toString() {
            return "Record{" +
                    "prt=" + prt +
                    ", bytes=" + Arrays.toString(bytes) +
                    '}';
        }

    }

    /**
     * 一级索引
     */
    static class Vector {
        /**
         * 指针
         */
        private int prt;
        /**
         * 起始IP地址前2段 0.0.x.x|x.x.x.x
         */
        private int ipStartFirst;
        /**
         * 起始IP地址后2段 x.x.0.0|x.x.x.x
         */
        private int ipStartLast;
        /**
         * 结束IP地址前2段 x.x.x.x|0.0.x.x
         */
        private int ipEndFirst;
        /**
         * 结束IP地址后2段 x.x.x.x|x.x.0.0
         */
        private int ipEndLast;
        /**
         * 记录值hash
         */
        private int recordHash;

        public Vector() {
        }

        /**
         * 构造一级索引
         *
         * @param ipStart    起始IP
         * @param ipEnd      结束IP
         * @param recordHash 记录值hash
         */
        public Vector(String ipStart, String ipEnd, int recordHash) {
            String[] start = ipStart.split("\\.");
            this.ipStartFirst = (Integer.parseInt(start[0]) << 8) + Integer.parseInt(start[1]);
            this.ipStartLast = (Integer.parseInt(start[2]) << 8) + Integer.parseInt(start[3]);
            String[] end = ipEnd.split("\\.");
            this.ipEndFirst = (Integer.parseInt(end[0]) << 8) + Integer.parseInt(end[1]);
            this.ipEndLast = (Integer.parseInt(end[2]) << 8) + Integer.parseInt(end[3]);
            this.recordHash = recordHash;
        }

        /**
         * 拆分<br>
         * 例如<code>0.1.0.3|0.6.0.8</code>占了上一个二级索引块的一部分<br>
         * 会被拆成<code>0.1.0.3|0.1.255.255</code>和<code>0.2.0.0|0.6.0.8</code>
         *
         * @return 不需要拆分，则返回null，原对象不变<br>
         * 需要拆分，原对象会修改成前块，并返回后块
         */
        public Vector separation() {
            // 跨越二级索引块 并且 起始IP地址后2段不为0
            if (ipStartFirst != ipEndFirst && ipStartLast != 0) {
                // 后块
                Vector vector = new Vector();
                vector.setIpStartFirst(ipStartFirst + 1);
                vector.setIpEndFirst(ipEndFirst);
                vector.setIpStartLast(0);
                vector.setIpEndLast(ipEndLast);
                vector.setRecordHash(recordHash);
                // 前块
                ipEndFirst = ipStartFirst;
                ipEndLast = 0xFFFF;
                return vector;
            }
            return null;
        }

        /**
         * IP地址后2段转byte[]
         */
        public byte[] getIpLastBytes() {
            return new byte[]{(byte) ipStartLast, (byte) (ipStartLast >> 8), (byte) ipEndLast, (byte) (ipEndLast >> 8)};
        }

        public int getPrt() {
            return prt;
        }

        public void setPrt(int prt) {
            this.prt = prt;
        }

        public int getIpStartFirst() {
            return ipStartFirst;
        }

        public void setIpStartFirst(int ipStartFirst) {
            this.ipStartFirst = ipStartFirst;
        }

        public int getIpStartLast() {
            return ipStartLast;
        }

        public void setIpStartLast(int ipStartLast) {
            this.ipStartLast = ipStartLast;
        }

        public int getIpEndFirst() {
            return ipEndFirst;
        }

        public void setIpEndFirst(int ipEndFirst) {
            this.ipEndFirst = ipEndFirst;
        }

        public int getIpEndLast() {
            return ipEndLast;
        }

        public void setIpEndLast(int ipEndLast) {
            this.ipEndLast = ipEndLast;
        }

        public int getRecordHash() {
            return recordHash;
        }

        public void setRecordHash(int recordHash) {
            this.recordHash = recordHash;
        }

        @Override
        public String toString() {
            return "Vector{" +
                    "prt=" + prt +
                    ", ipStartFirst=" + ipStartFirst +
                    ", ipStartLast=" + ipStartLast +
                    ", ipEndFirst=" + ipEndFirst +
                    ", ipEndLast=" + ipEndLast +
                    ", recordHash=" + recordHash +
                    '}';
        }

    }

}
