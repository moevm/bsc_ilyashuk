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


with open('result_ravdess.csv', mode='r') as csv_file:
    reader = csv.reader(csv_file, delimiter=',',
                        quotechar='"', quoting=csv.QUOTE_MINIMAL)

    rows = [row for row in reader]

    rows = list(filter(lambda row: row[0].split(
        '-')[2] in used_emotions, rows))

    accuracy_only_emotions = 0
    accuracy_gender = 0
    accuracy_complete = 0

    for row in rows:
        splitted = row[0].split('-')
        splitted[-1] = splitted[-1].split('.')[0]
        if int(row[2]) in matchers[splitted[2]]:
            accuracy_only_emotions = accuracy_only_emotions + 1

    for row in rows:
        splitted = row[0].split('-')
        splitted[-1] = splitted[-1].split('.')[0]
        if (int(splitted[-1]) % 2 == 1 and int(row[2]) > 4) or (int(splitted[-1]) % 2 == 0 and int(row[2]) <= 4):
            accuracy_gender = accuracy_gender + 1

    for row in rows:
        splitted = row[0].split('-')
        splitted[-1] = splitted[-1].split('.')[0]
        if int(row[2]) in matchers[splitted[2]] and ((int(splitted[-1]) % 2 == 1 and int(row[2]) > 4) or (int(splitted[-1]) % 2 == 0 and int(row[2]) <= 4)):
            accuracy_complete = accuracy_complete + 1

    print('Ravdess accuracy for only emotions ' +
          str(accuracy_only_emotions / len(rows) * 100) + '%')

    print('Ravdess gender accuracy ' +
          str(accuracy_gender / len(rows) * 100) + '%')

    print('Ravdess complete accuracy ' +
          str(accuracy_complete / len(rows) * 100) + '%')
