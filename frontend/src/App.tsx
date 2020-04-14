import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import './App.css';
import Controller from './controller';

type PublicProps = {};

type PrivateProps = {
  controller: Controller;
} & PublicProps;

const App: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  return (
    <div className='App'>
      <header className='App-header'>
        <input type='file' onChange={props.controller.onAttachFile} />
        <button type='submit' onClick={props.controller.upload}>
          Upload
        </button>
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(App as FunctionComponent<PublicProps>)
);
