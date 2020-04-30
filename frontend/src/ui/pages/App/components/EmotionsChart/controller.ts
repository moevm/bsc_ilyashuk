import { action, observable } from 'mobx';
import { labels } from '../../../../../config/labels';

export default class EmotionsChartController {
  @observable
  public selectedFilterIndex = -1;

  @observable
  public chartData: any[] = [];

  @action.bound
  public onFilterSelected = (event: React.ChangeEvent<{ value: unknown }>) => {
    this.selectedFilterIndex = event.target.value as number;
  };

  @action.bound
  public formChartData = (predictions: number[][]) => {
    const res: any[] = [];
    predictions.forEach((prediction: number[], index: number) => {
      var chartData: any = {};
      chartData.time = index * 2.5;
      labels.forEach((label, labelIndex) => {
        chartData[label] = prediction[labelIndex];
      });
      res.push(chartData);
    });
    this.chartData = res;
  };
}
