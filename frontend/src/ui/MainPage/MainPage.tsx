import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import MainController from '../../controllers/MainPage/MainPageController';
import AudioRecorder from './components/AudioRecorder/AudioRecorder';
import EmotionsChart from './components/EmotionsChart/EmotionsChart';
import Examples from './components/Examples/Examples';
import FileUploader from './components/FilePicker/FilePicker';
import GithubButton from './components/GithubButton/GithubButton';
import Metrics from './components/Metrics/Metrics';
import ProgressBar from './components/ProgressBar/ProgressBar';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const MainPage: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      <header className={classes.header}>
        <AudioRecorder />
        <Examples />
        <FileUploader />
        <ProgressBar />
        <EmotionsChart />
        <GithubButton />
        <Metrics />
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(MainPage as FunctionComponent<PublicProps>)
);
