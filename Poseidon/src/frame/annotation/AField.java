package frame.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解实体类中的属性名，注解后无须表单中的name与实体类属性名一致
 * Created by sumei on 17/9/7.
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AField {
    String name() default "";
}
