# 文档目录

本目录包含项目的各类文档。

## 📂 目录结构

```
docs/
├── README.md                    # 本文件
├── DEPLOYMENT_GUIDE.md          # 部署指南
├── QUICK_START_GUIDE.md         # 快速启动指南
├── development/                 # 开发过程文档
│   ├── API_ROUTES.md           # API路由说明
│   ├── AUTHENTICATION_*.md     # 认证功能开发记录
│   ├── BACKEND_*.md            # 后端开发记录
│   ├── PROJECT_STATUS.md       # 项目状态
│   └── ...                     # 其他开发文档
└── archived/                    # 已归档文档
```

## 📖 文档说明

### 用户文档（项目根目录）

以下文档保留在项目根目录，方便用户快速查看：

- `README.md` - 项目总体说明
- `DEPLOYMENT_CONFIG.md` - 部署配置说明
- `快速启动指南.md` - 快速启动指南
- `系统操作手册.md` - 用户操作手册

### 开发文档（docs/development/）

开发过程中的技术文档，包括：

- 功能开发记录
- 问题修复记录
- 技术方案说明
- 项目进度报告

这些文档主要用于：
- 开发团队内部参考
- 问题追溯和经验总结
- 技术知识传承

### 部署文档（docs/）

部署相关的技术文档：

- `DEPLOYMENT_GUIDE.md` - 详细部署指南
- `QUICK_START_GUIDE.md` - 快速启动指南

## 🔍 文档查找指南

| 需求 | 查看文档 |
|------|---------|
| 了解项目 | `README.md` |
| 快速启动 | `快速启动指南.md` 或 `docs/QUICK_START_GUIDE.md` |
| 部署配置 | `DEPLOYMENT_CONFIG.md` |
| 详细部署 | `docs/DEPLOYMENT_GUIDE.md` |
| 用户操作 | `系统操作手册.md` |
| 开发记录 | `docs/development/` 目录 |
| API说明 | `docs/development/API_ROUTES.md` |

## 📝 文档维护

### 添加新文档

- **用户文档**：放在项目根目录
- **开发文档**：放在 `docs/development/`
- **部署文档**：放在 `docs/`

### 归档文档

过时的开发文档可以移动到 `docs/archived/` 目录保留。

### 文档命名规范

- 使用大写字母和下划线：`FEATURE_NAME.md`
- 或使用中文命名：`功能说明.md`
- 文件名应清晰表达文档内容
