// 主逻辑文件

// 页面加载完成后初始化
document.addEventListener("DOMContentLoaded", function () {
  // 初始化当前页面
  const currentPage = window.location.pathname.split("/").pop() || "index.html";

  if (currentPage === "index.html" || currentPage === "") {
    initHomePage();
  }
});

// 初始化首页
async function initHomePage() {
  try {
    // 加载首页数据
    const response = await homeApi.getIndex();
    // 处理包装的响应格式 {data: {sanyaWeather, activeWarnings, publishedWarnings, abnormalTraffic, closedAttractions}, success: true}
    if (response && response.data) {
      displayHomeData(response.data);
    } else {
      displayHomeData(null);
    }
  } catch (error) {
    console.error("加载首页数据失败:", error);
    displayHomeData(null);
  }
}

// 显示首页数据
function displayHomeData(data) {
  const container = document.getElementById("home-content");
  if (!container) return;

  let weatherInfo = "";
  let warningsInfo = "";
  let trafficInfo = "";
  let attractionsInfo = "";

  if (data) {
    // 显示三亚天气
    if (data.sanyaWeather) {
      const weather = data.sanyaWeather;
      weatherInfo = `
                <div class="card mb-3">
                    <div class="card-header">
                        <i class="bi bi-cloud-sun"></i> 三亚实时天气
                    </div>
                    <div class="card-body">
                        <h3>${getWeatherIcon(
                          weather.weatherCondition || "晴"
                        )} ${weather.weatherCondition || "晴"}</h3>
                        <h2 class="text-primary">${
                          weather.temperature || "-"
                        }°C</h2>
                        <p>湿度: ${weather.humidity || "-"}% | 风速: ${
        weather.windSpeed || "-"
      } m/s</p>
                    </div>
                </div>
            `;
    }

    // 显示活跃预警
    if (data.activeWarnings && data.activeWarnings.length > 0) {
      warningsInfo = `
                <div class="card mb-3">
                    <div class="card-header bg-warning">
                        <i class="bi bi-exclamation-triangle"></i> 活跃预警 (${
                          data.activeWarnings.length
                        })
                    </div>
                    <div class="card-body">
                        ${data.activeWarnings
                          .slice(0, 3)
                          .map((w) => {
                            const levelColor = getWarningLevelColor(
                              w.warningLevel
                            );
                            return `<span class="badge bg-${levelColor} me-2">${w.warningLevel} ${w.warningType}</span>`;
                          })
                          .join("")}
                        ${
                          data.activeWarnings.length > 3
                            ? '<p class="mt-2"><small>还有 ' +
                              (data.activeWarnings.length - 3) +
                              " 条预警</small></p>"
                            : ""
                        }
                    </div>
                </div>
            `;
    }

    // 显示异常交通
    if (data.abnormalTraffic && data.abnormalTraffic.length > 0) {
      trafficInfo = `
                <div class="card mb-3">
                    <div class="card-header bg-danger">
                        <i class="bi bi-airplane"></i> 异常交通 (${data.abnormalTraffic.length})
                    </div>
                    <div class="card-body">
                        <p>有 ${data.abnormalTraffic.length} 个航班/列车出现延误或取消</p>
                    </div>
                </div>
            `;
    }

    // 显示关闭景点
    if (data.closedAttractions && data.closedAttractions.length > 0) {
      attractionsInfo = `
                <div class="card mb-3">
                    <div class="card-header bg-danger">
                        <i class="bi bi-geo-alt"></i> 关闭/限时开放景点 (${
                          data.closedAttractions.length
                        })
                    </div>
                    <div class="card-body">
                        <p>${data.closedAttractions
                          .map((a) => a.name)
                          .join("、")}</p>
                    </div>
                </div>
            `;
    }
  }

  container.innerHTML = `
        <div class="row mt-4">
            <div class="col-md-8">
                ${weatherInfo}
                ${warningsInfo}
                ${trafficInfo}
                ${attractionsInfo}
            </div>
            <div class="col-md-4">
                <div class="card feature-card mb-3">
                    <div class="card-body">
                        <i class="bi bi-cloud-sun"></i>
                        <h5>实时天气</h5>
                        <p>查询各城市实时天气情况</p>
                        <a href="pages/weather.html" class="btn btn-primary">查看天气</a>
                    </div>
                </div>
                <div class="card feature-card mb-3">
                    <div class="card-body">
                        <i class="bi bi-exclamation-triangle"></i>
                        <h5>天气预警</h5>
                        <p>查看最新天气预警信息</p>
                        <a href="pages/warnings.html" class="btn btn-primary">查看预警</a>
                    </div>
                </div>
                <div class="card feature-card mb-3">
                    <div class="card-body">
                        <i class="bi bi-airplane"></i>
                        <h5>交通信息</h5>
                        <p>查询航班和列车状态</p>
                        <a href="pages/traffic.html" class="btn btn-primary">查看交通</a>
                    </div>
                </div>
                <div class="card feature-card mb-3">
                    <div class="card-body">
                        <i class="bi bi-geo-alt"></i>
                        <h5>景点信息</h5>
                        <p>查看景点开放状态</p>
                        <a href="pages/attractions.html" class="btn btn-primary">查看景点</a>
                    </div>
                </div>
            </div>
        </div>
    `;
}
