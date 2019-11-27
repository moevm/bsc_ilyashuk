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

Использование [inaSpeechSegmenter](https://github.com/ina-foss/inaSpeechSegmenter)

```bash
python ina_speech_segmenter.py -i input_files -o output_directory -d smn -g true

```

Аргументы

```bash
  -i INPUT [INPUT ...], --input INPUT [INPUT ...]
                        Input media to analyse. May be a full path to a media
                        (/home/david/test.mp3), a list of full paths
                        (/home/david/test.mp3 /tmp/mymedia.avi), or a regex
                        input pattern ("/home/david/myaudiobooks/*.mp3")
  -o OUTPUT_DIRECTORY, --output_directory OUTPUT_DIRECTORY
                        Directory used to store segmentations. Resulting
                        segmentations have same base name as the corresponding
                        input media, with csv extension. Ex: mymedia.MPG will
                        result in mymedia.csv
  -d {sm,smn}, --vad_engine {sm,smn}
                        Voice activity detection (VAD) engine to be used
                        (default: 'smn'). 'smn' split signal into 'speech',
                        'music' and 'noise' (better). 'sm' split signal into
                        'speech' and 'music' and do not take noise into
                        account, which is either classified as music or
                        speech. Results presented in ICASSP were obtained
                        using 'sm' option
  -g {true,false}, --detect_gender {true,false}
                        (default: 'true'). If set to 'true', segments detected
                        as speech will be splitted into 'male' and 'female'
                        segments. If set to 'false', segments corresponding to
                        speech will be labelled as 'speech' (faster)
```
