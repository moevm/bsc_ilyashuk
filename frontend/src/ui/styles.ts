import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { backgroundColor } from '../config/style';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      backgroundColor: backgroundColor,
      minHeight: '100vh',
      flexDirection: 'column',
    },
    header: {
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      color: 'white',
    },
  })
);

export default useStyles;
