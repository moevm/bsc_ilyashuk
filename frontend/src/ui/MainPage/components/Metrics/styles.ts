import { createStyles, makeStyles, Theme } from '@material-ui/core';
import { backgroundColorDark } from '../../../../config/style';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    container: {
      width: '60vmax',
      marginBottom: '20vmin',
    },
    cell: {
      color: 'white',
    },
    row: {
      backgroundColor: backgroundColorDark,
    },
  })
);

export default useStyles;
