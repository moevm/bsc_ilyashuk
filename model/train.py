import os

import joblib
import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.model_selection import train_test_split
from tensorflow import keras

from config import MODEL_DIR_PATH, SAVE_DIR_PATH


class TrainModel:

    @staticmethod
    def train_neural_network(X, y) -> str:
        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.33, random_state=42)

        x_traincnn = np.expand_dims(X_train, axis=2)
        x_testcnn = np.expand_dims(X_test, axis=2)

        print(x_traincnn.shape, x_testcnn.shape)

        model = tf.keras.Sequential([
            tf.keras.layers.Conv1D(64, 5, padding='same',
                                   input_shape=(40, 1), name='input'),
            tf.keras.layers.Activation('relu'),
            tf.keras.layers.Dropout(0.2),
            tf.keras.layers.Flatten(),
            tf.keras.layers.Dense(8),
            tf.keras.layers.Activation('softmax', name='output')
        ])

        print(model.summary)

        model.compile(loss='sparse_categorical_crossentropy',
                      optimizer='rmsprop',
                      metrics=['accuracy'])

        cnn_history = model.fit(x_traincnn, y_train,
                                batch_size=16, epochs=50,
                                validation_data=(x_testcnn, y_test))

        # # Loss plotting
        # plt.plot(cnn_history.history['loss'])
        # plt.plot(cnn_history.history['val_loss'])
        # plt.title('model loss')
        # plt.ylabel('loss')
        # plt.xlabel('epoch')
        # plt.legend(['train', 'test'], loc='upper left')
        # plt.savefig('loss.png')
        # plt.close()

        # # Accuracy plotting
        # plt.plot(cnn_history.history['accuracy'])
        # plt.plot(cnn_history.history['val_accuracy'])
        # plt.title('model accuracy')
        # plt.ylabel('acc')
        # plt.xlabel('epoch')
        # plt.legend(['train', 'test'], loc='upper left')
        # plt.savefig('accuracy.png')

        predictions = model.predict_classes(x_testcnn)
        new_y_test = y_test.astype(int)
        matrix = confusion_matrix(new_y_test, predictions)

        print(classification_report(new_y_test, predictions))
        print(matrix)

        model_name = 'model.h5'

        # Save Keras model
        if not os.path.isdir(MODEL_DIR_PATH):
            os.makedirs(MODEL_DIR_PATH)
        model_path = os.path.join(MODEL_DIR_PATH, model_name)
        model.save(model_path)

        print(model.input)
        print(model.output)

        # Save in SavedModel format
        with tf.keras.backend.get_session() as sess:
            tf.saved_model.simple_save(
                sess,
                'saved_model',
                inputs={'input': model.input},
                outputs={'output': model.output})


if __name__ == '__main__':
    X = joblib.load(SAVE_DIR_PATH + '/X.joblib')
    y = joblib.load(SAVE_DIR_PATH + '/y.joblib')
    NEURAL_NET = TrainModel.train_neural_network(X=X, y=y)
