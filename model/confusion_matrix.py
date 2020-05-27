import itertools
import os

import joblib
import keras
import matplotlib.pyplot as plt
import numpy as np
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.model_selection import train_test_split

from config import MODEL_DIR_PATH, SAVE_DIR_PATH, classes


def plot_confusion_matrix(confusion_matrix, classes,
                          normalize=False,
                          title='Матрица ошибок',
                          cmap=plt.cm.Blues):
    plt.imshow(confusion_matrix, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(len(classes))
    plt.xticks(tick_marks, classes, rotation=45)
    plt.yticks(tick_marks, classes)

    if normalize:
        confusion_matrix = confusion_matrix.astype(
            'float') / confusion_matrix.sum(axis=1)[:, np.newaxis]
        print("Normalized confusion matrix")
    else:
        print('Confusion matrix, without normalization')

    print(confusion_matrix)

    thresh = confusion_matrix.max() / 2.
    for i, j in itertools.product(range(confusion_matrix.shape[0]), range(confusion_matrix.shape[1])):
        plt.text(j, i, confusion_matrix[i, j],
                 horizontalalignment="center",
                 color="white" if confusion_matrix[i, j] > thresh else "black")

    plt.tight_layout()
    plt.ylabel('Реальные классы')
    plt.xlabel('Расчетные классы')


if __name__ == '__main__':
    X = joblib.load(SAVE_DIR_PATH + '/X.joblib')
    y = joblib.load(SAVE_DIR_PATH + '/y.joblib')

    x_testcnn = np.expand_dims(X, axis=2)

    model = keras.models.load_model('model/model.h5')

    predictions = model.predict_classes(x_testcnn)
    new_y_test = y.astype(int)
    matrix = confusion_matrix(new_y_test, predictions)

    plot_confusion_matrix(matrix, classes=classes)
    plt.savefig('plots/confusion_matrix.png')
    plt.show()

    print(classification_report(new_y_test, predictions))
