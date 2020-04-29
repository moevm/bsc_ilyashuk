import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { primaryColor } from '../../../../../config/style';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      marginTop: '10vh',
    },
    audio: {
      border: 'solid',
      borderWidth: 2,
      borderColor: primaryColor,
      borderRadius: 2,
    },
    buttons: {
      marginTop: 10,
      flexDirection: 'row',
      display: 'flex',
      flex: 1,
      justifyContent: 'center',
      alignItems: 'center',
    },
  })
);

export default useStyles;
