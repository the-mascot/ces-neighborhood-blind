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

/*ID 유효성 검사*/
function validateId(id) {
    // 이메일 형식 확인
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    // 길이 확인
    const lengthValid = id.length <= 30;

    return emailRegex.test(id) && lengthValid;
}

/*비밀번호 유효성 검사*/
function validatePassword (password) {
    // 비밀번호는 8~16자, 대문자 1개 이상, 소문자 1개 이상, 특수문자 1개 이상
    const passwordRegex = /^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%^&*()_+])[A-Za-z\d!@#$%^&*()_+]{8,16}$/;
    return passwordRegex.test(password);
}

const ComUtils = {
    nullCheck: function(value) {
        if (value === null || value === '') {
            return true;
        }
        return false;
    }
}

const Editor = {
    create: async function (target) {
        return await ClassicEditor
            .create(document.querySelector(target), {
                toolbar: {
                    items: [
                        'undo', 'redo',
                        '|', 'heading',
                        '|', 'bold', 'italic',
                        '|', 'link', 'uploadImage', 'blockQuote',
                        '|', 'bulletedList', 'numberedList'
                    ],
                },
            })
            .then(newEditor => {
                newEditor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
                    return new UploadAdapter(loader)
                }
                return newEditor;
            })
            .catch(error => {
                console.error(error);
            });
    }
}

