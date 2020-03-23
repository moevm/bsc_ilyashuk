import csv

labels = [
    'female_angry',  # 0
    'female_calm',  # 1
    'female_fearful',  # 2
    'female_happy',  # 3
    'female_sad',  # 4
    'male_angry',  # 5
    'male_calm',  # 6
    'male_fearful',  # 7
    'male_happy',  # 8
    'male_sad',  # 9
]

used_emotions = ['02', '03', '04', '05', '06']

matchers = {
    '02': [1, 6],
    '03': [3, 8],
    '04': [4, 9],
    '05': [0, 5],
    '06': [2, 7],
}


with open('../result.csv', mode='r') as csv_file:
    reader = csv.reader(csv_file, delimiter=',',
                        quotechar='"', quoting=csv.QUOTE_MINIMAL)

    rows = [row for row in reader]

    rows = list(filter(lambda row: row[0].split(
        '-')[2] in used_emotions, rows))

    accuracy_only_emotions = 0

    for row in rows:
        if int(row[2]) in matchers[row[0].split('-')[2]]:
            accuracy_only_emotions = accuracy_only_emotions + 1

    print('Ravdess accuracy for only emotions ' +
          str(accuracy_only_emotions / len(rows) * 100) + '%')
