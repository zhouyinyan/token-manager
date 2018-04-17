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

    /**
     * 原始类型
     */
    private static List<Class<?>> primitiveTypes;

    /**
     * 包装类型
     */
    private static List<Class<?>> primitiveWrapTypes;

    static {
        Class<?>[] primitiveTypesAarray = new Class[]{byte.class, short.class, int.class, long.class,
                                                    double.class, float.class, boolean.class, char.class};

        Class<?>[] primitiveWrapTypesAarry = new Class[]{Byte.class, Short.class, Integer.class, Long.class,
                                                        Double.class, Float.class, Boolean.class, Character.class};

        Class<?>[] types = new Class[primitiveTypesAarray.length + primitiveWrapTypesAarry.length + 1];
        System.arraycopy(primitiveTypesAarray, 0, types, 0, primitiveTypesAarray.length);
        System.arraycopy(primitiveWrapTypesAarry, 0, types, primitiveTypesAarray.length, primitiveWrapTypesAarry.length);
        System.arraycopy(new Class<?>[]{String.class}, 0, types, primitiveTypesAarray.length + primitiveWrapTypesAarry.length,  1);


        primitiveTypes = Collections.unmodifiableList(Arrays.stream(primitiveTypesAarray)
                .collect(Collectors.toList()));

        primitiveWrapTypes = Collections.unmodifiableList(Arrays.stream(primitiveWrapTypesAarry)
                .collect(Collectors.toList()));

        supportTypes = Collections.unmodifiableList(Arrays.stream(types)
                                    .collect(Collectors.toList()));
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


    public static <T> List<String> fetchValuesAndProcessSpecialValue(List<Field> fields, T target) {

        Function<Field, String> function = field -> {
            boolean originalAccess = field.isAccessible();
            if(!originalAccess){
                field.setAccessible(true);
            }
            try {
                Object value = field.get(target);

                if(isPrimitiveWrap(field.getType()) && Objects.isNull(value)) {
                    //包装类型的NULL处理为空支付串(节省字符串长度）
                    value = "";
                }else if(isString(field.getType())){
                    //原始字符串对象是null
                    if (Objects.isNull(value)) {
                        value = TokenConstants.NULLSTRINGPREPRESENT;
                    }
                    //原始字符串包含分割符时抛出异常。
                    if (String.class.cast(value).contains(String.valueOf(TokenConstants.DELIMITER))) {
                        throw new TokenException("value contains illegal character : " + TokenConstants.DELIMITER);
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

    public static <T> T fillValuesAndProcessSpecialValue(Class<T> tClass, List<String> values){
        List<Field> fields = ReflectUtils.getAllFeildsIgoreNotSupportTypesAndIgoreAnno(tClass);

        T target = null;
        try {
            target = tClass.newInstance();

            final int[] index = {0};
            T finalTarget = target;
            fields.stream().forEach(field -> {
                boolean originalAccess = field.isAccessible();
                if(!originalAccess){
                    field.setAccessible(true);
                }

                try {
                    //特殊值处理
                    String tempVaule = values.get(index[0]);

                    setValue(field, tempVaule, finalTarget);

                    index[0]++;
                } catch (IllegalAccessException e) {
                    throw new TokenException(e.getMessage());
                }finally {
                    field.setAccessible(originalAccess);
                }
            });

            return finalTarget;
        } catch (InstantiationException e) {
            throw new TokenException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new TokenException(e.getMessage());
        }
    }

    private static <T> void setValue(Field field, String tempVaule, T finalTarget) throws IllegalAccessException {
        if(isPrimitiveWrap(field.getType())){ //包装类型
            if(tempVaule.equals("")) {
                field.set(finalTarget, null);
            }else{
                setValueWithPrimitiveOrWrap( field,  tempVaule,  finalTarget);
            }
        }else if(isString(field.getType()) ){
            if(tempVaule.equals(TokenConstants.NULLSTRINGPREPRESENT)) {
                field.set(finalTarget, null);
            }else{
                field.set(finalTarget, tempVaule);
            }
        }else if(isPrimitive(field.getType())){
            setValueWithPrimitiveOrWrap( field,  tempVaule,  finalTarget);
        }else{
            //忽略
        }
    }

    private static <T> void setValueWithPrimitiveOrWrap(Field field, String tempVaule, T finalTarget) throws IllegalAccessException {
        Class<?> type = field.getType();
        if(type.equals(byte.class) || type.equals(Byte.class)){
            field.setByte(finalTarget, Byte.valueOf(tempVaule));
        }else if(type.equals(short.class) || type.equals(Short.class)){
            field.setShort(finalTarget, Short.valueOf(tempVaule));
        }else if(type.equals(int.class) || type.equals(Integer.class)){
            field.setInt(finalTarget, Integer.valueOf(tempVaule));
        }else if(type.equals(long.class) || type.equals(Long.class)){
            field.setLong(finalTarget, Long.valueOf(tempVaule));
        }else if(type.equals(float.class) || type.equals(Float.class)){
            field.setFloat(finalTarget, Float.valueOf(tempVaule));
        }else if(type.equals(double.class) || type.equals(Double.class)){
            field.setDouble(finalTarget, Double.valueOf(tempVaule));
        }else if(type.equals(boolean.class) || type.equals(Boolean.class)){
            field.setBoolean(finalTarget, Boolean.valueOf(tempVaule));
        }else if(type.equals(char.class) || type.equals(Character.class)){
            field.setChar(finalTarget, Character.valueOf(tempVaule.charAt(0)));
        }
    }

    private static boolean isPrimitive(Class clazz){
        return primitiveTypes.contains(clazz);
    }

    private static boolean isPrimitiveWrap(Class clazz){
        return primitiveWrapTypes.contains(clazz);
    }

    private static boolean isString(Class clazz){
        return String.class.equals(clazz);
    }
}
