import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import {
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import Controller from '../../../controller';
import { labelColors, labels } from '../../../labels';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: Controller;
} & PublicProps;

const EmotionsChart: FunctionComponent<PrivateProps> = (
  props: PrivateProps
) => {
  const classes = useStyles();

  return (
    <div className={classes.container}>
      <LineChart
        width={1000} //TODO: Fix size
        height={400}
        data={props.controller.chartData}
        margin={{
          top: 5,
          right: 30,
          left: 20,
          bottom: 5,
        }}
      >
        {labels.map((element, index) => (
          <Line
            type='monotone'
            dataKey={element}
            stroke={labelColors[index]}
            key={index}
          />
        ))}

        <CartesianGrid strokeDasharray='3 3' />
        <XAxis dataKey='time' />
        <YAxis />

        <Tooltip />
        <Legend />
      </LineChart>
    </div>
  );
};

export default inject('controller')(
  observer(EmotionsChart as FunctionComponent<PublicProps>)
);
