import { action, observable } from 'mobx';
import { ReactMicStopEvent } from 'react-mic';
import PredictionService from '../../../services/prediction';
import EmotionsChartController from './components/EmotionsChart/controller';

export default class MainController {
  public readonly chart = new EmotionsChartController();

  private file?: File;

  @observable public uploadProgress = 0;

  public onAttachFile = (e: any) => {
    this.file = e.target.files[0];
  };

  @action.bound
  public uploadAttachment = async () => {
    if (!this.file) {
      return;
    }
    const prediction = await PredictionService.predict(this.file);

    this.chart.formChartData(prediction);
  };

  @observable public isRecording = false;

  @action.bound
  public changeRecordingState = () => {
    this.isRecording = !this.isRecording;
  };

  public onRecordCompleted = async (stopEvent: ReactMicStopEvent) => {
    const file = new File([stopEvent.blob], 'recording.webm', {
      type: 'audio/webm',
    });

    const prediction = await PredictionService.predict(file);

    this.chart.formChartData(prediction);
  };
}
