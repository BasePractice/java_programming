package ru.mirea.ippo.format;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public final class Test {
    private List<Property> properties;

    public static void main(String[] args) throws NoSuchFieldException {
        Field field = Test.class.getDeclaredField("properties");
        field.setAccessible(true);
        Class<?> type = field.getType();
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        assert type.isAssignableFrom(List.class);
        assert parameterizedType.getRawType().getTypeName().equals(List.class.getCanonicalName());
        String typeName = parameterizedType.getActualTypeArguments()[0].getTypeName().replace('$', '.');
        String canonicalName = Property.class.getCanonicalName();
        assert typeName.equals(canonicalName);
    }
}
