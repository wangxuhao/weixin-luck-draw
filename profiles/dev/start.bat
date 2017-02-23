@echo off
SETLOCAL

:: Javascript debug option
::set THINKBASE_NET_RHINO_DEBUGGER=local://CMS

:: PROFILE 通常可以设置为基于 ${PROFILE_REPO} 的相对目录, 如果 bat 文件就放在 profile 下则可以不设, 或者简单的设为 %~dp0
set PROFILE=%~dp0

:: 设置数据库连接信息(注意 MySQL 的 url 在命令行环境变量中要把 "&" 表示为 "^&" )
set JDBC_URL=jdbc:mysql://120.55.163.34:3306/weixin?useUnicode=true^&characterEncoding=utf8
set DB_USERNAME=u1214
set DB_PASSWORD=$p1214$
set JAVA_HOME=
:: 设置公众号TOKEN
set WEIXIN_TOKEN=weixinLuckDraw
:: 设置公众号APPID
set WEIXIN_APPID=wx53815fce4930ae44
:: 设置公众号APPSECRET
set WEIXIN_APPSECRET=a98759708ee61a453ac3f518fcb76a53
:: 设置公众号WEIXIN_ENCODINGAESKEY (43位字符组成)
set WEIXIN_ENCODINGAESKEY=3Qykhjp7W09yOLdhLh6uNXHqtAZRjQmAEt0Syx3cQ4p
:: 设置公众号微信号ACCOUNT
set WEIXIN_ACCOUNT=gh_efafa9df8b27
:: 最后简单的调用 boot.bat
call "%~dp0..\..\etc\runner\boot.bat"

ENDLOCAL