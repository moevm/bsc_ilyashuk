import { Button, LinearProgress } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import Controller from '../../../controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: Controller;
} & PublicProps;

const FileUploader: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className={classes.uploader}>
      <input
        type='file'
        onChange={props.controller.onAttachFile}
        accept='.wav'
      />

      <Button
        onClick={props.controller.upload}
        variant='contained'
        disableElevation
        className={classes.uploadButton}
      >
        Upload
      </Button>

      <LinearProgress variant='determinate' value={50} />
    </div>
  );
};

export default inject('controller')(
  observer(FileUploader as FunctionComponent<PublicProps>)
);
