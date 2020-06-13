import {
  Button,
  FormControl,
  InputLabel,
  MenuItem,
  Select,
} from '@material-ui/core';
import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import MainController from '../../../../controllers/MainPage/MainPageController';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const FilePicker: FunctionComponent<PrivateProps> = (props: PrivateProps) => {
  const classes = useStyles();

  const controller = props.controller.fileController;
  return (
    <div className={classes.container}>
      <div className={classes.row}>
        <input
          type='file'
          onChange={controller.onAttachFile}
          accept='audio/*'
          className={classes.fileInput}
        />
        <FormControl variant='filled' className={classes.formControl}>
          <InputLabel className={classes.whiteText}>
            Длина отрезка (сек)
          </InputLabel>
          <Select
            value={controller.selectedChunkLength}
            onChange={controller.onChunkLengthChanged}
            className={classes.whiteText}
          >
            {[...Array(9)].map((value, index) => (
              <MenuItem key={index} value={1 + index / 2}>
                {1 + index / 2}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </div>

      <Button
        onClick={props.controller.uploadAttachment}
        variant='contained'
        color='primary'
        className={classes.uploadButton}
      >
        Анализировать
      </Button>
    </div>
  );
};

export default inject('controller')(
  observer(FilePicker as FunctionComponent<PublicProps>)
);
