var matched, browser;

jQuery.uaMatch = function( ua ) {
    ua = ua.toLowerCase();

    var match = /(chrome)[ \/]([\w.]+)/.exec( ua ) ||
    /(webkit)[ \/]([\w.]+)/.exec( ua ) ||
    /(opera)(?:.*version|)[ \/]([\w.]+)/.exec( ua ) ||
    /(msie) ([\w.]+)/.exec( ua ) ||
    ua.indexOf("compatible") < 0 && /(mozilla)(?:.*? rv:([\w.]+)|)/.exec( ua ) ||
    [];

    return {
        browser: match[ 1 ] || "",
        version: match[ 2 ] || "0"
    };
};

matched = jQuery.uaMatch( navigator.userAgent );
browser = {};

if ( matched.browser ) {
    browser[ matched.browser ] = true;
    browser.version = matched.version;
}

// Chrome is Webkit, but Webkit is also Safari.
if ( browser.chrome ) {
    browser.webkit = true;
} else if ( browser.webkit ) {
    browser.safari = true;
}

jQuery.browser = browser;

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
    $(".js-example-placeholder-single").select2({
        placeholder: "Today",
        allowClear: true
    });
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
        showLogsPage(res);
    });
}

function showConfigPage(res){
    var param={};
    param.mode="getConfInfo";
    $.post("/ZAttendance/ClientAction.do",param,function(res){
        if(!res){
            return;
        }
        res = JSON.parse(res);
        $("#authToken").val(res.authtoken);
        $("#startTime").val(res.lastRequestTimeInDate.split(" ")[1]);
        $("#sleepTime").val(res.sleepTime / (60 * 1000));
        $("#host").val(res.proxyHostIP);
        $("#port").val(res.proxyPort);
        $("#proxyUserName").val(res.proxyUname);
        $("#proxyPassword").val(res.proxyPwd);
        $("#dbConnectionUrl").val(res.dburl);
        $("#dbUserName").val(res.dbuname);
        $("#dbPassword").val(res.dbpword);
        $("#dbQuery").val(res.dbquery);
        $("#timeZone").val(res.timeZone);
        $('.dvmodal-overlay').fadeIn(200, function() {
            $('#dvmodal-lft').animate({
                'right': '0'
            }, 400);
        });
        $("#startTime")[0].val = res.lastRequestTimeInDate.split(" ")[1];
        $("#sleepTime")[0].val = res.sleepTime / (60 * 1000);
        $("#host")[0].val = res.proxyHostIP;
        $("#port")[0].val = res.proxyPort;
        $("#proxyUserName")[0].val = res.proxyUname;
        $("#dbConnectionUrl")[0].val = res.dburl;
        $("#dbUserName")[0].val = res.dbuname;
        $("#dbQuery")[0].val = res.dbquery;  
    });
}

function hideConfigPage(){
    $('#dvmodal-lft').animate({
        'right': '-75%'
    }, 400, function() {
        $('.dvmodal-overlay').fadeOut('fast');
    });
}
  
function getLogs(fileName){
    if(timer!=null){
        clearTimeout(timer);
    }
    var param={};
    param.mode="getLogs";
    if(fileName!=null && fileName.trim()!=""){
        param.fileName=fileName;
    }else{
        getInfo();
        return;
    }
    $.post("/ZAttendance/ClientAction.do",param,function(res){
        if(!res){
            return;
        }
        res = JSON.parse(res);
        $("#logs1")[0].innerHTML = res.logs;
    });
}  

function showLogsPage(res){
    $("#logs1")[0].innerHTML = res.logs;
    setReportVal(res.statinfo);
    $("#lastRefreshTime span")[0].innerHTML = res.lastRequestTime;
    $("#selectDay option").remove();
    for(var i=res.logsFiles.length-1;i>=0;i--){
        $("#selectDay").append("<option>"+res.logsFiles[i]+"</option>")
    }
    if(res.isRunning){
        nextUpdateSecs=res.nextUpdateSecs;
        diffTime=res.offsetVal;
        displayClock();
        $("#stopSync").show();
        $("#resumeSync").hide();
        $("#resumeSync2").hide();
    }else{
        $("#stopSync").hide();
        $("#resumeSync").show();
        $("#resumeSync2").show();
    }
    
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
    hideConfigPage();
    $.post("/ZAttendance/ClientAction.do",param,function(res){
        if(!res){
            return;
        }
        res = JSON.parse(res);
        getInfo();
    });
}

function checkTime(i) {
    if (i<10) {
        i = "0" + i
    }
    return i;
}
function Resume(){
    var param={};
    param.mode="resume";
    hideConfigPage();
    $("#stopSync").show();
    $("#resumeSync").hide();
    $("#resumeSync2").hide();

    $.post("/ZAttendance/ClientAction.do",param,function(){
        setTimeout(function(){
            getInfo();
        },3000);
        
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
    
setReportVal=function(obj){
    if(obj==null)return;
    $("#numOfDataSent")[0].innerHTML = obj.dataCount;
    $("#numberOfSuccreqSent")[0].innerHTML = obj.reqSuccesCount;
    $("#failedReq")[0].innerHTML = obj.reqFailCount;
    $("#averageTime")[0].innerHTML = obj.averageTimeTaken;
}
