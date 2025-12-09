/**
 * 管理后台侧边栏组件
 */
class AdminSidebar {
  constructor(containerId, onNavigate) {
    this.containerId = containerId;
    this.onNavigate = onNavigate;
    this.currentPage = "dashboard";
    this.init();
  }

  init() {
    const container = document.getElementById(this.containerId);
    if (!container) {
      console.error("侧边栏容器不存在");
      return;
    }

    container.innerHTML = this.render();
    this.attachEvents();
  }

  render() {
    const menuItems = [
      {
        id: "dashboard",
        icon: "bi-speedometer2",
        label: "仪表盘",
        page: "dashboard.html",
      },
      { id: "users", icon: "bi-people", label: "用户管理", page: "users.html" },
      {
        id: "weather",
        icon: "bi-cloud-sun",
        label: "天气数据",
        page: "weather.html",
      },
      {
        id: "warnings",
        icon: "bi-exclamation-triangle",
        label: "预警管理",
        page: "warnings.html",
      },
      {
        id: "traffic",
        icon: "bi-airplane",
        label: "交通管理",
        page: "traffic.html",
      },
      {
        id: "attractions",
        icon: "bi-geo-alt",
        label: "景点管理",
        page: "attractions.html",
      },
      {
        id: "logs",
        icon: "bi-journal-text",
        label: "系统日志",
        page: "logs.html",
      },
    ];

    let html = `
      <div class="text-center mb-4">
        <h4>管理后台</h4>
      </div>
      <nav class="nav flex-column">
    `;

    menuItems.forEach((item) => {
      const activeClass = this.currentPage === item.id ? "active" : "";
      html += `
        <a class="nav-link ${activeClass}" data-page="${item.id}" data-url="${item.page}">
          <i class="bi ${item.icon}"></i> ${item.label}
        </a>
      `;
    });

    html += `
        <hr class="text-white" />
        <a class="nav-link" href="../../index.html">
          <i class="bi bi-house"></i> 返回首页
        </a>
        <a class="nav-link" href="#" id="logoutBtn">
          <i class="bi bi-box-arrow-right"></i> 退出登录
        </a>
      </nav>
    `;

    return html;
  }

  attachEvents() {
    // 导航链接点击事件
    const navLinks = document.querySelectorAll(
      `#${this.containerId} .nav-link[data-page]`
    );
    navLinks.forEach((link) => {
      link.addEventListener("click", (e) => {
        e.preventDefault();
        const pageId = link.getAttribute("data-page");
        const pageUrl = link.getAttribute("data-url");

        // 更新活动状态
        this.setActive(pageId);

        // 触发导航回调
        if (this.onNavigate) {
          this.onNavigate(pageUrl, pageId);
        }
      });
    });

    // 退出登录事件
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
      logoutBtn.addEventListener("click", async (e) => {
        e.preventDefault();
        try {
          await adminApi.logout();
          window.location.href = "login.html";
        } catch (error) {
          console.error("退出登录失败:", error);
          window.location.href = "login.html";
        }
      });
    }
  }

  setActive(pageId) {
    // 移除所有活动状态
    const navLinks = document.querySelectorAll(
      `#${this.containerId} .nav-link[data-page]`
    );
    navLinks.forEach((link) => {
      link.classList.remove("active");
    });

    // 设置当前活动项
    const activeLink = document.querySelector(
      `#${this.containerId} .nav-link[data-page="${pageId}"]`
    );
    if (activeLink) {
      activeLink.classList.add("active");
    }

    this.currentPage = pageId;
  }
}
