import os
import re
import time

import joblib
import librosa
import numpy as np

from config import (SAVE_DIR_PATH, SAVEE_ORIGINAL_FOLDER_PATH,
                    TRAINING_FILES_PATH)


def create_features(path, save_dir):
    lst = []

    start_time = time.time()

    for subdir, dirs, files in os.walk(path):

        for file in files:
            # Skip .DS_Store, songs and calm emotion
            if(file[0] == '.' or int(file[3:5]) == 2 or int(file[6:8]) == 2):
                print('Skip file ' + file)
                continue
            print(file)
            try:

                X, sample_rate = librosa.load(os.path.join(subdir, file),
                                              res_type='kaiser_fast')

                mfccs = np.mean(librosa.feature.mfcc(y=X, sr=sample_rate,
                                                     n_mfcc=40).T, axis=0)

                # Перевод лейблов из 1-8 в 0-7
                file = int(file[7:8]) - 1
                if file > 0:
                    file = file - 1
                arr = mfccs, file
                lst.append(arr)
            except Exception as err:
                print(err)
                continue

    label_conversion = {'n': 0,
                        'h': 1,
                        'sa': 2,
                        'a': 3,
                        'f': 4,
                        'd': 5,
                        'su': 6}

    for subdir, dirs, files in os.walk(SAVEE_ORIGINAL_FOLDER_PATH):
        for filename in files:
            r = re.compile("([a-zA-Z]+)([0-9]+)")
            match = r.match(filename)
            if match:
                X, sample_rate = librosa.load(os.path.join(subdir, filename),
                                              res_type='kaiser_fast')

                mfccs = np.mean(librosa.feature.mfcc(y=X, sr=sample_rate,
                                                     n_mfcc=40).T, axis=0)

                arr = mfccs, label_conversion[match.group(1)]
                lst.append(arr)
                print(filename)

    print("Data loaded. Loading time: %s seconds" %
          (time.time() - start_time))

    X, y = zip(*lst)

    X, y = np.asarray(X), np.asarray(y)

    print(X.shape, y.shape)
    X_name, y_name = 'X.joblib', 'y.joblib'

    joblib.dump(X, os.path.join(save_dir, X_name))
    joblib.dump(y, os.path.join(save_dir, y_name))


if __name__ == '__main__':
    create_features(path=TRAINING_FILES_PATH, save_dir=SAVE_DIR_PATH)
    print('Completed')
