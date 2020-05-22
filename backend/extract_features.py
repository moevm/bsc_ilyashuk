import json
import sys

import librosa
import numpy as np
import pandas as pd
from pydub import AudioSegment

filename = './uploads/' + sys.argv[1]

converted = AudioSegment.from_file(filename).export("temp.wav", format="wav")

filename = converted.name

sr = librosa.core.get_samplerate(filename)

duration = librosa.core.get_duration(filename=filename, sr=sr)

data, sr = librosa.load(filename, res_type='kaiser_fast', sr=sr)

raw_mfccs = librosa.feature.mfcc(y=data,
                                 sr=sr,
                                 n_mfcc=40).T

mfcc_chunks = np.array_split(raw_mfccs, duration / 3)

mean = [np.mean(chunk, axis=0).tolist() for chunk in mfcc_chunks]

print(json.dumps({"data": mean}))
