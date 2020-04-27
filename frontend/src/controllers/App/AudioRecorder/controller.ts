import { action, observable } from 'mobx';

export default class AudioRecorderController {
  @observable public isRecording = false;

  @action.bound
  public changeRecordingState = () => {
    this.isRecording = !this.isRecording;
  };
}
