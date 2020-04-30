sudo apt update && sudo apt upgrade -y

curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | sudo apt-key add -

echo "deb https://dl.yarnpkg.com/debian/ stable main" | sudo tee /etc/apt/sources.list.d/yarn.list

sudo apt update && sudo apt install -y yarn

apt install -y default-jdk python3-pip python3-venv yarn

python3 -m venv venv

pip3 install flask librosa numpy pandas libsndfile1-dev