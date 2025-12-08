/**
 * 导航栏组件
 */
class Navbar {
  constructor(containerId, currentPage = "") {
    this.containerId = containerId;
    this.currentPage = currentPage;
    this.init();
  }

  init() {
    const container = document.getElementById(this.containerId);
    if (!container) {
      console.error("导航栏容器不存在");
      return;
    }

    container.innerHTML = this.render();
  }

  render() {
    const menuItems = [
      { id: "index", label: "首页", url: "../index.html", page: "index.html" },
      {
        id: "weather",
        label: "天气查询",
        url: "weather.html",
        page: "weather.html",
      },
      {
        id: "warnings",
        label: "天气预警",
        url: "warnings.html",
        page: "warnings.html",
      },
      {
        id: "traffic",
        label: "交通信息",
        url: "traffic.html",
        page: "traffic.html",
      },
      {
        id: "attractions",
        label: "景点信息",
        url: "attractions.html",
        page: "attractions.html",
      },
    ];

    let html = `
      <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
          <a class="navbar-brand" href="#" onclick="if(window.parent && window.parent.navigateToPage) { window.parent.navigateToPage('index.html'); } else if(typeof navigateToPage === 'function') { navigateToPage('index.html'); } return false;">
            <i class="bi bi-cloud-sun"></i> 天气服务系统
          </a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto">
    `;

    menuItems.forEach((item) => {
      const activeClass = this.currentPage === item.id ? "active" : "";
      html += `
        <li class="nav-item">
          <a class="nav-link ${activeClass}" href="#" onclick="if(window.parent && window.parent.navigateToPage) { window.parent.navigateToPage('${item.page}'); } else if(typeof navigateToPage === 'function') { navigateToPage('${item.page}'); } return false;">
            ${item.label}
          </a>
        </li>
      `;
    });

    html += `
              <li class="nav-item">
                <a class="nav-link" href="pages/admin/login.html">
                  管理员登录
                </a>
              </li>
            </ul>
          </div>
        </div>
      </nav>
    `;

    return html;
  }

  setActive(pageId) {
    const navLinks = document.querySelectorAll(
      `#${this.containerId} .nav-link`
    );
    navLinks.forEach((link) => {
      link.classList.remove("active");
    });

    const activeLink = document.querySelector(
      `#${this.containerId} .nav-link[onclick*="${pageId}"]`
    );
    if (activeLink) {
      activeLink.classList.add("active");
    }

    this.currentPage = pageId;
  }
}
