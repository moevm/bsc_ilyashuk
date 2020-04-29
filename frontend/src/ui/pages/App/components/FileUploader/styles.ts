import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      width: '30vw',
      display: 'flex',
      flexDirection: 'row',
      alignItems: 'center',
      justifyContent: 'center',
      marginTop: '10px',
    },
    uploadButton: {
      marginRight: '10px',
    },
    progressBar: {
      marginTop: '10px',
    },
  })
);

export default useStyles;
