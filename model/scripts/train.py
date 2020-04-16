import librosa
import numpy as np
import pandas as pd
import tensorflow as tf
from sklearn.preprocessing import LabelEncoder
from sklearn.utils import shuffle
from tensorflow import keras

from utils import get_raw_files

if __name__ == "__main__":
    files = get_raw_files('raw')

    items = [item.split('/')[-1] for item in files]

    feeling_list = []
    for item in items:
        if item[6:-16] == '02':
            feeling_list.append('calm')
        elif item[6:-16] == '03':
            feeling_list.append('happy')
        elif item[6:-16] == '04':
            feeling_list.append('sad')
        elif item[6:-16] == '05':
            feeling_list.append('angry')
        elif item[6:-16] == '06':
            feeling_list.append('fearful')
        elif item[:1] == 'a':
            feeling_list.append('angry')
        elif item[:1] == 'f':
            feeling_list.append('fearful')
        elif item[:1] == 'n':
            feeling_list.append('calm')
        elif item[:1] == 'h':
            feeling_list.append('happy')
        elif item[:2] == 'sa':
            feeling_list.append('sad')

    labels = pd.DataFrame(feeling_list)

    df = pd.DataFrame(columns=['feature'])

    for index, y in enumerate(files):
        file = files[index].split('/')[-1]
        if file[6:-16] != '01' and file[6:-16] != '07' and file[6:-16] != '08' and file[:2] != 'su' and file[:1] != 'd':
            X, sample_rate = librosa.load(
                y, res_type='kaiser_fast', duration=2.5, sr=22050*2, offset=0.5)
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

    y_train = tf.keras.utils.to_categorical(lb.fit_transform(y_train))
    y_test = tf.keras.utils.to_categorical(lb.fit_transform(y_test))

    x_traincnn = np.expand_dims(X_train, axis=2)
    x_testcnn = np.expand_dims(X_test, axis=2)

    model = tf.keras.Sequential([
        tf.keras.layers.Conv1D(256, 5, padding='same',
                               input_shape=(216, 1), name='input'),
        tf.keras.layers.Activation('relu'),
        tf.keras.layers.Conv1D(128, 5, padding='same'),
        tf.keras.layers.Activation('relu'),
        tf.keras.layers.Dropout(0.1),
        tf.keras.layers.MaxPooling1D(pool_size=(8)),
        tf.keras.layers.Conv1D(128, 5, padding='same',),
        tf.keras.layers.Activation('relu'),
        tf.keras.layers.Conv1D(128, 5, padding='same',),
        tf.keras.layers.Activation('relu'),
        tf.keras.layers.Flatten(),
        tf.keras.layers.Dense(5),
        tf.keras.layers.Activation('softmax', name='output'),
    ])

    opt = tf.keras.optimizers.RMSprop(lr=0.00001, decay=1e-6)

    model.compile(loss='categorical_crossentropy',
                  optimizer=opt, metrics=['accuracy'])

    # Early stopping
    es = tf.keras.callbacks.EarlyStopping(monitor='val_loss',
                                          min_delta=0,
                                          patience=5,
                                          verbose=1, mode='auto')

    history = model.fit(x_traincnn, y_train, batch_size=16,
                        epochs=700, validation_data=(x_testcnn, y_test), callbacks=[es])

    # Save Keras model
    model.save('model.h5')
    model_json = model.to_json()
    with open('model.json', 'w') as json_file:
        json_file.write(model_json)

    # Save in SavedModel format
    with tf.keras.backend.get_session() as sess:
        tf.saved_model.simple_save(
            sess,
            'saved_model',
            inputs={'input': model.input},
            outputs={'output': model.output})
