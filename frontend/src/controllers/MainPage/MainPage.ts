import { action, observable } from 'mobx';
import { ReactMicStopEvent } from 'react-mic';
import PredictionService from '../../services/prediction';
import EmotionsChartController from './subcontrollers/EmotionsChart';
import MetricsController from './subcontrollers/Metrics';

export default class MainController {
  public readonly chart = new EmotionsChartController();
  public readonly metrics = new MetricsController();

  private file?: File;

  @observable public uploadProgress = 0;
  @observable public isRecording = false;
  @observable public isPredicted = false;

  public onAttachFile = (e: any) => {
    this.file = e.target.files[0];
  };

  @action.bound
  public uploadAttachment = async () => {
    if (!this.file) {
      return;
    }
    this.predict(this.file);
  };

  @action.bound
  public changeRecordingState = () => {
    this.isRecording = !this.isRecording;
  };

  public onRecordCompleted = async (stopEvent: ReactMicStopEvent) => {
    const file = new File([stopEvent.blob], 'recording.webm', {
      type: 'audio/webm',
    });

    this.predict(file);
  };

  private predict = async (file: File) => {
    this.isPredicted = false;
    const result = await PredictionService.predict(file, this.onUploadProgress);
    this.chart.formData(result.predictions);

    this.metrics.formData(result.metrics);

    this.isPredicted = true;
  };

  @action.bound
  private onUploadProgress = (event: ProgressEvent) => {
    this.uploadProgress = (event.loaded / event.total) * 100;
  };

  public openGithub = () => {
    window.open('https://github.com/moevm/bsc_ilyashuk', '_blank');
  };
}
