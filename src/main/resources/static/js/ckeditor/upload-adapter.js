class UploadAdapter {
    constructor( loader ) {
        // The file loader instance to use during the upload.
        this.loader = loader;
    }

    upload() {
        return this.loader.file
            .then(file => new Promise((resolve, reject) => {
                const maxLimit = 10 * 1024 * 1024;
                if (file.size > maxLimit) {
                    document.getElementById('alertMsg').innerText = '첨부할 수 있는 이미지 용량은 최대 10MB 까지입니다.';
                    const modal = new bootstrap.Modal('#alertModal');
                    modal.show();
                    return reject();
                }
                const data = new FormData();
                data.append( 'image', file);
                axios.post('http://localhost:8010/s3/upload/image', data)
                    .then(response => {
                        resolve({
                            default: response.data.data
                        });
                    })
                    .catch(error => {
                        return reject(error);
                    });
            }));
    }
}
