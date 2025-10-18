# TimetableApp — 简洁强大的课表应用（Jetpack Compose）

一个基于 Kotlin 与 Jetpack Compose 构建的 Android 课表应用示例，支持时间/节次两种排课模式、周数选择（每周/单周/双周/自定义）、课程颜色、周末显示开关、背景图透明叠加、主题与文字缩放等灵活配置。采用 Hilt 进行依赖注入，Room 管理课程数据，DataStore 管理设置项，并使用 Navigation Compose 组织页面导航。


## 功能特性

- 周视图课表
  - 支持 5 天或 7 天（周末开关）
  - 顶部展示“第 N 周”，懒加载滚动
  - FAB 一键添加课程
- 两种时间模式
  - 时间模式：以分钟为刻度（0:00–24:00）
  - 节次模式：以节次为刻度（默认最多 12 节，可自定义每节开始/结束时间）
- 课程管理
  - 添加/编辑/删除课程
  - 字段：课程名称、上课地点、授课教师（可选）、星期、时间/节次、课程颜色
  - 周数选择：每周、单周、双周、自定义（位图掩码存储）
- 个性化设置
  - 主题模式：跟随系统、浅色、深色
  - 主题种子色与动态色（Android 12+）
  - 文字缩放（0.8x–1.4x）
  - 显示周末开关
  - 背景图 URI 与透明度（0–0.6）
  - 自定义节次时间表（按 HH:MM 填写）
- 现代架构与库
  - Kotlin + Jetpack Compose + Material 3
  - Navigation Compose
  - Hilt 依赖注入
  - Room 持久化课程
  - DataStore Preferences 存储设置
  - Coil 异步加载图片


## 运行效果

- 周视图：按天分栏，支持点击空白区域快速添加课程
- 编辑课程：表单化录入，色板/自定义 RGB 取色
- 设置中心：主题、文字、周末、周数/当前周、背景、时间模式与节次表

提示：仓库未附带截图，欢迎在实际运行后补充到 docs/screenshots/ 并在此引用。


## 快速开始

### 环境要求
- Android Studio Koala+（或兼容版本）
- Android Gradle Plugin 8.6.0
- Kotlin 1.9.24
- JDK 17
- Android SDK：compileSdk 34，minSdk 24，targetSdk 34
- Gradle 8.7+（如使用命令行构建且仓库未包含 Gradle Wrapper）

### 克隆与启动
1. 克隆仓库
   ```bash
   git clone <your-repo-url>
   cd TimetableApp
   ```
2. 使用 Android Studio 打开根目录，等待 Gradle 同步完成
3. 选择设备/模拟器，直接运行 app 模块

### 命令行构建（可选）
- 若本地安装了 Gradle 8.7+：
  ```bash
  gradle assembleDebug
  ```
- 若仓库包含 Gradle Wrapper（gradlew）：
  ```bash
  ./gradlew assembleDebug
  ```


## 项目结构

```
.
├─ app/
│  ├─ src/main/
│  │  ├─ java/com/example/timetableapp/
│  │  │  ├─ data/
│  │  │  │  ├─ dao/            # Room DAO
│  │  │  │  ├─ db/             # Room Database 定义
│  │  │  │  ├─ entity/         # 实体（Course）
│  │  │  │  ├─ repository/     # 仓库层（CourseRepository）
│  │  │  │  └─ settings/       # 设置模型与 SettingsRepository
│  │  │  ├─ di/                # Hilt 模块
│  │  │  ├─ navigation/        # Navigation Graph（AppNavHost）
│  │  │  ├─ ui/
│  │  │  │  ├─ components/     # 通用 UI 组件（如 ColorPicker、CourseCard）
│  │  │  │  ├─ edit/           # 编辑课程 ViewModel
│  │  │  │  ├─ screens/
│  │  │  │  │  ├─ weekly/      # WeeklyScreen（课表主界面）
│  │  │  │  │  ├─ edit/        # EditCourseScreen（编辑界面）
│  │  │  │  │  └─ settings/    # Settings、PeriodScheduleScreen
│  │  │  │  ├─ settings/       # SettingsViewModel
│  │  │  │  └─ theme/          # 主题、字体、颜色
│  │  │  ├─ util/              # 工具函数（时间格式、周数位图等）
│  │  │  ├─ MainActivity.kt
│  │  │  └─ TimetableApp.kt    # Hilt Application
│  │  └─ res/                   # 资源文件
│  ├─ build.gradle.kts
├─ build.gradle.kts             # 根级 Gradle 配置
├─ settings.gradle.kts
├─ gradle.properties
└─ .gitignore
```


