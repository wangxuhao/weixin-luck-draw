@SETLOCAL
@call .etc\setEnv.cmd

@REM 参数处理部分 ===================================================================
@echo off
REM 判断是否有输入参数
if NOT "%1"=="" goto SET_CMDLINE
goto SET_CMDLINE_END
:SET_CMDLINE
SET $_CMDLINE=TRUE
:SET_CMDLINE_END

REM 处理输入参数 1-Common Name, 2-Organizational Unit Name, 3-export password
if NOT "%1"=="" goto SET_CLIENT_DN_CN
goto SET_CLIENT_DN_CN_END
:SET_CLIENT_DN_CN
SET CLIENT_DN_CN=%1
echo CLIENT_DN_CN=%CLIENT_DN_CN%
:SET_CLIENT_DN_CN_END

if NOT "%2"=="" goto SET_CLIENT_DN_OU
goto SET_CLIENT_DN_OU_END
:SET_CLIENT_DN_OU
SET CLIENT_DN_OU=%2
echo CLIENT_DN_OU=%CLIENT_DN_OU%
:SET_CLIENT_DN_OU_END

if NOT "%3"=="" goto SET_CLIENT_EXP_PWD
goto SET_CLIENT_EXP_PWD_END
:SET_CLIENT_EXP_PWD
SET CLIENT_EXP_PWD=%3
echo CLIENT_EXP_PWD=%CLIENT_EXP_PWD%
:SET_CLIENT_EXP_PWD_END

@echo on
@REM ================================================================================


%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成客户端证书 ================================================================
@echo ===============================================================================

mkdir work
mkdir work\client
del /s /q work\client

%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成client私钥: work\client\client-key.pem
@REM genrsa [产生密钥命令] -out[密钥文件输出路径] 1024 [密钥位数]
openssl genrsa -out work\client\client-key.pem 1024

@SET $_CLIENT_DN=/C=%BASE_DN_C%/ST=%BASE_DN_ST%/L=%BASE_DN_L%/O=%BASE_DN_O%/OU=%CLIENT_DN_OU%/CN=%CLIENT_DN_CN%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成待签名证书: work\client\client-req.csr
@REM req[产生证书命令] -new[新生成] -out[证书文件输出路径] -key[私钥文件路径]
@REM -subj[request's subject, 这里放置Distinguished Name(DN)信息]
openssl req -new -out work\client\client-req.csr -key work\client\client-key.pem -subj %$_CLIENT_DN%


@SET CMDLINE=openssl
@REM x509[签发x509证书命令] -req[输入待签发证书] -in[输入待签发证书文件路径] -out[产生x509证书文件输出路径]
@SET CMDLINE=%CMDLINE% x509 -req -in work\client\client-req.csr -out work\client\client.crt
@REM -signkey [密钥文件路径]
@SET CMDLINE=%CMDLINE% -signkey work\client\client-key.pem
@REM // -CA[签发根证书] -CAkey[根证书密钥文件] -days[证书有效期] -CAcreateserial[创建序列号]
@REM // SET CMDLINE=%CMDLINE% -CA work\ca\ca-cert.pem -CAkey work\ca\ca-key.pem -days %CLIENT_KEY_DAYS% -CAcreateserial
@REM -CA[签发根证书] -CAkey[根证书密钥文件] -days[证书有效期] -CAserial[CA序列号文件]
@SET CMDLINE=%CMDLINE% -CA work\ca\ca-cert.pem -CAkey work\ca\ca-key.pem -days %CLIENT_KEY_DAYS% -CAserial %OPENSSL_SRL_FILE%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 用CA私钥进行签名, 产生x509证书文件: work\client\client.crt
%CMDLINE%

@SET $_CLIENT_P12_FILE=%CLIENT_DN_OU%-%CLIENT_DN_CN%.p12
@SET CMDLINE=openssl
@REM pkcs12[生成PKS12格式证书命令] -export[导出文件] -clerts[仅导出client证书] -password[导出密码]
@SET CMDLINE=%CMDLINE% pkcs12 -export -clcerts -password pass:%CLIENT_EXP_PWD%
@REM -in[输入的client证书文件路径] -inkey[client证书密钥文件路径] -out[导出PKS12格式文件路径]
@SET CMDLINE=%CMDLINE% -in work\client\client.crt -inkey work\client\client-key.pem -out work\client\%$_CLIENT_P12_FILE%
@REM -name[好记的名字]
@SET CMDLINE=%CMDLINE% -name %CLIENT_DN_OU%-%CLIENT_DN_CN%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成client端的个人证书: work\client\%$_CLIENT_P12_FILE%
%CMDLINE%

%CMD_NEWLINE%
@echo ===============================================================================
@echo 将生成的证书保存起来(发布给用户)
copy work\client\%$_CLIENT_P12_FILE% dist\client\%$_CLIENT_P12_FILE%

@if "%$_CMDLINE%"=="TRUE" goto NO_PAUSE
@pause
:NO_PAUSE

@ENDLOCAL