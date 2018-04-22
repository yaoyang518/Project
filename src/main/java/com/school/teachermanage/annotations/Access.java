package com.school.teachermanage.annotations;

import com.school.teachermanage.enumeration.PermissionEnum;

import java.lang.annotation.*;

/**
 * @author mandy
 * @Date 2017/11/16.
 */
@Documented
@Inherited
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Access {

    PermissionEnum authorities();

    String[] roles() default {};
}
