import { Button } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { ReactMic } from 'react-mic';
import { backgroundColor, primaryColor } from '../../../../../config/style';
import MainController from '../../controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const AudioRecorder: FunctionComponent<PrivateProps> = (
  props: PrivateProps
) => {
  const classes = useStyles();

  const recorderController = props.controller.recorder;

  return (
    <div className={classes.container}>
      <ReactMic
        backgroundColor={backgroundColor}
        strokeColor={primaryColor}
        className={classes.audio}
        record={recorderController.isRecording}
        onData={recorderController.onAudioData}
        onStop={recorderController.onRecordCompleted}
      />
      <div className={classes.buttons}>
        <Button
          variant='contained'
          color={recorderController.isRecording ? 'secondary' : 'primary'}
          onClick={recorderController.changeRecordingState}
        >
          {recorderController.isRecording
            ? 'Stop recording'
            : 'Start recording'}
        </Button>
      </div>
    </div>
  );
};

export default inject('controller')(
  observer(AudioRecorder as FunctionComponent<PublicProps>)
);
