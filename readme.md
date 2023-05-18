## ПРИЛОЖЕНИЕ. ИНСТРУКЦИЯ ПО РАЗВЕРТЫВАНИЮ СЕРВИСА

Ниже перечислены шаги, которые необходимо предпринять для правильного развертывания сервиса.

В первую очередь необходимо скачать исходный код приложения который располагается в интернете по
адресу https://github.com/slowslav/moochwizard. Исходный код данного проекта защищен лицензией Creative Commons
Attribution-NonCommercial (CC BY-NC). Это означает, что можно свободно использовать, копировать, модифицировать и
распространять код, при условии, что указано авторство и код не используется в коммерческих целях.

После нужно получить ключи и токены, необходимые для приложения.

- Токен и имя бота Telegram: Их можно получить у BotFather в Telegram. Инструкция находится в официальной документации
  Telegram.
- Токен Instagram: необходимо создать учетную запись разработчика Instagram и создать приложение, чтобы получить токен
  доступа. Инструкциям приведены в документации Instagram API.
- Spotify Client ID и Secret: необходимо создать учетную запись разработчика Spotify и приложение, чтобы получить эти
  учетные данные. Инструкциям приведены в официальной документации Spotify.
- Ключи и токены Twitter OAuth: необходимо создать учетную запись разработчика Twitter и приложение, чтобы получить эти
  учетные данные. Инструкциям приведены в официальной документации Twitter.

Как только все эти ключи и токены получены, надо заполнить файлы application.properties и twitter4j.properties.

Файл application.properties должен выглядеть примерно так:

```properties
server.port=<server_port>
bot.token=<telegram_bot_token>
bot.name=<telegram_bot_name>
instagram.token=<instagram_token>
spotify.client.id=<spotify_client_id>
spotify.client.secret=<spotify_client_secret>
```

Файл twitter4j.properties должен выглядеть примерно так:

```properties
oauth.consumerKey=<twitter_consumer_key>
oauth.consumerSecret=<twitter_consumer_secret>
oauth.accessToken=<twitter_access_token>
oauth.accessTokenSecret=<twitter_access_token_secret>
```

Каждое место для размещения замещается фактическими ключами или токенами. Прежде чем запускать Dockerfile, необходимо
настроить сервер и установить Docker.

Сервер можно установить на любом удобном облачном провайдере, таком как AWS, Google Cloud или Azure. Можно также
использовать VPS-провайдера, например DigitalOcean или Vultr. У каждого из этих провайдеров есть свои инструкции по
настройке сервера.

После того как сервер запущен, можно установить Docker. Если сервер работает под управлением Ubuntu, можно установить
Docker с помощью следующих команд:

```bash
sudo apt-get update
sudo apt-get install apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
sudo apt-get update
sudo apt-get install docker-ce
```

После установки Docker можно собрать и запустить Dockerfile сервера. В каталог, содержащий Dockerfile и выполняется
следующая команда для создания образа Docker:

```bash
docker build -t mooch-app:1.0.1 .
```

Эта команда создаст образ Docker с именем "mooch-app" и пометит его версией "1.0.1". После создания образа его можно
запустить с помощью следующей команды:

```bash
docker run -d -p <server_port>:<server_port> --name mooch-app mooch-app:1.0.1
```

Необходимо выполнить замену <server_port> на порт, который был указан в файле application.properties. Параметр -d
указывает Docker на запуск контейнера в фоновом режиме, а параметр -p сопоставляет порт сервера с портом в контейнере
Docker.

Теперь приложение запущено внутри контейнера Docker на сервере и доступно для взаимодействия через Telegram.
