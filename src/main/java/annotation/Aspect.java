package annotation;

import java.lang.annotation.*;

/**
 * Created by liq on 2018/4/11.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /**
     * 指Aspect注解括号里面的值  如@Aspect(Controller.class)
     */
    Class<? extends Annotation> value();
}
