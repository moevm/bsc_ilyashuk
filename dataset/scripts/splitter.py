from pydub import AudioSegment
import csv

audioFile = AudioSegment.from_wav('./samples/1.wav')

with open('./out/1.csv') as csvfile:
    reader = csv.reader(csvfile, delimiter='\t')
    next(reader)
    for i, row in enumerate(reader):
        print('Splitting segment {i}'.format(i=i))
        t1 = float(row[1]) * 1000
        t2 = float(row[2]) * 1000
        newAudio = audioFile[t1:t2]
        newAudio.export(
            './splitted/{status}{i}.wav'.format(status=row[0], i=i), format='wav')
print('===Splitting completed===')
