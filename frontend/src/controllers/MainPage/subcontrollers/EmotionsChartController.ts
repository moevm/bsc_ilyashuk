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

    var zeroChartData: any = { time: 0 };
    labels.forEach((label) => {
      zeroChartData[label] = 0;
    });
    res.push(zeroChartData);
    predictions.forEach((prediction: TimePrediction) => {
      var chartData: any = {};
      chartData.time = ((prediction.timeTo + prediction.timeFrom) / 2).toFixed(
        2
      );
      labels.forEach((label, labelIndex) => {
        chartData[label] = prediction.prediction[labelIndex].toFixed(3);
      });
      res.push(chartData);
    });
    zeroChartData = {
      time: predictions[predictions.length - 1].timeTo.toFixed(2),
    };
    labels.forEach((label) => {
      zeroChartData[label] = 0;
    });
    res.push(zeroChartData);
    this.chartData = res;
  };
}
