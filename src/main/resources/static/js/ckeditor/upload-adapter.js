class UploadAdapter {
    constructor( loader ) {
        // The file loader instance to use during the upload.
        this.loader = loader;
    }

    upload() {
        return this.loader.file
            .then(file => new Promise((resolve, reject) => {
                this.validateFileSize(file, reject)
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

    validateFileSize(file, reject) {
        const maxLimit = 10 * 1024 * 1024;
        if (file.size > maxLimit) {
            document.getElementById('alertMsg').innerText = '첨부할 수 있는 이미지 용량은 최대 10MB 까지입니다.';
            let modal = bootstrap.Modal.getInstance('#alertModal');
            if (!modal) {
                modal = new bootstrap.Modal('#alertModal');
            }
            modal.show();
            return reject();
        }
    }
}

// Base64ImageAdapter 버전
/*   upload() {
       return new Promise(((t,e)=> {
           const n = this.reader = new window.FileReader;
           n.addEventListener("load", (()=> {
                   t({
                       default: n.result
                   })
               }
           ));
           n.addEventListener("error", (t=> {
                   e(t)
               }
           ));
           n.addEventListener("abort", (()=> {
                   e()
               }
           ));
           this.loader.file.then((t => {
               this.validateFileSize(t, e)
               n.readAsDataURL(t)
           }))
       }));
   }

    abort() {
        this.reader.abort()
    }*/
