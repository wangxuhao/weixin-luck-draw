define(function () {
    var log = getLogger("webapp.js");

    require (["std/sys", "std/os", "std/shell", "app/env"], function(sys, os, shell, env) {
    	shell.rmdir(os.normalizePath("${TOMCAT_HOME}/conf/Catalina/localhost"));

        //处理 JDBC Driver 并记入 env(注意其中“type”通常只用于 Yigo)
        var driverTable = {
            "db2": {type:0, driver: "com.ibm.db2.jcc.DB2Driver"},
            "ora": {type:1, driver: "oracle.jdbc.driver.OracleDriver"},
            "jtd": {type:2, driver: "net.sourceforge.jtds.jdbc.Driver"},
            "sql": {type:2, driver: "com.microsoft.sqlserver.jdbc.SQLServerDriver"},
            "mys": {type:3, driver: "com.mysql.jdbc.Driver"},
        };
        var jdbcUrlKey = env.JDBC_URL.substr(5,3);
        var jdbcDriver = driverTable[jdbcUrlKey]||{type:-1, driver:"unknown"};
        env.DB_TYPE = jdbcDriver.type;
        env.JDBC_DRIVER = jdbcDriver.driver;
        
        //检查并强制包含 “yigo-farm” 插件, 以兼容原有项目
        (function(){
        	var yigoFarmExists = false;
        	var ps = env.options.plugins;
        	for(var i=0; i<ps.length; i++){
        		var plugin = ps[i];
        		if ("web-apps"==plugin.name){
        			yigoFarmExists = true;
        			break;
        		};
        	}
        	if (!yigoFarmExists){
        		log.info("未注册 yigo-farm 插件, 将使用默认值注册此插件, 以兼容原有的 profile.js ...");
        		//FIXME: 此处 copy 了 env 中处理插件的逻辑
        		var p = {
        			name: "web-apps",
	        		path: "${PRODUCT_REPO}/web-apps",
	        		options: {},
	        		onDeploy: function(env){
	            		sys.raiseErr("插件 ["+pluginPath+"] 中未定义 [onDeploy]", -9)
	            	}
        		};
        		env.__preparePlugin(p);
        		//ps[ps.length] = p;
                ps.splice(0,0,p);   //yigo-farm 需要作为第一个插件注册
        	}
        })();

        //简单的插件依赖检查
        (function(){
            var _doCheck = function(p, ps){
                if (p.dependencies && p.dependencies.length){
                    var _check = function(selfName, depName, ps){
                        for(var i=0; i<ps.length; i++){
                            var plugin = ps[i];
                            if (depName==plugin.name){
                                return true;    //在找到自己之前找到了依赖, 通过检查
                            };
                            if (selfName==plugin.name){
                                //在找到依赖之前找到了自己, 说明当前 plugin 定义在依赖之前, 报错
                                throw "使用插件"+selfName+",需要先定义依赖插件: " + depName;
                            }
                        }
                    }
                    for (var i=0; i<p.dependencies.length; i++){
                        _check(p.name, p.dependencies[i], ps);
                    }
                }
            };
        	var ps = env.options.plugins;
        	for(var i=0; i<ps.length; i++){
        		var plugin = ps[i];
        		_doCheck(plugin, ps);
        	}
        })();

        //执行 plugins onDeploy
        (function(){
        	var p = env.options.plugins;
        	for(var i=0; i<p.length; i++){
        		var plugin = p[i];
        		log.info("执行插件: [" + plugin.path + "] ...");
        		plugin.onDeploy(env);
        	}
        })();
        
        //附加操作 - 部署注册的 WebApp
        (function(){
            var _deploy = function(appName, docBase, aliases, classpaths){
                var fileName;
                if ("/"==appName){
                    fileName = "ROOT.xml";
                }else{
                    fileName = appName+".xml";
                }
                var tomcatConfFile = os.normalizePath("${TOMCAT_HOME}/conf/Catalina/localhost/"+fileName);
                var xml = "" +
                    <r><![CDATA[<?xml version="1.0" encoding="UTF-8"?>
                    <Context path="${WEBAPP_CTX_PATH}"
                             docBase="${WEBAPP_DOC_BASE}"
                             reloadable="true" allowLinking="true">
                        <Resources className="org.apache.naming.resources.VirtualDirContext"
                                   extraResourcePaths="${ALIASES}" />
                        <Loader className="org.apache.catalina.loader.VirtualWebappLoader"
                                virtualClasspath="${CLASSPATH}"
                                searchVirtualFirst="true"/>
                    </Context>
                    ]]></r>;
                //构造 aliases 字符串
                if (! aliases){
                    aliases = {};
                }
                var tmp = [];
                for (var key in aliases){
                    var aliasPath = aliases[key];
                    aliasPath = os.normalizePath(aliasPath, env.PROFILE);
                    tmp[tmp.length] = key + "=" + aliasPath;
                }
                aliases = tmp.join(",");
                //构造 classpath 字符串
                if (! classpaths){
                    classpaths = [];
                }
                tmp = [];
                for (var i=0; i<classpaths.length; i++){
                    var cp = classpaths[i];
                    cp = os.normalizePath(cp, env.PROFILE);
                    tmp[tmp.length] = cp;
                }
                var classpath = tmp.join(";");
                //解析 xml 模板并保存
                docBase = os.normalizePath(docBase, env.PROFILE);
                var vars = {
                    WEBAPP_CTX_PATH: appName,
                    WEBAPP_DOC_BASE: docBase,
                    ALIASES: aliases,
                    CLASSPATH: classpath
                };
                require (["std/utils/misc"], function(replacer) {
                    xml = replacer.replacePlaceHolder(xml, function(key){
                        return vars[key];
                    });
                });
                os.writeTextFile(tomcatConfFile, xml);
                log.info("Web app ["+appName+"] 部署定义文件: " + tomcatConfFile);
            };
            var w = env.options.webapps;
            if (w && w.length){
            	for (var i=0; i<w.length; i++){
            		var wa = w[i];
            		_deploy(wa.appName, wa.docBase, wa.aliases, wa.classpaths);
            	}
            }
        })();

        //构造命令行, 启动 Tomcat
        require(["std/utils/misc"], function(misc){
            var cmdLine;
            if (os.isWindows){
                cmdLine = '"${TOMCAT_HOME}\\bin\\catalina.bat" run';
            }else{
                cmdLine = '"${TOMCAT_HOME}/bin/catalina.sh" run';
            }
            cmdLine = misc.replacePlaceHolder(cmdLine, {TOMCAT_HOME:env.TOMCAT_HOME});
            
            //计算 profile stamp, 主要用于"安全、彻底"关闭其他未关闭的本 profile Java 子进程
            require(["std/utils/MD5"], function(md5){
                var stamp = md5(env.PROFILE);
                env.PROFILE_STAMP = stamp;
            });
            //计算 instance stamp, 主要用于"安全、彻底"关闭本次运行启动的 java 子进程
            env.INSTANCE_STAMP = env.PROFILE_STAMP + "." + (new Date()).getTime();
            //最后的调整, 处理环境变量 TITLE, JAVA_OPTS 和 CATALINA_HOME
            env.TITLE = env.PROFILE_NAME + "("+env.PROFILE+")";
            env.JAVA_OPTS = env.JAVA_OPTS + " " + env.options.deployment.javaopts.join(" ");
            env.JAVA_OPTS = env.JAVA_OPTS + " -DTOMCAT_PORT_HTTP=" + env.TOMCAT_PORT_HTTP;
            env.JAVA_OPTS = env.JAVA_OPTS + " -DTOMCAT_PORT_HTTPS=" + env.TOMCAT_PORT_HTTPS;
            env.JAVA_OPTS = env.JAVA_OPTS + " -DPROFILE_STAMP=" + env.PROFILE_STAMP;
            env.JAVA_OPTS = env.JAVA_OPTS + " -DINSTANCE_STAMP=" + env.INSTANCE_STAMP;
            env.CATALINA_HOME = env.TOMCAT_HOME;
            
            //启动 Tomcat
            log.info("启动 Tomcat: [" + cmdLine + "] ...");
            log.info("================================================================================");
            shell.startProcess(cmdLine, env, env.PROFILE_STAMP, env.INSTANCE_STAMP);
            log.info("================================================================================");
        });
        
    });
    return {};
});