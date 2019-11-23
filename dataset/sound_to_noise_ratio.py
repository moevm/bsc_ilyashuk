import scipy.io.wavfile as wavfile
import numpy
import os.path


def snr(file):
    if (os.path.isfile(file)):
        data = wavfile.read(file)[1]
        singleChannel = data
        try:
            singleChannel = numpy.sum(data, axis=1)
        except:
            # was mono after all
            pass
        norm = singleChannel / \
            (max(numpy.amax(singleChannel), -1 * numpy.amin(singleChannel)))
        return signaltonoise(norm)


def signaltonoise(a, axis=0, ddof=0):
    a = numpy.asanyarray(a)
    m = a.mean(axis)
    sd = a.std(axis=axis, ddof=ddof)
    return numpy.where(sd == 0, 0, m/sd)


if __name__ == "__main__":
    print(snr('test.wav'))
