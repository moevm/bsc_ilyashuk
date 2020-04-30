import axios from 'axios';

export default class PredictionService {
  public static predict = async (
    file: File,
    onProgress: (progress: ProgressEvent) => void
  ): Promise<number[][]> => {
    const formData = new FormData();
    formData.append('file', file);

    const url = 'http://64.225.111.172:8080/predict';

    const config = {
      headers: {
        'content-type': 'multipart/form-data',
      },
      onUploadProgress: onProgress,
    };

    const response = await axios.post(url, formData, config);
    return (response.data as unknown) as number[][];
  };
}
