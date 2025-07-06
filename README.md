# KillReward Plugin

一个适用于高版本Minecraft服务端（Spigot/Paper/Bukkit）的击杀奖励插件。

## 功能特性

- 🎯 **多类型生物奖励**: 支持怪物、动物、中立生物的击杀奖励
- 👥 **玩家PVP奖励**: 击杀玩家可获得对方一定百分比的余额
- 🌍 **多语言支持**: 支持中文(zh_CN)和英文(en_US)
- 💰 **经济系统集成**: 完美对接Vault和各种经济插件
- ⚙️ **高度可配置**: 所有文本和奖励都可在配置文件中自定义
- 🔧 **特定实体配置**: 可为特定实体类型设置独特的奖励
- 🌐 **世界管理**: 可指定插件在哪些世界生效
- 🔄 **热重载**: 支持在线重新加载配置文件
- 🐛 **调试模式**: 内置调试功能，便于问题排查
- 🛡️ **权限控制**: 细粒度的权限控制系统

## 安装要求

- Minecraft 1.20+
- Spigot/Paper/Bukkit 服务端
- Java 17+
- Vault 插件（必需）
- 经济插件（必需，如 BetterEconomy, EssentialsX Economy, CMI Economy 等）

## 安装步骤

1. 下载插件jar文件
2. 将jar文件放入服务器的 `plugins` 文件夹
3. 确保已安装 Vault 和经济插件
4. 重启服务器
5. 插件会自动生成配置文件和语言文件
6. 根据需要修改 `plugins/KillReward/config.yml` 配置文件
7. 使用 `/kr reload` 命令重新加载配置（无需重启服务器）

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

- `killreward.admin` - 管理员权限，可使用所有管理命令
- `killreward.receive` - 接收击杀奖励的权限（默认所有玩家都有）
- `kr.reload` - 重新加载配置文件的权限（默认OP）

## 命令

- `/killreward` 或 `/kr` - 显示帮助信息
- `/killreward reload` - 重新加载配置文件（需要 `kr.reload` 权限）
- `/killreward help` - 显示帮助信息
- `/killreward info` - 显示插件信息和当前状态
- `/killreward version` - 显示插件版本信息

## 支持的实体类型

### 怪物 (MONSTER)
- 僵尸 (ZOMBIE)、骷髅 (SKELETON)、爬行者 (CREEPER)、蜘蛛 (SPIDER)
- 女巫 (WITCH)、末影人 (ENDERMAN)、烈焰人 (BLAZE)、恶魂 (GHAST)
- 史莱姆 (SLIME)、岩浆怪 (MAGMA_CUBE)、凋灵骷髅 (WITHER_SKELETON)
- 流浪者 (STRAY)、尸壳 (HUSK)、幻翼 (PHANTOM)、溺尸 (DROWNED)
- 掠夺者 (PILLAGER)、卫道士 (VINDICATOR)、唤魔者 (EVOKER)、恼鬼 (VEX)
- 劫掠兽 (RAVAGER)、凋灵 (WITHER)、末影龙 (ENDER_DRAGON)
- 守卫者 (GUARDIAN)、远古守卫者 (ELDER_GUARDIAN)、潜影贝 (SHULKER)
- 蠹虫 (SILVERFISH)、末影螨 (ENDERMITE)、洞穴蜘蛛 (CAVE_SPIDER)
- 僵尸村民 (ZOMBIE_VILLAGER)、疣猪兽 (HOGLIN)、僵尸疣猪兽 (ZOGLIN)
- 猪灵蛮兵 (PIGLIN_BRUTE)、监守者 (WARDEN)

### 动物 (ANIMAL)
- 牛 (COW)、猪 (PIG)、鸡 (CHICKEN)、羊 (SHEEP)
- 兔子 (RABBIT)、马 (HORSE)、驴 (DONKEY)、骡 (MULE)
- 羊驼 (LLAMA)、商人羊驼 (TRADER_LLAMA)、猫 (CAT)、豹猫 (OCELOT)
- 狼 (WOLF)、鹦鹉 (PARROT)、蝙蝠 (BAT)、哞菇 (MUSHROOM_COW)
- 村民 (VILLAGER)、流浪商人 (WANDERING_TRADER)、海龟 (TURTLE)
- 熊猫 (PANDA)、狐狸 (FOX)、蜜蜂 (BEE)、炽足兽 (STRIDER)
- 美西螈 (AXOLOTL)、发光鱿鱼 (GLOW_SQUID)、山羊 (GOAT)
- 悦灵 (ALLAY)、青蛙 (FROG)、蝌蚪 (TADPOLE)、骆驼 (CAMEL)
- 嗅探兽 (SNIFFER)

