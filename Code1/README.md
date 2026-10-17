# 四川轻化工大学校园失物招领系统

原生 JavaWeb：JSP + Servlet + Service + JavaBean + Dao。首页按类型和关键词筛选。

## 运行

1. 用 MySQL Workbench 8.0 打开并执行 `sql/init.sql`。
2. 确认 `src/main/resources/db.properties` 里的账号密码。当前机器是 `root` / `123456`，库名 `suse_lostfound`。
3. 使用 JDK 8+ 与 Tomcat 9（`javax.servlet`，不要用 Tomcat 10）。
4. 在项目根目录执行 `mvn package`，把 `target/lostfound.war` 部署到 Tomcat，或在 IDEA 中以 Maven 项目导入后挂到 Tomcat。
5. 访问 `http://localhost:8080/lostfound/`。

## 账号

| 姓名 | 账号 | 密码 | 身份 |
| --- | --- | --- | --- |
| 丁玥 | 24101100527 | 24101100527 | 管理员 |
| 张想想 | 24101100534 | 24101100534 | 管理员 |
| 陈跃国 | 24101100501 | 24101100501 | 管理员 |
| 代京呈 | 24101100502 | 24101100502 | 管理员 |
| 李明 | 241011005001 | 241011005001 | 学生，2024 级 005 班 |
| 周予安 | 241011005018 | 241011005018 | 学生 |
| 孙可欣 | 241011012023 | 241011012023 | 学生 |

学生账号是 12 位学号：前两位是入学学年，接着是专业代码 `1011` 和 3 位班号，末三位是班内学号。例如 `241011005001` 表示 2024 级、005 班、001 号。预设学生的密码与学号相同。管理员仍使用原来的 11 位学号。新注册只能填写 12 位学号，注册后的身份都是学生。

学生登录后进入首页浏览模式，导航右侧是「退出」「个人中心」。管理员登录后自动进入后台，回到前台时从「个人中心」进入管理功能。公告里的手工通知只显示「管理员发布」。

## 分层

作业中的目录对应 Maven 包 `com.suse.lostfound`：

- `entity` JavaBean：User、LostItem、Notice
- `dao`：JDBC 增删改查
- `service`：校验通过并写入数据库后，自动发布系统公告
- `servlet`：UserServlet、ItemServlet、AdminServlet，公告列表在 NoticeServlet
- `filter`：LoginFilter 拦截未登录的发布和后台，并拦截学生进入管理员页面

游客可以浏览首页、详情和公告。点击发布时弹出提示，确认后进入登录页。
