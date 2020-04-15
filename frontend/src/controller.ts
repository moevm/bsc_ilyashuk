import axios from 'axios';

export default class Controller {
  private file?: File;

  public onAttachFile = (e: any) => {
    this.file = e.target.files[0];
  };

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

    console.log(resp);
  };
}