## 重要概念与实现说明

### 课程实体与周数掩码
- 课程实体 `Course` 使用 Room 持久化，关键字段：
  - `dayOfWeek`：1..7（周一=1）
  - 时间模式：`startMinutes`/`endMinutes`
  - 节次模式：`startPeriod`/`endPeriod`
  - `weeksMask`：Long 类型位图（第 n 位代表第 n 周），便于快速查询当周课程
- DAO 查询示例：
  ```sql
  SELECT * FROM courses WHERE ((weeksMask >> (:week-1)) & 1) = 1
  ```

### 两种时间模式
- `TimeMode.TIME`：周视图以分钟为纵轴，默认每分钟 0.5dp
- `TimeMode.PERIOD`：以节次为纵轴（默认 MAX_PERIODS=12），高度 64dp/节；可在设置中自定义每节开始/结束时间，默认内置 10 节常见作息

### 设置中心与 DataStore
- `SettingsRepository` 使用 Preferences DataStore 存储：
  - 主题模式、主题色、文字缩放
  - 时间模式、显示周末、总周数、当前周
  - 背景图 URI 与透明度
  - 是否使用动态色（Android 12+）
  - 自定义节次时间表（序列化成字符串形式 `period:start-end;...`）

### 背景图
- 设置项“图片 URI”支持：
  - 本地内容 URI（content://）
  - 文件路径（file://）
  - 远程 URL（http/https）
- 使用 Coil 的 `AsyncImage` 加载，并叠加指定透明度背景以提升可读性

### 状态管理
- ViewModel + StateFlow + `collectAsState()`
- 通过 Hilt 注入 Repository/Settings，组合状态驱动 UI


## 自定义与扩展

- 默认节次数量与高度：`ui/screens/weekly/WeeklyScreen.kt` 中的 `MAX_PERIODS` 与 `PERIOD_HEIGHT_DP`
- 默认节次时间表：`SettingsRepository.defaultPeriodSchedule()`
- 颜色面板：`ui/components/ColorPicker.kt` 中的 `presetColors`
- 默认总周数与当前周：`SettingsRepository` 中 `totalWeeks/currentWeek` 的默认值
- 周数选择（每周/单周/双周/自定义）实现：`util/TimeUtils.kt` 的 `WeeksSelection` 与 `buildWeeksMask`


## 使用提示

- 首次进入课表为空，点击右下角“+”或任一天列的空白区域添加课程
- 在“设置中心”中：
  - 切换“时间模式/节次模式”
  - 设置“总周数/当前周”，顶部会显示“第 N 周”
  - 设置背景图 URI 与透明度以获得个性化外观
  - 自定义节次时间表后，节次模式下课程块会按对应时间定位


## 测试与调试

- 单元测试依赖（JUnit4）已添加，可在 Android Studio 的 Run/Debug Configurations 中运行
- UI 测试依赖（Compose UI Test）已添加，但项目未包含示例测试用例，可自行补充


## 常见问题（FAQ）

- 为什么我改了“节次时间表”，课表并没有变化？
  - 请确认已切换到“节次模式”，并且课程使用的是“节次”字段（非“时间”）
- 背景图 URI 应该怎么填？
  - 可填本地 `content://` 或 `file://`，也可直接填 `https://` 图片链接（依赖网络权限与图片可访问性）
- 我只上 16 周课，可以吗？
  - 可以，将“总周数”调整为 16，并在添加课程时选择合适的周数范围/模式


## 贡献指南

欢迎提交 Issue 与 Pull Request：
- 讨论新功能、报告 Bug、完善文档/注释/示例
- 保持 Kotlin/Compose 一致的编码风格，尽量模块化、可测试
- 若引入新依赖，请说明用途与替代方案


## 许可证

本项目当前未包含开源许可证文件（LICENSE）。在公开发布或二次分发前，请补充适当的许可证。


## 致谢

- Android Jetpack 团队与 Compose 社区
- Hilt、Room、DataStore、Coil 等优秀开源项目


## 元信息
- 应用 ID：`com.example.timetableapp`
- 版本：1.0（versionCode 1）
- 最低支持：Android 7.0（API 24）
- 目标：Android 14（API 34）
