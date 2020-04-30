import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { primaryColor } from '../../../../../config/style';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      width: '40vmax',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      marginTop: '30px',
    },
    input: {
      border: 'solid',
      borderWidth: 2,
      borderColor: primaryColor,
      borderRadius: 2,
      padding: '10px',
    },
    uploadButton: {
      marginTop: '10px',
    },
  })
);

export default useStyles;
