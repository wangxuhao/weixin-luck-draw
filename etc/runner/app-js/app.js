require.config({
    baseUrl: "/"
});
var log = getLogger("app.js");
log.info("开始启动 Web App ...");
require (["app/webapp"], function(webapp) {
    //都在 app/webapp 中做了
});
log.info("Web App 运行结束.");