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


if __name__ == '__main__':
    raw_files = get_raw_files()

    df = pd.DataFrame(columns=['feature'])

    for index, file, in enumerate(raw_files[:1000]):
        data, sample_rate = librosa.load(
            file, res_type='kaiser_fast', duration=2.5, sr=22050*2, offset=0.5)

        sample_rate = np.array(sample_rate)

        mfccs = np.mean(librosa.feature.mfcc(y=data,
                                             sr=sample_rate,
                                             n_mfcc=13),
                        axis=0)
        feature = mfccs
        df.loc[index] = [feature]

    df3 = pd.DataFrame(df['feature'].values.tolist())
    X_test = np.array(df3)
    x_testcnn = np.expand_dims(X_test, axis=2)

    model = load_model()

    predictions = model.predict(x_testcnn,
                                batch_size=32,
                                verbose=1)

    predictions_labels = [labels[p] for p in predictions.argmax(axis=1)]

    print(predictions_labels)
