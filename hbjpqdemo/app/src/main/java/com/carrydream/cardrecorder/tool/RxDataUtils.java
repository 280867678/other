package com.carrydream.cardrecorder.tool;

import android.os.Build;
//import android.support.v4.media.session.PlaybackStateCompat;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import com.carrydream.cardrecorder.tool.RxConstUtils;
import com.huantansheng.easyphotos.utils.file.FileUtils;
//import com.umeng.analytics.pro.ai;
//import com.umeng.analytics.pro.c;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/* loaded from: classes.dex */
public class RxDataUtils {
    private static int[] pyValue = {-20319, -20317, -20304, -20295, -20292, -20283, -20265, -20257, -20242, -20230, -20051, -20036, -20032, -20026, -20002, -19990, -19986, -19982, -19976, -19805, -19784, -19775, -19774, -19763, -19756, -19751, -19746, -19741, -19739, -19728, -19725, -19715, -19540, -19531, -19525, -19515, -19500, -19484, -19479, -19467, -19289, -19288, -19281, -19275, -19270, -19263, -19261, -19249, -19243, -19242, -19238, -19235, -19227, -19224, -19218, -19212, -19038, -19023, -19018, -19006, -19003, -18996, -18977, -18961, -18952, -18783, -18774, -18773, -18763, -18756, -18741, -18735, -18731, -18722, -18710, -18697, -18696, -18526, -18518, -18501, -18490, -18478, -18463, -18448, -18447, -18446, -18239, -18237, -18231, -18220, -18211, -18201, -18184, -18183, -18181, -18012, -17997, -17988, -17970, -17964, -17961, -17950, -17947, -17931, -17928, -17922, -17759, -17752, -17733, -17730, -17721, -17703, -17701, -17697, -17692, -17683, -17676, -17496, -17487, -17482, -17468, -17454, -17433, -17427, -17417, -17202, -17185, -16983, -16970, -16942, -16915, -16733, -16708, -16706, -16689, -16664, -16657, -16647, -16474, -16470, -16465, -16459, -16452, -16448, -16433, -16429, -16427, -16423, -16419, -16412, -16407, -16403, -16401, -16393, -16220, -16216, -16212, -16205, -16202, -16187, -16180, -16171, -16169, -16158, -16155, -15959, -15958, -15944, -15933, -15920, -15915, -15903, -15889, -15878, -15707, -15701, -15681, -15667, -15661, -15659, -15652, -15640, -15631, -15625, -15454, -15448, -15436, -15435, -15419, -15416, -15408, -15394, -15385, -15377, -15375, -15369, -15363, -15362, -15183, -15180, -15165, -15158, -15153, -15150, -15149, -15144, -15143, -15141, -15140, -15139, -15128, -15121, -15119, -15117, -15110, -15109, -14941, -14937, -14933, -14930, -14929, -14928, -14926, -14922, -14921, -14914, -14908, -14902, -14894, -14889, -14882, -14873, -14871, -14857, -14678, -14674, -14670, -14668, -14663, -14654, -14645, -14630, -14594, -14429, -14407, -14399, -14384, -14379, -14368, -14355, -14353, -14345, -14170, -14159, -14151, -14149, -14145, -14140, -14137, -14135, -14125, -14123, -14122, -14112, -14109, -14099, -14097, -14094, -14092, -14090, -14087, -14083, -13917, -13914, -13910, -13907, -13906, -13905, -13896, -13894, -13878, -13870, -13859, -13847, -13831, -13658, -13611, -13601, -13406, -13404, -13400, -13398, -13395, -13391, -13387, -13383, -13367, -13359, -13356, -13343, -13340, -13329, -13326, -13318, -13147, -13138, -13120, -13107, -13096, -13095, -13091, -13076, -13068, -13063, -13060, -12888, -12875, -12871, -12860, -12858, -12852, -12849, -12838, -12831, -12829, -12812, -12802, -12607, -12597, -12594, -12585, -12556, -12359, -12346, -12320, -12300, -12120, -12099, -12089, -12074, -12067, -12058, -12039, -11867, -11861, -11847, -11831, -11798, -11781, -11604, -11589, -11536, -11358, -11340, -11339, -11324, -11303, -11097, -11077, -11067, -11055, -11052, -11045, -11041, -11038, -11024, -11020, -11019, -11018, -11014, -10838, -10832, -10815, -10800, -10790, -10780, -10764, -10587, -10544, -10533, -10519, -10331, -10329, -10328, -10322, -10315, -10309, -10307, -10296, -10281, -10274, -10270, -10262, -10260, -10256, -10254};
    private static String[] pyStr = {"a", "ai", "an", "ang", "ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng", "bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca", "cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan", "chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou", "chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci", "cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai", "dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die", "ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo", "e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo", "fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen", "geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui", "gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei", "hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang", "hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie", "jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka", "kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou", "ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la", "lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia", "lian", "liang", "liao", "lie", "lin", "ling", "liu", "long", "lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man", "mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao", "mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan", "nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang", "niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan", "nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei", "pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po", "pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing", "qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao", "re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui", "run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen", "seng", "sha", "shai", "shan", "shang", "shao", "she", "shen", "sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang", "shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui", "sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng", "ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan", "tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen", "weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie", "xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya", "yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong", "you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang", "zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang", "zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu", "zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi", "zong", "zou", "zu", "zuan", "zui", "zun", "zuo"};
    static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final DecimalFormat amountFormat = new DecimalFormat("###,###,###,##0.00");

