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

used_emotions = ['a', 'f', 'h', 'n', 's']

matchers = {
    'a': [0, 5],
    'f': [2, 7],
    'h': [3, 8],
    'n': [1, 6],
    's': [4, 9],
}


with open('result_savee.csv', mode='r') as csv_file:
    reader = csv.reader(csv_file, delimiter=',',
                        quotechar='"', quoting=csv.QUOTE_MINIMAL)

    rows = [row for row in reader]

    rows = list(filter(lambda row: row[0][0] in used_emotions, rows))

    accuracy = 0

    for row in rows:
        if int(row[2]) in matchers[row[0][0]]:
            accuracy = accuracy + 1

    print('Savee complete accuracy ' +
          str(accuracy / len(rows) * 100) + '%')
