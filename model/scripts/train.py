import os

import keras
import librosa
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
from keras.layers import (Activation, AveragePooling1D, Conv1D, Dense, Dropout,
                          Embedding, Flatten, Input, MaxPooling1D)
from keras.models import Model, Sequential
from keras.utils import np_utils
from sklearn.preprocessing import LabelEncoder
from sklearn.utils import shuffle

if __name__ == "__main__":
    files = os.listdir('../raw/')

    feeling_list = []
    for item in files:
        if item[6:-16] == '02' and int(item[18:-4]) % 2 == 0:
            feeling_list.append('female_calm')
        elif item[6:-16] == '02' and int(item[18:-4]) % 2 == 1:
            feeling_list.append('male_calm')
        elif item[6:-16] == '03' and int(item[18:-4]) % 2 == 0:
            feeling_list.append('female_happy')
        elif item[6:-16] == '03' and int(item[18:-4]) % 2 == 1:
            feeling_list.append('male_happy')
        elif item[6:-16] == '04' and int(item[18:-4]) % 2 == 0:
            feeling_list.append('female_sad')
        elif item[6:-16] == '04' and int(item[18:-4]) % 2 == 1:
            feeling_list.append('male_sad')
        elif item[6:-16] == '05' and int(item[18:-4]) % 2 == 0:
            feeling_list.append('female_angry')
        elif item[6:-16] == '05' and int(item[18:-4]) % 2 == 1:
            feeling_list.append('male_angry')
        elif item[6:-16] == '06' and int(item[18:-4]) % 2 == 0:
            feeling_list.append('female_fearful')
        elif item[6:-16] == '06' and int(item[18:-4]) % 2 == 1:
            feeling_list.append('male_fearful')
        elif item[:1] == 'a':
            feeling_list.append('male_angry')
        elif item[:1] == 'f':
            feeling_list.append('male_fearful')
        elif item[:1] == 'h':
            feeling_list.append('male_happy')
        elif item[:2] == 'sa':
            feeling_list.append('male_sad')

    labels = pd.DataFrame(feeling_list)

    df = pd.DataFrame(columns=['feature'])

    for index, y in enumerate(files):
        if files[index][6:-16] != '01' and files[index][6:-16] != '07' and files[index][6:-16] != '08' and files[index][:2] != 'su' and files[index][:1] != 'n' and files[index][:1] != 'd':
            X, sample_rate = librosa.load(
                '../raw/'+y, res_type='kaiser_fast', duration=2.5, sr=22050*2, offset=0.5)
            sample_rate = np.array(sample_rate)
            mfccs = np.mean(librosa.feature.mfcc(y=X,
                                                 sr=sample_rate,
                                                 n_mfcc=13),
                            axis=0)
            feature = mfccs
            df.loc[index] = [feature]

    df3 = pd.DataFrame(df['feature'].values.tolist())

    newdf = pd.concat([df3, labels], axis=1)

    rnewdf = newdf.rename(index=str, columns={"0": "label"})

    rnewdf = shuffle(newdf)

    rnewdf = rnewdf.fillna(0)

    newdf1 = np.random.rand(len(rnewdf)) < 0.8
    train = rnewdf[newdf1]
    test = rnewdf[~newdf1]

    trainfeatures = train.iloc[:, :-1]
    trainlabel = train.iloc[:, -1:]
    testfeatures = test.iloc[:, :-1]
    testlabel = test.iloc[:, -1:]

    X_train = np.array(trainfeatures)
    y_train = np.array(trainlabel)
    X_test = np.array(testfeatures)
    y_test = np.array(testlabel)

    lb = LabelEncoder()

    y_train = np_utils.to_categorical(lb.fit_transform(y_train))
    y_test = np_utils.to_categorical(lb.fit_transform(y_test))

    x_traincnn = np.expand_dims(X_train, axis=2)
    x_testcnn = np.expand_dims(X_test, axis=2)

    model = Sequential()

    model.add(Conv1D(256, 5, padding='same',
                     input_shape=(216, 1)))
    model.add(Activation('relu'))
    model.add(Conv1D(128, 5, padding='same'))
    model.add(Activation('relu'))
    model.add(Dropout(0.1))
    model.add(MaxPooling1D(pool_size=(8)))
    model.add(Conv1D(128, 5, padding='same',))
    model.add(Activation('relu'))
    model.add(Conv1D(128, 5, padding='same',))
    model.add(Activation('relu'))
    model.add(Flatten())
    model.add(Dense(10))
    model.add(Activation('softmax'))
    opt = keras.optimizers.rmsprop(lr=0.00001, decay=1e-6)

    model.compile(loss='categorical_crossentropy',
                  optimizer=opt, metrics=['accuracy'])
    cnnhistory = model.fit(x_traincnn, y_train, batch_size=16,
                           epochs=700, validation_data=(x_testcnn, y_test))

    model.save('../trained_model.h5')
