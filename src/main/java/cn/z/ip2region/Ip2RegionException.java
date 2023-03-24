package cn.z.ip2region;

/**
 * <h1>Ip2Region异常类</h1>
 *
 * <p>
 * createDate 2023/03/24 09:53:07
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 3.0.0
 **/
public class Ip2RegionException extends RuntimeException {

    /**
     * Ip2Region异常
     */
    public Ip2RegionException() {
        super();
    }

    /**
     * Ip2Region异常
     *
     * @param message 信息
     */
    public Ip2RegionException(String message) {
        super(message);
    }

}
