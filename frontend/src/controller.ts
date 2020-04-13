import { action, observable } from 'mobx';

export default class Controller {
  @observable public counter = 0;

  @action
  public increment = () => {
    this.counter++;
  };
}
