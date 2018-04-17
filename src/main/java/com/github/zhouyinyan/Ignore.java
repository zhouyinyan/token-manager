package com.github.zhouyinyan;

import java.lang.annotation.*;

/**
 * Created by zhouyinyan on 2018/4/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Ignore {
}
