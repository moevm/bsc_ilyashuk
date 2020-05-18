import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
} from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { labels } from '../../../../../config/labels';
import MainController from '../../controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const Metrics: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();

  const controller = props.controller.metrics;

  return controller.metrics != undefined ? (
    <TableContainer className={classes.container} component={Paper}>
      <Table aria-label='simple table'>
        <TableHead>
          <TableRow className={classes.row}>
            <TableCell />
            {labels.map((label, index) => (
              <TableCell key={index} align='right' className={classes.cell}>
                {label}
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow className={classes.row}>
            <TableCell component='th' scope='row' className={classes.cell}>
              Метрика
            </TableCell>
            {controller.metrics.volumes.map((volume, index) => (
              <TableCell key={index} align='right' className={classes.cell}>
                {volume}
              </TableCell>
            ))}
          </TableRow>
        </TableBody>
      </Table>
    </TableContainer>
  ) : null;
};

export default inject('controller')(
  observer(Metrics as FunctionComponent<PublicProps>)
);
