

def load_model():
    import tensorflow as tf
    json_file = open('trained_model.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = tf.keras.models.model_from_json(loaded_model_json)
    loaded_model.load_weights('trained_model.h5')
    return loaded_model


def get_raw_files(base_dir):
    import os
    f = []
    for root, dirs, files in os.walk(base_dir):
        for file in files:
            if(file[0] != '.'):
                f.append(root + '/' + file)
    return f


def listdir(path):
    import os
    return [f for f in os.listdir(path) if not f.startswith('.')]
