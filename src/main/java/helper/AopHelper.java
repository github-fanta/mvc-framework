package helper;

import annotation.Aspect;
import annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proxy.AspectProxy;
import proxy.Proxy;
import proxy.ProxyManager;
import proxy.TransactionProxy;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 方法拦截助手
 * Created by liq on 2018/4/14.
 */
public final class AopHelper {


    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    /**
     * 静态块初始化整个AOP框架
     */
    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            for(Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()){
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                //对于每一个具体的目标类，根据代理它的所有类，创建最终的代理proxy（链）
                Proxy proxy = ProxyManager.createProxy(targetClass, proxyList);
                //访问目标类，则访问代理类
                BeanHelper.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            LOGGER.error("aop failure", e);
        }
    }


    /**
     * 获取注解类与其目标类之间的映射关系
     * 一个Controller代理类对应多个标记Controller的具体类
     */
    public static Map<Class<?>, Set<Class<?>>> createProxyMap() throws Exception {

        HashMap<Class<?>, Set<Class<?>>> proxyMap = new HashMap<Class<?>, Set<Class<?>>>();
        addAspectProxy(proxyMap);
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    /**
     *添加普通切面代理的代理关系（继承自抽象类 AspectProxy）
     */
    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        // proxyClassSet 为各种继承自抽象类AspectProxy（继承自Proxy）,并覆盖某些钩子函数的类集合，
        // 如ControllerProxy，ServiceProxy这样一个个的Proxy。放在一起组成代理链
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetSuper(AspectProxy.class);
        for(Class<?> proxyClass : proxyClassSet){
            if (proxyClass.isAnnotationPresent(Aspect.class)){
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                //proxyClass如ControllerProxy（Controller的代理类），targetClassSet就是@Controller标注的那些类
                proxyMap.put(proxyClass, targetClassSet);
            }
        }

    }

    /**
     * 添加事务代理类对应关系（直接实现Proxy与AspectProxy平级）
     */
    public static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap){
        Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);
        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }



    /**
     * 获取标注着 @Aspect注解的值（如Controller） 的那些个类
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) throws Exception{

        HashSet<Class<?>> targetClassSet = new HashSet<Class<?>>();
        Class<? extends Annotation> annotation = aspect.value();
        if (annotation != null && !annotation.equals(aspect)){
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }


    /**
     * 创建每个具体类对应多个代理对象的映射关系
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws IllegalAccessException, InstantiationException {
        //一个标记@Controller的类，多个代理对象
        HashMap<Class<?>, List<Proxy>> targetMap = new HashMap<Class<?>, List<Proxy>>();
        //一个ControllerProxy（或其他）和所有@Controller（或其他）标记的类
        for(Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()){
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            //遍历每一个标记着@Controller(或其他）的类
            for (Class<?> targetClass : targetClassSet){
                //用其代理实现一个代理对象
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (targetMap.containsKey(targetClass)){
                    //标记着@Controller的这个类已包含在targetMap中

                    //添加此类的一个代理对象入Map中
                    targetMap.get(proxyClass).add(proxy);
                }else{
                    //还未建立此类对应的代理对象列表
                    ArrayList<Proxy> proxyList = new ArrayList<Proxy>();
                    proxyList.add(proxy);
                    targetMap.put(proxyClass, proxyList);
                }
            }
        }
        return  targetMap;
    }


}
