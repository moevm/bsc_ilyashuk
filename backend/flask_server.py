import librosa
import numpy as np
import pandas as pd
from flask import Flask, request

app = Flask(__name__)


@app.route('/')
def extract_features():
    filename = request.args.get('filename')

    sr = librosa.core.get_samplerate('./uploads/' + filename)

    data, sr = librosa.load(
        './uploads/' + filename, res_type='kaiser_fast', sr=sr)
    sr = np.array(sr)
    mfccs = np.mean(librosa.feature.mfcc(y=data,
                                         sr=sr,
                                         n_mfcc=13),
                    axis=0)

    return {"data": mfccs.tolist()}
