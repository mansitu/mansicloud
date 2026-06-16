
### 免责声明和提醒：本项目仅用于个人学习和使用，请遵循网络安全法规，及时更新源码修复漏洞，加强服务器防护，杜绝网络安全事件发生。

<h1 style="text-align: center">MansiCloud 轻量级工业物联网平台</h1>

### 项目简介

#### mansicloud基于 Spring Boot、 Spring Boot Jpa、 JWT、Spring Security、Redis、Vue、 Caffeine、 TDengin、 Mqtt、 Mysql等技术开发的前后端分离的开源物联网系统

### **⭐⭐⭐⭐⭐ 配合对应边缘数据网关,可兼容市面所有物联网设备接入,适合物联网数据采集项目快速落地。**

### 项目源码

|     | 后端源码                                              | 前端源码                                                  |
|---  |---------------------------------------------------|-------------------------------------------------------|
|  码云   | https://gitee.com/mansitu/mansicloud  <br/>mansicoud目录 | https://gitee.com/mansitu/mansicloud <br/>mansicould-web目录 |


#### 主要特性
- 创建产品、定义物模型、设置设备认证方式、管理产品分类
- 设备注册、分组、实时状态监测、操作日志追溯
- 提供标准化的属性、功能和事件定义，实现设备与平台的数据语义统一
- 基于MQTT数据收发，配合边缘网关，可兼容市面上所有主流设备接入
- 支持 MySQL、PostgreSQL、MSSQL 等关系型数据库
- 支持 TDengine时序数据库
- 使用最新技术栈，社区资源丰富。
- 高效率开发，代码生成器可一键生成前后端代码
- 支持数据字典，可方便地对一些状态进行管理
- 支持接口限流，避免恶意请求导致服务层压力过大
- 支持接口级别的功能权限与数据权限，可自定义操作
- 自定义权限注解与匿名接口注解，可快速对接口拦截与放行
- 对一些常用地前端组件封装：表格数据请求、数据字典等
- 前后端统一异常拦截处理，统一输出异常，避免繁琐的判断
- 支持在线用户管理与服务器性能监控，支持限制单用户登录
- 支持运维管理，可方便地对远程服务器的应用进行部署与管理

####  系统功能
- 首页：提供整个系统所有设备的概览信息，分类显示设备数据
- 设备管理：提供设备类型管理和设备列表管理，包括设备的数据模型定义等
- 数据查询：提供设备数据查询功能，可对设备数据进行查询、导出
- 测试工具：提供MQTT数据发送工具，测试系统数据收发是否正常
- 用户管理：提供用户的相关配置，新增用户后，默认密码为123456
- 角色管理：对权限与菜单进行分配，可根据部门设置角色的数据权限
- 菜单管理：已实现菜单动态路由，后端可配置化，支持多级菜单
- 部门管理：可配置系统组织架构，树形表格展示
- 岗位管理：配置各个部门的职位
- 字典管理：可维护常用一些固定的数据，如：状态，性别等
- 系统日志：记录用户操作日志与异常日志，方便开发人员定位排错
- 代码生成：高灵活度生成前后端代码，减少大量重复的工作任务
- 服务监控：监控服务器的负载情况
- 运维管理：一键部署你的应用

### 系统架构

<img src="https://www.mansitu.cn/images/mansicloud/system_arch.png"/>

### 其他

1. **商用授权**：开源版本采用 GPLv3 协议，可用于个人学习和使用，商业用途推荐购买[商业版本授权](https://www.mansitu.cn)。
2. **在线演示**：[商业版本演示地址](https://iot.mansitu.cn/)。 用户名：mansitu 密码：Mansi@Cloud
4. **QQ 交流群**：949919518(MansiCloud技术交流群)

### 系统截图

![部分功能截图](https://www.mansitu.cn/images/mansicloud/dashboard.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/devicetype.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/devices.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/latestdata.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/hiscurve.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/hisdata.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/mqtt.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/cache.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/rawdata.png)
![部分功能截图](https://www.mansitu.cn/images/mansicloud/devicemodel.png)

### 边缘网关支持的设备类型
![部分功能截图](https://www.mansitu.cn/images/mansicloud/gateway.png)



#### 开发文档

Mansicloud 项目代码完全开源，覆盖后端服务、前端应用，兼顾成熟度、性能与开发效率，适合中小企业快速构建物联网业务，也便于开发者进行二次开发与学习研究。

[可参考eladmin开发文档](https://eladmin.vip)
