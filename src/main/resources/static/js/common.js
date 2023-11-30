axios.interceptors.request.use(function (config) {
    if (['post', 'put', 'patch', 'delete'].includes(config.method.toLocaleLowerCase())) {
        const csrfToken = document.head.querySelector('meta[name="_csrf"]').content;
        const csrfHeader = document.head.querySelector('meta[name="_csrf_header"]').content;
        config.headers[csrfHeader] = csrfToken;
    }
    return config;
}, function (error) {
    return Promise.reject(error);
});

