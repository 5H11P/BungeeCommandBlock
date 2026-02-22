# BungeeCommandBlock

一个运行在 BungeeCord 的命令过滤插件。

它会根据 `config.yml` 中的白名单/黑名单配置，决定玩家输入的命令是：

- 放行到后端 Spigot/Paper 继续处理
- 在 BungeeCord 侧直接拦截并返回提示

## 功能特性

- 白名单命令放行（`whitelist-commands`）
- 黑名单命令拦截（`blacklist-commands`）
- Staff 额外白名单（`staff-whitelist-commands`）
- 控制台热重载配置命令（`/bcb reload`）
- 玩家使用插件命令时伪装为未定义命令（返回 `undefined-command-message`）
- 消息占位符 `%command%`（返回玩家输入的命令，不带前导 `/`）
- 绕过权限节点（允许管理员放行所有命令）

## 环境要求

- Java 8+
- BungeeCord（支持 `bungeecord-api 1.20-R0.2`）

## 安装方式

1. 编译插件

```bash
mvn -DskipTests package
```

2. 将生成的 `target/BungeeCommandBlock-1.0.0.jar` 放入 BungeeCord 的 `plugins` 目录
3. 启动或重启 BungeeCord
4. 编辑插件生成的 `plugins/BungeeCommandBlock/config.yml`

## 配置文件说明

默认配置文件路径：

- `plugins/BungeeCommandBlock/config.yml`

核心配置项：

- `whitelist-commands`: 普通玩家允许放行到后端的命令
- `blacklist-commands`: 明确禁止的命令（优先级高于白名单）
- `staff-whitelist-commands`: 仅 `bungeecommandblock.staff` 可用的额外命令
- `undefined-command-message`: 未定义命令提示
- `blacklist-command-message`: 黑名单命令提示

## 消息占位符

- `%command%`：玩家实际输入的命令内容（包含参数，不带前导 `/`）

示例：

- 玩家输入 `/plugins`
- `%command%` 的值为 `plugins`

## 权限节点

- `bungeecommandblock.staff`：允许使用 `staff-whitelist-commands`
- `bungeecommandblock.bypass`：绕过所有检测，允许所有命令
- `bungeecommandblock.*`：绕过所有检测，允许所有命令
- `*`：若权限系统支持，也会被视为绕过所有检测

## 插件命令（仅控制台）

- `bcb reload`：热重载 `config.yml`
- `bcbreload reload`：`bcb` 的别名形式

说明：

- 玩家执行上述命令不会获得插件专用提示
- 玩家会直接收到 `undefined-command-message`

## 处理优先级（玩家命令）

1. Bungee 代理命令：跳过（不拦截）
2. 拥有 bypass 权限：直接放行
3. 命中黑名单：拦截并返回 `blacklist-command-message`
4. 命中普通白名单：放行
5. 命中 staff 白名单且有 `bungeecommandblock.staff`：放行
6. 其他情况：拦截并返回 `undefined-command-message`

## 示例配置片段

```yaml
whitelist-commands:
  - "/party"
  - "/friends"

blacklist-commands:
  - "/plugins"
  - "/op"

staff-whitelist-commands:
  - "/staffchat"
  - "/mod"

undefined-command-message: "&cUnknown command: %command%"
blacklist-command-message: "&cYou do not have permission to use this command: %command%"
```

## 注意事项

- 命令匹配基于根命令，大小写不敏感
- `/ban Steve` 会按 `/ban` 进行匹配
- 配置中的命令可写成 `/cmd` 或 `cmd`，插件会自动标准化

