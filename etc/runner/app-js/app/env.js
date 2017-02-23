/** 检测和准备运行环境, 返回 env 对象, 包括各个环境参数 */
define(function () {
    var log = getLogger("env.js");
    /** 返回一个目录以及其父目录, 直到停止在某个层次的父目录
     * start - 开始的目录
     * stop - 停止的上层父目录
     * relatedDirs - 数组, 用于在找到每一层的目录后, 产生与之相对的几个目录
     */
    var findAllParents = function(start, stop, relatedDirs){
        if (!relatedDirs){
            relatedDirs = ["."];
        }
        if (! Array.isArray(relatedDirs)){   //如果不是数组 ...
            relatedDirs = [relatedDirs+""];
        }
    
        var File = Packages.java.io.File;
        var result = [];
        var dealOneFile = function(f){
            var path = f.getCanonicalPath()+"";
            for(var i=0; i<relatedDirs.length; i++){
                var tmp = path + "/" + relatedDirs[i];
                tmp = new File(tmp);
                if (tmp.exists()){
                    result[result.length]=tmp.getCanonicalPath()+"";
                }
            }
        }
        
        var f = new File(start+"");
        var sf = (null==stop)?f:new File(stop+"");
        while(null!=f && !f.equals(sf)){
            dealOneFile(f);
            f = f.getParentFile();
        }
        //最后加上 stop, 因为 stop 也会包含在结果中
        dealOneFile(sf);

        return result;
    }

    /** env - 包含全部的环境信息的对象 */
    var env;
    require (["std/sys", "std/os", "app/_tools"], function(sys, os, _tools) {
        env = sys.appProperties;
        
        // 注册 os 运行库的外部属性对象, 以便 os.normalizePath() 这样的方法能够使用
        os.regExtAttrs(env);
        
        /** env 除了包含各类环境变量之外, 还包含多种选项, 比如部署时虚拟化的资源目录和ClassLoader支持、插件支持等 */
        env.options = {
        	deployment: {
        		aliases: [ /* {contextPath:ctx1, filePath:path1}, {contextPath:ctx2, filePath:path2}, ... */ ],
        		classpath: [ /* path1, path2, ... */ ],
        		javaopts: [ /* JAVA_OPT1, JAVA_OPT2, ... */ ]
        	},
        	webapps: [],
        	plugins: []
        };
        /** 注册一个 alias (注意：针对的是 Yigo WebApp)
         * contextPath: Web 访问时的上下文路径;
         * filePath: 对应的文件/目录的路径
         */
        var env$registerAlias = function(contextPath, filePath){
        	var a = env.options.deployment.aliases;
        	a[a.length] = {
        		contextPath: contextPath,
        		filePath: filePath
        	};
        }
        /** 注册一个文件系统路径到 CLASSPATH (注意：针对的是 Yigo WebApp)
         * filePath 文件系统路径, 可以是 jar 文件的路径, 也可以是目录
         */
        var env$registerClasspath = function(filePath){
        	var c = env.options.deployment.classpath;
        	c[c.length] = filePath;
        }
        /** 注册一个 Java option
         */
        var env$registerJavaOpts = function(opt){
        	var o = env.options.deployment.javaopts;
        	if (o.indexOf(opt) < 0){
            	o[o.length] = opt;
        	}
        }
        /** 注册一个 WebApp
         * appName: WebApp 的名称，也是访问时的 context path
         * docBase: WebApp 的 doc base
         * aliases: 目录别名，Key-value 对象，参考 http://tomcat.apache.org/tomcat-7.0-doc/config/context.html#Virtual_webapp 之 VirtualDirContext
         * classpaths: 额外加载的 CLASSPATH 数组，参考 http://tomcat.apache.org/tomcat-7.0-doc/config/context.html#Virtual_webapp 之 VirtualWebappLoader
         */
        var env$registerWebApp = function(appName, docBase, aliases, classpaths){
        	var w = env.options.webapps;
        	w[w.length] = {
        			appName: appName,
        			docBase: docBase,
        			aliases: aliases,
        			classpaths: classpaths
        	};
        }
        /** 注册一个 plugin (注意: 同一个 plugin 可以多次注册, 系统将按照先后顺序执行)
         * pluginPath: plugin 的目录路径, 其中一定要保护一个文件 "plugin.js"
         * options: 插件选项
         */
        var env$registerPlugin = function(pluginPath, options){
        	if (null==options) options = {};
        	var $plugin = {
        		path: pluginPath,
        		options: options,
        		onDeploy: function(env){
            		sys.raiseErr("插件 ["+pluginPath+"] 中未定义 [onDeploy]", -9)
            	}
        	};
        	var p = env.options.plugins;
        	p[p.length] = $plugin;
        	
        	//返回当前这个 plugin
        	return $plugin;
        }
        
        //根据 app.js 的位置重新确认 REDIST_ROOT
        env.REDIST_ROOT = os.normalizePath("../../..");
        os.chroot(env.REDIST_ROOT);     //系统 root 切换为 REDIST_ROOT, 这样后面很多目录都可以以此目录为基础
        log.info("系统 root 切换为 REDIST_ROOT="+env.REDIST_ROOT);
        
        //PROFILE_DEFAULT, 固定为 default 的目录
        env.PROFILE_DEFAULT = os.normalizePath("default");

        //PROFILE_REPO, 存放多个 profile 的目录, 默认为 profiles
        env.PROFILE_REPO = os.normalizePath(os.getProp("PROFILE_REPO", "profiles"));
        
        //处理 profile, 主要逻辑: 确定使用哪些 profile.js 来设置 profile 环境
        //按照如下规则确定 PROFILE 位置: 1. PROFILE 环境变量指定, 2. 默认为当前目录(user.dir)
        env.PROFILE = os.normalizePath(os.getProp("PROFILE", os.getProp("user.dir")));
		//创建 PROFILE_RUNTIME 目录: 用于存放运行时临时产生的文件
		env.PROFILE_RUNTIME = os.normalizePath(".runtime", env.PROFILE);
        //当前 PROFILE_NAME 的默认值 - profile 目录名称
        env.PROFILE_NAME = (new Packages.java.io.File(env.PROFILE)).getName();
        //Profile 中设置可以按照优先顺序: PROFILE目录/.conf, PROFILE目录, PROFILE父目录/.conf ,PROFILE父目录, PROFILE_DEFAULT目录 依次产生作用:
        //  - profile.js - 用于设置除了 PROFILE_REPO/PROFILE 之外的其他项目参数
        // - 查找 profile 目录及其父目录, 再加上 PROFILE_DEFAULT 目录, 构造配置搜索路径
        var profileSearchDirs = findAllParents(env.PROFILE, env.PROFILE_REPO, [".conf", "."]);
        profileSearchDirs[profileSearchDirs.length] = env.PROFILE_DEFAULT;
        log.info("配置文件搜索路径: "+profileSearchDirs.join("; "));
        // - 查找配置文件 profile.js
        var profileJsPath = null;
        require(["std/utils/misc"], function( misc){
            misc.arrayForEach(profileSearchDirs, function(path){
                if (null==profileJsPath){
                    var pf = new Packages.java.io.File(os.normalizePath([path, "profile.js"]));
                    if (pf.exists()){
                        profileJsPath = pf.getCanonicalPath()+"";
                    }
                }
            });
        });
        env.PROFILE_JS = profileJsPath;
        log.info("使用配置文件 profile.js="+env.PROFILE_JS);
        // - 执行 profile.js, 完善项目配置, 比如所引用的 infrastructure/product/yigoapp 等, 这些定义会放在变量 "profile" 上
        var profile = {
        	registerAlias: env$registerAlias,
        	registerClasspath: env$registerClasspath,
        	registerJavaOpts: env$registerJavaOpts,
        	registerWebApp: env$registerWebApp,
        	registerPlugin: env$registerPlugin
        };
        if (env.PROFILE_JS){
            eval(os.readTextFile(env.PROFILE_JS));
        }else{
            log.warn("找不到 profile.js, 相关变量定制过程被忽略. 搜索目录包括: " + profileSearchDirs.join("; ") + " .");
        }
        if (!profile) profile = {}; //防御代码, 避免 profile.js 中清除此变量
        
        //在处理完 profile 之后, 查找运行需要的各项参数, 注意优先级最高的是环境变量
        //TOMCAT_HOME, 默认使用 infrastructure/tomcat/default
        env.TOMCAT_HOME = os.normalizePath(os.getProp("TOMCAT_HOME", profile.TOMCAT_HOME||"infrastructure/tomcat/default"));
        //PRODUCT_REPO, 存放多个 product 的目录, 默认为 products
        env.PRODUCT_REPO = os.normalizePath(os.getProp("PRODUCT_REPO", profile.PRODUCT_REPO||"products"));
        //YIGOAPP_REPO, 存放多个 Yigo 运行环境(Web app)的目录, 默认为 yigo-farm
        env.YIGOAPP_REPO = os.normalizePath(os.getProp("YIGOAPP_REPO", profile.YIGOAPP_REPO||"web-apps"));
        //引入 profile 中修改的 PROFILE_NAME
        env.PROFILE_NAME = profile.PROFILE_NAME || env.PROFILE_NAME ;

        //获取和检查指定的参数 - 优先系统环境变量, 如果未在环境变量中找到, 则尝试在 profile 变量中找
        var getRequireVar = function(varName, descr, blankAble){
            var v = os.getProp(varName);
            if (!v){
                v = profile[varName];
            }
            if (!v){
            	if (blankAble){
            		v = "";
            	}else{
            		_tools.pause("未找到环境变量 ["+varName+"]("+descr+"), 请检查系统配置.", -10);
            	}
            }
            return v;
        }
        //PRODUCT, 指定具体使用 PRODUCT_REPO 下面的哪个 product
        // env.PRODUCT = os.normalizePath(getRequireVar("PRODUCT", "指定具体使用 PRODUCT_REPO 下面的哪个 product"), env.PRODUCT_REPO);
		//MODULES, 指定具体使用 MODULES_REPO 下面的哪个 modules
		// env.MODULES = os.normalizePath(getRequireVar("PRODUCT", "指定具体使用 PRODUCT_REPO 下面的哪个 product"), env.MODULES_REPO);
        //YIGOAPP, 指定具体使用 YIGOAPP_REPO 下面的哪个 yigo 环境
        env.YIGOAPP = os.normalizePath(getRequireVar("YIGOAPP", "指定具体使用 YIGOAPP_REPO 下面的哪个 yigo 环境"), env.YIGOAPP_REPO);
        //CONFIG_LIST, 配置目录列表, 第一项为主配置, 使用 ";" 分隔
        //env.CONFIG_LIST = getRequireVar("CONFIG_LIST", "配置文件列表");
        /**require(["std/utils/misc"], function( misc){
            var list = env.CONFIG_LIST.split(";");   //按照 ";" 分割
            env.CONFIG_LIST = [];
            for(var i=0; i<list.length; i++){
                var configDir = misc.trim(list[i]);
                if (configDir){
                    configDir = os.normalizePath(configDir, env.PROFILE);   //配置文件可以采用基于 env.PROFILE 的相对目录
                    if (! os.fileExists(configDir)){
                        _tools.pause("找不到配置文件目录 ["+configDir+"], 请检查系统配置.", -20);
                    }
                    env.CONFIG_LIST[env.CONFIG_LIST.length] = configDir;
                }
            }
            if (env.CONFIG_LIST.length < 1){
                _tools.pause("未找到环境变量 [CONFIG_LIST], 或者配置有错误, 请检查系统配置.", -20);
            }
        });**/
        //MAIN_CONFIG, 主配置目录
       // env.MAIN_CONFIG = env.CONFIG_LIST[0];
        
        //JDBC_URL, 数据库的 JDBC URL
        env.JDBC_URL = getRequireVar("JDBC_URL", "数据库的 JDBC URL");
        //DB_USERNAME, 数据库用户名
        env.DB_USERNAME = getRequireVar("DB_USERNAME", "数据库用户名");
        //DB_PASSWORD, 数据库用户密码
        env.DB_PASSWORD = getRequireVar("DB_PASSWORD", "数据库用户密码", true);
        
        //TOMCAT_PORT_HTTP, Tomcat http 端口, 默认 8080
        env.TOMCAT_PORT_HTTP = os.getProp("TOMCAT_PORT_HTTP", profile.TOMCAT_PORT_HTTP||"8080");
        //TOMCAT_PORT_HTTPS, Tomcat https 端口, 默认 8443
        env.TOMCAT_PORT_HTTPS = os.getProp("TOMCAT_PORT_HTTPS", profile.TOMCAT_PORT_HTTPS||"8443");
        
        //JAVA_OPTS, Java 命令行的附加选项
        env.JAVA_OPTS = os.getProp("JAVA_OPTS", profile.JAVA_OPTS||"-server -Xmx1024m -XX:MaxNewSize=256m -XX:MaxPermSize=256m -Djava.awt.headless=true");
        
        //Profile 中可以定义扩展的信息, 这些扩展的信息必须定义在 profile.external 对象下, 处理后将通过 env.EXT_* 的方式存储到 env
        (function(){
            if (profile.external){
                for(var o in profile.external){
                    env["EXT_" + o] = profile.external[o];
                }
            }
        })();

        //plugins 处理, 保证 plugins 在定义时可以使用前面定义的 env 属性(例如 ${PRODUCT_REPO})
        //plugins 优先于 aliases 和 classpath 处理，保证可以在 plugin 中调整 alias 和 classpath
        var env$preparePlugin = function(p){
        	p.path = os.normalizePath(p.path);
        	p.name = (new java.io.File(p.path)).getName();
        	//执行 plugin 初始化
        	var js = os.readTextFile(os.normalizePath("plugin.js", p.path));
        	if (!js){
        		sys.raiseErr("插件 ["+p.path+"] 未找到", -9)
        	}
        	var f = function(pluginCtx){
        		var log = getLogger("plugin:"+p.name);
        		eval(js);
        	}
        	//在调用 plugin javascript 时, this 就是 plugin 对象, 参数 pluginCtx
        	f.apply(p, [{
        		profile: profile,
        		env: env,
        		options: p.options,
        		plugin: p
        	}]);
        	//初始化完毕后, 处理插件的 configFiles, 规则与 profile.js 的查找类似
            var pluginCfgSearchDirs = findAllParents(env.PROFILE, env.PROFILE_REPO, [".conf/plugins/" + p.name]);
            //最后增加默认的 plugins 插件 config files 目录
            pluginCfgSearchDirs[pluginCfgSearchDirs.length] = os.normalizePath("plugins/" + p.name, env.PROFILE_DEFAULT);
        	var tmpCfgFiles = [];
        	if (p.configFiles){
                require(["std/utils/misc"], function( misc){
                	for(var k=0; k<p.configFiles.length; k++){
                		var cfgFile = p.configFiles[k];
                		
                		tmpCfgFiles[k] = null;	//如果找不到, 则对应的 config file 项置为 null
                        misc.arrayForEach(pluginCfgSearchDirs, function(path){
                            if (null==tmpCfgFiles[k]){
                                var pf = new Packages.java.io.File(os.normalizePath([path, cfgFile]));
                                if (pf.exists()){
                                	tmpCfgFiles[k] = pf.getCanonicalPath()+"";
                                }
                            }
                        });
                	}
                });
        	}
        	p.configFiles = tmpCfgFiles;	//替换为处理后的具体配置文件
        };
        //FIXME: 定义 __preparePlugin, 仅为了方便对 Yigo 部署的兼容性处理(参见 /etc/runner/app-js/app/yigo.js)
        env.__preparePlugin = env$preparePlugin;
        (function(){
        	var ps = env.options.plugins;
            for (var i=0; i<ps.length; i++){
            	var p = ps[i];
            	env$preparePlugin(p);
            }
        })();

        //aliases 和 classpath 的处理, 这样可以保证它们在定义时可以使用前面定义的 env 属性(例如 ${PRODUCT_REPO})
        (function(){
            var aliases = env.options.deployment.aliases;
            for (var i=0; i<aliases.length; i++){
            	var alias = aliases[i];
            	alias.filePath = os.normalizePath(alias.filePath, env.PROFILE);
            }
            var pathes = env.options.deployment.classpath;
            for (var i=0; i<pathes.length; i++){
            	pathes[i] = os.normalizePath(pathes[i], env.PROFILE);
            }
        })();
        
        //显示运行环境信息
        require(["std/utils/json2-util"], function(util){
            //在输出配置信息前隐藏密码和 plugins 的一些信息
            var pwd = env.DB_PASSWORD;
            env.DB_PASSWORD = "********";
            var __p = env.__preparePlugin;
            delete(env.__preparePlugin);
            var plugins = env.options.plugins;
            var tmp = [];
            for (var i=0; i<plugins.length; i++){
                var pi = {
            		name: plugins[i].name,
            		path: plugins[i].path
                };
                if (plugins[i].options){pi.options = plugins[i].options;}
                if (plugins[i].configFiles){pi.configFiles = plugins[i].configFiles;}
                if (plugins[i].dependencies){pi.dependencies = plugins[i].dependencies;}
            	tmp[tmp.length] = pi;
            }
            env.options.plugins = tmp;
            
            //输出配置信息
            var str = util.format(env);
            
            //还原隐藏的配置信息
            env.DB_PASSWORD = pwd;
            env.__preparePlugin = __p;
            env.options.plugins = plugins;
            
            log.info("运行环境信息: " + str);
        });

    });
    return env;
});