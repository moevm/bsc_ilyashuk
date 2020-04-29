import axios from 'axios';
import { action, observable } from 'mobx';
import { ReactMicStopEvent } from 'react-mic';

export default class AudioRecorderController {
  @observable public isRecording = false;

  @action.bound
  public changeRecordingState = () => {
    this.isRecording = !this.isRecording;
  };

  public onRecordCompleted = (stopEvent: ReactMicStopEvent) => {
    console.log(stopEvent);
    const file = new File([stopEvent.blob], 'recording.webm', {
      type: 'audio/webm',
    });

    const formData = new FormData();
    formData.append('file', file);

    const url = 'http://localhost:8080/predict';

    const config = {
      headers: {
        'content-type': 'multipart/form-data',
      },
    };

    axios.post(url, formData, config).then(console.log);
  };

  public onAudioData = (blob: Blob) => {
    console.log(blob);
  };
}
