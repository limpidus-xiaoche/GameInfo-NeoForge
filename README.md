# GameInfo NeoForge (1.21.1)

GameInfo 的非官方移植版，将 [CodaR5/gameinfo](https://github.com/CodaR5/gameinfo)（原版由 [ifcifc](https://gitlab.com/ifcifc94/gameinfo-mod) 开发）向下移植到 **Minecraft 1.21.1 NeoForge**。

> ⚠️ 重要声明：这是原版模组 GameInfo 的非官方移植版，作者不是原作者，也不是官方分支。

## 功能

HUD 显示项均可单独开关：

- FPS
- 游戏天数与游戏内时间
- 当前坐标与朝向
- 当前生物群系
- 下界坐标转换：主世界显示 `N-XZ`（坐标 / 8），下界显示 `O-XZ`（坐标 × 8）
- 右侧装备耐久
- 右上角版本号，可自定义，也可跟随游戏版本
- 准星指向的方块信息
- 准星指向的实体血量
- 准星指向熔炉或打开熔炉时，显示输入、燃料、输出槽的物品
- 坐标行后的 `- Slime Chunk` 标记，显示为绿色

**按键与命令：**

- `U`：打开配置界面
- `/gameinfo`：模组主命令
- `/chunk`：区块管理命令，支持 `load`、`unload`、`list`、`check`

## 安装

需要 **NeoForge 21.1.x**。将 `GameInfo-1.21.1-3.0.8.jar` 放入 `.minecraft/mods/` 后启动游戏。

- **客户端必须安装**
- **服务端可选安装（但建议）**：安装后熔炉数据由服务端实时扫描同步，多人游戏中的史莱姆区块由服务端计算下发，不暴露世界种子。服务端未安装时客户端不会崩溃，史莱姆区块在多人模式下不显示，熔炉数据使用缓存。

## 配置

配置文件位于 `config/gameinfo.properties`。游戏内按 `U` 可直接修改，修改后即时生效。

## 多语言

内置多语言：英语、简体中文、繁体中文等。

## 构建

本项目使用离线构建脚本（无需 Gradle / maven.neoforged.net）：

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```

依赖：本机 JDK 21 与本地 NeoForge 21.1.250 运行库。

## 移植说明

- 原版（Fabric）：[ifcifc94/gameinfo-mod](https://gitlab.com/ifcifc94/gameinfo-mod)
- 上游移植（Fabric 26.x）：[CodaR5/gameinfo](https://github.com/CodaR5/gameinfo)
- 本移植（NeoForge 1.21.1）：基于上游 26.3 NeoForge 版向下移植

## 许可证

[Apache License 2.0](LICENSE)

Copyright (c) 2026 Coda_R5（上游作者）
