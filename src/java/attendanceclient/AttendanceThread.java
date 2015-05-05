package attendanceclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceThread extends Thread {

    protected static boolean startSync = false;
    private static int requestCount = 0;
    private boolean isStopped = true;
    public static final Logger LOGGER = Logger.getLogger("AttendanceThread");

    public boolean isRunning() {
        return !isStopped;
    }

    public void startSync() {
        LOGGER.log(Level.INFO, "\n\n\n\n\n\n\n  start sync called==>>" + isStopped + "====" + this.isAlive());
        requestCount = 0;
        isStopped = false;
        if (this.isAlive()) {
            return;
        }

        if (!ConfigInfo.getTimeZone().equals("")) {
            TimeZone.setDefault(TimeZone.getTimeZone(ConfigInfo.getTimeZone()));
        }
        this.start();
    }

    public void stopSync() {
        isStopped = true;
    }

    public static void resetCount() {
        requestCount = 0;
    }

    @Override
    public void run() {
        while (!isStopped) {
            //LogFile.configLogger();
            try {
                String dateformat = "yyyy-MM-dd HH:mm:ss";

                Date now = new Date();
                String ftime = new SimpleDateFormat(dateformat).format(new Date(ConfigInfo.getLastRequestTime()));
                String ttime = new SimpleDateFormat(dateformat).format(now.getTime());

                LogFile.appendLog(ttime);
                LogFile.appendLog("," + ftime);
                LogFile.appendLog("," + ttime);
                LOGGER.log(Level.INFO, "\n\n\n\n  thread run {0} ===== {1}", new Object[]{ftime, ttime});
                ZPAServerRequest.sendArrayHTTPRequest(SQLAccess.readFromMSSQLServer(ftime, ttime), now);
                LogFile.appendLog("\n");
                ConfigInfo.writeFile();

                long sleepTime = ConfigInfo.getSleepTime();
                AttendanceThread.sleep(sleepTime);

            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
    }
}
