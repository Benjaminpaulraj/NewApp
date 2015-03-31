<!DOCTYPE html>
<html>
    <head>
        <title>Log Window</title>
        <script type="text/javascript"  src="js/jquery-ui-1.8.24/jquery-1.8.2.js"></script>
        <script type="text/javascript" src="js/attendance.js"></script>
        <script type="text/javascript" src="js/jquery.timeentry.js"></script>
        <style type="text/css">
            #addInput .firstHalf{width:500px;float: left;margin-top: 120px;}
            #addInput .secondHalf{float: left;width: 650px;margin-left: 40px;margin-top: 120px;}
            #info .firstHalf{width:406px;float: left;margin-top: 10px;}
            #info .secondHalf{float: left;width: 410px;margin-left: 40px;margin-top: 10px;}
            ul{list-style:none;padding:10px 10px;color: #666}
            #addInput li{padding:15px 0px}
            #info li{padding:8px 0px}
            li span{display: inline-block;}
            li label{ margin-left: 30px;}
            h2{margin-left: 10px;color: gray}
            #addInput .inputs{border: 1px solid #ddd;color: #666;padding: 5px;font-size: 14px;vertical-align: top;}
            #logTime{float: left;height: 300px;margin-top: 10px;border:1px solid black;width: 800px;margin-left: 10px;}
            #empty{float: left;height: 300px;margin-top: 10px;border:1px solid black;width: 800px;margin-left: 10px;}
            .textarea{border: 1px solid #ddd;color: #666;padding: 5px;font-size: 14px;height: 115px;width:350px;box-shadow: 0 1px 1px #eee inset;resize: none; font-family: Arial       }
            #addInput li span{width: 171px;vertical-align: top;}
            #info li span{width: 180px;font-size: 15px;}
            li em{color: #999;float: left;font-size: 13px;padding-top: 5px;width: 100%;}
            #addInput .submit{position: relative;height: 25px;width: 115px;top: 52px;left: -141px;cursor: pointer;}
            #info .submit{position: relative;height: 25px;width: 65px;top: 0px;left: 815px;cursor: pointer}
            #host{width: 110px;}
            #port{width: 60px;margin-left: -20px;}
            .req{border-color: red;}
            .resume{position: relative;height: 25px;width: 115px;top: 52px;left: -94px;cursor: pointer}
            #displayClock{position: absolute;right: 75px;top: 50px;height: 25px;width: 306px;font-size : 20px}
            #displayServerTime{position: absolute;right: 75px;top: 125px;height: 25px;width: 306px;font-size : 20px}
            #displayClock{position: absolute;right: 75px;top: 50px;height: 25px;width: 306px;font-size : 20px}
            #displayServerTimeZone{position: absolute;right: 75px;top: 50px;height: 25px;width: 306px;font-size : 20px}
            #nextRefresh{position: absolute;right: 0px;top: 130px;height: 25px;width: 380px;display: none;font-size : 20px}
            #lastRefreshTime{position: absolute;right: 0px;top: 90px;height: 25px;width: 380px;font-size : 20px;display: none;}
            #logs1{position: relative;border: 1px solid black;height: 450px;width: 1000px;top: 265px;margin: auto;overflow: auto;padding: 10px;display: none;}
            #logsHeader{position: relative;height: 10px;width: 1000px;display: none;top: 265px;margin: auto;padding: 10px;}
            
        </style>
    </head>
    <body onload="onLoad()" style="margin:0px;padding: 0px;overflow:scroll">
        <div id="info" style="position: absolute;top: 0px;left: 0px;right: 0px;bottom: 0px;display: none" >
            <div style="margin:auto;width:880px;height:300px;font-family: Arial">
                <div class="firstHalf">
                    <ul>
                        <li>
                            <span>Number of data sent</span> : <span id="numOfDataSent"></span>
                        </li>
                        <li>
                            <span>Average time taken</span> : <span id="averageTime"></span>
                        </li>
                    </ul>
                </div>

                <div class="secondHalf">
                    <ul>
                        <li>
                            <span>Total number of successful request sent</span> : <span id="numberOfSuccreqSent"></span>
                        </li>
                        <li>
                            <span>Request failed</span> : <span id="failedReq"></span>
                        </li>
                    </ul>
                </div>
                <input type="button" class="submit" value="Stop Sync" onclick="stop();">
            </div>
        </div>
        
        <div id="addInput" style="position: absolute;top: 0px;left: 0px;right: 0px;bottom: 0px;display:none" >
            <div style="margin:auto;width:1213px;height:500px;font-family: Arial">
                <div class="firstHalf" style="border-right: 1px solid black">
                    <h2>Server Request configuration</h2>
                    <ul>
                        <li>
                            <span><req style="color: red">*</req>Authtoken</span>  <label><input id="authToken" class="inputs" type="password"></label>
                        </li>
                        <li>
                            <span>TimeZone</span>  
                            <div style="font-size:13px;margin-left:210px;margin-top: -15px">
                                <input type="text" id="timeZone" checked="checked"/>
                            </div>
                        </li>
                        <li>
                            <span>Start Time</span>  <label><input id="startTime" class="inputs" type="text"></label>
                            <div style="font-size:12px;margin-left:210px;color: #999">Example Time format : HH:mm:ss</div>
                        </li>
                        <li>
                            <span>Sleep Time(mins)</span>  <label><input id="sleepTime" class="inputs" type="text"></label>
                        </li>
                        <li>
                            <span>Proxy host and port</span>  <label><input id="host" class="inputs" type="text"></label><label><input id="port" class="inputs" type="text"></label>
                        </li>
                        <li>
                            <span>Proxy username</span>  <label><input id="proxyUserName" class="inputs" type="text"></label>
                        </li>
                        <li>
                            <span>Proxy password</span>  <label><input id="proxyPassword" class="inputs" type="password"></label>
                        </li>
                    </ul>
                </div>

                <div class="secondHalf">
                    <h2>Database Configuration</h2>
                    <ul>
                        <li>
                            <span><req style="color: red">*</req>Connection URL </span>  <label><input id="dbConnectionUrl" style="width:350px" class="inputs" type="text"></label>
                            <em>EX : jdbc:jtds:sqlserver://localhost:1433/ZAttendance</em>
                        </li>
                        <li>
                            <span><req style="color: red">*</req>User name</span>  <label><input id="dbUserName" class="inputs" type="text"></label>
                        </li>
                        <li>
                            <span><req style="color: red">*</req>Password</span>  <label><input id="dbPassword" class="inputs" type="password"></label>
                        </li>
                        <li>
                            <span><req style="color: red">*</req>SQL Query</span>  <label><textarea id="dbQuery" class="textarea" ></textarea><em>EX : select employeeId,eventTime,isCheckin from Attendance where evenTime > $1 and $2 </em></label>
                        </li>
                    </ul>
                </div>
                <input type="button" class="submit" value="Save & Start Sync" onclick="saveStart();">
                <input type="button" class="resume" value="Resume" onclick="resume();">
            </div>
        </div>
        <div id="displayServerTime" style="display:none">
            Server Time <span style="color: #4a89dc" ></span>
        </div>

        <div id="displayClock">
            Next update in <span style="color: #4a89dc" ></span>
        </div>
        
        
        <div id="lastRefreshTime">
            Last updated time : <span style="color: #4a89dc"></span>
        </div>
        
        <div id="logsHeader">
            <table style="margin-top: -6px;font-size: 20px">
                <tr height="28">
                    <td width="170px">Time</td> 
                    <td width="168px">From</td>
                    <td width="168px">To</td> 
                    <td width="60px">Count</td>
                    <td width="60px">Status</td> 
                    <td width="100px">Time taken</td>
                </tr>
            </table>
        </div>
        <div id="logs1">
        </div>
    </body>
</html>
