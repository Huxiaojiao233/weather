# 天气服务系统 - 前端

这是一个基于 Bootstrap 5 和原生 JavaScript 开发的天气服务系统前端应用。

## 功能特性

- ✅ 实时天气查询
- ✅ 天气预报查询
- ✅ 历史天气查询
- ✅ 天气预警查询
- ✅ 交通信息查询（航班、列车）
- ✅ 景点信息查询
- ✅ 响应式设计，支持移动端

## 技术栈

- **HTML5** - 页面结构
- **Bootstrap 5.3.0** - UI 框架
- **Bootstrap Icons** - 图标库
- **原生 JavaScript** - 业务逻辑
- **Fetch API** - HTTP 请求

## 项目结构

```
weather_front/
├── index.html              # 首页入口
├── pages/                  # 页面目录
│   ├── weather.html        # 天气查询页面
│   ├── warnings.html       # 天气预警页面
│   ├── traffic.html        # 交通信息页面
│   ├── attractions.html    # 景点信息页面
│   ├── help.html          # 帮助页面
│   └── about.html         # 关于页面
├── css/
│   └── style.css          # 自定义样式（蓝色主题）
├── js/
│   ├── api.js             # API 请求封装
│   ├── utils.js           # 工具函数
│   └── main.js            # 主逻辑
└── README.md              # 说明文档
```

## 使用方法

1. **确保后端服务运行**
   - 后端服务地址：`http://localhost:8080/weather`
   - 确保后端 API 服务已启动

2. **打开前端页面**
   - 直接在浏览器中打开 `index.html`
   - 或使用本地服务器（推荐）：
     ```bash
     # 使用 Python
     python -m http.server 8000
     
     # 使用 Node.js (需要安装 http-server)
     npx http-server -p 8000
     ```
   - 然后访问 `http://localhost:8000`

3. **使用功能**
   - 在首页可以快速访问各个功能模块
   - 每个页面都提供了相应的查询功能
   - 支持按条件筛选和搜索

## API 配置

如果需要修改后端 API 地址，请编辑 `js/api.js` 文件中的 `API_BASE_URL`：

```javascript
const API_BASE_URL = 'http://localhost:8080/weather';
```

## 浏览器支持

- Chrome (推荐)
- Firefox
- Safari
- Edge

## 注意事项

1. 由于浏览器的 CORS 策略，如果后端服务不在同一域名下，可能需要配置 CORS
2. 确保后端服务正常运行，否则前端无法获取数据
3. 所有 API 请求都是异步的，请耐心等待数据加载

## 开发说明

- 使用原生 JavaScript，无依赖框架
- 代码结构清晰，易于维护和扩展
- 采用 Bootstrap 5 响应式设计
- 蓝色主题，界面美观

## 许可证

© 2025 天气服务系统. 保留所有权利.

