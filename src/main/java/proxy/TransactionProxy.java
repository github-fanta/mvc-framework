package proxy;

import annotation.Transaction;
import helper.DatabaseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * 事务代理切面类
 * Created by liq on 2018/4/17.
 */
public class TransactionProxy implements Proxy {

    private static final Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
    private  static final ThreadLocal<Boolean> FLAG_HOLDER = new ThreadLocal<Boolean>(){
        // 保证一个线程中事务控制只执行一次
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };
    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result = null;
        boolean flag = FLAG_HOLDER.get();//此线程是否执行过事务控制逻辑
        boolean annotationPresent = proxyChain.getTargetMethod().isAnnotationPresent(Transaction.class);
        if (!flag && annotationPresent){
            try {
                FLAG_HOLDER.set(true);
                DatabaseHelper.beginTransaction();
                LOGGER.debug("beginTransaction");
                result = proxyChain.doProxyChain();
                DatabaseHelper.commitTransaction();
                LOGGER.debug("commitTransaction");
            }catch(Exception e){
                DatabaseHelper.rollbackTransaction();
                LOGGER.debug("rollback transaction");
            }finally {
                FLAG_HOLDER.remove();
            }
        }else{
            //未标记或已经执行过事务控制逻辑
            result = proxyChain.doProxyChain();
        }
        return result;
    }
}
