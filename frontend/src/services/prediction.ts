import axios from 'axios';

export type TimePrediction = { time: number; pred: number[] };

export default class PredictionService {
  public static predict = async (
    file: File,
    onProgress: (progress: ProgressEvent) => void
  ): Promise<TimePrediction[]> => {
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
    return response.data.predictions;
  };
}