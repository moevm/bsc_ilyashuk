import pathlib

working_dir_path = pathlib.Path().absolute()

TRAINING_FILES_PATH = str(working_dir_path) + '/RAW/'
SAVE_DIR_PATH = str(working_dir_path) + '/features/'
MODEL_DIR_PATH = str(working_dir_path) + '/model/'
TESS_ORIGINAL_FOLDER_PATH = str(working_dir_path) + '/TESS_RAW/'
