package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 控制器注解
 * Created by liq on 2018/3/27.
 */
@Target(ElementType.TYPE) //指明 Controller注解用于描述类接口（包括注解类型）或enum声明
@Retention(RetentionPolicy.RUNTIME)  //Retention这个注解的生命周期一直程序运行时都存在
public @interface Controller {
}
