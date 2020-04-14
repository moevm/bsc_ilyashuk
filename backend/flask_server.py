import librosa
import numpy as np
import pandas as pd
from flask import Flask, request

app = Flask(__name__)


@app.route('/')
def extract_features():
    filename = request.args.get('filename')

    data, sample_rate = librosa.load(
        './uploads/' + filename, res_type='kaiser_fast', duration=2.5, sr=22050*2, offset=0.5)
    sample_rate = np.array(sample_rate)
    mfccs = np.mean(librosa.feature.mfcc(y=data,
                                         sr=sample_rate,
                                         n_mfcc=13),
                    axis=0)

    return {"data": mfccs.tolist()}
