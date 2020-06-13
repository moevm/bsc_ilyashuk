import { action, observable } from 'mobx';
import { ReactMicStopEvent } from 'react-mic';
import PredictionService from '../../services/prediction';
import EmotionsChartController from './subcontrollers/EmotionsChartController';
import FilePickerController from './subcontrollers/FilePickerController';
import MetricsController from './subcontrollers/MetricsController';

export default class MainController {
  public readonly chartController = new EmotionsChartController();
  public readonly metricsController = new MetricsController();
  public readonly fileController = new FilePickerController();

  @observable public uploadProgress = 0;
  @observable public isRecording = false;
  @observable public isPredicted = false;

  @action.bound
  public uploadAttachment = async () => {
    if (!this.fileController.file) {
      alert('Выберите аудио файл для загрузки');
      return;
    }
    this.predict(this.fileController.file);
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
    const result = await PredictionService.predict(
      file,
      this.fileController.selectedChunkLength,
      this.onUploadProgress
    );

    this.chartController.formData(result.predictions);
    this.metricsController.formData(result.metrics);
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
