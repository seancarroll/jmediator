package jmediator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 
 *
 */
public final class ReflectionUtils {
    private ReflectionUtils() {
        // static utility
    }

    /**
     * 
     * @param clazz
     * @param genericInterface
     * @return
     */
    public static Class<?> getTypeArgumentForGenericInterface(Class<?> clazz, Class<?> genericInterface) {
        Type[] genericInterfaces = clazz.getGenericInterfaces();
        ParameterizedType type = findByRawType(genericInterfaces, genericInterface);
        return (Class<?>) type.getActualTypeArguments()[0];
    }

    /**
     * 
     * @param genericInterfaces
     * @param expectedRawType
     * @return
     */
    private static ParameterizedType findByRawType(Type[] genericInterfaces, Class<?> expectedRawType) {
        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parametrized = (ParameterizedType) type;
                if (expectedRawType.equals(parametrized.getRawType())) {
                    return parametrized;
                }
            }
        }
        throw new RawTypeForGenericInterfaceNotFoundException();
    }
}
