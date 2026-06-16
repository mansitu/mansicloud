# MansiCloud-WEB

MansiCloud 前端源码

#### 项目源码

|        | 后端源码                                                    | 前端源码                                                        |
|--------|---------------------------------------------------------|-------------------------------------------------------------|
| 码云     | https://gitee.com/mansitu/mansicloud  <br/>mansicoud目录  | https://gitee.com/mansitu/mansicloud <br/>mansicould-web目录  |
| Github | https://github.com/mansitu/mansicloud  <br/>mansicoud目录 | https://github.com/mansitu/mansicloud <br/>mansicould-web目录 |


#### Build Setup
**推荐 node 版本：12-16**
``` bash
# 配置镜像加速
https://www.ydyno.com/archives/1219.html

# 安装依赖
npm install

# 启动服务 localhost:8013
npm run dev

# 构建生产环境
npm run build:prod
```

#### 常见问题

1、linux 系统在安装依赖的时候会出现 node-sass 无法安装的问题

解决方案：
```
1. 单独安装：npm install --unsafe-perm node-sass 
2. 直接使用：npm install --unsafe-perm
```

2、加速node-sass安装

https://izlzl.com/archives/1219.html

3、出现 No module named 'distutils'

```
需要安装 python，安装 pip 或者 pip3
然后安装 pip install distutils / pip3 install distutils
```


#### 开发文档

[可参考eladmin开发文档](https://eladmin.vip)