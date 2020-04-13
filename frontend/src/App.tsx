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
        <p>{props.controller.counter}</p>
        <button onClick={props.controller.increment}>Increment</button>
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(App as FunctionComponent<PublicProps>)
);
