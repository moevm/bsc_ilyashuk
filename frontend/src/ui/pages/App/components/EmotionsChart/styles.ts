import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      display: 'flex',
      marginTop: '50px',
      marginBottom: '100px',
    },
  })
);

export default useStyles;
