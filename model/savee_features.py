"""
Feature extraction SAVEE dataset
"""

import os
import random
import re
import shutil

import joblib
import librosa
import numpy as np

from config import SAVEE_ORIGINAL_FOLDER_PATH


def create_savee_folders(path):
    label_conversion = {'n': 0,
                        'h': 2,
                        'sa': 3,
                        'a': 4,
                        'f': 5,
                        'd': 6,
                        'su': 7}

    lst = []

    for subdir, dirs, files in os.walk(path):
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
                print(str(match.group(1)) + ' ' +
                      str(label_conversion[match.group(1)]))

    X, y = zip(*lst)
    X, y = np.asarray(X), np.asarray(y)

    print(X.shape, y.shape)
    X_name, y_name = 'X.joblib', 'y.joblib'

    joblib.dump(X, os.path.join('features/savee', X_name))
    joblib.dump(y, os.path.join('features/savee', y_name))


if __name__ == '__main__':
    create_savee_folders(SAVEE_ORIGINAL_FOLDER_PATH)
