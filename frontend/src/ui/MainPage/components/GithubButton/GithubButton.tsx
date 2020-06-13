import { IconButton } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import MainController from '../../../../controllers/MainPage/MainPage';
import github from '../../assets/github.png';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const GithubButton: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();
  return (
    <IconButton
      className={classes.githubButton}
      onClick={props.controller.openGithub}
    >
      <img src={github} width={40} />
    </IconButton>
  );
};

export default inject('controller')(
  observer(GithubButton as FunctionComponent<PublicProps>)
);
