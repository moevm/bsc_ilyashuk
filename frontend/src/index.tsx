import { Provider } from 'mobx-react';
import React from 'react';
import ReactDOM from 'react-dom';
import MainController from './controllers/MainPage/MainPageController';
import './index.css';
import MainPage from './ui/MainPage/MainPage';

const storage = {
  controller: new MainController(),
};

ReactDOM.render(
  <Provider {...storage}>
    <MainPage />
  </Provider>,
  document.getElementById('root')
);
