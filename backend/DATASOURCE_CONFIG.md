# 数据库连接池配置说明

## 问题描述

错误信息：
```
java.sql.SQLNonTransientConnectionException: No operations allowed after connection closed.
```

**原因**：
MySQL服务器关闭了空闲连接，但Druid连接池不知道，仍然尝试使用已关闭的连接。

## 解决方案

### 关键配置项

#### 1. 连接验证配置
```yaml
validation-query: SELECT 1              # 验证SQL
validation-query-timeout: 3             # 验证超时时间（秒）
test-while-idle: true                   # 空闲时测试
test-on-borrow: true                    # 借出时测试（重要！）
test-on-return: false                   # 归还时测试
```

**说明**：
- `test-on-borrow: true` - 每次从连接池获取连接时，先验证连接是否有效
- 这会增加一点性能开销，但能避免使用已关闭的连接

#### 2. 保活配置
```yaml
keep-alive: true                        # 启用保活
keep-alive-between-time-millis: 30000   # 保活间隔（30秒）
```

**说明**：
- 定期发送`SELECT 1`保持连接活跃
- 防止MySQL因wait_timeout关闭连接

#### 3. 连接回收配置
```yaml
time-between-eviction-runs-millis: 60000    # 回收线程运行间隔（60秒）
min-evictable-idle-time-millis: 300000      # 连接最小空闲时间（5分钟）
```

**说明**：
- 定期检查并移除空闲时间过长的连接
- 确保连接池中的连接都是有效的

#### 4. 异常连接处理
```yaml
remove-abandoned: true                 # 移除泄露的连接
remove-abandoned-timeout: 1800         # 泄露超时时间（30分钟）
log-abandoned: true                    # 记录泄露日志
```

**说明**：
- 如果连接被借出超过30分钟未归还，认为是泄露
- 自动回收并记录日志，便于排查问题

## 完整配置

### application.yml
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      validation-query-timeout: 3
      test-while-idle: true
      test-on-borrow: true
      test-on-return: false
      keep-alive: true
      keep-alive-between-time-millis: 30000
      remove-abandoned: true
      remove-abandoned-timeout: 1800
      log-abandoned: true
```

## MySQL服务器配置建议

### 检查wait_timeout
```sql
SHOW VARIABLES LIKE 'wait_timeout';
```

默认值通常是8小时（28800秒）。如果设置太短，需要调整Druid配置：

```yaml
# 如果MySQL wait_timeout = 3600 (1小时)
min-evictable-idle-time-millis: 1800000  # 30分钟
keep-alive-between-time-millis: 1800000  # 30分钟
```

### 推荐的MySQL配置
```ini
[mysqld]
wait_timeout = 28800          # 8小时
interactive_timeout = 28800   # 8小时
max_connections = 200         # 最大连接数
```

## 监控和排查

### 1. 查看连接池状态
访问Druid监控页面：
```
http://localhost:8080/druid/index.html
```

### 2. 查看连接泄露日志
如果启用了`log-abandoned: true`，会在日志中看到：
```
连接泄露警告: 连接被借出超过1800秒未归还
```

### 3. 常见问题排查

**问题1：连接池耗尽**
```
错误: wait timeout, try to get connection failed
```
解决：
- 增加`max-active`
- 检查是否有连接泄露
- 优化SQL查询，减少连接占用时间

**问题2：连接已关闭**
```
错误: No operations allowed after connection closed
```
解决：
- 确保`test-on-borrow: true`
- 确保`keep-alive: true`
- 检查MySQL的wait_timeout设置

**问题3：连接超时**
```
错误: Communications link failure
```
解决：
- 检查网络连接
- 增加连接超时配置
- 检查MySQL服务器状态

## 性能优化建议

### 1. 连接池大小
```yaml
# 小型应用
initial-size: 3
min-idle: 3
max-active: 10

# 中型应用
initial-size: 5
min-idle: 5
max-active: 20

# 大型应用
initial-size: 10
min-idle: 10
max-active: 50
```

### 2. 验证频率
```yaml
# 高可靠性（推荐生产环境）
test-on-borrow: true
keep-alive-between-time-millis: 30000

# 高性能（仅开发环境）
test-on-borrow: false
test-while-idle: true
time-between-eviction-runs-millis: 30000
```

## 部署后验证

### 1. 检查连接池配置是否生效
```bash
# 查看应用日志
tail -f /root/data-asset-security/backend/app.log | grep -i druid
```

### 2. 测试数据库连接
```bash
# 登录MySQL
mysql -h localhost -u root -p'1Q2w3e4R#' data_asset_security

# 查看连接数
SHOW PROCESSLIST;

# 查看当前连接数
SELECT COUNT(*) FROM information_schema.PROCESSLIST WHERE USER='root';
```

### 3. 压力测试
```bash
# 使用ab或wrk进行压力测试
ab -n 1000 -c 10 http://localhost:8082/api/classificationAssistStatistics/overview
```

观察连接池状态，确保没有连接泄露或耗尽。

## 相关文档

- [Druid官方文档](https://github.com/alibaba/druid/wiki)
- [MySQL连接超时问题](https://dev.mysql.com/doc/refman/8.0/en/gone-away.html)
- [Spring Boot数据源配置](https://docs.spring.io/spring-boot/docs/current/reference/html/data.html#data.sql.datasource)
