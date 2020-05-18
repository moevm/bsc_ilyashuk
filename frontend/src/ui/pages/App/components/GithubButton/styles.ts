import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    githubButton: {
      position: 'absolute',
      right: '10px',
      top: '10px',
    },
  })
);

export default useStyles;
