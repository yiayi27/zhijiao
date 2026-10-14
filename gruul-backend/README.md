# gruul

#### 介绍

gruul 小程序商城

#### 软件架构

1. 模块说明
   ```yaml
   gruul
   ├── gruul-common  --公共模块
   ├── gruul-nacos  --注册配置中心[8848]
   ├── gruul-gateway  --gateway网关[9999]
   ├── gruul-oss  --OSS模块
   │   ├── gruul-oss-api  --OSS公共api
   │   ├── gruul-oss-service  --OSS业务处理[9300]

   ├── gruul-order  --订单模块
   │   ├── gruul-order-api  --订单公共api
   │   ├── gruul-order-service  --订单业务处理[9150]

   ├── gruul-platform  --平台模块
   │   ├── gruul-platform-api  --平台公共api
   │   ├── gruul-platform-service  --平台业务处理[9200]

    ├── gruul-account  --用户模块
    │   ├── gruul-account-api  --用户公共api
    │   ├── gruul-account-service  --用户业务处理[9450]

     ├── gruul-afs      --售后模块
     │   ├── gruul-afs-api  --售后公共api
     │   ├── gruul-afs-service  --售后业务处理[9800]

      ├── gruul-datasets      --数据模块
      │   ├── gruul-datasets-api  --数据公共api
      │   ├── gruul-datasets-service  --数据业务处理[9600] 

      ├── gruul-discount      --抵扣模块
      │   ├── gruul-discount-api  --抵扣公共api
      │   ├── gruul-discount-service  --抵扣业务处理[9950]

      ├── gruul-finance      --财务模块
      │   ├── gruul-finance-api  --财务公共api
      │   ├── gruul-finance-service  --财务业务处理[9550]

      ├── gruul-goods      --商品模块
      │   ├── gruul-goods-api  --商品公共api
      │   ├── gruul-goods-service  --商品业务处理[9100]

      ├── gruul-integral      --积分商城模块
      │   ├── gruul-integral-api  --积分商城公共api
      │   ├── gruul-integral-service  --积分商城业务处理[9700]

      ├── gruul-live      --直播模块
      │   ├── gruul-live-api  --直播公共api
      │   ├── gruul-live-service  --直播业务处理[9750]

      ├── gruul-logistics      --物流模块
      │   ├── gruul-logistics-api  --物流公共api
      │   ├── gruul-logistics-service  --物流业务处理[9178]

      ├── gruul-oss      --oss短信模块
      │   ├── gruul-oss-api  --oss公共api
      │   ├── gruul-oss-service  --oss业务处理[9300]

      ├── gruul-payment      --支付模块
      │   ├── gruul-payment-api  --支付公共api
      │   ├── gruul-payment-service  --支付业务处理[9350]     

      ├── gruul-recycling      --回收模块
      │   ├── gruul-recycling-api  --回收公共api
      │   ├── gruul-recycling-service  --回收业务处理[9460]   

      ├── gruul-shops      --配送模块
      │   ├── gruul-shops-api  --配送公共api
      │   ├── gruul-shops-service  --配送业务处理[9211] 

      ├── gruul-sms      --短信
      │   ├── gruul-sms-api  --配送公共api
      │   ├── gruul-sms-service  --配送业务处理[9050]    

      ├── gruul-solitaire      --接龙模块(慎用)
      │   ├── gruul-solitaire-api  --配送公共api
      │   ├── gruul-solitaire-service  --配送业务处理[9850]  
   
   ```
2. swagger使用
    - 在主类上添加注解 @EnableGruulSwagger2
    ```java
        @EnableGruulSwagger2
        @SpringCloudApplication
        @EnableFeignClients
        @MapperScan("com.medusa.gruul.oss.dao")
        public class OSSApplication {
        
           public static void main(String[] args) {
               SpringApplication.run(OSSApplication.class, args);
           }
        
        }
   ```
    - 定制化swagger配置详见com.medusa.gruul.common.swagger.config.SwaggerProperties中的属性

