package attendanceclient;

/**
 *
 * @author benjamin-1215
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class SQLAccess {

    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private static final int objCount = 2000;

private static final Logger LOGGER = Logger.getLogger("SQLAccess");

    public static ArrayList readFromMSSQLServer(String ftime,String ttime) {


        LOGGER.log(Level.INFO, "readFromMSSQLServer called {0} to {1}", new Object[]{ftime, ttime});
        
        Connection conn = null;

        String url = ConfigInfo.getDBurl();
        Statement stmt;
        ResultSet result = null;
        String driver = "net.sourceforge.jtds.jdbc.Driver";
        
        String databaseUserName = ConfigInfo.getDBusername();
        String databasePassword = ConfigInfo.getDBpassword();
        ArrayList<String> list = new ArrayList();
        try {
            StringBuffer query = new StringBuffer();
            query.append(ConfigInfo.getDBQuery());
            query.insert(query.indexOf("select") + 7, "top 20000 ");
            query.replace(query.indexOf("$1"), query.indexOf("$1") + 2, "'" + ftime + "'");
            
            //query.replace(query.indexOf("$1"), query.indexOf("$1") + 2, "'2015-02-03 00:00:00'");
            query.replace(query.indexOf("$2"), query.indexOf("$2") + 2, "'" + ttime + "'");

            //AttendanceClient.mainWindow.setDBInfodisplay("\n" + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            //AttendanceClient.mainWindow.setDBInfodisplay("    " + new SimpleDateFormat(dateformat).format(new Date(ConfigInfo.getLastRequestTime()))+" to "+new SimpleDateFormat(dateformat).format(new Date()));


            LOGGER.log(Level.INFO, "\n DBquery {0}", query);

            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url, databaseUserName, databasePassword);
            //conn = DriverManager.getConnection("jdbc:sqlserver://192.168.30.159:1433;databaseName=ZAttendance", "zpeople", "zpeople");
            //conn = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.30.159:1433/ZAttendance", "zpeople", "zpeople");
            stmt = conn.createStatement();
            String employeeId;
            String eventTime;
            Boolean isIn;
            result = stmt.executeQuery(query.toString());
            //result = stmt.executeQuery("select * from AttendanceEvents");
            int i=0,j=0, count=0;
            for (i = 0; i < 10; i++) {
                JSONArray out = new JSONArray();
                for (j = 0; j < objCount; j++) {
                    if (!result.next()) {
                        i=10;
                        break;
                    }
                    count++;
                    employeeId = result.getString("employeeId").trim();
                    if(employeeId.equals("")){
                        continue;
                    }
                    eventTime = result.getString("eventTime").trim();
                    isIn = result.getBoolean("isCheckIn");
                    JSONObject info = new JSONObject();
                    try {
                        info.put("empId", employeeId);
                        if (isIn) {
                            info.put("checkIn", eventTime.trim());
                        } else {
                            info.put("checkOut", eventTime.trim());
                        }
                        out.put(info);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                list.add(out.toString());
            }
            LOGGER.log(Level.INFO, "count {0}", count);
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            ConfigInfo.logString.append("\t").append(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ConfigInfo.logString.append("\t").append(ex);
                    Logger.getLogger(SQLAccess.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }

    public void readMySQLDataBase() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found !!");
            return;
        }
        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/db15294db", "root", "");

            String orgday = "2014-03-24";
            preparedStatement = connection.prepareStatement("select * from P_Attendance where ORIGINDAY = ?");
            preparedStatement.setString(1, orgday);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                // iterate through results
                System.out.println("???" + resultSet.getString("ORIGINDAY") + "==>" + resultSet.getString("FROMDATE"));
            }
            System.out.println("SQL Connection to database established!");

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console" + e);
            return;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                System.out.println("Connection closed !!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
