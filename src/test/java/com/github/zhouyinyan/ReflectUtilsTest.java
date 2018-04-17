package com.github.zhouyinyan;

import com.sun.org.apache.regexp.internal.RE;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created by zhouyinyan on 2018/4/17.
 */
public class ReflectUtilsTest {

    @Test
    public void testGetAllFeildsIgoreNotSupportTypesAndIgoreAnno(){
        List<Field> fields =  ReflectUtils.getAllFeildsIgoreNotSupportTypesAndIgoreAnno(TestTokenInfo.class);
        System.out.println(fields);
        TestTokenInfo info = new TestTokenInfo();
        info.setF1(1);
        info.setF2(2);
        info.setF3(3);
        info.setF4("1abc");
        info.setF5(2.3d);
        info.setId("sss");
        info.setF6(new Date());
        List<String> values = ReflectUtils.fetchValuesAndProcessSpecialValue(fields, info);
        System.out.println(values);

    }
}
