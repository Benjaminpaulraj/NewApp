/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package action;

import attendanceclient.AttendanceThread;
import attendanceclient.ConfigInfo;
import attendanceclient.LogFile;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;


public class ClientAction extends org.apache.struts.action.Action {

    private static AttendanceThread attendanceThread;
    private static final Logger LOGGER = Logger.getLogger("ClientAction");

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        response.setCharacterEncoding("UTF-8");
        String mode = request.getParameter("mode");
        JSONObject confobj = ConfigInfo.getConfInfo();
        PrintWriter out = response.getWriter();
        
        
        if (mode == null) {
            out.print("invalid input");
            return null;
        }
        
        LOGGER.log(Level.INFO, "server request ", mode);
        
        if (attendanceThread == null) {
            attendanceThread = new AttendanceThread();
        }
        if (mode.equals("getInfo")) {
            JSONObject obj = new JSONObject();
            obj.put("isRunning", attendanceThread.isRunning());
            
            if (!attendanceThread.isRunning()) {
                JSONObject info = new JSONObject();
                info.put("dbquery", ConfigInfo.getDBQuery());
                info.put("dburl", ConfigInfo.getDBurl());
                info.put("dbuname", ConfigInfo.getDBusername());
                info.put("dbpword", "password");
                
                info.put("proxyUname", ConfigInfo.getProxyUsername());
                info.put("proxyPwd", "password");
                info.put("proxyHostIP", ConfigInfo.getProxyHostIP());
                info.put("proxyPort", ConfigInfo.getProxyHostPort());
                
                info.put("authtoken", "authtoken");
                info.put("lastRequestTime", ConfigInfo.getLastRequestTime());
                info.put("lastRequestTimeInDate", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(ConfigInfo.getLastRequestTime())));
                info.put("sleepTime", ConfigInfo.getSleepTime());
                info.put("timeZone", ConfigInfo.getTimeZone());
                obj.put("info", info);
            } else {
                obj.put("lastRequestTime", new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)").format(new Date(ConfigInfo.getLastRequestTime())));
                if (new Date().getTime() - ConfigInfo.getLastRequestTime() < ConfigInfo.getSleepTime()) {
                    obj.put("nextUpdateSecs", (ConfigInfo.getSleepTime() - new Date().getTime() + ConfigInfo.getLastRequestTime()) / 1000);
                } else {
                    obj.put("nextUpdateSecs", ConfigInfo.getSleepTime() / 1000);
                }
                JSONObject statinfo = ConfigInfo.getstatinfo();
                obj.put("statinfo", statinfo);
                obj.put("logs", LogFile.getLogs());
                obj.put("offsetVal", TimeZone.getTimeZone(ConfigInfo.getTimeZone()).getRawOffset());
                obj.put("time", new Date().getTime());
            }
            obj.put("currentTimeGMT1", new Date());
            out.print(obj);
        } else if (mode.equals("start") || mode.equals("resume")) {
            JSONObject obj = new JSONObject();
            if (mode.equals("start")) {
                if (request.getParameter("authtoken") != null && !request.getParameter("authtoken").equals("")) {
                    obj.put("authtoken", request.getParameter("authtoken"));
                }
                if (request.getParameter("sleepTime") != null && !request.getParameter("sleepTime").equals("")) {
                    obj.put("sleepTime", request.getParameter("sleepTime"));
                }
                if (request.getParameter("lastRequestTime") != null && !request.getParameter("lastRequestTime").equals("")) {
                    obj.put("lastRequestTime", request.getParameter("lastRequestTime"));
                }
                
                if (request.getParameter("proxyHostIP") != null && !request.getParameter("proxyHostIP").equals("")) {
                    obj.put("proxyHostIP", request.getParameter("proxyHostIP"));
                }
                if (request.getParameter("proxyPort") != null && !request.getParameter("proxyPort").equals("")) {
                    obj.put("proxyPort", request.getParameter("proxyPort"));
                }
                if (request.getParameter("proxyUname") != null && !request.getParameter("proxyUname").equals("")) {
                    obj.put("proxyUname", request.getParameter("proxyUname"));
                }
                if (request.getParameter("proxyPwd") != null && !request.getParameter("proxyPwd").equals("")) {
                    obj.put("proxyPwd", request.getParameter("proxyPwd"));
                }
                
                if (request.getParameter("dburl") != null && !request.getParameter("dburl").equals("")) {
                    obj.put("dburl", request.getParameter("dburl"));
                }
                if (request.getParameter("dbuname") != null && !request.getParameter("dbuname").equals("")) {
                    obj.put("dbuname", request.getParameter("dbuname"));
                }
                if (request.getParameter("dbpword") != null && !request.getParameter("dbpword").equals("")) {
                    obj.put("dbpword", request.getParameter("dbpword"));
                }
                if (request.getParameter("dbquery") != null && !request.getParameter("dbquery").equals("")) {
                    obj.put("dbquery", request.getParameter("dbquery"));
                }
                if (request.getParameter("timeZone") != null && !request.getParameter("timeZone").equals("")) {
                    obj.put("timeZone", request.getParameter("timeZone"));
                }
                ConfigInfo.setConfigInfo(obj);
            }
            attendanceThread.startSync();
            obj = new JSONObject();
            obj.put("isRunning", attendanceThread.isRunning());
            obj.put("filepath", new File("ss").getAbsolutePath());
            out.print(obj);
            
            
        } else if (mode.equals("stop")) {
            
            attendanceThread.stopSync();
            out.print(attendanceThread.isRunning());
            
            attendanceThread = null;
            
        } else if (mode.equals("setTimeZone")) {
        } else if (mode.equals("getLogs")) {
            
            JSONObject obj = new JSONObject();
            obj.put("statinfo", ConfigInfo.getstatinfo());
            obj.put("logs", LogFile.getLogs());
            obj.put("logs1", confobj);
            out.print(obj);
            
        } else {
            out.print("invalid input");
        }
        return null;
    }
}
