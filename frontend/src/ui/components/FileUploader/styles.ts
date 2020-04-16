import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      width: '40%',
      display: 'flex',
      flexDirection: 'column',
      flex: 1,
    },
    uploadButton: {
      width: '10vh',
      marginTop: '10px',
    },
    progressBar: {
      marginTop: '10px',
    },
  })
);

export default useStyles;
