package cn.z.ip2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
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

    /**
     * 数据文件生成
     */
    // @Test
    void test00DataGeneration() throws Exception {
        String txtPath = "E:/ip.merge.txt";
        String dbPath = "E:/ip2region.db";
        String zdbPath = "E:/ip2region.zdb";
        txt2dat(txtPath, dbPath);
        compress(dbPath, zdbPath);
    }

    // @Test
    void c() throws FileNotFoundException {
        CRC32 crc32 = new CRC32();
        byte[] bytes = Ip2Region.inputStream2bytes(new FileInputStream("E:/ip2region.zip"));
        crc32.update(bytes, 0, bytes.length);
        long value = crc32.getValue();
        log.info(String.valueOf(value));
    }

    /**
     * txt文件转db文件
     */
    void txt2dat(String txtPath, String dbPath) throws Exception {
        // 记录区Set
        Set<String> recordSet = new TreeSet<>((o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1, o2));
        // 记录区Map<记录值hash,Record>
        Map<Integer, Record> recordMap = new LinkedHashMap<>();
        // 索引区List[{起始IP地址结束IP地址,国家|省份|城市|ISP}]
        List<String[]> vectorList = new ArrayList<>();
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
        /* 计算文件大小 */
        // 版本号4字节，索引偏移量4字节
        int size = 8;
        // 记录区
        for (String s : recordSet) {
            // java的String为UTF16LE是变长4字节，而UTF8是变长3字节
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            recordMap.put(s.hashCode(), new Record(size, bytes));
            // 每条记录以0x00结尾
            size += (bytes.length + 1);
        }
        // 记录获取索引偏移量
        int indicesOffset = size;
        // 索引区
        size += vectorList.size() * 9;
        /* 创建二进制文件 */
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        // 版本号
        // buffer.put(version.getBytes(StandardCharsets.UTF_8));
        // 索引偏移量
        buffer.putInt(indicesOffset);
        // 记录区
        for (Record r : recordMap.values()) {
            buffer.put(r.getBytes());
        }
        // 索引区
        // vectorList.forEach((tel, s) -> {
        //     // 手机号码前7位
        //     buffer.putInt(tel);
        //     // 偏移量
        //     buffer.putInt(recordMap.get(s[0].hashCode()).getOffset());
        // });
        /* 导出文件 */
        FileOutputStream fileOutputStream = new FileOutputStream(dbPath);
        fileOutputStream.write(buffer.array());
        fileOutputStream.flush();
        fileOutputStream.close();
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

        public Record() {
        }

        public Record(int offset, byte[] bytes) {
            this.offset = offset;
            this.bytes = bytes;
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

        @Override
        public String toString() {
            return "Record{" + "offset=" + offset + ", bytes=" + new String(bytes) + '}';
        }
    }

    /**
     * 压缩
     */
    void compress(String filePath, String zipPath) throws Exception {
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipPath));
        File file = new File(filePath);
        zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
        FileInputStream fileInputStream = new FileInputStream(filePath);
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
