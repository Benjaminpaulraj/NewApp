package attendanceclient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author benjamin-1215
 */
public class ZPAServerRequest {

    public static final Logger LOGGER = Logger.getLogger("ZPAServerRequest");

    public static void sendArrayHTTPRequest(ArrayList<String> dataArr, Date now) {
        int dataCount = 0;

        String authtoken = ConfigInfo.getAuthtoken();
        String targetURL = "https://people.zoho.com/people/api/attendance/bulkImport";
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

        try {
            if (dataArr.isEmpty()) {
                return;
            }
            LOGGER.log(Level.INFO, "All record count {0}, data {1} token {2}", new Object[]{dataArr.toString().replaceAll("[^}]", "").length(), dataArr.toString(), authtoken});
            Iterator<String> iData = dataArr.iterator();
            PostMethod post = null;

            HttpClient httpclient = new HttpClient();
            if (!ConfigInfo.getProxyHostIP().equals("")) {
                httpclient.getHostConfiguration().setProxy(ConfigInfo.getProxyHostIP(), Integer.parseInt(ConfigInfo.getProxyHostPort()));
                httpclient.getState().setProxyCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ConfigInfo.getProxyUsername(), ConfigInfo.getProxyPassword()));
            }
            LOGGER.log(Level.INFO, "before {0} {1} {2}", new Object[]{ConfigInfo.reqCount, ConfigInfo.timeTakenTotal,ConfigInfo.dataCount});
            /*-------------------------------------- Execute the http request--------------------------------*/
            try {
                int result = 0;
                while (iData.hasNext()) {

                    String val = iData.next();
                    dataCount = val.replaceAll("[^}]", "").length();
                    if (dataCount == 0) {
                        return;
                    }
                    if(result!=0){
                        LogFile.appendLog("<br>,,,");
                    }
                    post = new PostMethod(targetURL);
                    post.setParameter("authtoken", authtoken);
                    post.setParameter("dateFormat", dateTimeFormat);
                    post.setParameter("timeZone",ConfigInfo.getTimeZone());
                    post.setParameter("data", val);

                    Date beforeReq = new Date();
                    result = httpclient.executeMethod(post);

                    long ms = new Date().getTime() - beforeReq.getTime();
                    String timeTaken = "";

                    if (ms >= 1000) {
                        LOGGER.log(Level.INFO, "timeTaken {0}s", (new Date().getTime() - beforeReq.getTime()) / 1000);
                        timeTaken = ((new Date().getTime() - beforeReq.getTime()) / 1000) + "s";
                    } else {
                        LOGGER.log(Level.INFO, "timeTaken {0}ms", (new Date().getTime() - beforeReq.getTime()));
                        timeTaken = (new Date().getTime() - beforeReq.getTime()) + "ms";
                    }

                    LogFile.appendLog(","+dataCount);
                    LogFile.appendLog(","+result);
                    LogFile.appendLog(","+timeTaken);
                    ConfigInfo.reqCount++;
                    if (result == 200) {
                        LOGGER.log(Level.INFO, "HTTP Response status code: {0}", result);
                        ConfigInfo.dataCount += dataCount;
                        ConfigInfo.timeTakenTotal += ms;
                        ConfigInfo.reqSuccessCount++;

                        if (iData.hasNext()) {
                            LOGGER.log(Level.INFO, "about to sleep {0}", 3000);
                            Thread.sleep(3000l);
                        }
                    } else {
                        LOGGER.log(Level.INFO, "HTTP Response status code:{0}", result);
                        break;
                    }
                }
                if (result == 200) {
                    ConfigInfo.setLastRequestTime(now.getTime());
                }


                LOGGER.log(Level.INFO, "After {0} {1} {2}", new Object[]{ConfigInfo.reqCount, ConfigInfo.timeTakenTotal,ConfigInfo.dataCount});


            } catch (Exception e) {
                e.printStackTrace();
                LogFile.appendLog(e+"");
            } finally {
                if (post != null) {
                    post.releaseConnection();
                }
            }
        } catch (Exception e) {
            LogFile.appendLog(e+"");
        }
    }
}
