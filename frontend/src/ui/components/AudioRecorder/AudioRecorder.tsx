import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { ReactMic } from 'react-mic';
import Controller from '../../../controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: Controller;
} & PublicProps;

const AudioRecorder: FunctionComponent<PrivateProps> = (
  props: PrivateProps
) => {
  const classes = useStyles();

  return (
    <div className={classes.container}>
      <ReactMic record />
    </div>
  );
};

export default inject('controller')(
  observer(AudioRecorder as FunctionComponent<PublicProps>)
);
