import axios from 'axios';
import { action, observable } from 'mobx';
import { labels } from '../../config/labels';
import AudioRecorderController from './AudioRecorder/controller';

export default class MainController {
  public readonly recorder = new AudioRecorderController();

  private file?: File;

  @observable public uploadProgress = 0;

  @observable
  public chartData: any[] = [];

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

    axios.post(url, formData, config).then((response) => {
      this.formChartData(response.data);
    });
  };

  @action.bound
  public formChartData = (data: any) => {
    const res: any[] = [];
    data.forEach((prediction: number[], index: number) => {
      var chartData: any = {};
      chartData.time = index;
      labels.forEach((label, labelIndex) => {
        chartData[label] = prediction[labelIndex];
      });
      res.push(chartData);
    });
    this.chartData = res;
  };
}
