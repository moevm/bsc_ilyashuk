import json
import os
import sys

import librosa
import numpy as np
from pydub import AudioSegment

filename = './uploads/' + sys.argv[1]
chunk_length = float(sys.argv[2])

converted = AudioSegment.from_file(filename).export("temp.wav", format="wav")

filename = converted.name

sr = librosa.core.get_samplerate(filename)

duration = librosa.core.get_duration(filename=filename, sr=sr)

data, sr = librosa.load(filename, res_type='kaiser_fast', sr=sr)

raw_mfccs = librosa.feature.mfcc(y=data,
                                 sr=sr,
                                 n_mfcc=40).T

mfcc_chunks = np.array_split(raw_mfccs, duration / chunk_length)

mean = [np.mean(chunk, axis=0).tolist() for chunk in mfcc_chunks]

os.remove(converted.name)

print(json.dumps({"data": mean}))
