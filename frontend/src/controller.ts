import axios from 'axios';
import { action, observable } from 'mobx';

export default class Controller {
  private file?: File;

  @observable public uploadProgress = 0;

  @observable public result: string = '';

  public onAttachFile = (e: any) => {
    this.file = e.target.files[0];
  };

  @action.bound
  public upload = async () => {
    if (!this.file) {
      return;
    }
    const formData = new FormData();
    formData.append('file', this.file);

    const url = 'http://localhost:8080/predict';

    const config = {
      onUploadProgress: (progressEvent: ProgressEvent) =>
        (this.uploadProgress = Math.round(
          (progressEvent.loaded * 100) / progressEvent.total
        )),
      headers: {
        'content-type': 'multipart/form-data',
      },
    };

    const resp = await axios.post(url, formData, config);
    console.log(resp);
    this.result = resp.data;
  };
}
