import { FormControl, InputLabel, MenuItem, Select } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { labels } from '../../../../../../../config/labels';
import MainController from '../../../../controller';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const EmotionsFilter: FunctionComponent<PrivateProps> = (
  props: PrivateProps
) => {
  const classes = useStyles();

  return (
    <div className={classes.container}>
      <FormControl variant='filled' className={classes.formControl}>
        <InputLabel className={classes.whiteText}>Filter</InputLabel>
        <Select
          value={props.controller.chart.selectedFilterIndex}
          onChange={props.controller.chart.onFilterSelected}
          className={classes.whiteText}
        >
          <MenuItem value={-1}>All</MenuItem>
          {labels.map((label, index) => (
            <MenuItem key={index} value={index}>
              {label}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    </div>
  );
};

export default inject('controller')(
  observer(EmotionsFilter as FunctionComponent<PublicProps>)
);
