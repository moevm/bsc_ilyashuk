import { Button, createStyles, makeStyles, Theme } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import './App.css';
import Controller from './controller';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    button: {
      marginTop: '10px',
    },
  })
);

type PublicProps = {};

type PrivateProps = {
  controller: Controller;
} & PublicProps;

const App: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className='App'>
      <header className='App-header'>
        <input
          type='file'
          onChange={props.controller.onAttachFile}
          accept='.wav'
        />

        <Button
          onClick={props.controller.upload}
          variant='contained'
          disableElevation
          className={classes.button}
        >
          Upload
        </Button>
        <p>{props.controller.result}</p>
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(App as FunctionComponent<PublicProps>)
);
