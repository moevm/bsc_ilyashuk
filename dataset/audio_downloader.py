from __future__ import unicode_literals
import youtube_dl


ydl_opts = {
    'format': 'bestaudio/best',
    'postprocessors': [{
        'key': 'FFmpegExtractAudio',
        'preferredcodec': 'wav',
        'preferredquality': '192',
    }],
}


playlists = [
    'https://www.youtube.com/watch?v=4pXf9f37btE&list=PLp2aUaz0EAxCBeTw-kcLLERrF4wBZKjJv',
    'https://www.youtube.com/watch?v=CFBJG2ksvqo&list=PL8EJzNcJZNp0Uq-r2sMcL5nHyI-Po0Tjq',
    'https://www.youtube.com/watch?v=uCm4TK2Hggg&list=PLQTGSfnaYlCuonYGidCFg1aqii9a6cqL4'
]


with youtube_dl.YoutubeDL(ydl_opts) as ydl:
    ydl.download(playlists)