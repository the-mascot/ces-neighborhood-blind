class UploadAdapter {
    constructor( loader ) {
        // The file loader instance to use during the upload.
        this.loader = loader;
    }

    upload() {
        return this.loader.file
            .then(file => new Promise((resolve, reject) => {
                const data = new FormData();
                data.append( 'image', file);
                axios.post('http://localhost:8010/s3/upload/image', data)
                    .then(response => {
                        resolve({
                            default: response.data.data
                        });
                    })
                    .catch(error => {
                        console.log(error);
                        reject('Upload Failed');
                    });
            }));
    }
}
