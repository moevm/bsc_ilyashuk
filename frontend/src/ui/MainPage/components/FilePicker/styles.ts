import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { primaryColor } from '../../../../config/style';

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
    fileInput: {
      border: 'solid',
      borderWidth: 2,
      borderColor: primaryColor,
      borderRadius: 2,
      padding: '15px',
    },
    uploadButton: {
      marginTop: '10px',
    },
    formControl: {
      minWidth: 160,
      marginLeft: '10px',
    },
    whiteText: {
      color: 'white',
    },
    row: {
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
    },
  })
);

export default useStyles;
