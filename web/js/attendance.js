var Attendance={};
Attendance.sleeptime = 1;
Attendance.lastReqTime = 0;

function onLoad(){
    $("#startTime").timeEntry({
        show24Hours: true, 
        spinnerImage: '', 
        showSeconds: true
    });        
    $("#authToken").focus(function() {
        if($(this).val()=="authtoken"){
            $(this).val("");
        }
    });
    $("#dbPassword").focus(function() {
        if($(this).val()=="password"){
            $(this).val("");
        }
    });
    $("#proxyPassword").focus(function() {
        if($(this).val()=="password"){
            $(this).val("");
        }
    });
    $(".inputs").focus(function(){
        $(this).css("border-color", "#a0bed9");
    })
    $("#dbQuery").focus(function(){
        $(this).css("border-color", "#a0bed9")
    })
    $("#authToken").blur(function() {
        if($(this).val()==""){
            $(this).val("authtoken");
        }
    } );
    $("#dbPassword").blur(function() {
        if($(this).val()==""){
            $(this).val("password");
        }
    } );
    $("#proxyPassword").blur(function() {
        if($(this).val()==""){
            $(this).val("password");
        }
    } );
    $(".inputs").blur(function(){
        $(this).css("border-color", "#ddd")
    })
    $("#dbQuery").blur(function(){
        $(this).css("border-color", "#ddd")
    })
    getInfo();
//displayClock()
}
var nextUpdateSecs=120;
var timer=null;
var diffTime=0;
function displayClock() {
    if(nextUpdateSecs==null){
        return;
    }
    if(nextUpdateSecs<=-1){
        nextUpdateSecs=60;
        getInfo();
        return;
    }
    
    var now= new Date(new Date().getTime()+diffTime);
    
    $("#displayClock span")[0].innerHTML = nextUpdateSecs--+"secs";
    timer=setTimeout(function(){
        displayClock();
    },1000);
}

function twoDigits(num){
    return num > 9 ? "" + num: "0" + num;
}

function getInfo(){
    if(timer!=null){
        clearTimeout(timer);
    }
    var param={};
    param.mode="getInfo";
    $.post("/ZAttendance/ClientAction.do",param,function(res){
        if(!res){
            return;
        }
        res = JSON.parse(res);
        if (res.isRunning == true){
            showLogsPage(res);
        } else{
            showConfigPage(res);
        }
    });
}

function showConfigPage(res){
    $("#info").hide();
    $("#logs").hide();
    $("#displayClock").hide();
    $("#logsHeader").hide();
    $("#addInput").show();
    $("#lastRefreshTime").hide();
    $("#authToken").val(res.info.authtoken);
    $("#startTime").val(res.info.lastRequestTimeInDate.split(" ")[1]);
    $("#sleepTime").val(res.info.sleepTime / (60 * 1000));
    $("#host").val(res.info.proxyHostIP);
    $("#port").val(res.info.proxyPort);
    $("#proxyUserName").val(res.info.proxyUname);
    $("#proxyPassword").val(res.info.proxyPwd);
    $("#dbConnectionUrl").val(res.info.dburl);
    $("#dbUserName").val(res.info.dbuname);
    $("#dbPassword").val(res.info.dbpword);
    $("#dbQuery").val(res.info.dbquery);
    $("#timeZone").val(res.info.timeZone);
    $("#logs1")[0].style.display="none";
}
  

function showLogsPage(res){
    $("#info").show();
    $("#logs").show();
    $("#displayClock").show();
    $("#logsHeader").show();
    $("#addInput").hide();
    $("#lastRefreshTime").show();
    $("#logs1")[0].innerHTML = res.logs;
    $("#logs1")[0].style.display="block";
    Attendance.setValues(res.statinfo);
    $("#lastRefreshTime span")[0].innerHTML = res.lastRequestTime;    
    nextUpdateSecs=res.nextUpdateSecs;
    diffTime=res.offsetVal;
    displayClock();
}

