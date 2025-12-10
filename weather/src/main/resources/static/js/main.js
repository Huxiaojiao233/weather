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

  let leftColumn = "";
  let rightColumn = "";

  if (data) {
    // 左侧：活跃预警、异常交通、关闭景点
    let warningsInfo = "";
    let trafficInfo = "";
    let attractionsInfo = "";

    // 显示活跃预警（可点击跳转）
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
                          .slice(0, 5)
                          .map((w) => {
                            const levelColor = getWarningLevelColor(
                              w.warningLevel
                            );
                            const warningText = `${w.warningLevel} ${w.warningType || ""}`;
                            return `<a href="#" onclick="if(window.parent.navigateToPage) { window.parent.navigateToPage('warnings.html'); return false; } else { window.location.href='warnings.html'; return false; }" class="badge bg-${levelColor} me-2 mb-2" style="cursor: pointer; text-decoration: none;">${warningText}</a>`;
                          })
                          .join("")}
                        ${
                          data.activeWarnings.length > 5
                            ? '<p class="mt-2"><small>还有 ' +
                              (data.activeWarnings.length - 5) +
                              " 条预警</small></p>"
                            : ""
                        }
                    </div>
                </div>
            `;
    } else {
      warningsInfo = `
                <div class="card mb-3">
                    <div class="card-header bg-warning">
                        <i class="bi bi-exclamation-triangle"></i> 活跃预警
                    </div>
                    <div class="card-body">
                        <p class="text-muted">暂无活跃预警</p>
                    </div>
                </div>
            `;
    }

    // 显示异常交通（改为按钮）
    if (data.abnormalTraffic && data.abnormalTraffic.length > 0) {
      trafficInfo = `
                <div class="card mb-3">
                    <div class="card-header bg-danger">
                        <i class="bi bi-airplane"></i> 异常交通 (${data.abnormalTraffic.length})
                    </div>
                    <div class="card-body">
                        <p>有 ${data.abnormalTraffic.length} 个航班/列车出现延误或取消</p>
                        <button class="btn btn-danger btn-sm" onclick="if(window.parent.navigateToPage) { window.parent.navigateToPage('traffic.html'); return false; } else { window.location.href='traffic.html'; return false; }">
                            <i class="bi bi-arrow-right"></i> 查看详情
                        </button>
                    </div>
                </div>
            `;
    }

    // 显示关闭景点（改为按钮，点击弹出详情）
    if (data.closedAttractions && data.closedAttractions.length > 0) {
      const attractionsList = data.closedAttractions
        .map((a) => `<li>${a.name} - ${getStatusText(a.openStatus)}</li>`)
        .join("");
      attractionsInfo = `
                <div class="card mb-3">
                    <div class="card-header bg-danger">
                        <i class="bi bi-geo-alt"></i> 关闭/限时开放景点 (${
                          data.closedAttractions.length
                        })
                    </div>
                    <div class="card-body">
                        <p>有 ${data.closedAttractions.length} 个景点关闭或限时开放</p>
                        <button class="btn btn-danger btn-sm me-2" onclick="showAttractionsDetail(${JSON.stringify(data.closedAttractions).replace(/"/g, '&quot;')})">
                            <i class="bi bi-info-circle"></i> 查看详情
                        </button>
                        <button class="btn btn-primary btn-sm" onclick="if(window.parent.navigateToPage) { window.parent.navigateToPage('attractions.html'); return false; } else { window.location.href='attractions.html'; return false; }">
                            <i class="bi bi-arrow-right"></i> 前往管理
                        </button>
                    </div>
                </div>
            `;
    }

    leftColumn = warningsInfo + trafficInfo + attractionsInfo;

    // 右侧：四个城市的天气
    const cities = [
      { code: "SANYA", name: "三亚", weather: data.sanyaWeather },
      { code: "HAIKOU", name: "海口", weather: data.haikouWeather },
      { code: "DONGFANG", name: "东方", weather: data.dongfangWeather },
      { code: "QIONGHAI", name: "琼海", weather: data.qionghaiWeather },
    ];

    rightColumn = `
            <div class="row">
                ${cities
                  .map((city) => {
                    const weather = city.weather;
                    if (!weather) {
                      return `
                        <div class="col-md-6 mb-3">
                            <div class="card">
                                <div class="card-header">
                                    <i class="bi bi-cloud-sun"></i> ${city.name}
                                </div>
                                <div class="card-body">
                                    <p class="text-muted">暂无数据</p>
                                </div>
                            </div>
                        </div>
                      `;
                    }
                    return `
                        <div class="col-md-6 mb-3">
                            <div class="card">
                                <div class="card-header">
                                    <i class="bi bi-cloud-sun"></i> ${city.name}
                                </div>
                                <div class="card-body">
                                    <h4>${getWeatherIcon(
                                      weather.weatherCondition || "晴"
                                    )} ${weather.weatherCondition || "晴"}</h4>
                                    <h3 class="text-primary">${
                                      weather.temperature || "-"
                                    }°C</h3>
                                    <p class="mb-1"><small>湿度: ${
                                      weather.humidity || "-"
                                    }%</small></p>
                                    <p class="mb-0"><small>风速: ${
                                      weather.windSpeed || "-"
                                    } m/s</small></p>
                                </div>
                            </div>
                        </div>
                    `;
                  })
                  .join("")}
            </div>
        `;
  } else {
    leftColumn = '<div class="card mb-3"><div class="card-body"><p class="text-muted">加载中...</p></div></div>';
    rightColumn = '<div class="card"><div class="card-body"><p class="text-muted">加载中...</p></div></div>';
  }

  container.innerHTML = `
        <div class="row mt-4">
            <div class="col-md-8">
                ${leftColumn}
            </div>
            <div class="col-md-4">
                ${rightColumn}
            </div>
        </div>
    `;
}

// 显示景点详情模态框
function showAttractionsDetail(attractions) {
  if (!attractions || attractions.length === 0) return;

  const modalHtml = `
    <div class="modal fade" id="attractionsDetailModal" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">关闭/限时开放景点详情</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">
            <ul class="list-group">
              ${attractions.map(a => `
                <li class="list-group-item">
                  <strong>${a.name || "-"}</strong><br>
                  <small class="text-muted">状态: ${getStatusText(a.openStatus)}</small><br>
                  ${a.closeReason ? `<small class="text-muted">原因: ${a.closeReason}</small>` : ""}
                  ${a.address ? `<small class="text-muted">地址: ${a.address}</small>` : ""}
                </li>
              `).join("")}
            </ul>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
            <button type="button" class="btn btn-primary" onclick="if(window.parent.navigateToPage) { window.parent.navigateToPage('attractions.html'); return false; } else { window.location.href='attractions.html'; return false; }">
              前往管理
            </button>
          </div>
        </div>
      </div>
    </div>
  `;

  // 移除旧的模态框
  const oldModal = document.getElementById("attractionsDetailModal");
  if (oldModal) oldModal.remove();

  // 添加新的模态框
  document.body.insertAdjacentHTML("beforeend", modalHtml);

  // 显示模态框
  const modal = new bootstrap.Modal(document.getElementById("attractionsDetailModal"));
  modal.show();
}
