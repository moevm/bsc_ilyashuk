import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { primaryColor } from '../../../config/style';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {},
    audio: {
      border: 'solid',
      borderWidth: 2,
      borderColor: primaryColor,
      borderRadius: 2,
    },
    buttons: {
      marginTop: 10,
      flexDirection: 'row',
    },
  })
);

export default useStyles;
