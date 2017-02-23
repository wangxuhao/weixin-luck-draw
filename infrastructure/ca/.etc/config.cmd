REM ===========================================================================
REM 请根据您的具体情况修改本文的配置信息
REM ===========================================================================


REM 基本信息 ==================================================================
REM 所有证书公用的 distinguished name 信息
REM (O:Organization Name, L:Locality Name, ST:State or Province Name, C:Country Name)
SET BASE_DN_O=yigo-redist
SET BASE_DN_L=bokesoft
SET BASE_DN_ST=Shanghai
SET BASE_DN_C=CN
REM ===========================================================================


REM CA 相关配置 ===============================================================
REM CA私钥证书文件的有效期(天)
SET CA_KEY_DAYS=3700

REM CA 证书中的 distinguished name 信息
REM CA对应的CN (Common Name)
SET CA_DN_CN=yigo-redist
REM CA对应的OU (Organizational Unit Name)
SET CA_DN_OU=yigo-redist-ca
REM ===========================================================================


REM 服务器证书相关配置 ========================================================
SET WEBSITE=yigo-redist.dev.bokesoft.com
SET WEBSERVER=YigoRedistWebSite

REM 服务器证书的有效期(天)
SET SERVER_KEY_DAYS=3700

REM KEYPASS:密钥保护密码, STOREPASS:存储密码(用在Tomcat上时, 这两个密码必须一样)
SET SERVER_KEYPASS=openssl
SET SERVER_STOREPASS=openssl

REM 服务器证书中的 distinguished name 信息(Organizational Unit Name)
SET SERVER_DN_OU=yigo-redist-website
REM ===========================================================================


REM 客户端证书相关配置 ========================================================
REM 客户端证书文件的有效期(天)
SET CLIENT_KEY_DAYS=365

REM 客户证书中的默认 distinguished name 信息
SET CLIENT_DN_CN=test-user
SET CLIENT_DN_OU=yigo-redist-client

REM 客户证书的默认导出密码
SET CLIENT_EXP_PWD=openssl
REM ===========================================================================
