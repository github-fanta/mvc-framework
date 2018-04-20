package util;

/**
 * Created by liq on 2018/3/15.
 */

/**
 * 转型操作工具类
 */
public final class CastUtil {

    /**
     *转成String型（默认为空字符串）
     */
    public static String castString(Object obj){
        return castString(obj,"");
    }
    /**
     *转成String型（可指定默认值）
     */
    public static String castString(Object obj, String defaultValue){
        return obj == null ? defaultValue : String.valueOf(obj);
    }
    /**
     *转成Double型(默认0)
     */
    public static double castDouble(Object obj){
        return castDouble(obj, 0);
    }
    /**
     *转成String型（可指定默认值）
     */
    public static double castDouble(Object obj, double defaultValue){
        double value = defaultValue;
        if (obj != null){
            String strValue = castString(value);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    value = Double.parseDouble(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }
    /**
     *转成long型(默认为0）
     */
    public static long castLong(Object obj){
        return castLong(obj, 0);
    }
    /**
     *转成long型（可指定默认值）
     */
    public static long castLong(Object obj, long defaultValue){
        long value = defaultValue;
        if (obj != null){
            String strValue = castString(value);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    value =Long.parseLong(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    /**
     *转成int型(默认为0）
     */
    public static int castInt(Object obj){
        return castInt(obj, 0);
    }
    /**
     *转成int型（可指定默认值）
     */
    public static int castInt(Object obj, int defaultValue){
        int value = defaultValue;
        if (obj != null){
            String strValue = castString(value);
            if (StringUtil.isNotEmpty(strValue)) {
                try {
                    value =Integer.parseInt(strValue);
                }catch (NumberFormatException e){
                    value = defaultValue;
                }
            }
        }
        return value;
    }

    /**
     *转成boolean型(默认为false）
     */
    public static boolean castBoolean(Object obj){
        return castBoolean(obj, false);
    }
    /**
     *转成boolean型（可指定默认值）
     */
    public static boolean castBoolean(Object obj, boolean defaultValue){
        boolean value = defaultValue;
        if (obj != null){
            Boolean.parseBoolean(castString(obj));
        }
        return value;
    }
}
