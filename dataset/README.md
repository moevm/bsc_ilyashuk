# Dataset

Для работы скриптов требуется [ffmpeg](https://www.ffmpeg.org/).

1. audio_downloader - загрузка аудиодорожек с youtube.
2. ina_speech_segmenter - сегментация аудио на сегменты с голосом/музыкой/тишиной, на выходе получаем csv файл.
3. splitter - разбивает wav файл на фрагменты согласно полученному на шаге 2 csv файлу.
4. sound_to_noise_ratio - вычисления sound to noise ratio (попытка оценить качество собранных материалов).

Процедура установки [inaSpeechSegmenter](https://github.com/ina-foss/inaSpeechSegmenter)

```bash
virtualenv -p python3 inaSpeechSegEnv
source inaSpeechSegEnv/bin/activate
pip install tensorflow
pip install inaSpeechSegmenter
```
