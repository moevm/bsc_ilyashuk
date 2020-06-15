import { Button } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import MainController from '../../../../controllers/MainPage/MainPageController';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const Examples: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();

  return (
    <div className={classes.container}>
      <Button
        onClick={() => props.controller.loadExample(1)}
        variant='contained'
        color='primary'
        style={{ marginRight: '10px' }}
      >
        Пример 1
      </Button>
      <Button
        onClick={() => props.controller.loadExample(2)}
        variant='contained'
        color='primary'
      >
        Пример 2
      </Button>
    </div>
  );
};

export default inject('controller')(
  observer(Examples as FunctionComponent<PublicProps>)
);
