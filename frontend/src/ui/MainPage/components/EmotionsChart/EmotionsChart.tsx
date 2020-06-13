import { inject, observer } from 'mobx-react';
import React, { FunctionComponent } from 'react';
import {
  Brush,
  CartesianGrid,
  Label,
  Legend,
  Line,
  LineChart,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import { labelColors, labels } from '../../../../config/labels';
import { primaryColor } from '../../../../config/style';
import MainController from '../../../../controllers/MainPage/MainPage';
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
                  strokeWidth={1.5}
                  type='monotone'
                  dataKey={element}
                  stroke={labelColors[index]}
                  key={index}
                />
              ))
            ) : (
              <Line
                type='monotone'
                strokeWidth={1.5}
                dataKey={labels[props.controller.chart.selectedFilterIndex]}
                stroke={labelColors[props.controller.chart.selectedFilterIndex]}
              />
            )}

            <CartesianGrid strokeDasharray='1 1' />

            <Legend />
            <Brush dataKey='time' height={30} stroke={primaryColor} />
            <Tooltip />
            <XAxis dataKey='time' height={50}>
              <Label
                value='Время, с.'
                position='insideBottom'
                style={{ fill: 'grey' }}
              />
            </XAxis>
            <YAxis>
              <Label
                color='#FFFFFF'
                value='Вероятность'
                position='insideLeft'
                angle={-90}
                style={{
                  textAnchor: 'middle',
                  fill: 'grey',
                }}
              />
            </YAxis>
          </LineChart>
        </>
      ) : null}
    </div>
  );
};

export default inject('controller')(
  observer(EmotionsChart as FunctionComponent<PublicProps>)
);
