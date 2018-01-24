### JT808-DSLink
#### 包结构
* com.c503.jt808.iot.dslink<br>
包含JT808-DSLink逻辑实现所有类
* com.c503.jt808.netty<br>
包含解析JT808协议逻辑<br>
原工程：[hylexus/jt-808-protocol](https://github.com/hylexus/jt-808-protocol)
#### 程序入口
com.c503.jt808.iot.dslink.Jt808DsLink
#### 实现功能
* SetupTCPServer<br>
通过DGLux5平台初始化netty服务
* StartTCPServer<br>
启动指定netty服务
* StopTCPServer<br>
停止指定netty服务
* DeleteTCPServer<br>
删除指定netty服务
* QueryOnlineTerminal<br>
查询在线终端
* AutoAddTerminal<br>
自动添加终端节点
* ModifyHeartBeatInterval<br>
修改终端上报心跳时间间隔
* RemoveTermianl<br>
删除未连接终端节点信息
* 自动更新终端位置数据<br>
* 页面监控终端连接状态
#### Node结构
````
/   root
|
|__status
|
|__nettyServerName
           |
           |__status
           |
           |__terminalPhone
                    |__status
                    |__locationInfo
                    |__auth code
                    |__otherInfo


````
