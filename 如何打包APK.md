# 用药管家 - 打包 APK 安装包说明

按以下步骤可在本机打出可直接安装的 APK，发到手机安装使用。

## 方法一：Android Studio（推荐）

1. **打开项目**
   - 启动 Android Studio。
   - 选择 **File → Open**，选中「用药管家」项目根目录（包含 `build.gradle.kts` 和 `app` 文件夹的目录），点 **OK**。

2. **等待同步**
   - 首次打开会自动同步 Gradle，右下角有进度条。
   - 需联网，等待「Gradle sync finished」完成。

3. **打包 Debug APK**
   - 菜单栏 **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**。
   - 等待构建完成（几分钟）。

4. **找到 APK**
   - 构建完成后右下角会弹出 **APK(s) built successfully**，点击 **locate** 可打开输出目录。
   - 或手动进入项目目录：
     ```
     app/build/outputs/apk/debug/app-debug.apk
     ```

5. **传到手机安装**
   - 将 `app-debug.apk` 传到手机（数据线、网盘、微信文件等）。
   - 在手机上打开该文件，按提示安装（若提示「未知来源」，需在系统设置中允许从该来源安装应用）。

---

## 方法二：命令行（已安装 Gradle 时）

若本机已安装 [Gradle](https://gradle.org/install/) 且能访问网络：

1. 在项目根目录执行一次生成 Wrapper（若还没有 `gradlew`）：
   ```bash
   gradle wrapper --gradle-version=8.2
   ```

2. 执行打包：
   ```bash
   ./gradlew assembleDebug
   ```

3. 生成的 APK 路径同上：`app/build/outputs/apk/debug/app-debug.apk`。

---

## 说明

- **Debug APK**：可直接安装、调试，无需签名配置，适合自己用或内测。
- **Release APK**：需配置签名（Build → Generate Signed Bundle / APK），适合上架或正式分发。
- 安装到手机后，若为 **Android 13+**，首次使用需在系统设置中允许本应用发送「用药提醒」通知。
