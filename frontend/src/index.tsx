import { Provider } from 'mobx-react';
import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import './index.css';

const store = {
  test: 'test',
};

ReactDOM.render(
  <React.StrictMode>
    <Provider {...store}>
      <App />
    </Provider>
  </React.StrictMode>,
  document.getElementById('root')
);
