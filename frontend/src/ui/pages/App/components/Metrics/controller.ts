import { action, observable } from 'mobx';
import { Metrics } from '../../../../../services/prediction';

export default class MetricsController {
  @observable metrics?: Metrics;

  @action.bound
  public formData = (metrics: Metrics) => {
    this.metrics = metrics;
  };
}
