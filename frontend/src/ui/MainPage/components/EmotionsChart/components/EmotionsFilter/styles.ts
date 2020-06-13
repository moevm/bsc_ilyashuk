import { createStyles, makeStyles, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {},

    formControl: {
      margin: theme.spacing(1),
      minWidth: 120,
    },
    whiteText: {
      color: 'white',
    },
  })
);

export default useStyles;
