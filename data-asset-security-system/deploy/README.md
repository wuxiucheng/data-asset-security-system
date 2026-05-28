# 部署指南

## 目录结构

```
deploy/
├── deploy-backend.sh    # 部署后端到远程服务器
├── deploy-frontend.sh   # 部署前端到远程服务器
├── deploy-all.sh        # 一键部署全栈应用
└── README.md            # 本文档
```

## 前置要求

### 本地环境
- JDK 17+
- Node.js 18+
- Maven 3.8+
- SSH密钥已配置（免密登录）

### 远程服务器
- Java 17+ 运行环境
- Nginx（可选，用于前端静态文件服务）
- MySQL 8.0+
- Redis 6.0+（可选）

## 配置说明

在执行部署脚本前，请修改以下配置：

### 1. SSH配置

编辑 `~/.ssh/config`：

```
Host data-asset-server
    HostName 47.94.52.27
    Port 22022
    User root
    IdentityFile ~/.ssh/id_ed25519
```

### 2. 脚本配置

修改各脚本中的远程配置：

```bash
REMOTE_USER="root"
REMOTE_HOST="47.94.52.27"  # 修改为实际服务器IP
REMOTE_PORT="22022"         # 修改为实际SSH端口
REMOTE_KEY="$HOME/.ssh/id_ed25519"  # 修改为实际密钥路径
REMOTE_PATH="/root/data-asset-security"
```

## 使用方法

### 一键部署（推荐）

```bash
cd deploy
./deploy-all.sh
```

### 分步部署

#### 1. 仅部署后端

```bash
./deploy-backend.sh
```

流程：
1. Maven打包 → 生成JAR文件
2. SCP上传JAR到服务器
3. SSH远程执行：停止旧服务 → 启动新服务

#### 2. 仅部署前端

```bash
./deploy-frontend.sh
```

流程：
1. npm install → npm run build
2. 打包dist目录为tar.gz
3. SCP上传到服务器
4. SSH远程解压部署

## 服务器目录结构

部署后的服务器目录：

```
/root/data-asset-security/
├── backend/
│   ├── app.jar        # 后端应用
│   ├── app.pid        # 进程ID
│   └── app.log        # 运行日志
└── frontend/
    ├── index.html
    ├── assets/
    └── ...
```

## 常用命令

### 查看服务状态

```bash
ssh root@47.94.52.27 -p 22022 "
    echo '=== 后端状态 ==='
    ps aux | grep app.jar | grep -v grep
    echo ''
    echo '=== 后端日志(最后50行) ==='
    tail -50 /root/data-asset-security/backend/app.log
"
```

### 重启后端服务

```bash
ssh root@47.94.52.27 -p 22022 "
    cd /root/data-asset-security/backend
    if [ -f app.pid ]; then
        kill \$(cat app.pid) || true
        rm -f app.pid
    fi
    nohup java -jar app.jar --spring.profiles.active=prod > app.log 2>&1 &
    echo \$! > app.pid
    echo '后端已重启'
"
```

### 查看实时日志

```bash
ssh root@47.94.52.27 -p 22022 "tail -f /root/data-asset-security/backend/app.log"
```

## Nginx配置（可选）

如果使用Nginx代理前端：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端
    location / {
        root /root/data-asset-security/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 故障排查

### 后端启动失败

1. 检查Java版本：
```bash
java -version  # 需要17+
```

2. 检查端口占用：
```bash
netstat -tlnp | grep 8080
```

3. 查看详细日志：
```bash
tail -200 /root/data-asset-security/backend/app.log
```

### 前端访问失败

1. 检查Nginx状态：
```bash
systemctl status nginx
```

2. 检查文件权限：
```bash
ls -la /root/data-asset-security/frontend/
```

### SSH连接失败

1. 检查密钥权限：
```bash
chmod 600 ~/.ssh/id_ed25519
```

2. 测试连接：
```bash
ssh -v root@47.94.52.27 -p 22022
```
