import { action, observable } from 'mobx';
import { labels } from '../../../../../config/labels';

export default class EmotionsChartController {
  @observable
  public chartData: any[] = [];

  @action.bound
  public formChartData = (predictions: number[][]) => {
    const res: any[] = [];
    predictions.forEach((prediction: number[], index: number) => {
      var chartData: any = {};
      chartData.time = index;
      labels.forEach((label, labelIndex) => {
        chartData[label] = prediction[labelIndex];
      });
      res.push(chartData);
    });
    this.chartData = res;
  };
}
