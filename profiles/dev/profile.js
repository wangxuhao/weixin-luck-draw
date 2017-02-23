/** 实际上, 这个 js 文件是执行过程的一部分, 所以可以做很多事情 */
require (["app/_tools"], function(_tools) {
    var javaVer = java.lang.System.getProperty("java.version")+"";
    if (javaVer < "1.7"){
        _tools.pause("当前 Java 版本 ("+javaVer+") 小于 1.7, 无法运行 CMS 系统.", -80);
    }
});

/** 通常(如果完全按照 Yigo-redist 目录结构部署)不需要特别设置 */
// profile.PROFILE_NAME     = ...;      //Profile 的名称, 默认等于 PROFILE 目录的名称
// profile.PRODUCT_REPO     = ...;      //存放多个 product 的目录, 默认为 products
// profile.YIGOAPP_REPO     = ...;      //存放多个 Yigo 运行环境(Web app)的目录, 默认为 yigo-farm

/** 在实际使用中按照系统部署要求修改 */
// profile.JAVA_OPTS        = ...;      //Java运行参数选项, 默认值 -server -Xmx1024m -XX:MaxNewSize=256m -XX:MaxPermSize=256m -Djava.awt.headless=true
profile.TOMCAT_PORT_HTTP = 80;      //Tomcat http 端口, 默认 8080
// profile.TOMCAT_PORT_HTTPS= ...;      //Tomcat https 端口, 默认 8443

/** 通常在 profile.js 中设置就可以了, 一般不同开发/实施人员会使用相同的设置 */
profile.YIGOAPP      = "weixin";                  //指定具体使用 YIGOAPP_REPO 下面的哪个 yigo 环境; 可以使用基于 YIGOAPP_REPO 的相对路

/** 通常会被启动脚本(bat 或者 sh)文件中的环境变量覆盖 */
profile.JDBC_URL="jdbc:mysql://localhost:3306/sltz?useUnicode=true&characterEncoding=utf8";
profile.DB_USERNAME="root";
profile.DB_PASSWORD="";

/** 调试设置 */
profile.JAVA_OPTS = "-server -Xmx1024m -XX:MaxNewSize=256m -XX:MaxPermSize=256m -Djava.awt.headless=true";
profile.JAVA_OPTS += " -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=7777,server=y,suspend=n"