package proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 切面代理
 * Created by liq on 2018/4/11.
 */
public abstract class AspectProxy implements Proxy {
    /**
     *  the logger should be static and final. Also preferably private. There needs be only one logger instance per class and also unless you are going to change the log preference dynamically, it is better to make it final.
     *  Logger are thread safe and you do not have to worry about threading.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AspectProxy.class);

    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {

        Object result = null;
        Class<?> cls = proxyChain.getTargetClass();
        Method method = proxyChain.getTargetMethod();
        Object[] params = proxyChain.getMethodParams();

        begin();
        try{
            //是否开启拦截
            if (intercepter(cls, method, params)){
                before(cls, method, params);
                result = proxyChain.doProxyChain();
                after(cls, method, params, result);
            }else{
                //不拦截
                result = proxyChain.doProxyChain();
            }
        }catch (Exception e){
            LOGGER.error("proxy failure", e);
            error(cls, method, params, e);
            throw e;
        }finally {
            end();
        }

        return result;
    }

    public void end() {
    }

    public void error(Class<?> cls, Method method, Object[] params, Throwable e) {
    }

    public void after(Class<?> cls, Method method, Object[] params, Object result) throws Throwable{
    }

    public void before(Class<?> cls, Method method, Object[] params) throws Throwable{
    }

    public boolean intercepter(Class<?> cls, Method method, Object[] params) {
        return true;
    }

    public void begin() {
    }
}
