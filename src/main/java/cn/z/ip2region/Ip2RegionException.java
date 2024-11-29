package cn.z.ip2region;

/**
 * <h1>Ip2Region异常</h1>
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
     * @param message 详细信息
     */
    public Ip2RegionException(String message) {
        super(message);
    }

    /**
     * Ip2Region异常
     *
     * @param message 详细信息
     * @param cause   原因
     */
    public Ip2RegionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Ip2Region异常
     *
     * @param cause 原因
     */
    public Ip2RegionException(Throwable cause) {
        super(cause);
    }

    /**
     * Ip2Region异常
     *
     * @param message            详细信息
     * @param cause              原因
     * @param enableSuppression  是否启用抑制
     * @param writableStackTrace 堆栈跟踪是否为可写的
     */
    protected Ip2RegionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
