import librosa
import numpy as np
import pandas as pd
from pydub import AudioSegment
from pydub.silence import detect_nonsilent

labels = [
    'female_angry',
    'female_calm',
    'female_fearful',
    'female_happy',
    'female_sad',
    'male_angry',
    'male_calm',
    'male_fearful',
    'male_happy',
    'male_sad',
]


def split(filepath):
    sound = AudioSegment.from_wav(filepath)
    dBFS = sound.dBFS
    chunks = detect_nonsilent(sound,
                              min_silence_len=300,
                              silence_thresh=dBFS-16)
    return chunks


def load_model():
    from keras.models import model_from_json
    json_file = open('../model.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    loaded_model.load_weights('../model.h5')
    return loaded_model


def extract_features(chunks):
    df = pd.DataFrame(columns=['feature'])
    for index, chunk, in enumerate(chunks):
        sample_rate = np.array(42100)
        mfccs = np.mean(librosa.feature.mfcc(y=chunk,
                                             sr=sample_rate,
                                             n_mfcc=13),
                        axis=0)
        feature = mfccs
        df.loc[index] = [feature]
        if((index + 1) % 100 == 0):
            print('Extracted features from ' + str(index + 1) + ' files')

    return pd.DataFrame(df['feature'].values.tolist())


def divide_chunks(l, n):

    for i in range(0, len(l), n):
        yield l[i:i + n]


if __name__ == "__main__":
    sound = AudioSegment.from_wav('../raw_harvard/OSR_us_000_0010_8k.wav')
    total_length = len(sound)
    data, sampling_rate = librosa.load(
        '../raw_harvard/OSR_us_000_0016_8k.wav', res_type='kaiser_fast', sr=42100)

    data_per_ms = len(data) / total_length
    chunks = list(divide_chunks(data, int(data_per_ms * 2500) + 500))

    features = extract_features(chunks[0: -1])

    test_data = np.expand_dims(np.array(features), axis=2)

    model = load_model()

    predictions = model.predict(test_data,
                                batch_size=32,
                                verbose=1)

    predictions_labels = [labels[p] for p in predictions.argmax(axis=1)]

    print(predictions_labels)
