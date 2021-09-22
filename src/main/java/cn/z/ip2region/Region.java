package cn.z.ip2region;


import org.lionsoul.ip2region.DataBlock;

/**
 * <h1>区域</h1>
 *
 * <p>
 * createDate 2021/09/22 15:43:13
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class Region {

    /**
     * 国家
     */
    private String country = "";
    /**
     * 省份
     */
    private String province = "";
    /**
     * 城市
     */
    private String city = "";
    /**
     * 区域
     */
    private String area = "";
    /**
     * isp
     */
    private String isp = "";

    /**
     * 构造函数
     */
    public Region() {

    }

    /**
     * 构造函数
     *
     * @param dataBlock dataBlock
     * @see DataBlock
     */
    public Region(DataBlock dataBlock) {
        if (dataBlock != null && dataBlock.getRegion() != null) {
            // 国家|区域|省份|城市|ISP
            String[] s = dataBlock.getRegion().split("\\|");
            if (s.length == 5) {
                if (!"0".equals(s[0])) {
                    country = s[0];
                }
                if (!"0".equals(s[1])) {
                    area = s[1];
                }
                if (!"0".equals(s[2])) {
                    province = s[2];
                }
                if (!"0".equals(s[3])) {
                    city = s[3];
                }
                if (!"0".equals(s[4])) {
                    isp = s[4];
                }
            }
        }
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    @Override
    public String toString() {
        return "Region{" + "country='" + country + '\'' + ", province='" + province + '\'' + ", city='" + city + '\'' + ", area='" + area + '\'' + ", isp='" + isp + '\'' + '}';
    }
}
