@SETLOCAL
@call .etc\setEnv.cmd

%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成CA私钥以及自签名根证书, 最后得到PKCS12格式的CA根证书 ======================
@echo ===============================================================================

mkdir work
mkdir work\ca
del /s /q work\ca

%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成CA私钥 ca-key.pem
@REM genrsa [产生密钥命令] -out[密钥文件输出路径] 1024 [密钥位数]
openssl genrsa -out work\ca\ca-key.pem 1024

@SET $_CA_DN=/C=%BASE_DN_C%/ST=%BASE_DN_ST%/L=%BASE_DN_L%/O=%BASE_DN_O%/OU=%CA_DN_OU%/CN=%CA_DN_CN%
%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成待签名证书 ca-req.csr
@REM req[产生证书命令] -new[新生成] -out[证书文件输出路径] -key[私钥文件路径]
openssl req -new -out work\ca\ca-req.csr -key work\ca\ca-key.pem -subj %$_CA_DN%

%CMD_NEWLINE%
@echo ===============================================================================
@echo 用CA私钥进行自签名, 产生x509证书文件 ca-cert.pem
@REM x509[签发x509证书命令] -req[输入待签发证书] -in[输入待签发证书文件路径] -out[产生x509证书文件输出路径]
@REM -signkey[自签发密钥文件路径] -days[证书有效期]
openssl x509 -req -in work\ca\ca-req.csr -out work\ca\ca-cert.pem -signkey work\ca\ca-key.pem -days %CA_KEY_DAYS%

%CMD_NEWLINE%
@echo ===============================================================================
@echo 生成CA证书: work\ca\ca-cert.pfx, 注意一定要记住导出密码
@REM pkcs12[生成PKCS12格式证书命令] -export[导出文件] -clerts[仅导出client证书] -password[导出密码]
@REM -in[输入的client证书文件路径] -inkey[client证书密钥文件路径] -out[导出PKS12格式文件路径]
openssl pkcs12 -export -clcerts -in work\ca\ca-cert.pem -inkey work\ca\ca-key.pem -out work\ca\ca-cert.pfx

%CMD_NEWLINE%
@echo ===============================================================================
@echo 将生成的证书保存起来(以后需要用到)
copy work\ca\ca-cert.pfx dist\ca-cert\%CA_DN_CN%-%CA_DN_OU%.pfx

%CMD_NEWLINE%
@echo ===============================================================================
@echo 重置CA序列号文件
echo 00 > %OPENSSL_SRL_FILE%

@pause

@ENDLOCAL