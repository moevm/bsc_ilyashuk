import json
import math
import os
import sys

import librosa
import numpy as np
from pydub import AudioSegment


def split_into_chunks(array, chunk_size):
    chunks = []
    number_of_chunks = math.ceil((len(array) / chunk_size))
    for i in range(number_of_chunks):
        start_index = math.ceil(i * chunk_size)
        end_index = math.ceil((i + 1) * chunk_size)
        chunks.append(array[start_index: end_index])

    return chunks


if __name__ == "__main__":
    filename = './uploads/' + sys.argv[1]
    chunk_length = float(sys.argv[2])

    converted = AudioSegment.from_file(
        filename).export("temp.wav", format="wav")

    filename = converted.name

    sr = librosa.core.get_samplerate(filename)
    duration = librosa.core.get_duration(filename=filename, sr=sr)
    data, sr = librosa.load(filename, res_type='kaiser_fast', sr=sr)

    raw_mfccs = librosa.feature.mfcc(y=data,
                                     sr=sr,
                                     n_mfcc=40).T

    mfcc_per_second = len(raw_mfccs) / duration
    mfcc_chunks = split_into_chunks(raw_mfccs, mfcc_per_second * chunk_length)
    mean = [np.mean(chunk, axis=0).tolist() for chunk in mfcc_chunks]

    os.remove(converted.name)

    result = {"chunks": mean, "duration": duration}
    print(json.dumps(result))
