# Snake Android App

简单贪吃蛇游戏，纯 Kotlin 实现，适配横屏。

---

## 🎮 功能

- 上下左右控制（需添加触摸控制，当前自动演示）
- 分数统计
- 撞墙/撞自己自动重置

---

## 🔨 编译

```bash
./gradlew :app:assembleRelease
```

输出 APK: `app/build/outputs/apk/release/app-release.apk`

---

## 📦 GitHub Actions

推送代码后自动编译，在 GitHub Release 页面下载 APK。

---

## ⚙️ 配置

- minSdk: 26 (Android 8.0)
- targetSdk: 34 (Android 14)
- Kotlin + Android SDK

---

**注意:** 当前版本无需特殊权限。
# Build fix applied
