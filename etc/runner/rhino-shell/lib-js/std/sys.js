define(function () {
    var appProperties = {};
    /** Append property into application properties */
    var setAppProperty = function(key, value){
        appProperties[key] = value;
    }

    /** Get command line arguments */
    var getCommandLineArgs = function(){
        var tmp = [];
        for (var i=0; i<__ARGS__.length; i++){
            tmp[i] = __ARGS__[i] + "";  //Force convert to javascript string
        }
        return tmp;
    }
    
    /** Get the application home(Where app.js exists) */
    var getAppHome = function(){
        var appjsUrl = __CTX__.getClass().getResource("/app.js");
        var appjsFile = new java.io.File(appjsUrl.getPath());
        var appHome = appjsFile.getParentFile().getCanonicalPath();
        
        appHome += "";  //Force convert to javascript string
        
        //Remember it's into System Properties
        //NOTE: You can change APP_HOME if needed
        setAppProperty("APP_HOME", appHome);
        
        return appHome;
    }
    
    /** Raise an error and exit app */
    var raiseErr = function(ex, exitCode){
        exitCode = parseInt(exitCode);
        if (exitCode){
            __CTX__.setExitCode(exitCode);
        }
        if (ex instanceof java.lang.Throwable){
            throw ex;
        }else{
            throw ex;
        }
    }
    
    return {
        args: getCommandLineArgs(),
        home: getAppHome(),
        appProperties: appProperties,
        setAppProperty: setAppProperty,
        raiseErr: raiseErr
    }
});