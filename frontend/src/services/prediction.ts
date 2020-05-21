import axios from 'axios';

export type TimePrediction = { time: number; pred: number[] };
export type Metrics = { totalVolume: number; volumes: number[] };

export default class PredictionService {
  public static predict = async (
    file: File,
    onProgress: (progress: ProgressEvent) => void
  ): Promise<{ predictions: TimePrediction[]; metrics: Metrics }> => {
    const formData = new FormData();
    formData.append('file', file);

    const url = 'https://bsc-ilyashuk.herokuapp.com/predict';

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
