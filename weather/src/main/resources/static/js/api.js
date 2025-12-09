// API 基础配置
const API_BASE_URL = 'http://localhost:8080/weather';

// API 请求封装
class ApiClient {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    // 通用请求方法
    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        const config = {
            credentials: 'include', // 重要：携带Cookie，用于Session认证
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
            ...options
        };

        try {
            const response = await fetch(url, config);
            if (!response.ok) {
                // 如果是401错误，尝试解析响应体获取错误信息
                if (response.status === 401) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || '未授权访问');
                }
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            return data;
        } catch (error) {
            console.error('API请求失败:', error);
            throw error;
        }
    }

    // GET 请求
    async get(endpoint, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        return this.request(url, { method: 'GET' });
    }

    // POST 请求
    async post(endpoint, data = {}, params = {}) {
        const queryString = new URLSearchParams(params).toString();
        const url = queryString ? `${endpoint}?${queryString}` : endpoint;
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }
}

// 创建 API 客户端实例
const api = new ApiClient(API_BASE_URL);

// 天气相关 API
const weatherApi = {
    // 获取实时天气
    getRealtime: (location = 'SANYA') => {
        return api.get('/api/weather/realtime', { location });
    },

    // 获取天气预报
    getForecast: (location = 'SANYA') => {
        return api.get('/api/weather/forecast', { location });
    },

    // 获取历史天气
    getHistory: (location = 'SANYA', date = '') => {
        return api.get('/api/weather/history', { location, date });
    },

    // 天气对比
    getCompare: (location1 = '', location2 = '') => {
        return api.get('/api/weather/compare', { location1, location2 });
    },

    // 获取天气预警
    getWarnings: () => {
        return api.get('/api/weather/warnings');
    },

    // 按类型获取预警
    getWarningsByType: (warningType) => {
        return api.get('/api/weather/warnings/type', { warningType });
    },

    // 按级别获取预警
    getWarningsByLevel: (warningLevel) => {
        return api.get('/api/weather/warnings/level', { warningLevel });
    },

    // 预警详情
    getWarningDetail: (id) => {
        return api.get('/api/weather/warning/detail', { id });
    }
};

// 预警相关 API
const warningApi = {
    // 综合预警列表
    getComprehensive: () => {
        return api.get('/api/warnings/comprehensive');
    },

    // 综合预警详情
    getComprehensiveDetail: (id) => {
        return api.get(`/api/warnings/comprehensive/${id}`);
    },

    // 预警摘要
    getSummary: () => {
        return api.get('/api/warnings/summary');
    }
};

// 交通相关 API
const trafficApi = {
    // 获取航班状态
    getFlights: () => {
        return api.get('/api/traffic/flights');
    },

    // 获取列车状态
    getTrains: () => {
        return api.get('/api/traffic/trains');
    },

    // 交通搜索
    search: (params = {}) => {
        return api.get('/api/traffic/search', params);
    },

    // 实时交通
    getRealtime: () => {
        return api.get('/api/traffic/realtime');
    },

    // 交通详情
    getDetail: (type, number) => {
        return api.get('/api/traffic/detail', { type, number });
    }
};

// 景点相关 API
const attractionApi = {
    // 获取景点状态
    getStatus: () => {
        return api.get('/api/attractions/status');
    },

    // 景点搜索
    search: (params = {}) => {
        return api.get('/api/attractions/search', params);
    },

    // 按位置获取景点
    getByLocation: (locationCode = 'SANYA') => {
        return api.get('/api/attractions/by-location', { locationCode });
    },

    // 景点详情
    getDetail: (name) => {
        return api.get('/api/attractions/detail', { name });
    },

    // 今日开放景点
    getOpenToday: () => {
        return api.get('/api/attractions/open-today');
    }
};

// 首页相关 API
const homeApi = {
    // 首页数据
    getIndex: () => {
        return api.get('/api/index');
    },

    // 天气信息
    getWeatherInfo: () => {
        return api.get('/api/weather/info');
    },

    // 预警信息
    getWarningsInfo: () => {
        return api.get('/api/warnings/info');
    },

    // 交通信息
    getTrafficInfo: () => {
        return api.get('/api/traffic/info');
    },

    // 景点信息
    getAttractionsInfo: () => {
        return api.get('/api/attractions/info');
    }
};

// 管理员相关 API
const adminApi = {
    // 登录
    login: (username, password) => {
        return api.post('/api/admin/login', { username, password });
    },

    // 退出
    logout: () => {
        return api.get('/api/admin/logout');
    },

    // 仪表盘数据
    getDashboard: () => {
        return api.get('/api/admin/dashboard');
    },

    // 用户管理
    getUsers: () => {
        return api.get('/api/admin/users');
    },

    // 天气数据管理
    getWeatherData: () => {
        return api.get('/api/admin/weather-data');
    },
    updateWeatherData: (weatherData) => {
        return api.post('/api/admin/weather-data/update', weatherData);
    },

    // 预警管理
    getWarnings: () => {
        return api.get('/api/admin/warnings');
    },
    getCreateWarningPage: () => {
        return api.get('/api/admin/warnings/create');
    },
    createWarning: (warningData) => {
        return api.post('/api/admin/warnings/create', warningData);
    },
    publishWarning: (warningId) => {
        const queryString = new URLSearchParams({ warningId: warningId }).toString();
        return api.post(`/api/admin/warnings/publish?${queryString}`, {});
    },

    // 交通管理
    getTraffic: () => {
        return api.get('/api/admin/traffic');
    },
    updateTraffic: (id, trafficData) => {
        return api.post('/api/admin/traffic/update', trafficData, { id: id });
    },
    addTraffic: (trafficData) => {
        return api.post('/api/admin/traffic/add', trafficData);
    },
    deleteTraffic: (id) => {
        const queryString = new URLSearchParams({ id: id }).toString();
        return api.request(`/api/admin/traffic/delete?${queryString}`, {
            method: 'DELETE'
        });
    },

    // 景点管理
    getAttractions: () => {
        return api.get('/api/admin/attractions');
    },
    updateAttraction: (id, attractionData) => {
        return api.post('/api/admin/attractions/update', attractionData, { id: id });
    },
    addAttraction: (attractionData) => {
        return api.post('/api/admin/attractions/add', attractionData);
    },
    deleteAttraction: (id) => {
        const queryString = new URLSearchParams({ id: id }).toString();
        return api.request(`/api/admin/attractions/delete?${queryString}`, {
            method: 'DELETE'
        });
    },

    // 系统日志
    getLogs: () => {
        return api.get('/api/admin/logs');
    },

    // 同步天气和预警数据
    syncWeatherData: () => {
        return api.post('/api/admin/weather/sync', {});
    },

    // 模拟发布天气预警
    simulateWeatherWarning: (warningData) => {
        return api.post('/api/admin/warnings/simulate', warningData);
    }
};