### 中立生物 (NEUTRAL)
- 铁傀儡 (IRON_GOLEM)、雪傀儡 (SNOWMAN)、鱿鱼 (SQUID)
- 海豚 (DOLPHIN)、北极熊 (POLAR_BEAR)、猪灵 (PIGLIN)
- 僵尸猪灵 (ZOMBIFIED_PIGLIN)、猪灵蛮兵 (PIGLIN_BRUTE)

## 多语言支持

插件支持多语言，语言文件位于 `plugins/KillReward/langs/` 目录下：

- `zh_CN.yml` - 中文
- `en_US.yml` - 英文

你可以修改这些文件来自定义所有消息文本。

## 配置示例

### 基础配置示例

```yaml
# 基础击杀奖励配置
language: zh_CN
debug: false

rewards:
  MONSTER:
    enabled: true
    range:
      min: 5.0
      max: 20.0
  
  ANIMAL:
    enabled: true
    range:
      min: 2.0
      max: 8.0
  
  NEUTRAL:
    enabled: true
    range:
      min: 3.0
      max: 12.0
  
  PLAYER:
    enabled: true
    percentage: "15%"
    minimum_balance: 50.0
    maximum_steal: 500.0

# 特定实体高额奖励
specific_entities:
  ENDER_DRAGON:
    enabled: true
    range:
      min: 1000.0
      max: 2000.0
  WITHER:
    enabled: true
    range:
      min: 500.0
      max: 1000.0

# 世界控制
worlds:
  enabled_worlds:
    - "world"
    - "world_nether"
    - "world_the_end"
  disabled_worlds: []
```

## 故障排除

### 常见问题

**Q: 插件无法加载？**
A: 请检查：
- 是否安装了 Vault 插件
- 是否安装了经济插件
- 服务器版本是否为 1.20+
- Java 版本是否为 17+

**Q: 击杀没有奖励？**
A: 请检查：
- 玩家是否有 `killreward.receive` 权限
- 当前世界是否在启用列表中
- 配置文件中对应实体类型是否启用
- 开启调试模式查看详细信息

**Q: 经济系统无法连接？**
A: 请确保：
- Vault 插件正常运行
- 经济插件正常运行
- 使用 `/kr info` 查看经济系统状态

### 调试模式

在 `config.yml` 中设置 `debug: true` 可以启用调试模式，这将在控制台输出详细的调试信息，帮助排查问题。

## 性能优化建议

1. **合理设置奖励范围**: 避免设置过大的奖励范围，以免影响服务器经济平衡
2. **限制启用世界**: 只在需要的世界启用插件，减少不必要的计算
3. **定期检查配置**: 使用 `/kr info` 命令检查插件状态
4. **监控经济变化**: 定期查看服务器经济状况，调整奖励设置

## 开发信息

- **作者**: Snowball_233
- **命名空间**: cc.vastsea
- **开发语言**: Kotlin
- **构建工具**: Maven
- **API版本**: 1.20
- **源码**: 开源项目

## 更新日志

### v1.0.0
- ✨ 初始版本发布
- 🎯 支持多类型生物击杀奖励
- 👥 支持玩家PVP奖励系统
- 🌍 多语言支持（中文/英文）
- 💰 完整的Vault经济系统集成
- ⚙️ 高度可配置的奖励系统
- 🔧 特定实体奖励配置
- 🌐 世界管理功能
- 🔄 热重载配置支持
- 🐛 调试模式
- 🛡️ 细粒度权限控制

## 兼容性

### 测试过的服务端
- ✅ Spigot 1.20+
- ✅ Paper 1.20+
- ✅ Bukkit 1.20+

### 测试过的经济插件
- ✅ EssentialsX Economy
- ✅ BetterEconomy
- ✅ CMI Economy
- ✅ iConomy
- ✅ BOSEconomy

## 贡献指南

欢迎为本项目贡献代码！请遵循以下步骤：

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

### 代码规范
- 使用 Kotlin 编写代码
- 遵循现有的代码风格
- 添加适当的注释
- 确保代码通过编译测试

## 许可证

本插件遵循 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 支持与反馈

如果你遇到任何问题或有功能建议：

- 📧 **邮箱**: 联系作者 Snowball_233
- 🐛 **Bug报告**: 请提供详细的错误信息和复现步骤
- 💡 **功能建议**: 欢迎提出新功能想法
- ⭐ **评价**: 如果插件对你有帮助，请给个好评！

## 致谢

感谢以下项目和社区的支持：
- Bukkit/Spigot/Paper 开发团队
- Vault 插件开发者
- Kotlin 编程语言
- Maven 构建工具
- 所有测试用户和反馈者

---

**感谢使用 KillReward 插件！希望它能为你的服务器带来更好的游戏体验！** 🎮✨