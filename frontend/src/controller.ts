import axios from 'axios';
import { action, observable } from 'mobx';

export default class Controller {
  private file?: File;

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

    const url = 'http://0.0.0.0:8080/predict';

    const config = {
      headers: {
        'content-type': 'multipart/form-data',
      },
    };

    const resp = await axios.post(url, formData, config);
    this.result = resp.data;
  };
}