    public static boolean isNullString(String str) {
        return str == null || str.length() == 0 || "".equals(str) || "null".equals(str);
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if ((obj instanceof String) && obj.toString().length() == 0) {
            return true;
        }
        if (obj.getClass().isArray() && Array.getLength(obj) == 0) {
            return true;
        }
        if ((obj instanceof Collection) && ((Collection) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof Map) && ((Map) obj).isEmpty()) {
            return true;
        }
        if ((obj instanceof SparseArray) && ((SparseArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof SparseBooleanArray) && ((SparseBooleanArray) obj).size() == 0) {
            return true;
        }
        if ((obj instanceof SparseIntArray) && ((SparseIntArray) obj).size() == 0) {
            return true;
        }
        return Build.VERSION.SDK_INT >= 18 && (obj instanceof SparseLongArray) && ((SparseLongArray) obj).size() == 0;
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException unused) {
            return false;
        }
    }

    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException unused) {
        }
        return str.contains(FileUtils.HIDDEN_PREFIX);
    }

    public static boolean isNumber(String str) {
        return isInteger(str) || isDouble(str);
    }

    public static String getAstro(int i, int i2) {
        String[] strArr = {"魔羯座", "水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座"};
        int i3 = i - 1;
        if (i2 < new int[]{22, 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22}[i3]) {
            i = i3;
        }
        return strArr[i];
    }

    public static String hideMobilePhone4(String str) {
        return str.substring(0, 3) + "****" + str.substring(7, 11);
    }

    public static String formatCard(String str) {
        return (str.substring(0, 4) + " **** **** ") + str.substring(str.length() - 4);
    }

    public static String formatCardEnd4(String str) {
        return "" + str.substring(str.length() - 4);
    }

    public static int stringToInt(String str) {
        if (isNullString(str)) {
            return 0;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public static int[] stringToInts(String str) {
        int[] iArr = new int[str.length()];
        if (!isNullString(str)) {
            int i = 0;
            while (i < str.length()) {
                int i2 = i + 1;
                iArr[i] = Integer.parseInt(str.substring(i, i2));
                i = i2;
            }
        }
        return iArr;
    }

    public static int intsGetSum(int[] iArr) {
        int i = 0;
        for (int i2 : iArr) {
            i += i2;
        }
        return i;
    }

    public static long stringToLong(String str) {
        if (isNullString(str)) {
            return 0L;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException unused) {
            return 0L;
        }
    }

    public static double stringToDouble(String str) {
        if (isNullString(str)) {
            return 0.0d;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException unused) {
            return 0.0d;
        }
    }

    public static String format2Decimals(String str) {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        if (decimalFormat.format(stringToDouble(str)).startsWith(FileUtils.HIDDEN_PREFIX)) {
            return "0" + decimalFormat.format(stringToDouble(str));
        }
        return decimalFormat.format(stringToDouble(str));
    }

    public static InputStream StringToInputStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }

    public static String upperFirstLetter(String str) {
        if (isNullString(str) || !Character.isLowerCase(str.charAt(0))) {
            return str;
        }
        return String.valueOf((char) (str.charAt(0) - ' ')) + str.substring(1);
    }

    public static String lowerFirstLetter(String str) {
        if (isNullString(str) || !Character.isUpperCase(str.charAt(0))) {
            return str;
        }
        return String.valueOf((char) (str.charAt(0) + ' ')) + str.substring(1);
    }

    public static String reverse(String str) {
        int length = str.length();
        if (length <= 1) {
            return str;
        }
        int i = length >> 1;
        char[] charArray = str.toCharArray();
        for (int i2 = 0; i2 < i; i2++) {
            char c = charArray[i2];
            int i3 = (length - i2) - 1;
            charArray[i2] = charArray[i3];
            charArray[i3] = c;
        }
        return new String(charArray);
    }

    public static String toDBC(String str) {
        if (isNullString(str)) {
            return str;
        }
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        for (int i = 0; i < length; i++) {
            if (charArray[i] == 12288) {
                charArray[i] = ' ';
            } else if (65281 <= charArray[i] && charArray[i] <= 65374) {
                charArray[i] = (char) (charArray[i] - 65248);
            } else {
                charArray[i] = charArray[i];
            }
        }
        return new String(charArray);
    }

    public static String toSBC(String str) {
        if (isNullString(str)) {
            return str;
        }
        char[] charArray = str.toCharArray();
        int length = charArray.length;
        for (int i = 0; i < length; i++) {
            if (charArray[i] == ' ') {
                charArray[i] = 12288;
            } else if ('!' <= charArray[i] && charArray[i] <= '~') {
                charArray[i] = (char) (charArray[i] + 65248);
            } else {
                charArray[i] = charArray[i];
            }
        }
        return new String(charArray);
    }

    private static int oneCn2ASCII(String str) {
        if (str.length() != 1) {
            return -1;
        }
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length == 1) {
                return bytes[0];
            }
            if (bytes.length == 2) {
                return (((bytes[0] + 256) * 256) + (bytes[1] + 256)) - 65536;
            }
            throw new IllegalArgumentException("Illegal resource string");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static String oneCn2PY(String str) {
        int oneCn2ASCII = oneCn2ASCII(str);
        if (oneCn2ASCII == -1) {
            return null;
        }
        if (oneCn2ASCII >= 0 && oneCn2ASCII <= 127) {
            return String.valueOf((char) oneCn2ASCII);
        }
        for (int length = pyValue.length - 1; length >= 0; length--) {
            if (pyValue[length] <= oneCn2ASCII) {
                return pyStr[length];
            }
        }
        return null;
    }

    public static String getPYFirstLetter(String str) {
        if (isNullString(str)) {
            return "";
        }
        String oneCn2PY = oneCn2PY(str.substring(0, 1));
        if (oneCn2PY == null) {
            return null;
        }
        return oneCn2PY.substring(0, 1);
    }

    public static String cn2PY(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            int i2 = i + 1;
            String oneCn2PY = oneCn2PY(str.substring(i, i2));
            if (oneCn2PY == null) {
                oneCn2PY = "?";
            }
            sb.append(oneCn2PY);
            i = i2;
        }
        return sb.toString();
    }

    public static String bytes2HexString(byte[] bArr) {
        char[] cArr = new char[bArr.length << 1];
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            int i3 = i + 1;
            char[] cArr2 = hexDigits;
            cArr[i] = cArr2[(bArr[i2] >>> 4) & 15];
            i = i3 + 1;
            cArr[i3] = cArr2[bArr[i2] & 15];
        }
        return new String(cArr);
    }

    public static byte[] hexString2Bytes(String str) {
        int length = str.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        char[] charArray = str.toUpperCase().toCharArray();
        byte[] bArr = new byte[length >>> 1];
        for (int i = 0; i < length; i += 2) {
            bArr[i >> 1] = (byte) ((hex2Dec(charArray[i]) << 4) | hex2Dec(charArray[i + 1]));
        }
        return bArr;
    }

    private static int hex2Dec(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                throw new IllegalArgumentException();
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    public static byte[] chars2Bytes(char[] cArr) {
        int length = cArr.length;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr[i] = (byte) cArr[i];
        }
        return bArr;
    }

    public static char[] bytes2Chars(byte[] bArr) {
        int length = bArr.length;
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            cArr[i] = (char) (bArr[i] & 255);
        }
        return cArr;
    }

    /* renamed from: com.carrydream.cardrecorder.tool.RxDataUtils$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit;

        static {
            int[] iArr = new int[RxConstUtils.MemoryUnit.values().length];
            $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit = iArr;
            try {
                iArr[RxConstUtils.MemoryUnit.BYTE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit[RxConstUtils.MemoryUnit.KB.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit[RxConstUtils.MemoryUnit.MB.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit[RxConstUtils.MemoryUnit.GB.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    public static double byte2Size(long j, RxConstUtils.MemoryUnit memoryUnit) {
        double d;
        double d2;
        if (j < 0) {
            return -1.0d;
        }
        int i = AnonymousClass1.$SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit[memoryUnit.ordinal()];
        if (i == 2) {
            d = j;
            d2 = 1024.0d;
        } else if (i == 3) {
            d = j;
            d2 = 1048576.0d;
        } else if (i != 4) {
            d = j;
            d2 = 1.0d;
        } else {
            d = j;
            d2 = 1.073741824E9d;
        }
        return d / d2;
    }

    public static long size2Byte(long j, RxConstUtils.MemoryUnit memoryUnit) {
        if (j < 0) {
            return -1L;
        }
        int i = AnonymousClass1.$SwitchMap$com$carrydream$cardrecorder$tool$RxConstUtils$MemoryUnit[memoryUnit.ordinal()];
        return j * (i != 2 ? i != 3 ? i != 4 ? 1L : 1073741824L : 1048576 : 1024L);
    }

    public static String byte2FitSize(long j) {
        return j < 0 ? "shouldn't be less than zero!" : j < 1024 ? String.format(Locale.getDefault(), "%.3fB", Double.valueOf(j)) : j < 1048576 ? String.format(Locale.getDefault(), "%.3fKB", Double.valueOf(j / 1024.0d)) : j < 1073741824 ? String.format(Locale.getDefault(), "%.3fMB", Double.valueOf(j / 1048576.0d)) : String.format(Locale.getDefault(), "%.3fGB", Double.valueOf(j / 1.073741824E9d));
    }

    public static ByteArrayOutputStream input2OutputStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[1024];
            while (true) {
                int read = inputStream.read(bArr, 0, 1024);
                if (read == -1) {
                    return byteArrayOutputStream;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ByteArrayInputStream output2InputStream(OutputStream outputStream) {
        if (outputStream == null) {
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) outputStream).toByteArray());
    }

    public static byte[] inputStream2Bytes(InputStream inputStream) {
        return input2OutputStream(inputStream).toByteArray();
    }

    public static InputStream bytes2InputStream(byte[] bArr) {
        return new ByteArrayInputStream(bArr);
    }

    public static byte[] outputStream2Bytes(OutputStream outputStream) {
        if (outputStream == null) {
            return null;
        }
        return ((ByteArrayOutputStream) outputStream).toByteArray();
    }

    public static OutputStream bytes2OutputStream(byte[] bArr) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(bArr);
            return byteArrayOutputStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String inputStream2String(InputStream inputStream, String str) {
        if (inputStream != null && !isNullString(str)) {
            try {
                return new String(inputStream2Bytes(inputStream), str);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static InputStream string2InputStream(String str, String str2) {
        if (str != null && !isNullString(str2)) {
            try {
                return new ByteArrayInputStream(str.getBytes(str2));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String outputStream2String(OutputStream outputStream, String str) {
        if (outputStream == null) {
            return null;
        }
        try {
            return new String(outputStream2Bytes(outputStream), str);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static OutputStream string2OutputStream(String str, String str2) {
        if (str != null && !isNullString(str2)) {
            try {
                return bytes2OutputStream(str.getBytes(str2));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String stringFormat(String str, String str2) {
        return String.format(str, str2);
    }

    public static String stringFormat(String str, int i) {
        return String.format(str, Integer.valueOf(i));
    }

    public static String stringFormat(String str, float f) {
        return String.format(str, Float.valueOf(f));
    }

    public static String getAmountValue(double d) {
        return amountFormat.format(d);
    }

    public static String getAmountValue(String str) {
        return amountFormat.format(Double.parseDouble(str));
    }

    public static String getRoundUp(BigDecimal bigDecimal, int i) {
        return bigDecimal.setScale(i, 4).toString();
    }

    public static String getRoundUp(double d, int i) {
        return new BigDecimal(d).setScale(i, 4).toString();
    }

    public static String getRoundUp(String str, int i) {
        return new BigDecimal(Double.parseDouble(str)).setScale(i, 4).toString();
    }

    public static String getPercentValue(BigDecimal bigDecimal, int i) {
        return getRoundUp(bigDecimal.multiply(new BigDecimal(100)), i);
    }

    public static String getPercentValue(double d, int i) {
        return getPercentValue(new BigDecimal(d), i);
    }

    public static String getPercentValue(double d) {
        return getPercentValue(new BigDecimal(d), 2);
    }
}
