@SETLOCAL
@call .etc\setEnv.cmd

%CMD_NEWLINE%
@echo ===============================================================================
@echo 从PKCS12格式的CA根证书导出 CA私钥和未加密CA根证书 =============================
@echo ===============================================================================

mkdir work
mkdir work\ca
del /s /q work\ca

%CMD_NEWLINE%
@echo ===============================================================================
@echo 从PKCS12格式的CA证书导出不加密的CA证书: work\ca\ca-cert.pem
openssl pkcs12 -in dist\ca-cert\%CA_DN_CN%-%CA_DN_OU%.pfx -clcerts -nodes -nokeys -out work\ca\ca-cert.pem.1
@REM 将得到的证书文件的开头四行(Bag Attributes)去掉
@REM 因为step2-server.bat中keytool import时会认为含有Bag Attributes的文件"不是一个 X.509 认证"
%CMD_SED% -e '1,4d' work\ca\ca-cert.pem.1 > work\ca\ca-cert.pem

%CMD_NEWLINE%
@echo ===============================================================================
@echo 从PKCS12格式的CA证书导出CA私钥: work\ca\ca-key.pem
openssl pkcs12 -in dist\ca-cert\%CA_DN_CN%-%CA_DN_OU%.pfx -clcerts -nodes -out work\ca\file.pem
openssl rsa -in work\ca\file.pem -out work\ca\ca-key.pem

@pause