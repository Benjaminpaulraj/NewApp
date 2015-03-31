package attendanceclient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ConfigInfo {

    public static JSONObject config;
    protected static int reqCount = 0;
    protected static int reqSuccessCount = 0;
    protected static int dataCount = 0;
    protected static long timeTakenTotal = 0;
    protected static String currentFileName = null;
    protected static StringBuilder logString = new StringBuilder();
    private static final String ALGORITHM = "AES";
    private static final String UNICODE_FORMAT = "UTF8";
    private static final Logger LOGGER = Logger.getLogger("ConfigInfo");

    private static String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes(UNICODE_FORMAT));
        String encryptedValue = new BASE64Encoder().encode(encValue);
        return encryptedValue;
    }

    private static String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedValue);
        byte[] decValue = c.doFinal(decordedValue);//////////LINE 50
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private static Key generateKey() throws Exception {
        byte[] keyAsBytes;
        keyAsBytes = "ECNADNETTAOHOZEL".getBytes(UNICODE_FORMAT);
        Key key = new SecretKeySpec(keyAsBytes, ALGORITHM);
        return key;
    }

    public static String getAuthtoken() {
        String val = "";
        try {
            val = config.getString("authtoken");
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static long getSleepTime() {
        long val = 1000 * 60 * 5l;
        try {
            val = 1000 * 60 * (config.getInt("sleepTime"));
        } catch (JSONException ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static long getLastRequestTime() {

        Long val = null;
        try {
            if (config.has("lastRequestTime")) {
                val = config.getLong("lastRequestTime");
                if ((val + 1000 * 60 * 60 * 24) <= new Date().getTime()) {
                    val = setLastRequestTime(null);
                }
            } else {
                val = setLastRequestTime(null);
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static long getNextRequestTime() {

        long val = getLastRequestTime();
        try {
            val += getSleepTime();
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getDBurl() {
        String val = "";
        try {
            val = config.getString("dburl");
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getDBusername() {
        String val = "";
        try {
            val = config.getString("dbuname");
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getDBpassword() {
        String val = "";
        try {
            val = config.getString("dbpword");
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getProxyHostIP() {
        String val = "";
        try {
            if (config.has("proxyHostIP")) {
                val = config.getString("proxyHostIP");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getProxyHostPort() {
        String val = "";
        try {
            if (config.has("proxyPort")) {
                val = config.getString("proxyPort");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getProxyUsername() {
        String val = "";
        try {
            if (config.has("proxyUname")) {
                val = config.getString("proxyUname");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getProxyPassword() {
        String val = "";
        try {

            if (config.has("proxyPwd")) {
                val = config.getString("proxyPwd");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getDBQuery() {
        String val = "";
        try {
            val = config.getString("dbquery");
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static String getTimeZone() {
        String val = "GMT";
        try {
            if (config.has("timeZone")) {
                val = config.getString("timeZone");
            } else {
                config.put("timeZone", "GMT");
            }
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static long setLastRequestTime(Long val) {
        if (val == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            val = cal.getTimeInMillis();
        }
        try {
            config.put("lastRequestTime", val);
        } catch (Exception ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }

    public static void writeFile() throws Exception {
        File file = new File(".config");
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        updateStat();
        bw.write(encrypt(config.toString()));
        bw.close();
        fw.close();

        LOGGER.log(Level.INFO, "\n\n\n read from file" + config);
    }

    public static void clearStatInfo() {
        reqCount = 0;
        reqSuccessCount = 0;
        dataCount = 0;
        timeTakenTotal = 0;

    }

    private static void updateStat() throws Exception {
        config.put("reqCount", reqCount);
        config.put("reqSuccessCount", reqSuccessCount);
        config.put("dataCount", dataCount);
        config.put("timeTakenTotal", timeTakenTotal);
        config.put("currentFileName", currentFileName);
    }


    public static void setConfigInfo(JSONObject obj) throws Exception {

        if (config == null) {
            config = new JSONObject();
        }

        if (obj.has("dbquery")) {
            config.put("dbquery", obj.getString("dbquery"));
        }
        if (obj.has("dburl")) {
            config.put("dburl", obj.getString("dburl"));
        }
        if (obj.has("dbuname")) {
            config.put("dbuname", obj.getString("dbuname"));
        }
        if (obj.has("dbpword")) {
            config.put("dbpword", obj.getString("dbpword"));
        }


        if (obj.has("proxyUname")) {
            config.put("proxyUname", obj.getString("proxyUname"));
        }
        if (obj.has("proxyPwd")) {
            config.put("proxyPwd", obj.getString("proxyPwd"));
        }
        if (obj.has("proxyHostIP")) {
            config.put("proxyHostIP", obj.getString("proxyHostIP"));
        }
        if (obj.has("proxyPort")) {
            config.put("proxyPort", obj.getString("proxyPort"));
        }

        if (obj.has("authtoken")) {
            config.put("authtoken", obj.getString("authtoken"));
        }
        if (obj.has("sleepTime")) {
            config.put("sleepTime", obj.getLong("sleepTime"));
        }
        if (obj.has("timeZone")) {
            config.put("timeZone", obj.getString("timeZone"));
        }
        if (!ConfigInfo.getTimeZone().equals("")) {
            TimeZone.setDefault(TimeZone.getTimeZone(ConfigInfo.getTimeZone()));
        }
        if (obj.has("lastRequestTime")) {
            config.put("lastRequestTime", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(obj.getString("lastRequestTime")).getTime());
        }
        writeFile();
    }

    public static JSONObject getstatinfo() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("reqCount", reqCount);
            obj.put("reqFailCount", reqCount - reqSuccessCount);
            obj.put("reqSuccesCount", reqSuccessCount);
            obj.put("dataCount", dataCount);

            if (timeTakenTotal != 0 && reqCount != 0) {
                obj.put("averageTimeTaken", timeTakenTotal / reqCount);
                long ms = timeTakenTotal / reqCount;
                if (ms >= 1000) {
                    obj.put("averageTimeTaken", timeTakenTotal / reqCount / 1000 + "s");
                } else {
                    obj.put("averageTimeTaken", timeTakenTotal / reqCount + "ms");
                }
            } else {
                obj.put("averageTimeTaken", 0);
            }
        } catch (JSONException ex) {
            Logger.getLogger(ConfigInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    public static JSONObject getConfInfo() {
        LOGGER.log(Level.INFO, "\n\n\n getConfInfo called" + config);
        FileInputStream fis = null;
        try {
            File file = new File(".config");
            if (file.exists()) {
                fis = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                fis.read(data);
                fis.close();
                config = new JSONObject(decrypt(new String(data, "UTF-8")));

                reqCount = config.getInt("reqCount");
                reqSuccessCount = config.getInt("reqSuccessCount");
                dataCount = config.getInt("dataCount");
                timeTakenTotal = config.getLong("timeTakenTotal");
                currentFileName = config.getString("currentFileName");
            } else {
                file.createNewFile();
                config = new JSONObject();
                writeFile();
            }
        } catch (Exception e) {
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
            }
        }
        return config;
    }
}