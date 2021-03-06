import { FormControl, InputLabel, MenuItem, Select } from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import { labels } from '../../../../../../config/labels';
import MainController from '../../../../../../controllers/MainPage/MainPageController';
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
        <InputLabel className={classes.whiteText}>Фильтр</InputLabel>
        <Select
          value={props.controller.chartController.selectedFilterIndex}
          onChange={props.controller.chartController.onFilterSelected}
          className={classes.whiteText}
        >
          <MenuItem value={-1}>Все</MenuItem>
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
