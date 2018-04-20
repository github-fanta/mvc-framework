package proxy;

/**
 * 代理接口
 * Created by liq on 2018/4/11.
 */
public interface Proxy {
    /**
     * 执行链式代理
     */
    Object doProxy(ProxyChain proxyChain) throws Throwable;
}
