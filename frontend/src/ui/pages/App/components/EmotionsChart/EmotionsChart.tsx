import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import {
  Brush,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { labelColors, labels } from '../../../../../config/labels';
import { primaryColor } from '../../../../../config/style';
import MainController from '../../controller';
import EmotionsFilter from './components/EmotionsFilter/EmotionsFilter';
import useStyles from './styles';

type PublicProps = {};

type PrivateProps = {
  controller: MainController;
} & PublicProps;

const EmotionsChart: FunctionComponent<PrivateProps> = (
  props: PrivateProps
) => {
  const classes = useStyles();
  const chartController = props.controller.chart;

  return (
    <div className={classes.container}>
      {chartController.chartData.length != 0 ? (
        <>
          <EmotionsFilter />
          <LineChart
            width={1000}
            height={400}
            data={chartController.chartData}
            margin={{
              top: 5,
              right: 30,
              left: 20,
              bottom: 5,
            }}
          >
            {props.controller.chart.selectedFilterIndex === -1 ? (
              labels.map((element, index) => (
                <Line
                  type='monotone'
                  dataKey={element}
                  stroke={labelColors[index]}
                  key={index}
                />
              ))
            ) : (
              <Line
                type='monotone'
                dataKey={labels[props.controller.chart.selectedFilterIndex]}
                stroke={labelColors[props.controller.chart.selectedFilterIndex]}
              />
            )}

            <CartesianGrid strokeDasharray='3 3' />

            <Legend />
            <Brush dataKey='time' height={30} stroke={primaryColor} />
            <Tooltip />
            <XAxis dataKey='time' />
            <YAxis />
          </LineChart>
        </>
      ) : null}
    </div>
  );
};

export default inject('controller')(
  observer(EmotionsChart as FunctionComponent<PublicProps>)
);
