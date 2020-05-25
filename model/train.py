import os
import shutil
from pathlib import Path

import joblib
import keras
import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.model_selection import train_test_split

from config import MODEL_DIR_PATH, SAVE_DIR_PATH


def train_neural_network(X, y):
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.33, random_state=42)

    x_traincnn = np.expand_dims(X_train, axis=2)
    x_testcnn = np.expand_dims(X_test, axis=2)

    print(x_traincnn.shape, x_testcnn.shape)

    model = keras.Sequential([
        keras.layers.Conv1D(64, 5, padding='same',
                            input_shape=(40, 1), name='input'),
        keras.layers.Activation('relu'),
        keras.layers.Dropout(0.2),
        keras.layers.Flatten(),
        keras.layers.Dense(8),
        keras.layers.Activation('softmax', name='output')
    ])

    model.compile(loss='sparse_categorical_crossentropy',
                  optimizer='rmsprop',
                  metrics=['accuracy'])

    print(model.summary())

    cnn_history = model.fit(x_traincnn, y_train,
                            batch_size=16, epochs=50,
                            validation_data=(x_testcnn, y_test))

    # Loss plotting
    plt.plot(cnn_history.history['loss'])
    plt.plot(cnn_history.history['val_loss'])
    plt.title('Функция ошибки')
    plt.ylabel('Ошибка')
    plt.xlabel('Эпоха')
    plt.legend(['train', 'test'], loc='upper left')
    plt.savefig('plots/loss.png')
    plt.close()

    # Accuracy plotting
    plt.plot(cnn_history.history['accuracy'])
    plt.plot(cnn_history.history['val_accuracy'])
    plt.title('Точность модели')
    plt.ylabel('Точность')
    plt.xlabel('Эпоха')
    plt.legend(['train', 'test'], loc='upper left')
    plt.savefig('plots/accuracy.png')

    predictions = model.predict_classes(x_testcnn)
    new_y_test = y_test.astype(int)
    matrix = confusion_matrix(new_y_test, predictions)

    print(classification_report(new_y_test, predictions))
    print(matrix)

    # Remove model save folder if exists
    dirpath = Path('model')
    if dirpath.exists() and dirpath.is_dir():
        shutil.rmtree(dirpath)

    # Save Keras model
    if not os.path.isdir(MODEL_DIR_PATH):
        os.makedirs(MODEL_DIR_PATH)
    model_path = os.path.join(MODEL_DIR_PATH, 'model.h5')
    model.save(model_path)

    # Save in SavedModel format
    with tf.keras.backend.get_session() as sess:
        tf.saved_model.simple_save(
            sess,
            'model/saved_model',
            inputs={'input': model.input},
            outputs={'output': model.output})


if __name__ == '__main__':
    X = joblib.load(SAVE_DIR_PATH + '/X.joblib')
    y = joblib.load(SAVE_DIR_PATH + '/y.joblib')
    train_neural_network(X=X, y=y)
