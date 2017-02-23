# weixin-luck-draw
+ 功能列表
	- 用户关注微信公众号
	- 通过大屏幕为微信公众号粉丝就行抽奖
	- 微信公众号粉丝微信摇一摇红包雨
+ 目录结构
	-  整个目录包含了整个运行环境
	- `default` 默认的插件；包含jdbc的模板
	- `etc` 系统文件；包含 `/runner` 启动脚本目录， `/db` 数据库初始化脚本
	- `infrastructure` 基础设施；集成了`jdk`、`tomcat`、`mysql`、`ca`
	- `products` 项目插件以及集成项目；
	- `profiles` 启动目录；内包含`windows`启动脚本`copy-to-batch.txt`、`linux`启动脚本`copy-to-shell.txt`
	- `web-apps` 存放web项目；里面包含了该项目源码