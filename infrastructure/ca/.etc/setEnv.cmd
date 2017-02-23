@echo off
REM ===========================================================================
REM 注意: 本文的内容属于系统设置, 请不要修改本文中的内容!
REM ===========================================================================

SET PATH=%JAVA_HOME%\bin;.\.bin\openssl

SET PROMPT=--$g
SET CMD_NEWLINE=@.bin\cygwin\echo.exe -e "\n"
SET CMD_SED=.bin\cygwin\sed.exe

REM OPENSSL配置文件
SET OPENSSL_CONF=.etc\openssl.cnf.properties
REM 存储OPENSSL的序列号
SET OPENSSL_SRL_FILE=.etc\ca-cert.srl

call .etc\config.cmd


REM JSSE 信任的CA根证书的密钥存储文件路径
SET JDK_KEYSTORE=%JAVA_HOME%\jre\lib\security\cacerts
REM JSSE 信任的CA根证书的存储密码, 似乎不能改变
SET JDK_STOREPASS=changeit

REM JDK_CA_ROOT_ALIAS:信任的CA根证书别名
SET JDK_CA_ROOT_ALIAS=%WEBSITE%-CA-ROOT

REM ALIAS:KeyPair别名/服务器证书别名
SET SERVER_ALIAS=%WEBSITE%-%WEBSERVER%

REM KEYSTORE:密钥存储文件名
SET SERVER_KEYSTORE=%SERVER_ALIAS%.keystore

REM 服务器证书中的 distinguished name 信息(Common Name)
SET SERVER_DN_CN=%WEBSITE%

REM 建立发布的目录结构
mkdir dist
mkdir dist\ca-cert
mkdir dist\client
mkdir dist\server

@echo on