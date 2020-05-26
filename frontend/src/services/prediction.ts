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

    const url = 'http://0.0.0.0:8080/predict';

    const config = {
      headers: {
        'content-type': 'multipart/form-data',
      },
      onUploadProgress: onProgress,
    };

    const response = await axios.post(url, formData, config);

    console.log(JSON.stringify(response.data));
    return response.data;
  };
}
