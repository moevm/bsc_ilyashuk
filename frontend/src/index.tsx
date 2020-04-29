import { Provider } from 'mobx-react';
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './ui/pages/App/App';
import MainController from './ui/pages/App/controller';

const storage = {
  controller: new MainController(),
};

ReactDOM.render(
  <Provider {...storage}>
    <App />
  </Provider>,
  document.getElementById('root')
);
