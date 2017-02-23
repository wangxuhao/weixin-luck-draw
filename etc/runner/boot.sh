#!/bin/bash

log() {
    echo "`date '+%Y-%m-%d %H:%M:%S'` >> " "$*"
}
errorMsg() {
    echo ""
    echo -e ">> ERROR!" "$*"
    echo ""
}
error() {
    errorMsg "$*"
    read -p "Press <Enter> to exit ..." tmp
}

# 禁止使用 root 组的用户(gid=0(root))启动系统
if [ "$(id -g)" == "0" ]; then
    error ">>> 不能使用具有 root 权限的用户(gid=0(root))启动系统, 请使用普通用户运行; \\n\\n * 如果需要绑定 1024 以下的端口, 请使用 authbind, 或者通过 iptables 进行端口转发;"
	exit -10
fi

# SHELL_ROOT - Location(Path) of this batch file
SHELL_ROOT=$(cd "$(dirname "$0")"; pwd)

# REDIST_ROOT - The root path of Yigo-redist
REDIST_ROOT=$(cd "$SHELL_ROOT/../.."; pwd)

# JAVA_HOME
if [ -z $JAVA_HOME ]; then
    error ">>> 环境变量 [JAVA_HOME](Java环境安装目录)未设置, 系统退出;"
	exit -90
fi

# Call rhino-shell
CLASSPATH="$REDIST_ROOT/etc/runner/app-js:$REDIST_ROOT/etc/runner/rhino-shell/lib-js:$REDIST_ROOT/etc/runner/rhino-shell/lib-java/*:"
CMDLINE="$JAVA_HOME/bin/java -cp $CLASSPATH net.thinkbase.shell.rhino.Main"
echo $CMDLINE
$CMDLINE
