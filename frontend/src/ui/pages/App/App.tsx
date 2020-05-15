import { IconButton } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import github from './assets/github.png';
import AudioRecorder from './components/AudioRecorder/AudioRecorder';
import EmotionsChart from './components/EmotionsChart/EmotionsChart';
import FileUploader from './components/FilePicker/FilePicker';
import ProgressBar from './components/ProgressBar/ProgressBar';
import MainController from './controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const App: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      <header className={classes.header}>
        <AudioRecorder />
        <FileUploader />
        <ProgressBar />
        <EmotionsChart />
        <IconButton
          className={classes.githubButton}
          onClick={props.controller.openGithub}
        >
          <img src={github} width={40} />
        </IconButton>
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(App as FunctionComponent<PublicProps>)
);
