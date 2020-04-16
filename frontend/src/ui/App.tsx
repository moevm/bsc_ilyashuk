import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import Controller from '../controller';
import FileUploader from './components/FileUploader/FileUploader';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: Controller;
} & PublicProps;

const App: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <div className={classes.container}>
      <header className={classes.header}>
        <FileUploader />
      </header>
    </div>
  );
};

export default inject('controller')(
  observer(App as FunctionComponent<PublicProps>)
);
