import csv

labels = [
    'angry',  # 0
    'calm',  # 1
    'fearful',  # 2
    'happy',  # 3
    'sad',  # 4
]

used_emotions = ['a', 'f', 'h', 'n', 's']

matchers = {
    'a': 0,
    'f': 2,
    'h': 3,
    'n': 1,
    's': 4,
}


with open('result_savee.csv', mode='r') as csv_file:
    reader = csv.reader(csv_file, delimiter=',',
                        quotechar='"', quoting=csv.QUOTE_MINIMAL)

    rows = [row for row in reader]

    rows = list(filter(lambda row: row[0][0] in used_emotions, rows))

    accuracy = 0

    for row in rows:
        if int(row[2]) == matchers[row[0][0]]:
            accuracy = accuracy + 1

    print('Savee accuracy ' +
          str(accuracy / len(rows) * 100) + '%')
