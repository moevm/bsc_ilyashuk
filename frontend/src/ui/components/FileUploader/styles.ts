import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    uploader: {
      width: '40%',
    },
    uploadButton: {
      marginTop: '10px',
    },
  })
);

export default useStyles;
