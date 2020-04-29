import axios from 'axios';

export default class PredictionService {
  public static predict = async (file: File): Promise<number[][]> => {
    const formData = new FormData();
    formData.append('file', file);

    const url = 'http://localhost:8080/predict';

    const config = {
      headers: {
        'content-type': 'multipart/form-data',
      },
    };

    const response = await axios.post(url, formData, config);
    return (response.data as unknown) as number[][];
  };
}
