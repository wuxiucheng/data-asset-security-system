# 部署脚本调试指南

## 🚀 快速开始

### 1. 检查服务器环境

首先运行环境检查脚本，确保服务器满足部署要求：

```bash
cd deploy
./check-env.sh
```

这个脚本会检查：
- ✅ SSH连接是否正常
- ✅ Java环境是否安装
- ✅ 端口是否可用
- ✅ 磁盘空间是否充足
- ✅ 内存是否充足
- ✅ 必要服务是否安装

### 2. 本地构建测试

在部署到服务器前，建议先在本地测试构建：

```bash
# 仅构建后端，不上传
./deploy-backend.sh --build-only

# 仅构建前端，不上传
./deploy-frontend.sh --build-only
```

### 3. 部署到服务器

确认环境正常后，执行部署：

```bash
# 一键部署全栈
./deploy-all.sh

# 或分步部署
./deploy-backend.sh
./deploy-frontend.sh
```

## 🔧 配置说明

### 后端配置

在 `deploy-backend.sh` 中可以修改：

```bash
# 应用端口
APP_PORT="8080"  # 如果8080被占用，可以改为8081、8082等

# JVM参数
JVM_OPTS="-Xms512m -Xmx1024m"  # 根据服务器内存调整
```

### 前端配置

前端会自动构建并部署到服务器的 `/root/data-asset-security/frontend` 目录。

## 🐛 常见问题排查

### 问题1: SSH连接失败

**症状**：
```
❌ SSH连接失败
```

**解决方案**：
1. 检查SSH密钥权限：
```bash
chmod 600 ~/.ssh/id_ed25519
```

2. 测试SSH连接：
```bash
ssh -v root@47.94.52.27 -p 22022 -i ~/.ssh/id_ed25519
```

3. 检查服务器防火墙是否开放22022端口

### 问题2: 端口被占用

**症状**：
```
⚠️  端口 8080 已被占用
```

**解决方案**：

**方案1**: 修改应用端口
```bash
# 编辑 deploy-backend.sh
APP_PORT="8081"  # 改为其他端口
```

**方案2**: 停止占用端口的进程
```bash
# 查看占用进程
ssh root@47.94.52.27 -p 22022 "netstat -tlnp | grep :8080"

# 停止进程
ssh root@47.94.52.27 -p 22022 "kill -9 <PID>"
```

### 问题3: Java版本不兼容

**症状**：
```
Unsupported class file major version 61
```

**解决方案**：

服务器需要Java 17+，检查并安装：

```bash
# 检查Java版本
ssh root@47.94.52.27 -p 22022 "java -version"

# 如果版本不对，安装Java 17
# CentOS/RHEL:
ssh root@47.94.52.27 -p 22022 "yum install java-17-openjdk-devel"

# Ubuntu/Debian:
ssh root@47.94.52.27 -p 22022 "apt install openjdk-17-jdk"
```

### 问题4: 后端启动失败

**症状**：
```
❌ 后端服务启动失败
```

**排查步骤**：

1. 查看详细日志：
```bash
ssh root@47.94.52.27 -p 22022 "tail -200 /root/data-asset-security/backend/app.log"
```

2. 检查数据库连接：
```bash
# 检查MySQL是否运行
ssh root@47.94.52.27 -p 22022 "systemctl status mysql"

# 测试数据库连接
ssh root@47.94.52.27 -p 22022 "mysql -u root -p"
```

3. 检查配置文件：
```bash
# 查看生产环境配置
ssh root@47.94.52.27 -p 22022 "cat /root/data-asset-security/backend/application-prod.yml"
```

### 问题5: 前端访问404

**症状**：访问前端页面显示404

**解决方案**：

1. 检查前端文件是否存在：
```bash
ssh root@47.94.52.27 -p 22022 "ls -la /root/data-asset-security/frontend/"
```

2. 配置Nginx（如果使用）：
```bash
# 检查Nginx配置
ssh root@47.94.52.27 -p 22022 "nginx -t"

# 重启Nginx
ssh root@47.94.52.27 -p 22022 "systemctl restart nginx"
```

## 📊 服务管理

### 查看服务状态

```bash
# 查看后端进程
ssh root@47.94.52.27 -p 22022 "ps aux | grep app.jar"

# 查看端口监听
ssh root@47.94.52.27 -p 22022 "netstat -tlnp | grep 8080"

# 查看服务日志
ssh root@47.94.52.27 -p 22022 "tail -f /root/data-asset-security/backend/app.log"
```

### 重启服务

```bash
# 重启后端
ssh root@47.94.52.27 -p 22022 bash << 'EOF'
cd /root/data-asset-security/backend
if [ -f app.pid ]; then
    kill $(cat app.pid) || true
    rm -f app.pid
fi
nohup java -Xms512m -Xmx1024m -jar app.jar --spring.profiles.active=prod > app.log 2>&1 &
echo $! > app.pid
echo "后端已重启，PID: $(cat app.pid)"
EOF
```

### 停止服务

```bash
# 停止后端
ssh root@47.94.52.27 -p 22022 bash << 'EOF'
cd /root/data-asset-security/backend
if [ -f app.pid ]; then
    kill $(cat app.pid) || true
    rm -f app.pid
    echo "后端已停止"
fi
EOF
```

## 🔐 安全建议

1. **修改默认端口**：如果服务器有其他服务，建议修改应用端口
2. **配置防火墙**：只开放必要的端口
3. **使用HTTPS**：配置SSL证书
4. **定期备份**：备份数据库和配置文件

## 📝 日志查看

### 实时查看日志

```bash
# 后端日志
ssh root@47.94.52.27 -p 22022 "tail -f /root/data-asset-security/backend/app.log"

# Nginx日志（如果使用）
ssh root@47.94.52.27 -p 22022 "tail -f /var/log/nginx/access.log"
ssh root@47.94.52.27 -p 22022 "tail -f /var/log/nginx/error.log"
```

### 下载日志到本地

```bash
# 下载后端日志
scp -P 22022 -i ~/.ssh/id_ed25519 \
    root@47.94.52.27:/root/data-asset-security/backend/app.log \
    ./backend.log
```

## 🎯 性能优化

### JVM优化

根据服务器内存调整JVM参数：

```bash
# 4GB内存
JVM_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC"

# 8GB内存
JVM_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC"

# 16GB内存
JVM_OPTS="-Xms4g -Xmx8g -XX:+UseG1GC"
```

### 数据库优化

确保MySQL配置合理：

```bash
# 检查MySQL配置
ssh root@47.94.52.27 -p 22022 "cat /etc/my.cnf"
```

## 📞 获取帮助

如果遇到问题：

1. 运行环境检查：`./check-env.sh`
2. 查看详细日志
3. 检查配置文件
4. 参考本文档的故障排查部分

---

祝部署顺利！🎉
