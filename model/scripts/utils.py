

def load_model():
    from keras.models import model_from_json
    json_file = open('../model.json', 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    loaded_model = model_from_json(loaded_model_json)
    loaded_model.load_weights('../model.h5')
    print('Loaded model')
    return loaded_model


def get_raw_files(base_dir):
    all_files = []
    dirs = listdir(base_dir)
    for dir in dirs:
        files = listdir(base_dir + dir)
        for file in files:
            all_files.append(base_dir + dir + '/' + file)
    return all_files


def listdir(path):
    import os
    return [f for f in os.listdir(path) if not f.startswith('.')]
