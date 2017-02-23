define(function () {
    return {
        pause: function(msg, errCode){
            print(msg);
            java.lang.System.out.print("按 Enter 退出 ...");
            java.lang.System.console().readLine();
            if ( null!=errCode && 0!=errCode){
                java.lang.System.exit(errCode);
            }
        }
    };
});