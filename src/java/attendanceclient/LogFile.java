package attendanceclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.json.JSONArray;

public class LogFile {

    public static final Logger LOGGER = Logger.getLogger("LogFile");
    private static FileHandler fh = null;

    public static void configLogger() {
        try {
            String pattern = getLogFilePattern();
            LOGGER.log(Level.CONFIG, "  handler called {0} {1} {2}", new Object[]{fh, ConfigInfo.currentFileName, pattern});
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
                    fh = null;
                }
            }
            if (fh == null) {
                fh = new FileHandler(pattern, true);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);
            }
            LOGGER.log(Level.INFO, " handler added {0}", fh);
            LOGGER.addHandler(fh);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void appendLog(String logs) {
        try {
            String pattern = getLogFilePattern();
            File file = new File(pattern);
            //if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
            bufferWritter.write(logs);
            bufferWritter.close();

        } catch (IOException ex) {
            Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, ex);
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

    public static Object getLogs(String fileName) {
        FileReader fr = null;
        StringBuilder logs = new StringBuilder();
        try {
            if (fileName == null) {
                fileName = getLogFilePattern();
            }
            File file = new File(fileName);
            if (file.exists()) {
                fr = new FileReader(file);
                String sRow;
                BufferedReader br = new BufferedReader(fr);
                while ((sRow = br.readLine()) != null) {
                    if(sRow.contains("server request") || sRow.contains("AttendanceThread"))continue;
                    if (sRow.startsWith("INFO")) {
                        sRow = sRow.replace("INFO:", "");
                        sRow = "<tr><td></td><td>" + sRow.replaceAll("\t", "</td><td>") + "</td></tr>";
                    } else {
                        sRow = "<tr><td></td><td>" + sRow.replaceAll(",", "</td><td>") + "</td></tr>";
                    }
                    logs.insert(0, sRow);
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

    public static JSONArray listFilesForFolder() {
        JSONArray arr = new JSONArray();
        final File folder = new File(".");
        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            String fileName = fileEntry.getName();
            if (fileName.endsWith(".log")) {
                arr.put(fileName.substring(9).replace(".log", ""));
            }
        }
        return arr;
    }
}