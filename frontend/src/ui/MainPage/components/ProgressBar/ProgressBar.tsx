import { LinearProgress } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import MainController from '../../../../controllers/MainPage/MainPageController';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const ProgressBar: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      {!props.controller.isPredicted ? (
        <LinearProgress
          variant={
            props.controller.uploadProgress === 100
              ? 'indeterminate'
              : 'determinate'
          }
          value={props.controller.uploadProgress}
        />
      ) : null}
    </div>
  );
};

export default inject('controller')(
  observer(ProgressBar as FunctionComponent<PublicProps>)
);
