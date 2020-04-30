import { action, observable } from 'mobx';
import { ReactMicStopEvent } from 'react-mic';
import PredictionService from '../../../services/prediction';
import EmotionsChartController from './components/EmotionsChart/controller';

export default class MainController {
  public readonly chart = new EmotionsChartController();

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
    const prediction = await PredictionService.predict(
      file,
      this.onUploadProgress
    );
    this.chart.formChartData(prediction);
    this.isPredicted = true;
  };

  @action.bound
  private onUploadProgress = (event: ProgressEvent) => {
    this.uploadProgress = (event.loaded / event.total) * 100;
  };
}
