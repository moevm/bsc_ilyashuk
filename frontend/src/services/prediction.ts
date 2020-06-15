import axios from 'axios';

export type TimePrediction = {
  timeFrom: number;
  timeTo: number;
  prediction: number[];
};
export type Metrics = { totalVolume: number; volumes: number[] };

export default class PredictionService {
  public static predict = async (
    file: File,
    chunkLength: number,
    onProgress: (progress: ProgressEvent) => void
  ): Promise<{ predictions: TimePrediction[]; metrics: Metrics }> => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('chunkLength', chunkLength.toString());

    const url = 'https://bsc-ilyashuk.herokuapp.com/predict';
    // const url = 'http://0.0.0.0:8080/predict';

    const config = {
      headers: {
        'content-type': 'multipart/form-data',
      },
      onUploadProgress: onProgress,
    };

    const response = await axios.post(url, formData, config);

    return response.data;
  };
}
