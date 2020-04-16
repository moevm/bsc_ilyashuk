import csv

import librosa
import numpy as np
import pandas as pd

from utils import get_raw_files, load_model

labels = [
    'angry',
    'calm',
    'fearful',
    'happy',
    'sad',
]


def extract_features(files):
    df = pd.DataFrame(columns=['feature'])
    for index, file, in enumerate(files):
        data, sample_rate = librosa.load(
            file, res_type='kaiser_fast', duration=2.5, sr=22050*2, offset=0.5)
        sample_rate = np.array(sample_rate)
        mfccs = np.mean(librosa.feature.mfcc(y=data,
                                             sr=sample_rate,
                                             n_mfcc=13),
                        axis=0)
        feature = mfccs
        df.loc[index] = [feature]
        if((index + 1) % 100 == 0):
            print('Extracted features from ' + str(index + 1) + ' files')

    return pd.DataFrame(df['feature'].values.tolist())


if __name__ == '__main__':
    raw_files = get_raw_files('raw/raw_ravdess/')

    features = extract_features(raw_files)

    test_data = np.expand_dims(np.array(features), axis=2)

    model = load_model()

    predictions = model.predict(test_data,
                                batch_size=32,
                                verbose=1)

    predictions_labels = [labels[p] for p in predictions.argmax(axis=1)]

    with open('result_ravdess.csv', mode='w') as csv_file:
        writer = csv.writer(csv_file, delimiter=',',
                            quotechar='"', quoting=csv.QUOTE_MINIMAL)

        writer.writerows([[file.split('/')[-1], predictions_labels[index], predictions.argmax(axis=1)[index]]
                          for index, file in enumerate(raw_files)])
