# работает на версии 1.15, на версиях >=2 не работает

import tensorflow as tf

converter = tf.lite.TFLiteConverter.from_keras_model_file('model.h5')
tflite_model = converter.convert()
open('model.tflite', 'wb').write(tflite_model)
