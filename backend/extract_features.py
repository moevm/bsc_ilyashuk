import json
import sys

import librosa
import numpy as np
import pandas as pd

filename = './uploads/' + sys.argv[1]

sr = librosa.core.get_samplerate(filename)

data, sr = librosa.load(filename, res_type='kaiser_fast', sr=sr)
sr = np.array(sr)
mfccs = np.mean(librosa.feature.mfcc(y=data,
                                     sr=sr,
                                     n_mfcc=13),
                axis=0)

print(json.dumps({"data": mfccs.tolist()}))
