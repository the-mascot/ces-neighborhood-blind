axios.defaults.headers.common['Content-Type'] = 'application/json';

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

function like(evt) {
    let element = evt.target;
    let data = {};
    data.postNo = element.getAttribute('data-post-no');
    data.postType = element.getAttribute('data-post-type') === null ? 'POST' : element.getAttribute('data-post-type');
    axios.post('/board/like', data)
        .then(response => {
            element.innerHTML = response.data.data.likeCnt === 0 ? '좋아요' : response.data.data.likeCnt;
            element.className = '';
            element.classList.add(response.data.data.isLiked ? 'like-cnt-span-on' : 'like-cnt-span');
            element.classList.add('mr-14');
        })
        .catch(error => {
            console.error(error);
        })
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
    create: function (id) {
        return ClassicEditor
            .create(document.getElementById(id), {
                language: 'ko',
                toolbar: {
                    items: [
                        'undo', 'redo',
                        '|', 'heading',
                        '|', 'bold', 'italic',
                        '|', 'link', 'uploadImage', 'blockQuote',
                        '|', 'bulletedList', 'numberedList'
                    ],
                },
                image: {
                    upload: {
                        types: [ "jpg", "jfif", "pjpeg", "jpeg", "pjp", "png", "gif", "bmp", "dib", "webp", "heic", "heif" ],
                    }
                }
            })
            .then(editor => {
                editor.plugins.get('FileRepository').createUploadAdapter = (loader) => { return new UploadAdapter(loader); }
                return editor;
            })
            .catch(error => { console.error(error); });
    }
}

