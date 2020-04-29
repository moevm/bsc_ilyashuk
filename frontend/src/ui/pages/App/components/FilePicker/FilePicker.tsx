import { Button } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import MainController from '../../controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const FilePicker: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      <Button
        onClick={props.controller.uploadAttachment}
        variant='contained'
        color='primary'
        className={classes.uploadButton}
      >
        Upload
      </Button>
      <input
        type='file'
        onChange={props.controller.onAttachFile}
        accept='.wav'
      />

      {/* <LinearProgress
        variant='determinate'
        value={props.controller.uploadProgress}
        className={classes.progressBar}
      /> */}
    </div>
  );
};

export default inject('controller')(
  observer(FilePicker as FunctionComponent<PublicProps>)
);
