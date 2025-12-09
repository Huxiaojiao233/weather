// 工具函数

// 格式化日期
function formatDate(dateInput) {
    if (!dateInput) return '-';
    
    // 处理数组格式的日期时间 [2025, 12, 7, 17, 54]
    if (Array.isArray(dateInput)) {
        if (dateInput.length >= 3) {
            const [year, month, day, hour = 0, minute = 0] = dateInput;
            return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')} ${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
        }
    }
    
    // 处理字符串格式的日期
    const date = new Date(dateInput);
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// 格式化日期（仅日期）
function formatDateOnly(dateInput) {
    if (!dateInput) return '-';
    
    // 处理数组格式的日期 [2025, 12, 8]
    if (Array.isArray(dateInput)) {
        const [year, month, day] = dateInput;
        return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    }
    
    // 处理字符串格式的日期
    const date = new Date(dateInput);
    return date.toLocaleDateString('zh-CN');
}

// 格式化时间
function formatTime(timeInput) {
    if (!timeInput) return '-';
    
    // 处理数组格式的时间 [9, 0] 或 [17, 30]
    if (Array.isArray(timeInput)) {
        const [hour, minute = 0] = timeInput;
        return `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
    }
    
    // 处理字符串格式的时间
    if (typeof timeInput === 'string') {
        return timeInput.substring(0, 5); // HH:mm
    }
    
    return '-';
}

// 显示加载状态
function showLoading(element) {
    if (element) {
        element.innerHTML = `
            <div class="loading">
                <div class="spinner-border" role="status">
                    <span class="visually-hidden">加载中...</span>
                </div>
                <p class="mt-2">加载中...</p>
            </div>
        `;
    }
}

// 显示错误信息
function showError(element, message = '加载失败，请稍后重试') {
    if (element) {
        element.innerHTML = `
            <div class="alert alert-danger" role="alert">
                <i class="bi bi-exclamation-triangle"></i> ${message}
            </div>
        `;
    }
}

// 显示空状态
function showEmpty(element, message = '暂无数据') {
    if (element) {
        element.innerHTML = `
            <div class="empty-state">
                <i class="bi bi-inbox"></i>
                <p>${message}</p>
            </div>
        `;
    }
}

// 显示成功消息
function showSuccess(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success alert-dismissible fade show';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    setTimeout(() => {
        alertDiv.remove();
    }, 3000);
}

// 获取预警级别颜色
function getWarningLevelColor(level) {
    const colors = {
        '红色': 'danger',
        '橙色': 'warning',
        '黄色': 'info',
        '蓝色': 'primary'
    };
    return colors[level] || 'secondary';
}

// 获取状态颜色
function getStatusColor(status) {
    const colors = {
        'OPEN': 'success',
        'CLOSED': 'danger',
        'LIMITED': 'warning',
        'NORMAL': 'success',
        'DELAYED': 'warning',
        'CANCELLED': 'danger',
        'ACTIVE': 'success',
        'EXPIRED': 'secondary'
    };
    return colors[status] || 'secondary';
}

// 获取状态文本
function getStatusText(status) {
    const texts = {
        'OPEN': '开放',
        'CLOSED': '关闭',
        'LIMITED': '限时开放',
        'NORMAL': '正常',
        'DELAYED': '延误',
        'CANCELLED': '取消',
        'ACTIVE': '有效',
        'EXPIRED': '已过期'
    };
    return texts[status] || status;
}

// 获取天气图标
function getWeatherIcon(condition) {
    if (!condition) return '☀️';
    const icons = {
        '晴': '☀️',
        '多云': '⛅',
        '阴': '☁️',
        '小雨': '🌦️',
        '中雨': '🌧️',
        '大雨': '⛈️',
        '暴雨': '🌧️',
        '雷阵雨': '⛈️'
    };
    return icons[condition] || '🌤️';
}

// 防抖函数
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// 节流函数
function throttle(func, limit) {
    let inThrottle;
    return function(...args) {
        if (!inThrottle) {
            func.apply(this, args);
            inThrottle = true;
            setTimeout(() => inThrottle = false, limit);
        }
    };
}

// 城市代码映射
const cityMap = {
    'SANYA': '三亚',
    'HAIKOU': '海口',
    'DANZHOU': '儋州',
    'SANSA': '三沙',
    'QIONGHAI': '琼海',
    'WANNING': '万宁',
    'DONGFANG': '东方',
    'WUZHISHAN': '五指山',
    'CHENGMAI': '澄迈',
    'LINGAO': '临高'
};

// 获取城市名称
function getCityName(code) {
    return cityMap[code] || code;
}

