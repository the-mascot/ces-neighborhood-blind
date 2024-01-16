class UploadAdapter {
    constructor( loader ) {
        // The file loader instance to use during the upload.
        this.loader = loader;
    }

    // Starts the upload process.
    upload() {
        return this.loader.file
            .then(file => {
                this._sendRequest(file);
            });
    }

    _sendRequest(file) {
        const data = new FormData();
        data.append( 'image', file );
        axios.post('http://localhost:8010/upload/image', data)
            .then(response => {
                console.log(response);
            })
            .catch(error => {
                console.log(error);
            })
    }
}