function saveStart(){
        
    var param={};
    param.mode="start";
    if($("#authToken").val().trim() != ""  && $("#authToken").val().trim() != "authtoken"){
        param.authtoken = $("#authToken").val().trim();
    }else if($("#authToken").val().trim() == ""){
        $("#authToken").css("border-color", "red");
        return;
    }
    if($("#startTime").val().trim() != "" && $("#startTime")[0].val != $("#startTime").val().trim()){
        var startTimeVal = $("#startTime").val().trim();
        var currDate = new Date();
        var date = checkTime(currDate.getDate());
        var month = checkTime(currDate.getMonth()+1);
        var year = checkTime(currDate.getFullYear());
        param.lastRequestTime = date+"/"+month+"/"+year +" "+startTimeVal;
    }
    if($("#sleepTime").val().trim() != "" && $("#sleepTime")[0].val != $("#sleepTime").val().trim()){
        param.sleepTime = $("#sleepTime").val().trim();
    }
    if($("#host").val().trim() != "" && $("#host")[0].val != $("#host").val().trim()){
        param.proxyHostIP = $("#host").val().trim();
    }
    if($("#port").val().trim() != "" && $("#port")[0].val != $("#port").val().trim()){
        param.proxyPort = $("#port").val().trim();
    }
    if($("#proxyUserName").val().trim() != "" && $("#proxyUserName")[0].val != $("#proxyUserName").val().trim()){
        param.proxyUname = $("#proxyUserName").val().trim();
    }
    if($("#proxyPassword").val().trim() != "" && $("#proxyPassword").val().trim() != "password"){
        param.proxyPwd = $("#proxyPassword").val().trim();
    }
    if($("#dbConnectionUrl").val().trim() != "" && $("#dbConnectionUrl")[0].val != $("#dbConnectionUrl").val().trim()){
        param.dburl = $("#dbConnectionUrl").val().trim();
    }else if($("#dbConnectionUrl").val().trim() == ""){
        $("#dbConnectionUrl").css("border-color", "red")
        return;
    }
    if($("#dbUserName").val().trim() != "" && $("#dbUserName")[0].val != $("#dbUserName").val().trim()){
        param.dbuname = $("#dbUserName").val().trim();
    } else if($("#dbUserName").val().trim() == ""){
        $("#dbUserName").css("border-color", "red")
        return;
    }
    if($("#dbPassword").val().trim() != "" && $("#dbPassword").val().trim() != "password"){
        param.dbpword = $("#dbPassword").val().trim();
    }else if($("#dbPassword").val().trim() == ""){
        $("#dbPassword").css("border-color", "red")
        return;
    }
    if($("#dbQuery").val().trim() != "" && $("#dbQuery")[0].val != $("#dbQuery").val().trim()){
        param.dbquery = $("#dbQuery").val().trim();
    }else if($("#dbQuery").val().trim() == ""){
        $("#dbQuery").css("border-color", "red")
        return;
    }
    if($("#timeZone").val().trim() != "" && $("#timeZone")[0].val != $("#timeZone").val().trim()){
        param.timeZone = $("#timeZone").val().trim();
    }else if($("#timeZone").val().trim() == ""){
        $("#timeZone").css("border-color", "red")
        return;
    }    

    $.post("/ZAttendance/ClientAction.do",param,function(res){
        if(!res){
            return;
        }
        res = JSON.parse(res);
        if (res.isRunning == true){
            getInfo();
        } else{
            showConfigPage(res);
        }
    });
}

function checkTime(i) {
    if (i<10) {
        i = "0" + i
    }
    return i;
}
function resume(){
    var param={};
    param.mode="resume";
    $.post("/ZAttendance/ClientAction.do",param,function(){
        getInfo();
    });
}
    
function stop(confirmed){
    
    if(!confirmed && confirm("Do you really want to stop ?")){
        stop(true);
    }else{
        return;
    }
    
    var param={};
    param.mode="stop";
    $.post("/ZAttendance/ClientAction.do",param,function(res){
        getInfo();
    });
}
    
Attendance.setValues=function(obj){
    $("#numOfDataSent")[0].innerHTML = obj.dataCount;
    $("#numberOfSuccreqSent")[0].innerHTML = obj.reqSuccesCount;
    $("#failedReq")[0].innerHTML = obj.reqFailCount;
    $("#averageTime")[0].innerHTML = obj.averageTimeTaken;
}
