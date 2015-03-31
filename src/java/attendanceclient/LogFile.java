/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package attendanceclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author benjamin-1215
 */
public class LogFile {

    public static final Logger LOGGER = Logger.getLogger("LogFile");
    private static FileHandler fh = null;

    public static void configLogger() {
        try {
            String pattern = getLogFilePattern();
            if (fh != null && ConfigInfo.currentFileName != null && ConfigInfo.currentFileName.equals(pattern)) {
                return;
            }
            ConfigInfo.currentFileName = pattern;
            File file = new File(pattern);
            if (!file.exists()) {
                ConfigInfo.clearStatInfo();
                if (fh != null) {
                    fh.flush();
                    fh.close();
                    LOGGER.removeHandler(fh);
                    fh=null;
                }
            }
            if (fh == null) {
                fh = new FileHandler(pattern, true);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
            }
            LOGGER.addHandler(fh);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public static String getFirstLogFileName() {
//        String fname = String.format("zpa_logs_%d-%d-%d_0.log", Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
//        return fname;
//    }
    public static String getLogFilePattern() {
        //String pattern = String.format("zpa_logs_%d-%d-%d_%%g.log", Calendar.getInstance().get(Calendar.DATE), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
        String pattern = String.format("zpa_logs_%d-%d-%d.log", Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
        //String pattern = String.format("zpa_logs_%d-%d-%d-%d.log", Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.YEAR));
        return pattern;
    }

    public static Object getLogs() {
        FileReader fr = null;
        StringBuilder logs = new StringBuilder();
        try {
            String pattern = getLogFilePattern();
            File file = new File(pattern);
            if (file.exists()) {
                fr = new FileReader(file);
                String sRow;
                BufferedReader br = new BufferedReader(fr);
                while ((sRow = br.readLine()) != null) {
                    if (sRow.startsWith("INFO") && !sRow.contains("server request")) {
                        // logs.put("<br>1" + sRow);
                        sRow = sRow.replace("INFO:", "");
                        // logs.put("<br>2" + sRow);
                        sRow = sRow.replaceAll("\t", "<span style='margin-right:25px;'></span>");
                        logs.insert(0, sRow + "<br>");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (Exception e) {
            }
        }
        return logs;
    }
}