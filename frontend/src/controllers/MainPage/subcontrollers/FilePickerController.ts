import { action, observable } from 'mobx';

export default class FilePickerController {
  @observable
  public selectedChunkLength = 3;

  public file?: File;

  public onAttachFile = (e: any) => {
    this.file = e.target.files[0];
  };

  @action.bound
  public onChunkLengthChanged = (
    event: React.ChangeEvent<{ value: unknown }>
  ) => {
    this.selectedChunkLength = event.target.value as number;
  };
}
