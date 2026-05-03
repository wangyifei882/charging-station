# 充电站监管系统 — 阿里云部署指南

## 服务器信息

| 项目 | 值 |
|------|-----|
| 公网 IP | **8.136.31.200** |
| 操作系统 | Alibaba Cloud Linux 3 (RHEL 兼容) |
| 配置 | 2C / 1.8GB RAM / 40GB SSD |

## 访问地址

| 应用 | URL | 说明 |
|------|-----|------|
| 监管与调度平台 | `http://******` | supervision-frontend |
| 充电站运营管理平台 | `http://*****/plus/` | charging-station-frontend-plus |
| 后端 API | `********/api/` | Spring Boot，由 Nginx 代理 |

**登录凭据：** 用户名 `admin`，密码 `*****`

---

## 一、服务器环境安装

```bash
# SSH 登录
ssh root@********

# 安装 JDK 17
dnf install -y java-17-openjdk java-17-openjdk-devel

# 安装 Nginx
dnf install -y nginx
systemctl start nginx
systemctl enable nginx

# 安装 MySQL 8.0
dnf install -y mysql-server
systemctl start mysqld
systemctl enable mysqld
```

## 二、MySQL 初始配置

```bash
# 设置 root 密码
mysql -u root <<SQL
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '*********';
FLUSH PRIVILEGES;
SQL

# 创建数据库
mysql -u root -p'Wang926494334.' -e "CREATE DATABASE IF NOT EXISTS charging_station DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 开启远程访问
mysql -u root -p'********' <<SQL
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'Wang926494334.';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
SQL

# 绑定所有网络接口
echo 'bind-address = 0.0.0.0' >> /etc/my.cnf.d/mysql-server.cnf
systemctl restart mysqld
```

## 三、数据库迁移

```bash
# 在本地机器导出数据库
mysqldump -h localhost -P 3306 -u root -p"****" \
  --single-transaction --set-gtid-purged=OFF \
  --default-character-set=utf8mb4 charging_station \
  > /tmp/charging_station_dump.sql 2>/dev/null

# 上传到服务器
scp /tmp/charging_station_dump.sql root@*****:/tmp/

# 在服务器上导入
ssh root@****** "mysql -u root -p'******.' charging_station < /tmp/charging_station_dump.sql"
```

## 四、后端部署

### 4.1 创建生产环境配置

在本地项目 `charging-station-backend/src/main/resources/` 下创建 `application-prod.yml`：

```yaml
server:
  port: 8080
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/charging_station?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: "*****"
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
mybatis-plus:
  mapper-locations: classpath:mapper/**/*.xml
  type-aliases-package: com.charging.station.entity
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
jwt:
  secret: ***********
  expiration: 86400000
  header: Authorization
mqtt:
  broker:
    enabled: false
logging:
  level:
    com.charging.station: info
```

### 4.2 构建并上传

```bash
# 本地构建（跳过测试）
cd charging-station-backend
mvn clean package -DskipTests

# 上传 JAR 和配置
scp target/charging-station-backend-1.0.0.jar root@*****:/opt/charging-station-backend.jar
scp src/main/resources/application-prod.yml root@*******:/opt/application-prod.yml
```

### 4.3 注册为系统服务

在服务器上创建 `/etc/systemd/system/charging-station.service`：

```ini
[Unit]
Description=Charging Station Backend
After=network.target mysqld.service
Wants=mysqld.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod -Xmx512m -Xms256m /opt/charging-station-backend.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

```bash
systemctl daemon-reload
systemctl enable charging-station
systemctl start charging-station

# 查看日志
journalctl -u charging-station -f
```

## 五、前端部署

### 5.1 监管与调度平台 (supervision-frontend)

```bash
cd supervision-frontend
npx vite build

# 上传
ssh root@***** "mkdir -p /var/www/html"
scp -r dist/* root@******:/var/www/html/
```

### 5.2 充电站运营平台 (charging-station-frontend-plus)

部署到子路径 `/plus/`，需要修改 3 个文件：

**vite.config.js** — 添加 `base: '/plus/'`：
```js
export default defineConfig({
  base: '/plus/',
  plugins: [vue()],
  // ...
})
```

**src/router/index.js** — 设置路由 base：
```js
const router = createRouter({
  history: createWebHistory('/plus/'),
  routes
})
// 路由守卫中更新重定向路径为 /plus/login
```

**src/api/request.js** — 更新 401 跳转：
```js
window.location.href = '/plus/login'
```

```bash
cd charging-station-frontend-plus
npx vite build

# 上传
ssh root@****** "mkdir -p /var/www/plus"
scp -r dist/* root@*******:/var/www/plus/
```

## 六、Nginx 配置

服务器上创建 `/etc/nginx/conf.d/charging-station.conf`：

```nginx
server {
    listen       80;
    server_name  _;

    # 充电站运营管理平台（子路径）
    location /plus/ {
        alias /var/www/plus/;
        try_files $uri $uri/ /plus/index.html;
    }
    location = /plus {
        return 301 /plus/;
    }

    # 监管与调度平台（主站）
    location / {
        root   /var/www/html;
        index  index.html;
        try_files $uri $uri/ /index.html;
    }

    # API 代理到后端 8080 端口
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 30s;
        proxy_read_timeout 60s;
    }
}
```

```bash
nginx -t                                    # 测试配置
systemctl reload nginx                      # 重载配置
```

**注意：** 如果主 `nginx.conf` 中有默认 server block 冲突，需注释掉或删除。

## 七、阿里云安全组配置

在阿里云控制台 → ECS 实例 → 安全组 → 添加入方向规则：

| 端口 | 协议 | 授权对象 | 用途 |
|------|------|---------|------|
| 80 | TCP | 0.0.0.0/0 | HTTP 前端访问 |
| 22 | TCP | 0.0.0.0/0 | SSH 远程管理 |
| 3306 | TCP | 0.0.0.0/0 | MySQL（如需要远程连接） |

## 八、常用运维命令

```bash
# 服务状态
systemctl status charging-station mysqld nginx

# 重启服务
systemctl restart charging-station

# 查看日志
journalctl -u charging-station -f          # 实时日志
journalctl -u charging-station -n 50       # 最近 50 行

# 重新部署后端
scp target/charging-station-backend-1.0.0.jar root@******:/opt/charging-station-backend.jar
ssh root@****** "systemctl restart charging-station"

# 重新部署前端
cd supervision-frontend && npx vite build
scp -r dist/* root@******:/var/www/html/

# 数据库备份
ssh root@****** "mysqldump -u root -p'Wang926494334.' charging_station > /opt/backup.sql"
scp root@******:/opt/backup.sql ./
```

## 九、目录结构（服务器）

```
/opt/
  charging-station-backend.jar    # 后端 Spring Boot JAR
  application-prod.yml            # 生产环境配置

/var/www/
  html/                           # 监管与调度平台前端
    index.html
    assets/
  plus/                           # 充电站运营管理前端
    index.html
    assets/

/etc/nginx/conf.d/
  charging-station.conf           # Nginx 站点配置

/etc/systemd/system/
  charging-station.service        # 后端 systemd 服务
```
