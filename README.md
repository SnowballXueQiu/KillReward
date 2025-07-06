# KillReward Plugin

一个适用于高版本Minecraft服务端（Spigot/Paper/Bukkit）的击杀奖励插件。

## 功能特性

- 🎯 **多类型生物奖励**: 支持怪物、动物、中立生物的击杀奖励
- 👥 **玩家PVP奖励**: 击杀玩家可获得对方一定百分比的余额
- 🌍 **多语言支持**: 支持中文(zh_CN)和英文(en_US)
- 💰 **经济系统集成**: 完美对接Vault和BetterEconomy
- ⚙️ **高度可配置**: 所有文本和奖励都可在配置文件中自定义
- 🔧 **特定实体配置**: 可为特定实体类型设置独特的奖励
- 🌐 **世界管理**: 可指定插件在哪些世界生效

## 安装要求

- Minecraft 1.20.1+
- Spigot/Paper/Bukkit 服务端
- Vault 插件
- 经济插件 (如 BetterEconomy, EssentialsX Economy 等)

## 安装步骤

1. 下载插件jar文件
2. 将jar文件放入服务器的 `plugins` 文件夹
3. 确保已安装 Vault 和经济插件
4. 重启服务器
5. 根据需要修改配置文件

## 配置文件说明

### config.yml

```yaml
# 语言设置
language: zh_CN  # 可选: zh_CN, en_US

# 调试模式
debug: false

# 奖励设置
rewards:
  # 怪物奖励
  MONSTER:
    enabled: true
    range:
      min: 1    # 最小奖励
      max: 10   # 最大奖励
  
  # 动物奖励
  ANIMAL:
    enabled: true
    range:
      min: 5
      max: 15
  
  # 中立生物奖励
  NEUTRAL:
    enabled: true
    range:
      min: 3
      max: 8
  
  # 玩家PVP奖励
  PLAYER:
    enabled: true
    percentage: "25%"        # 获得对方余额的百分比
    minimum_balance: 100.0   # 对方最少余额要求
    maximum_steal: 1000.0    # 单次最大获得金额

# 特定实体配置（可选）
specific_entities:
  ENDER_DRAGON:
    enabled: true
    range:
      min: 100
      max: 500
```

## 权限节点

- `killreward.admin` - 管理员权限，可使用reload命令
- `killreward.receive` - 接收击杀奖励的权限（默认所有玩家都有）

## 命令

- `/killreward` 或 `/kr` - 显示帮助信息
- `/killreward reload` - 重新加载配置文件（需要管理员权限）
- `/killreward help` - 显示帮助信息
- `/killreward info` - 显示插件信息
- `/killreward version` - 显示插件版本

## 支持的实体类型

### 怪物 (MONSTER)
- 僵尸、骷髅、爬行者、蜘蛛
- 女巫、末影人、烈焰人、恶魂
- 史莱姆、岩浆怪、凋灵骷髅
- 流浪者、尸壳、幻翼、溺尸
- 掠夺者、卫道士、唤魔者、恼鬼
- 劫掠兽、凋灵、末影龙
- 守卫者、远古守卫者、潜影贝
- 蠹虫、末影螨、洞穴蜘蛛
- 僵尸村民、疣猪兽、僵尸疣猪兽
- 猪灵蛮兵、监守者

### 动物 (ANIMAL)
- 牛、猪、鸡、羊
- 兔子、马、驴、骡
- 羊驼、商人羊驼、猫、豹猫
- 狼、鹦鹉、蝙蝠、哞菇
- 村民、流浪商人、海龟
- 熊猫、狐狸、蜜蜂、炽足兽
- 美西螈、发光鱿鱼、山羊
- 悦灵、青蛙、蝌蚪、骆驼
- 嗅探兽

### 中立生物 (NEUTRAL)
- 铁傀儡、雪傀儡、鱿鱼
- 海豚、北极熊、猪灵
- 僵尸猪灵、猪灵蛮兵

## 多语言支持

插件支持多语言，语言文件位于 `plugins/KillReward/langs/` 目录下：

- `zh_CN.yml` - 中文
- `en_US.yml` - 英文

你可以修改这些文件来自定义所有消息文本。

## 开发信息

- **作者**: Snowball_233
- **命名空间**: cc.vastsea
- **开发语言**: Kotlin
- **构建工具**: Maven
- **API版本**: 1.20

## 许可证

本插件遵循 MIT 许可证。

## 支持

如果你遇到任何问题或有功能建议，请联系作者 Snowball_233。

---

感谢使用 KillReward 插件！