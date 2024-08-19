package cn.z.ip2region;


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
    private String country;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * ISP
     */
    private String isp;

    /**
     * 构造函数
     */
    public Region() {
    }

    /**
     * 构造函数
     *
     * @param region 区域字符串
     */
    public Region(String region) {
        // 国家|省份|城市|ISP
        String[] s = region.split("\\|", -1);
        if (s.length == 4) {
            this.country = s[0];
            this.province = s[1];
            this.city = s[2];
            this.isp = s[3];
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

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    @Override
    public String toString() {
        return "Region{" + "country='" + country + '\'' + ", province='" + province + '\'' + ", city='" + city + '\'' + ", isp='" + isp + '\'' + '}';
    }

}
