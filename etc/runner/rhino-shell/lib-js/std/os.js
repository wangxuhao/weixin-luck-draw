define(function () {
    var _export = {};
    require (["std/sys"], function(sys) {
        /** OS name in lower case (linux, windows, ...) */
        var name = java.lang.System.getProperty("os.name").toLowerCase();
        var isWindows = function(){
            return (name.indexOf("win") >= 0);
        };
        var isLinux = function(){
            return (name.indexOf("linux") >= 0);
        };
        var isMac = function() {
            return (name.indexOf("mac") >= 0);
        };
        var isUnix = function() {
            return (name.indexOf("nix") >= 0 || name.indexOf("nux") >= 0 || name.indexOf("aix") > 0 );
        };
        var isSolaris = function() {
            return (name.indexOf("sunos") >= 0);
        };
        
        /** The external javascript object to provide attributes */
        var externalAttrs = {};
        /** Change the external attributes object */
        var regExtAttrs = function(obj){
        	externalAttrs = obj;
        }
        
        /** Get java system property, environment variable or external attributes */
        var getProp = function(name, defaultValue){
            var tmp = sys.appProperties[name];
            if (null == tmp){
                tmp = java.lang.System.getProperty(name);
            }
            if (null == tmp){
                tmp = java.lang.System.getenv(name);
            }
            if (null == tmp){
            	tmp = externalAttrs[name];
            }
            if (null == tmp){
                tmp = defaultValue;
            }
            //If tmp is java string, convert it to js string, but other object shouldn't convert automatically
            if (null!=tmp && tmp instanceof java.lang.String){
                tmp = tmp + "";
            }
            return tmp;
        }
        
        var _replaceWithSystemProp = function(string){
            var result;
            require (["std/utils/misc"], function(replacer) {
                result = replacer.replacePlaceHolder(string, function(key){
                    return getProp(key);
                });
            });
            return result;
        }
        
        /** The root folder, default is the path of app.js */
        var root = sys.home;
        /** Change root folder */
        var chroot = function(newRoot){
            var newRoot = normalizePath(newRoot);
            root = newRoot;
        }
        
        /** 
         * Normalize a path with the specified basePath(parnet folder), or based on the AppRoot
         *  - path - The path to normalize
         *  - basePath - If path is not absolute, use basePath as it parent folder; optional, default is AppRoot;
         *  - strOnly - The path should be not a really path(for example, with wildcard); optional, default is false;
         */
        var normalizePath = function(path, basePath, strOnly){
            if (null==path) path="";
            require(["std/utils/misc"], function(misc){
            	path = misc.joinPath(path);		//If path is Array, join them
            });
            //Replace ${XXX} in path with java system property or environment variable
            path = _replaceWithSystemProp(path);
            //Detect the "path" is absolute or not
            path = new java.lang.String(path);
            var isAbs = false;
            if (path.startsWith("/")) isAbs = true;
            if (path.startsWith("\\")) isAbs = true;
            if ( isWindows() && (path.length()>1) && (path.substr(1,1)==":") ) isAbs = true;
            //Calculate the full path
            if (! isAbs){
                if (! basePath) {
                    basePath = root;
                }
                path = basePath + java.io.File.separator + path;
            }
            //Path string with wildcard should raise error if call new java.io.File(path)
            if (strOnly){
            	return path;
            }
            //Get normalized Path string
            var file = new java.io.File(path);
            try{
            	return file.getCanonicalPath()+"";  //Force convert to javascript string
            }catch(ex){
            	//在 Windows 下对于 .../*.jar 这样的路径无法执行 getCanonicalPath
            	return file+"";
            }
        }
        
        /** Check file/folder exists or not; support wildcard in file name part */
        var fileExists = function(path){
            if (! path) return false;
            
            if (path.join){ //If path is Array...
                var totalResult = false;
                for (var i=0; i<path.length; i++){
                    path[i] = path[i]+"";   //Force to javascript string
                    totalResult = fileExists(path[i]);
                    if (totalResult==tmp){
                        return false;
                    }
                }
                return totalResult;
            }else{
                path = _replaceWithSystemProp(path);
                //Find the folder part and file part of path 
                path = path.replace(/\\/g, "/");
                var tmp = path.split("/");
                var folder = [];
                var file = "";
                for(var i=0; i<tmp.length; i++){
                    if (i==(tmp.length-1)){
                        file = tmp[i];
                    }else{
                        folder[folder.length] = tmp[i];
                    }
                }
                folder = folder.join(java.io.File.separator);
                //Check folder exists or not
                folder = new java.io.File(normalizePath(folder));
                if (! folder.exists()){
                    return false;
                }
                //Check file exists, support wildcard
                var fileFilter = new Packages.org.apache.commons.io.filefilter.WildcardFileFilter(file);
                var files = folder.list(fileFilter);
                return (files.length > 0);
            }
        }
        
        /** Same as fileExists but return the missing file/folders as an array */
        var findMissingFiles = function(path){
            if (! path) return [];
            if (! path.join) path = [path];
            var result = [];
            require (["std/utils/misc"], function(misc) {
                misc.arrayForEach(path, function(f){
                    if (! fileExists(f)){
                        result[result.length] = f;
                    }
                });
            });
            return result;
        }
        
        /** Read text file, return null if file not found */
        var readTextFile = function(file){
            var FileUtils = Packages.org.apache.commons.io.FileUtils;
            var f = new Packages.java.io.File(file);
            if (f.exists()){
                var s = FileUtils.readFileToString(f, "UTF-8");
                return s + "";
            }else{
                return null;
            }
        }
        
        /** Write text file */
        var writeTextFile = function(file, text){
            var FileUtils = Packages.org.apache.commons.io.FileUtils;
            var f = new Packages.java.io.File(file);
            FileUtils.writeStringToFile(f, new java.lang.String(text+""), "UTF-8");
        }
        
        /** Get Mac Address (of first network adapter) */
        var getMacAddr = function() {
            var hexByte = function(b){
                var s = "000000" + java.lang.Integer.toHexString(b);
                return s.substring(s.length - 2);
            }

            var el = java.net.NetworkInterface.getNetworkInterfaces();
            while (el.hasMoreElements()) {
                var mac = el.nextElement().getHardwareAddress();
                if ( null==mac  || mac.length!=6 ){
                    continue;
                }
                var mac_s = hexByte(mac[0]) + "-" + hexByte(mac[1]) + "-"
                        + hexByte(mac[2]) + "-" + hexByte(mac[3]) + "-"
                        + hexByte(mac[4]) + "-" + hexByte(mac[5]);
                return mac_s;
            }
            return "";
        }
        
        _export = {
            isWindows:isWindows(), isLinux:isLinux(), isMac:isMac(), isUnix:isUnix(), isSolaris:isSolaris(),
            regExtAttrs: regExtAttrs,
            normalizePath: normalizePath,
            chroot: chroot,
            getProp: getProp,
            fileExists: fileExists,
            findMissingFiles: findMissingFiles,
            readTextFile: readTextFile,
            writeTextFile: writeTextFile,
            getMacAddr: getMacAddr
        }
    });
    return _export;
});