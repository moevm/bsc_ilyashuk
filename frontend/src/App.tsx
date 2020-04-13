import { inject } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import './App.css';

type PublicProps = {};

type PrivateProps = {
  test: string;
} & PublicProps;

const App: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  return (
    <div className='App'>
      <header className='App-header'>
        <p>{props.test}</p>
      </header>
    </div>
  );
};

export default inject('test')(App as FunctionComponent<PublicProps>);
