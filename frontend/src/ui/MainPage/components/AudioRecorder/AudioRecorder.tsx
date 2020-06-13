import { Button } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { ReactMic } from 'react-mic';
import { backgroundColor, primaryColor } from '../../../../config/style';
import MainController from '../../../../controllers/MainPage/MainPageController';
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
        record={props.controller.isRecording}
        onStop={props.controller.onRecordCompleted}
      />
      <div className={classes.buttons}>
        <Button
          variant='contained'
          color={props.controller.isRecording ? 'secondary' : 'primary'}
          onClick={props.controller.changeRecordingState}
        >
          {props.controller.isRecording ? 'Остановить' : 'Начать запись'}
        </Button>
      </div>
    </div>
  );
};

export default inject('controller')(
  observer(AudioRecorder as FunctionComponent<PublicProps>)
);
