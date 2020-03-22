import csv

labels = [
    'female_angry',
    'female_calm',
    'female_fearful',
    'female_happy',
    'female_sad',
    'male_angry',
    'male_calm',
    'male_fearful',
    'male_happy',
    'male_sad',
]

used_emotions = ['02', '03', '04', '05', '06']

matchers = {
    '02',
    '03',
    '04',
    '05',
    '06',
}


with open('../result.csv', mode='r') as csv_file:
    reader = csv.reader(csv_file, delimiter=',',
                        quotechar='"', quoting=csv.QUOTE_MINIMAL)

    rows = [row for row in reader]

    rows = list(filter(lambda row: row[0].split(
        '-')[2] in used_emotions, rows))

    print(len(rows))
