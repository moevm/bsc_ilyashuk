import { Provider } from 'mobx-react';
import React from 'react';
import ReactDOM from 'react-dom';
import Controller from './controller';
import './index.css';
import App from './ui/App';

const storage = {
  controller: new Controller(),
};

ReactDOM.render(
  <React.StrictMode>
    <Provider {...storage}>
      <App />
    </Provider>
  </React.StrictMode>,
  document.getElementById('root')
);
