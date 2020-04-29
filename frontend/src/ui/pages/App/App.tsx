import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import AudioRecorder from './components/AudioRecorder/AudioRecorder';
import EmotionsChart from './components/EmotionsChart/EmotionsChart';
import FileUploader from './components/FileUploader/FileUploader';
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
        <EmotionsChart />
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(App as FunctionComponent<PublicProps>)
);