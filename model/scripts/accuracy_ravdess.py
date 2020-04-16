import csv

labels = [
    'angry',  # 0
    'calm',  # 1
    'fearful',  # 2
    'happy',  # 3
    'sad',  # 4
]

used_emotions = ['02', '03', '04', '05', '06']

matchers = {
    '02': 1,
    '03': 3,
    '04': 4,
    '05': 0,
    '06': 2,
}


with open('result_ravdess.csv', mode='r') as csv_file:
    reader = csv.reader(csv_file, delimiter=',',
                        quotechar='"', quoting=csv.QUOTE_MINIMAL)

    rows = [row for row in reader]

    rows = list(filter(lambda row: row[0].split(
        '-')[2] in used_emotions, rows))

    accuracy = 0

    for row in rows:
        splitted = row[0].split('-')
        splitted[-1] = splitted[-1].split('.')[0]
        if int(row[2]) == matchers[splitted[2]]:
            accuracy = accuracy + 1

    print('Ravdess accuracy ' +
          str(accuracy / len(rows) * 100) + '%')
