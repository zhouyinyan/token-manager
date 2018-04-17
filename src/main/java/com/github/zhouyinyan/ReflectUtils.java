package com.github.zhouyinyan;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by zhouyinyan on 2018/4/17.
 * 反射工具类
 */
public class ReflectUtils {

    /**
     * 缓存原始信息对象以及其包含的字段信息.
     */
    private static  Map<Class, List<Field>> allFieldsCache = new ConcurrentHashMap<>();
    private static  Map<Class, List<Field>> igoreNotSupportTypesFieldsCache = new ConcurrentHashMap<>();
    private static  Map<Class, List<Field>> igoreNotSupportTypesAndIgoreAnno = new ConcurrentHashMap<>();


    /**
     * 支持的类型
     */
    private static List<Class<?>> supportTypes;

    static {
        Class<?>[] types = new Class[]{byte.class, short.class, int.class, long.class, double.class, float.class, boolean.class, char.class ,
                Byte.class, Short.class, Integer.class, Long.class, Double.class, Float.class, Boolean.class, Character.class,
                String.class};

        supportTypes = Collections.unmodifiableList(Arrays.stream(types).collect(Collectors.toList()));
    }


    public static List<Field> getAllFeilds(Class tClass){
        if (allFieldsCache.containsKey(tClass)){
            return allFieldsCache.get(tClass);
        }

        Field[] allFields = tClass.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());

        allFieldsCache.put(tClass, fields);
        return fields;
    }

    public static List<Field> getAllFeildsIgoreNotSupportTypes(Class tClass){

        if (igoreNotSupportTypesFieldsCache.containsKey(tClass)){
            return igoreNotSupportTypesFieldsCache.get(tClass);
        }

        Field[] allFields = tClass.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .filter(field -> supportTypes.contains(field.getType()))
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());

        igoreNotSupportTypesFieldsCache.put(tClass, fields);
        return fields;
    }

    public static List<Field> getAllFeildsIgoreNotSupportTypesAndIgoreAnno(Class tClass){

        if (igoreNotSupportTypesAndIgoreAnno.containsKey(tClass)){
            return igoreNotSupportTypesAndIgoreAnno.get(tClass);
        }

        Field[] allFields = tClass.getDeclaredFields();
        List<Field> fields = Arrays.stream(allFields)
                .filter(field -> field.getAnnotation(Ignore.class) == null)
                .filter(field -> supportTypes.contains(field.getType()))
                .sorted(Comparator.comparing(Field::getName))
                .collect(Collectors.toList());

        igoreNotSupportTypesAndIgoreAnno.put(tClass, fields);

        return fields;
    }


    public static List<String> fetchValuesAndProcessSpecialValue(List<Field> fields, Object target) {

        Function<Field, String> function = field -> {
            boolean originalAccess = field.isAccessible();
            if(!originalAccess){
                field.setAccessible(true);
            }
            try {
                Object value = field.get(target);

                if(!field.getType().isPrimitive()){ //非原始类型

                    if(!field.getType().equals(String.class)) {
                        if (Objects.isNull(value)) { //null值处理
                            value = ""; //非String类型的NULL处理为空支付串(节省字符串长度）
                        }
                    }else {
                        if (Objects.isNull(value)) { //原始字符串对象是null
                            value = TokenConstants.NULLSTRINGPREPRESENT;
                        }
                        if (String.class.cast(value).contains(String.valueOf(TokenConstants.DELIMITER))) { //原始字符串包含分割符时抛出异常。
                            throw new TokenException("value contains illegal character : " + TokenConstants.DELIMITER);
                        }
                    }
                }

                return value.toString();
            } catch (IllegalAccessException e) {
                throw new TokenException(e.getMessage());
            }finally {
                field.setAccessible(originalAccess);
            }
        };

        return fields.stream()
                .map(function)
                .collect(Collectors.toList());

    }


}
