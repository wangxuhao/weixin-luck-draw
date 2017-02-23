@SETLOCAL
@call .etc\setEnv.cmd

%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成服务器端证书 ==============================================================
@echo ===============================================================================

if "%JAVA_HOME%"=="" goto NO_JAVA_HOME

mkdir work
mkdir work\server
del /s /q work\server

@SET $_SERVER_DNAME=CN=%SERVER_DN_CN%, OU=%SERVER_DN_OU%, O=%BASE_DN_O%, L=%BASE_DN_L%, ST=%BASE_DN_ST%, C=%BASE_DN_C%
@SET CMDLINE=keytool
@REM -genkey[产生密钥对] -alias[密钥对别名] -validity[密钥有效期] -keyalg[密钥算法参数] -keysize[密钥位数]
@SET CMDLINE=%CMDLINE% -genkey -alias %SERVER_ALIAS% -validity %SERVER_KEY_DAYS% -keyalg RSA -keysize 1024
@REM -keypass[密钥保护密码]- storepass[存储密码]
@SET CMDLINE=%CMDLINE% -keypass %SERVER_KEYPASS% -storepass %SERVER_STOREPASS%
@REM -dname[别名相关附加信息,其中cn是服务器的名字一定要与WEB服务器中设置的一样] -keystore[密钥存储文件路径]
@SET CMDLINE=%CMDLINE% -dname "%$_SERVER_DNAME%" -keystore work\server\%SERVER_KEYSTORE%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成KeyPair: work\server\%SERVER_KEYSTORE%
%CMDLINE%

@SET CMDLINE=keytool
@REM -certreq[产生待签名证书] -alias[证书别名] -sigalg[证书算法参数] -file [产生文件输出路径]
@SET CMDLINE=%CMDLINE% -certreq -alias %SERVER_ALIAS% -sigalg MD5withRSA -file work\server\server.csr
@REM -keypass[密钥保护密码] -keystore[存储文件路径] -storepass[存储密码]
@SET CMDLINE=%CMDLINE% -keypass %SERVER_KEYPASS% -keystore work\server\%SERVER_KEYSTORE% -storepass %SERVER_STOREPASS%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成待签名证书 work\server\server.csr
%CMDLINE%

@SET CMDLINE=openssl
@REM x509[签发x509证书命令] -req[输入待签发证书] -in[输入待签发证书文件路径] -out[产生x509证书文件输出路径]
@SET CMDLINE=%CMDLINE% x509 -req -in work\server\server.csr -out work\server\server-cert.pem
@REM // -CA[签发根证书] -CAkey[根证书密钥文件] -days[证书有效期] -CAcreateserial[创建序列号]
@REM // SET CMDLINE=%CMDLINE% -CA work\ca\ca-cert.pem -CAkey work\ca\ca-key.pem -days %SERVER_KEY_DAYS% -CAcreateserial -sha1 -trustout
@REM -CA[签发根证书] -CAkey[根证书密钥文件] -days[证书有效期] -CAserial[CA序列号文件]
@SET CMDLINE=%CMDLINE% -CA work\ca\ca-cert.pem -CAkey work\ca\ca-key.pem -days %SERVER_KEY_DAYS%
@SET CMDLINE=%CMDLINE% -CAserial %OPENSSL_SRL_FILE% -sha1 -trustout
%CMD_NEWLINE%
@echo ===============================================================================
@echo 用CA私钥进行签名, 产生x509证书文件 work\server\server-cert.pem
%CMDLINE%

%CMD_NEWLINE%
@echo ===============================================================================
@echo 导入(替换)信任的CA根证书到JSSE的默认位置(%JDK_KEYSTORE%)
@REM 导入前首先删除(如果原来已经导入过)
keytool -delete -v -alias %JDK_CA_ROOT_ALIAS% -storepass %JDK_STOREPASS% -keystore %JDK_KEYSTORE%

@SET CMDLINE=keytool
@REM -import[导入命令] -v trustcacerts[导入信任证书] -storepass[存储密码] -alias[CA根证书的别名]
@SET CMDLINE=%CMDLINE% -import -v -trustcacerts -storepass %JDK_STOREPASS% -alias %JDK_CA_ROOT_ALIAS%
@REM -file[证书文件路径] -keystore[导入文件路径] -noprompt[不提示"信任这个认证？"]
@SET CMDLINE=%CMDLINE% -file work\ca\ca-cert.pem -keystore %JDK_KEYSTORE% -noprompt
%CMDLINE%

@SET CMDLINE=keytool
@REM -import[导入命令] -v trustcacerts[导入信任证书] -storepass[存储密码] -keypass[密钥保护密码]
@SET CMDLINE=%CMDLINE% -import -v -trustcacerts -storepass %SERVER_STOREPASS% -keypass %SERVER_KEYPASS%
@REM  -alias[服务器证书的别名] -file[证书文件路径] -keystore[导入文件路径]
@SET CMDLINE=%CMDLINE% -alias %SERVER_ALIAS% -file work\server\server-cert.pem -keystore work\server\%SERVER_KEYSTORE%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 把CA签名后的server端证书导入keystore: work\server\%SERVER_KEYSTORE%
%CMDLINE%

@SET CMDLINE=keytool
@REM -import[导入命令] -v trustcacerts[导入信任证书] -storepass[存储密码] -keypass[密钥保护密码]
@SET CMDLINE=%CMDLINE% -import -v -trustcacerts -storepass %SERVER_STOREPASS% -keypass %SERVER_KEYPASS%
@REM  -alias[服务器证书的别名] -file[证书文件路径] -keystore[导入文件路径] -noprompt[不提示"您仍然想要将它添加到自己的keystore 吗？"]
@SET CMDLINE=%CMDLINE% -alias %JDK_CA_ROOT_ALIAS% -file work\ca\ca-cert.pem -keystore work\server\%SERVER_KEYSTORE% -noprompt
%CMD_NEWLINE%
@echo ===============================================================================
@echo 把CA根证书导入keystore: work\server\%SERVER_KEYSTORE%
%CMDLINE%

%CMD_NEWLINE%
@echo 查看JSSE CA根证书 ==============================================================
REM keytool -list -storepass %JDK_STOREPASS% -keystore %JDK_KEYSTORE%
keytool -list -storepass %JDK_STOREPASS% -keystore %JDK_KEYSTORE% -alias %JDK_CA_ROOT_ALIAS% -v
%CMD_NEWLINE%
@echo 删除导入到JSSE的默认位置的CA根证书(使JDK恢复原状)==============================
keytool -delete -v -alias %JDK_CA_ROOT_ALIAS% -storepass %JDK_STOREPASS% -keystore %JDK_KEYSTORE%
@echo ===============================================================================

%CMD_NEWLINE%
@echo 将生成的证书保存起来(供服务器使用) ============================================
copy work\server\%SERVER_KEYSTORE% dist\server\%SERVER_KEYSTORE%
%CMD_NEWLINE%
@echo 查看server端证书 ==============================================================
keytool -list -storepass %SERVER_STOREPASS% -keystore dist\server\%SERVER_KEYSTORE% -v
@echo ===============================================================================



@goto END

:NO_JAVA_HOME
@echo 要运行"生成服务器端证书"的命令, 您需要安装JDK并设置"JAVA_HOME"环境变量

:END
@pause

@ENDLOCAL