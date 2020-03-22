import csv

import librosa
import numpy as np
import pandas as pd

labels = [
    'male_calm',
    'male_sad',
    'male_fearful',
    'female_calm',
    'male_fearful',
    'male_happy',
    'female_calm',
    'female_fearful',
    'male_angry',
    'male_happy',
]


def get_raw_files():
    base_folder = '../raw/'
    all_files = []
    dirs = listdir(base_folder)
    for dir in dirs:
        files = listdir(base_folder + dir)
        for file in files:
            all_files.append(base_folder + dir + '/' + file)
    return all_files


def listdir(path):
    import os
    return [f for f in os.listdir(path) if not f.startswith('.')]


def load_model():
    from keras.models import model_from_json
    json_file = open('../model.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    loaded_model.load_weights('../model.h5')
    print('Loaded model')
    return loaded_model


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
    raw_files = get_raw_files()

    features = extract_features(raw_files)

    test_data = np.expand_dims(np.array(features), axis=2)

    model = load_model()

    predictions = model.predict(test_data,
                                batch_size=32,
                                verbose=1)

    predictions_labels = [labels[p] for p in predictions.argmax(axis=1)]

    with open('../result.csv', mode='w') as csv_file:
        writer = csv.writer(csv_file, delimiter=',',
                            quotechar='"', quoting=csv.QUOTE_MINIMAL)

        writer.writerows([[file.split('/')[-1], predictions_labels[index], predictions.argmax(axis=1)[index]]
                          for index, file in enumerate(raw_files)])
