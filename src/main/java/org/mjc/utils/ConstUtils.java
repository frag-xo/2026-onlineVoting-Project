package org.mjc.utils;

import java.util.HashMap;

public class ConstUtils {

    public static HashMap<String, String> eventMap = new HashMap<String, String>() {{
        put("0001", "进球");
        put("0002", "越位");
        put("0003", "换上");
        put("0004", "换下");
        put("0005", "黄牌");
        put("0006", "射门");
    }};
}
