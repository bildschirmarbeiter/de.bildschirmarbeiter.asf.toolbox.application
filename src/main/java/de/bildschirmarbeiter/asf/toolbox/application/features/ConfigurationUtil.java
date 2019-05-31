package de.bildschirmarbeiter.asf.toolbox.application.features;

import java.lang.reflect.Array;
import java.util.Collection;

import org.apache.commons.lang3.StringEscapeUtils;

public class ConfigurationUtil {

    private static final String SIMPLE_STRING_TEMPLATE = "\"%s\"";

    private static final String SIMPLE_TEMPLATE = "%s";

    private static final String ARRAY_TEMPLATE = "new %s";

    private static final String COLLECTION_TEMPLATE = "new ArrayList<>()";

    public static String toJavaString(final Object value) {
        final String string;
        if (value.getClass().isArray()) {

            /*
            final int length = Array.getLength(value);
            final Object array = Array.newInstance(value.getClass().getComponentType(), length + 1);
            System.arraycopy(v, 0, array, 0, length);
            Array.set(array, length, value);
            configuration.put(key, array);
            */

            boolean isStringArray = value.getClass().getComponentType() == String.class;

            final StringBuilder sb = new StringBuilder();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                String element = (String) Array.get(value, i);
                if (isStringArray) {
                    sb.append("\"");
                }
                final String escaped = StringEscapeUtils.escapeJava(element);
                sb.append(escaped);
                if (isStringArray) {
                    sb.append("\"");
                }
                if (i < length - 1) {
                    sb.append(", ");
                }
            }

            string = String.format(ARRAY_TEMPLATE, value.getClass().getComponentType().getSimpleName()).concat("[]{" + sb + "}");
        } else if (value instanceof Collection) {
            string = COLLECTION_TEMPLATE;
        } else if (value instanceof String) {
            string = String.format(SIMPLE_STRING_TEMPLATE, value);
        } else {
            string = String.format(SIMPLE_TEMPLATE, value);
        }

        return string;
    }


}