#### 安装教程

1. 下载本项目

2.相关应用安装 Nacos Mysql Redis Rabbitmq(延迟队列插件)
```

3. 初始化数据库
    - 运行建库语句gruul.sql
    - 运行初始化的sql文件
4. 修改配置文件
    - 运行nacos 默认账号密码（nacos/nacos）
    - 修改application-dev.yml文件中的redis配置
    - 修改oss-dev.yml等文件中的数据库配置
5. 安装lombok插件
   详见 [idea安装Lombok](https://www.jianshu.com/p/37e24fe833d6)

6. 启动顺序  
   1.NacosApplication.java   (安装的nacos)
   2.GatewayApplication.java  
   3.随意(注: 启动AfterServiceApplication前应启动OrderApplication 使监听队列自动注册成功)

7. 基础配置
   oss图片上传配置 后台页面-商城设置-通用设置-OSS设置。 配置完成后重启oss服务或等待10分钟 oss自动更新配置
   相关实现：com.medusa.gruul.oss.task.UpdateConfigTask.class
   短信配置: 短信配置为数据库写死 相关表 t_sms_*
   支付配置：后台页面-商城设置-支付配置 (注:支付配置编辑需完成网站应用验证--二维码认证)
   商户修改：后台页面-商家中心(店铺名称哪里)-头像换绑

#### 开发说明

1. git commit 提交规范  
   关于git Commit message 可以阅读相关文章[Commit message 和 Change log 编写指南
   ](http://www.ruanyifeng.com/blog/2016/01/commit_message_change_log.html)  
   基于实际情况考虑，对上文中提到的commit log规范进行了简化,具体如下：
   ```yaml
   <type>: <subject>
   <body>
   ```
    1. type  
       提交 commit 的类型，包括以下几种  
       feat: 新功能  
       fix: 修复问题  
       docs: 修改文档  
       style: 修改代码格式，不影响代码逻辑  
       refactor: 重构代码，理论上不影响现有功能  
       perf: 提升性能  
       test: 增加修改测试用例  
       revert: 回退
    2. subject  
       用一句话清楚的描述这次提交做了什么。书写要遵循以下四种规则：格式尽量使用谓宾，使用谓宾不通顺时，可以使用主谓，例如：  
       谓宾：修复xxxx  
       主谓：支持xxxx

        - 除了名称之外，描述尽可能使用中文，方便不同开发者理解
        - 结尾不加句号
        - 描述控制在20个汉字以内
    3. body  
       对本地提交的详细描述，不建议。我们建议多次少量提交，而不是一次巨量的提交，有助于revert和code review。
2. 编码规范
   安装插件Alibaba Java Coding Guidelines plugin 提交代码前须使用插件检查代码，全部修改之后在进行提交

jenkins file 详解 Jenkinsfile为Jenkins的流水线脚本 语法说明
buildDiscarder(logRotator(numToKeepStr: '2')) 保留成功次数
DEV_NODE jenkins节点管理
PORT 项目版本号
DOCKER_REGISTRY docker镜像上传版本
MAPPING_PATH 映射地址
steps {
maven版本 maven标识
withMaven(maven: 'maven3.6.2', mavenSettingsConfig: 'jenkins中mavenSettings的值') {
maven打包
sh "mvn clean package -U -Dmaven.test.skip=true deploy"
}
}
docker镜像build push到仓库
docker.withRegistry("http://${DOCKER_REGISTRY}", "harbor") {
def customImage = docker.build("${IMAGE_NAME}:${IMAGE_VERSION}")
customImage.push()
}
Dockerfile 详解
引入依赖镜像    
FROM anapsix/alpine-java:8_server-jre_unlimited
创建软连接同步时间
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
add *.jar
ADD gruul-platform-service/target/*.jar gruul-platform.jar
配置内存
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom" ,"-jar" , "-Xms512m","-Xmx512m" ,"/gruul-platform.jar"]