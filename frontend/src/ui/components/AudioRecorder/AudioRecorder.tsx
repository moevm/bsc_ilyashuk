import { Button } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { ReactMic } from 'react-mic';
import { backgroundColor, primaryColor } from '../../../config/style';
import MainController from '../../../controllers/App/controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const AudioRecorder: FunctionComponent<PrivateProps> = (
  props: PrivateProps
) => {
  const classes = useStyles();

  return (
    <div className={classes.container}>
      <ReactMic
        backgroundColor={backgroundColor}
        strokeColor={primaryColor}
        className={classes.audio}
        record={props.controller.recorder.isRecording}
      />
      <div className={classes.buttons}>
        <Button
          variant='contained'
          color='primary'
          onClick={props.controller.recorder.changeRecordingState}
        >
          {props.controller.recorder.isRecording ? 'Stop' : 'Start'}
        </Button>
      </div>
    </div>
  );
};

export default inject('controller')(
  observer(AudioRecorder as FunctionComponent<PublicProps>)
);
