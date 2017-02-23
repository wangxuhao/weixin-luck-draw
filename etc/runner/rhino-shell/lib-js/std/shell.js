define(function () {
    var log = getLogger("shell.js");
    var FileUtils = Packages.org.apache.commons.io.FileUtils;
    var WildcardFileFilter = Packages.org.apache.commons.io.filefilter.WildcardFileFilter;
    
    var _formalPath = function(path, action){
        if (! path){
        	log.warn("Path is BLANK("+path+") when doing " + action);
            return null;
        }
        require(["std/utils/misc"], function(misc){
        	path = misc.joinPath(path);
        });
        if (! (path instanceof java.io.File) ){
            path = new java.io.File(new java.lang.String(path+""));
        }
        return path;
    }
    
    /** List all files which match wildcard in a folder, and call callback with it
      */
    var listFiles = function(folder, wildcard, callback){
    	path = _formalPath(folder, "listFiles");
        if (! path){
            log.warn("Path is BLANK("+path+"), no file to list");
            return;
        }

    	var fileFilter = new WildcardFileFilter(wildcard);
    	var files = path.list(fileFilter);
    	if (null==files){
    		files = [];	//如果找不到, list() 返回 null
    	}
    	for (var i = 0; i < files.length; i++) {
    		var filePath = _formalPath([path, files[i]], "Callback for listed path ["+files[i]+"]");
    		callback.call(this, filePath);
    	}
    }

    /** Makes a directory, including any necessary but non-existence parent directories;
      * support more then one directory(argument "path" as array). */
    var mkdir = function(path){
    	path = _formalPath(path, "mkdir");
        if (! path){
            log.warn("Path is BLANK("+path+"), no directory created");
            return;
        }
        FileUtils.forceMkdir(path);
        log.info("Directory ["+path+"] created.");
    }
    
    /** Deletes a file or directory(and all sub-directories). */
    var rm = function(path, quietly){
    	path = _formalPath(path, "rm");
        if (! path){
            log.warn("Path is BLANK("+path+"), nothing to delete");
            return;
        }
        if (quietly){
        	FileUtils.deleteQuietly(path);
        }else{
        	FileUtils.forceDelete(path);
        }
        log.info("File or directory ["+path+"] deleted.");
    }
    
    /** Deletes a directory recursively. */
    var rmdir = function(path){
    	path = _formalPath(path, "rmdir");
        if (! path){
            log.warn("Path is BLANK("+path+"), no directory to delete");
            return;
        }
        FileUtils.deleteDirectory(path);
        log.info("Directory ["+path+"] deleted.");
    }
    
    /** Copy file and replace place-holder variables, always use UTF-8 encoding */
    var copyTemplateFile = function(tmplPath, targetPath, dataOrProvider, thisObj){
        if (! thisObj){
            thisObj = this;
        }
        var template = FileUtils.readFileToString(new java.io.File(tmplPath), "UTF-8");
        require (["std/utils/misc"], function(replacer) {
            var result = replacer.replacePlaceHolder(template, dataOrProvider, thisObj);
            FileUtils.write(new java.io.File(targetPath), result, "UTF-8");
        });
    }
    
    /** Start a process with given environment variables */
    var startProcess = function(cmdLine, envVars, profileStamp, instanceStamp){
        return _startProcess(cmdLine, envVars, profileStamp, true, instanceStamp);
    }
    var _startProcess = function(cmdLine, envVars, profileStamp, withShutdownHook, instanceStamp){
        log.info("Start process [" + cmdLine + "]," +
                 " profileStamp=["+profileStamp+"]," +
                 " withShutdownHook=["+withShutdownHook+"]," +
                 " instanceStamp=["+instanceStamp+"]");
        var _stamp4WmicCmdLine = function(stamp){	//将 stamp 拼接到 wmic 命令行时需要的调整
        	if (stamp){
        		var tmp = stamp.replace(/\\/g, "\\\\");
        		return tmp;
        	}else{
        		return stamp;
        	}
        };

        var exitValue = 0;
        var ProcessBuilder = Packages.java.lang.ProcessBuilder;
        var VerboseProcess = Packages.com.jcabi.log.VerboseProcess;
        require (["std/os"], function(os) {
            var killByStamp = function(stamp){
                if (os.isWindows){
                    var ver = os.getProp("os.version");
                    var forceSkipWmic = os.getProp("WINDOWS_FORCE_SKIP_WMIC");
                    if (ver<=5.1 || forceSkipWmic){    //XP or below: wmic command should block stdout
                        var vbs = os.getProp("XP_PATCH_KILL_BY_CMDLINE");
                        _startProcess("cscript \"" + vbs + "\" " + stamp, {}, null, false, null);
                    }else{
                        _startProcess("wmic process where \"commandline like '%%"+_stamp4WmicCmdLine(stamp)+"%%'\" call terminate", {}, null, false, null);
                    }
                }else{
                    _startProcess("pkill -f '"+stamp+"'", {}, null, false, null);
                    java.lang.Thread.sleep(1000);      //FIXME: in linux, pkill should not terminate process immediately
                }
            };
            //If given profileStamp, kill subprocess by stamp when shutdown
            if (profileStamp) {
                log.info("Clean existing subprocess with same stamp: ["+profileStamp+"]...");
                killByStamp(profileStamp);
            }
        
            var pb;
            if (os.isWindows){
                pb = new ProcessBuilder("cmd", "/c", cmdLine);
            }else{
                pb = new ProcessBuilder("/bin/bash", "-c", cmdLine);
            }
            var env = pb.environment();
            //Put env into environment variables
            for(var o in envVars){
                env.put(new java.lang.String(o+""), new java.lang.String(envVars[o]+""));
            }
            //Start process
            var p = pb.start();
            //Register shutdown hook to destory process
            if (withShutdownHook){
                var thread = new java.lang.Thread(new java.lang.Runnable({
                    run: function () {
                        log.info("Stop process when JVM shutdown: " + pb.command());
                        p.destroy();
                        //If given instanceStamp, kill subprocess by stamp when shutdown
                        if (instanceStamp) {
                            log.info("Clean subprocess with same stamp: ["+instanceStamp+"]...");
                            killByStamp(instanceStamp);
                        }
                    }
                }), "ShutdownHook-" + pb.command());
                java.lang.Runtime.getRuntime().addShutdownHook(thread);
            }
            //Echo it's stdout and stderr
            (new VerboseProcess(p)).stdoutQuietly();
            //Check exit code
            exitValue = p.exitValue();
            log.info(">>> 执行结果: " + exitValue + " (" + cmdLine + ")");
        });
        
        return exitValue;
    }
    
    return {
    	listFiles: listFiles,
    	rm: rm,
        mkdir: mkdir,
        rmdir: rmdir,
        copyTemplateFile: copyTemplateFile,
        startProcess: startProcess
    }
});