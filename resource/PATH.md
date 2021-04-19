# 环境搭建

## 虚拟机配置

### 时间校准

```shell script
yum -y install ntp

# 使用 ntpdate 测试 NTP
ntpdate pool.ntp.org

# 查看服务器时间
date
```

### Docker

```shell script

```

### Docker-Compose

```shell script

```

## 开发环境

### MySQL

```shell script
docker pull mysql:5.7

docker run --name mysql \
--privileged=true \
-p 3306:3306 \
-e MYSQL_ROOT_PASSWORD=yueliminvc@outlook.com \
-d mysql:5.7
```

### Redis

```shell script
docker pull redis:4

docker run --name redis -p 6379:6379  \
-d redis:4 \
--requirepass "yueliminvc@outlook.com" \
--appendonly yes
```

### Zookeeper

```shell script
docker pull zookeeper:3.4.13

docker run -id --name zookeeper \
--privileged=true \
-p 2181:2181 \
zookeeper:3.4.13
```

### RocketMQ

创建配置文件: `broker.conf`

```properties
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
diskMaxUsedSpaceRatio = 88
mapedFileSizeCommitLog = 1073741824
fileReservedTime = 120
mapedFileSizeConsumeQueue = 300000
maxMessageSize = 65536
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
brokerIP1 = 192.168.158.160
listenPort = 10911
# 是否允许 Broker 自动创建Topic, 建议线下开启, 线上关闭
autoCreateTopicEnable = true
# 是否允许 Broker 自动创建订阅组, 建议线下开启, 线上关闭
autoCreateSubscriptionGroup = true
```

容器创建

```shell script
# rocketmq
docker pull rocketmqinc/rocketmq:4.4.0

docker run -d -p 9876:9876 \
-v /root/docker/rocketmq/namesrv/logs:/root/logs \
-v /root/docker/rocketmq/namesrv/store:/root/store \
--name rmqnamesrv \
-e "MAX_POSSIBLE_HEAP=100000000" rocketmqinc/rocketmq:4.4.0 sh mqnamesrv

docker run -d -p 10911:10911 -p 10909:10909 \
--privileged=true \
-v /root/docker/rocketmq/broker/logs:/root/logs \
-v /root/docker/rocketmq/broker/store:/root/store \
-v /root/docker/rocketmq/broker/conf/broker.conf:/opt/rocketmq-4.4.0/conf/broker.conf \
--name rmqbroker \
--link rmqnamesrv:namesrv \
-e "NAMESRV_ADDR=namesrv:9876" \
-e "MAX_POSSIBLE_HEAP=200000000" \
rocketmqinc/rocketmq:4.4.0 sh mqbroker -c /opt/rocketmq-4.4.0/conf/broker.conf

# 监控
docker pull styletang/rocketmq-console-ng

docker run -d --name rmqconsole \
-p 8080:8080 \
-e "JAVA_OPTS=-Drocketmq.namesrv.addr=192.168.158.160:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" \
styletang/rocketmq-console-ng
```

### RabbitMQ

```shell script
docker pull rabbitmq:management

docker run -id --name=rabbitmq \
-p 5671:5617 \
-p 5672:5672 \
-p 4369:4369 \
-p 15671:15671 \
-p 15672:15672 \
-p 25672:25672 \
rabbitmq:management
```
