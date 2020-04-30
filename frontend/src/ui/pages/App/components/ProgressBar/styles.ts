import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      width: '50vmax',
      marginTop: '30px',
    },
  })
);

export default useStyles;
