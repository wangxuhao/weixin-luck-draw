/**
 * Yigo 集成插件, 将 Yigo war 部署在 /Yigo(或者被配置的其他路径) 下;
 * 支持的 Options:
 *    - forceDisable: 默认为 false, =true 时将强制不部署 Yigo Web app
 *    - ctxPath: Yigo war 被部署后的 Web App 路径, 默认 /Yigo; 此选项会被加入到环境变量 YIGO_REDIST_CONTEXT_PATH 中
 */
(function(plugin, ctx){
	plugin.configFiles = [
	    "jdbc.properties"
	];
	
	//处理 ctxPath 的默认值
    if(plugin.options && plugin.options.ctxPath){
    	log.info("webapp 被部署到: [" + plugin.options.ctxPath + "] .");
	}else{
		plugin.options.ctxPath = "/Weixin";
		log.info("webapp 被部署到默认路径: [" + plugin.options.ctxPath + "] .");
	}
	env.YIGO_REDIST_CONTEXT_PATH = plugin.options.ctxPath;	//将路径名设置到环境变量中, 以便一些程序获取
	
	plugin.onDeploy = function(env){
		//整个系统默认是需要部署 Yigo web app 的(参见 /etc/runner/app-js/app/yigo.js);
		//如果确实不需要部署 Yigo 时, 需要设置 option “forceDisable=true” 以强制不部署
		if (plugin.options && plugin.options.forceDisable) {
			log.warn("webapp 已被 web-apps plugin 的 参数 [forceDisable] 强制取消部署.");
			return;
		}
		
		var _buildClasspath = function(paths, narrow){	/*narrow: 尽可能缩减 classpath 的长度, 主要用于产生一个供 YigoCAD 使用的classpath*/
			if (!paths || !paths.length){
				return [];
			}
			var c = [];
			for (var i=0; i<paths.length; i++){
				var filePath = paths[i];
				var pathList = [filePath];
				require(["std/os", "std/shell", "std/utils/misc"], function(os, shell, misc){
				    //支持以 *.jar 一次性设置多个 jar 包
					var re = new RegExp(".*[\\/\\\\].*\\*.*\\.jar$", "gi"); // - /.../XX*XX.jar
					if (re.test(filePath)){
						var lastFileSp = filePath.lastIndexOf("/");
						if (lastFileSp<0){
							lastFileSp = filePath.lastIndexOf("\\");
						}
						var wildcard = filePath.substr(lastFileSp+1);
					    if (misc.endsWith(filePath, "/"+wildcard) || misc.endsWith(filePath, "\\"+wildcard)){
					        var _path = filePath.substr(0, filePath.length-("/"+wildcard).length);
					        _path = os.normalizePath(_path);
					        if (wildcard=="*.jar" && narrow){
					        	//如果 classpath 是 .../*.jar, 那么在 YigoCAD 中可以使用 .../* 代替而不用扩展为具体的 jar 包
					        	if (os.isWindows){
					        		pathList = [_path+"\\*"];
					        	}else{
					        		pathList = [_path+"/*"];
					        	}
					        }else{
						        pathList = [];
						        shell.listFiles(_path, wildcard, function(file){
						            pathList[pathList.length] = file;
						        });
					        }
					    }
					}
				});
				for (var j=0; j<pathList.length; j++){
				    var p = pathList[j];
				    if (c.indexOf(p) < 0){
				        c[c.length] = p;
				    }
				}
			}
			return c;
		}
		//部署 tomcat context xml 
	    require (["std/sys", "std/os", "std/shell", "app/env"], function(sys, os, shell, env) {
			var _ctxPath = plugin.options.ctxPath;
			var fileName;
			if ("/"==_ctxPath){
				fileName = "/ROOT.xml";
			}else{
				fileName = _ctxPath+".xml";
			}
			
	        var tomcatConfFile = os.normalizePath("${TOMCAT_HOME}/conf/Catalina/localhost"+fileName);
	        var corePropFile = os.normalizePath("${YIGOAPP}/WEB-INF/jdbc.properties");

	        (function(){
	            var xml = "" +
	                <r><![CDATA[<?xml version="1.0" encoding="UTF-8"?>
	                <Context path="${WEBAPP_CTX_PATH}"
	                         docBase="${YIGOAPP}"
	                         reloadable="true" allowLinking="true">
	                    <Resources className="org.apache.naming.resources.VirtualDirContext"
	                               extraResourcePaths="${ALIASES}" />
	                    <Loader className="org.apache.catalina.loader.VirtualWebappLoader"
	                            virtualClasspath="${CLASSPATH}"
	                            searchVirtualFirst="true"/>
	                </Context>
	                ]]></r>;
	            //构造 aliases 字符串
	            var a = env.options.deployment.aliases;
	            var aliases = [];
	            for (var i=0; i<a.length; i++){
	            	aliases[aliases.length] = a[i].contextPath + "=" + a[i].filePath;
	            }
	            aliases = aliases.join(",");
	            //构造 classpath 列表
	            var classpath = _buildClasspath(env.options.deployment.classpath);
	            var classpathCAD = _buildClasspath(env.options.deployment.classpath, true);
	            env.options.deployment._ExpandedClasspath4CAD = classpathCAD;	//FIXME: 记录一个扩展后的 classpath 列表, 主要是为 YigoCAD 中使用
	            //解析 xml 模板并保存
	            var vars = {
	                YIGOAPP: env.YIGOAPP,
	                ALIASES: aliases,
	                CLASSPATH: classpath.join(";"),
	                WEBAPP_CTX_PATH: _ctxPath
	            };
	            require (["std/utils/misc"], function(replacer) {
	                xml = replacer.replacePlaceHolder(xml, function(key){
	                    return vars[key];
	                });
	            });
	            os.writeTextFile(tomcatConfFile, xml);
	            log.info("Yigo web app 部署定义文件: " + tomcatConfFile);
	        })();
	        
			
			 //产生 jdbc.properties 配置文件
	        (function(){
	            require (["std/utils/misc"], function(misc) {
	            	var n2a = misc.prepare4Property;    //native2ascii
	                //构造 jdbc.properties
	                var vars = {    //基本的 jdbc.properties 替换变量
	                    JDBC_DRIVER: env.JDBC_DRIVER,
	                    JDBC_URL: n2a(env.JDBC_URL),
	                    DB_USERNAME: n2a(env.DB_USERNAME),
	                    DB_PASSWORD: n2a(env.DB_PASSWORD)
	                };
	                //允许 env.EXT_* 都作为替换的变量
	                for(var o in env){
	                    if ( (o+"").indexOf("EXT_") == 0){
	                        vars[o+""] = n2a(env[o]);
	                    }
	                }
	                //替换模板产生 jdbc.properties
					debugger;
	                var coreTemplate = plugin.configFiles[0];
	                shell.copyTemplateFile(coreTemplate, corePropFile, vars);
		            log.info("jdbc.properties 配置文件: " + corePropFile);
	            });
	        })(); 
			
			
			
	    });
	}
})(this, pluginCtx);