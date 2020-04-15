import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    app: {
      backgroundColor: '#282c34',
      minHeight: '100vh',
      flexDirection: 'column',
    },
    header: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: 'calc(10px + 2vmin)',
      color: 'white',
    },
  })
);

export default useStyles;
