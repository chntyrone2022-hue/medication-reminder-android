# 无 Homebrew、无 Android Studio 时如何打包 APK

你的环境里没有 `brew` 和 Android Studio，按下面步骤**先装 Java 和 Gradle**，再在项目里打包。

---

## 一、安装 Java 17（必须）

1. 打开浏览器，访问：**https://adoptium.net/zh-CN/temurin/releases/**
2. 选择：
   - **操作系统**：macOS  
   - **架构**：Apple Silicon 选 aarch64，Intel 选 x64  
   - **版本**：17 (LTS)  
   - **包类型**：JDK  
   - **格式**：**.pkg**
3. 下载 `.pkg` 后双击安装，按提示一路下一步。
4. 安装完成后**新开一个终端窗口**，执行：
   ```bash
   java -version
   ```
   能看到 `openjdk version "17.x.x"` 就说明装好了。

---

## 二、安装 Gradle（命令行）

1. 打开：**https://gradle.org/releases/**
2. 找到 **Gradle 8.2**，点进该版本页面。
3. 下载 **Binary-only** 的 **gradle-8.2-bin.zip**。
4. 解压到某个目录，例如你的用户目录下：
   ```bash
   # 在「下载」里解压后，移到用户目录
   mkdir -p ~/gradle
   unzip -q ~/Downloads/gradle-8.2-bin.zip -d ~/gradle
   ```
5. 把 Gradle 加到环境变量（二选一）：

   **方式 A：临时使用（只对当前终端有效）**
   ```bash
   export PATH="$HOME/gradle/gradle-8.2/bin:$PATH"
   ```

   **方式 B：长期使用（推荐）**
   ```bash
   echo 'export PATH="$HOME/gradle/gradle-8.2/bin:$PATH"' >> ~/.zshrc
   source ~/.zshrc
   ```

6. 新开终端或执行 `source ~/.zshrc` 后，验证：
   ```bash
   gradle -v
   ```
   能显示 Gradle 8.2 就说明装好了。

---

## 三、在项目里生成 Wrapper 并打包

1. 进入项目目录：
   ```bash
   cd "/Users/long/Desktop/AI /用药管家"
   ```

2. 用刚装好的 Gradle 生成 Wrapper（只需做一次）：
   ```bash
   gradle wrapper --gradle-version 8.2
   ```

3. 打包 Debug APK：
   ```bash
   ./gradlew assembleDebug
   ```
   第一次会下载依赖，等出现 **BUILD SUCCESSFUL** 即完成。

4. 安装包路径：
   ```
   /Users/long/Desktop/AI /用药管家/app/build/outputs/apk/debug/app-debug.apk
   ```
   把这个文件拷到手机安装即可。

---

## 四、你当前已处理好的部分

- **`./gradlew` 权限**：已经改成可执行（`chmod +x gradlew`），不会再出现 `permission denied`。
- 完成上面的「一、二、三」后，直接执行 `./gradlew assembleDebug` 即可。

---

## 五、如果仍然报错

- **`JAVA_HOME` 相关**：装好 Java 17 后，可加一行（路径按你本机实际安装位置改）：
  ```bash
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
  source ~/.zshrc
  ```
- **`gradle: command not found`**：说明 PATH 里没有 Gradle，检查第二步里 `~/gradle/gradle-8.2/bin` 是否写对，并重新 `source ~/.zshrc` 或新开终端再试。

按顺序做完「一 → 二 → 三」，就可以在不装 Homebrew 和 Android Studio 的情况下打出 APK。
