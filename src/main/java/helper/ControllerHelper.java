package helper;

import annotation.Action;
import bean.Handler;
import bean.Request;
import util.ArrayUtil;
import util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 控制器助手类
 * Created by liq on 2018/3/29.
 */
public final class ControllerHelper {

    /**
     * 存放请求与处理器之间的映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

    static {
        //获取所有的Controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        //不为空，遍历所有的Controller类
        if (CollectionUtil.isNotEmpty(controllerClassSet)){
            //获取每一个Controller类里面的方法
            for(Class<?> controllerClass : controllerClassSet){
                Method[] methods = controllerClass.getDeclaredMethods();//getDeclaredMethods能拿到所有方法（除了继承来的）getMethods只能拿到public的方法
                //不为空，遍历这些方法
                for (Method method : methods) {
                    //对于带有Action注解的方法
                    if (method.isAnnotationPresent(Action.class)){
                        Action action = method.getAnnotation(Action.class);
                        //从Action注解中获取URL映射规则
                        String mapping = action.value();

                        //验证URL映射规则 \w表示字符类（包括大小写字母，数字） /w*表示0个或者多个字符
                        if (mapping.matches("\\w+:/\\w*")){
                            String[] array = mapping.split(":");
                            if (ArrayUtil.isNotEmpty(array) && array.length == 2){
                                //封装方法名和请求路径到Request对象
                                String requestMethod = array[0];
                                String requestPath = array[1];
                                Request request = new Request(requestMethod, requestPath);
                                //封装Contoller类和Method到Handler对象
                                Handler handler = new Handler(controllerClass, method);

                                //将Request对象和Method对象放入ACTION_MAP
                                ACTION_MAP.put(request, handler);
                            }
                        }
                    }

                }
            }
        }
    }


    /**
     * 获取Handler
     */
    public static Handler getHandler(String requestMethod, String requestPath){
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
