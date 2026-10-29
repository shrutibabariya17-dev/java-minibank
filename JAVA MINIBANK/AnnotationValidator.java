import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotationValidator {

    public static String[] validate(Object obj) {
        if (obj == null) {
            return new String[]{"Validation error: Object to validate is null"};
        }

        List<String> errors = new ArrayList<>();
        Class<?> currentClass = obj.getClass();

        while (currentClass != null && currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);

                try {
                    Object value = field.get(obj);

                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive positive = field.getAnnotation(Positive.class);

                        if (value == null) {
                            errors.add("Field '" + field.getName() + "' is null, but " + positive.message());
                        } else if (value instanceof Number number) {
                            if (number.doubleValue() <= 0) {
                                errors.add("Field '" + field.getName() + "' " + positive.message()
                                        + " (actual value: " + value + ")");
                            }
                        }
                    }

                    if (field.isAnnotationPresent(MaxLength.class)) {
                        MaxLength maxLength = field.getAnnotation(MaxLength.class);

                        if (value instanceof String str) {
                            if (str.length() > maxLength.value()) {
                                errors.add("Field '" + field.getName() + "' exceeds maximum length of "
                                        + maxLength.value() + " (actual length: " + str.length() + ")");
                            }
                        }
                    }

                } catch (IllegalAccessException e) {
                    errors.add("Unable to access field '" + field.getName() + "': " + e.getMessage());
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return errors.toArray(new String[0]);
    }
}
