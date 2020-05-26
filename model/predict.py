import joblib
import keras
import librosa
import numpy as np

from config import MODEL_DIR_PATH, SAVE_DIR_PATH

# label_conversion = {'0': 'neutral',
#                             '1': 'calm',
#                             '2': 'happy',
#                             '3': 'sad',
#                             '4': 'angry',
#                             '5': 'fearful',
#                             '6': 'disgust',
#                             '7': 'surprised'}


if __name__ == '__main__':
    path = MODEL_DIR_PATH + 'model.h5'
    loaded_model = keras.models.load_model(path)

    X = joblib.load(SAVE_DIR_PATH + '/X.joblib')
    y = joblib.load(SAVE_DIR_PATH + '/y.joblib')

    counter = 0

    for index, x in enumerate(X):
        x = np.expand_dims(x, axis=2)
        x = np.expand_dims(x, axis=0)
        predictions = loaded_model.predict_classes(x)

        if predictions[0] == y[index]:
            counter = counter + 1

        X = joblib.load(SAVE_DIR_PATH + '/X.joblib')
    y = joblib.load(SAVE_DIR_PATH + '/y.joblib')

    print(counter / len(X))

    X = joblib.load(SAVE_DIR_PATH + '/savee/X.joblib')
    y = joblib.load(SAVE_DIR_PATH + '/savee/y.joblib')

    savee_counter = 0

    for index, x in enumerate(X):
        x = np.expand_dims(x, axis=2)
        x = np.expand_dims(x, axis=0)
        predictions = loaded_model.predict_classes(x)

        if predictions[0] == y[index]:
            savee_counter = savee_counter + 1

    print(savee_counter / len(X))
