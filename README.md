# 用药管家

一款 Android 端用药提醒 App，支持添加多种药品、设定吃药周期、推送/铃声提醒，以及日历看板查看用药完成情况。

## 功能说明

- **药品管理**：添加/编辑/删除药品，填写药品名称、用途、每次剂量、每日次数、提醒时间、开始/结束日期。
- **多种药品**：支持同时添加多种药品，各自独立提醒。
- **吃药周期**：可设置开始日期与结束日期（留空表示长期服用）。
- **用药提醒**：通过系统推送通知提醒；支持自定义铃声（在添加药品时可配置）。
- **日历看板**：月历视图显示每日用药计划，每个日期显示「已服用/计划数」；点击日期可查看当日用药列表，并勾选「已服用」。

## 技术栈

- Kotlin + Jetpack Compose (Material3)
- Room 数据库
- AlarmManager 定时提醒
- 通知栏推送 (NotificationCompat)

## 环境要求

- Android Studio Ladybug (2024.2.1) 或更高
- JDK 17
- minSdk 24, targetSdk 34

## 构建与运行

1. 用 Android Studio 打开项目根目录（选择「用药管家」文件夹）。
2. 等待 Gradle 同步完成（首次会下载依赖，需联网）。
3. 连接设备或启动模拟器，点击 Run 运行。

## 如何打包 APK 安装包（发到手机安装）

1. 用 **Android Studio** 打开本项目。
2. 菜单栏选择 **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**。
3. 构建完成后，右下角会提示 **Locate**，点击可打开输出目录；或手动打开：
   - **APK 路径**：`app/build/outputs/apk/debug/app-debug.apk`
4. 将 `app-debug.apk` 拷贝到手机（数据线、网盘、微信等），在手机上点击安装即可（需允许「未知来源」安装）。

## 权限说明

- `POST_NOTIFICATIONS`：用于用药提醒推送（Android 13+ 需用户授权）。
- `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM`：用于精确时间提醒。
- `RECEIVE_BOOT_COMPLETED`：开机后重新注册提醒。

## 项目结构概览

```
app/src/main/java/com/medication/reminder/
├── MainActivity.kt                 # 主界面入口
├── data/
│   ├── entity/                     # 药品、用药记录实体
│   ├── dao/                        # Room DAO
│   ├── database/                   # Room Database
│   └── repository/                 # 数据仓库
├── receiver/                       # 提醒广播、开机广播
├── ui/
│   ├── navigation/                 # 主导航（药品 / 日历）
│   ├── screens/                   # 药品列表、日历看板
│   ├── components/                # 添加/编辑药品对话框
│   ├── viewmodel/                 # ViewModel
│   └── theme/                     # 主题与字体
└── util/                          # 提醒管理、通知工具
```

## 使用提示

1. 首次使用建议在「药品」页添加药品，填写提醒时间（如 `08:00,12:00,18:00`，逗号分隔）。
2. 保存后会自动创建用药记录并设置提醒；到点会收到通知。
3. 在「日历」页可切换月份、点击某天查看当日计划，并标记「已服用」。
4. 自定义铃声可在后续版本中通过设置或添加药品时的「铃声」选项扩展实现（当前版本使用系统默认铃声）。

---

如有问题或建议，欢迎反馈。
