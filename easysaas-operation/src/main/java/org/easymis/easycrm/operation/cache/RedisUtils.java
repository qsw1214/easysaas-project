package org.easymis.easycrm.operation.cache;

public class RedisUtils {



    public  static String joinKey(Object ... values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            builder.append(values[i]);
        }
        return builder.toString();
    }

}
