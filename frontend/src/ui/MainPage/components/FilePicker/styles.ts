import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { primaryColor } from '../../../../config/style';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      width: '90vmax',
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      marginTop: '50px',
    },
    fileInput: {
      border: 'solid',
      borderWidth: 2,
      borderColor: primaryColor,
      borderRadius: 2,
      padding: '15px',
    },
    uploadButton: {
      marginTop: '15px',
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
