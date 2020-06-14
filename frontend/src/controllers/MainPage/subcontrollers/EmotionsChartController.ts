import { action, observable } from 'mobx';
import { labels } from '../../../config/labels';
import { TimePrediction } from '../../../services/prediction';

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
  public formData = (predictions: TimePrediction[]) => {
    const res: any[] = [];
    predictions.forEach((prediction: TimePrediction) => {
      var chartData: any = {};
      chartData.time = prediction.timeFrom;
      labels.forEach((label, labelIndex) => {
        chartData[label] = prediction.prediction[labelIndex].toFixed(3);
      });
      res.push(chartData);
    });
    this.chartData = res;
  };
}