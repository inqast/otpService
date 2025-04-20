# Механизм учета финансов

## Зависимости 
- Docker Compose
- JDK
- Maven
- Make

## Начало работы
- создать файлы конфигурации (db.env, application.yaml, email/sms/telegram.propertie) файлы из example файлов в директориях
- make build
- make up
- make migrate
- make up (в первый раз контейнер с приложением упадет тк база пуста)

## Публичный блок

### Создание пользователя
POST /auth/register
body:
 - username string
 - password string
 - role string ADMIN/USER

curl
```
curl --location 'localhost:8080/auth/register' \
--header 'Content-Type: application/json' \
--data '{
    "username": "user1",
    "password": "123456",
    "role": "ADMIN"
}'
```

### Получение токена для пользователя
POST /auth/login
body:
 - username string
 - password string

curl
```
curl --location 'localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "username": "lala",
    "password": "jopa"
}'
```

## Блок для пользователей

### Создание кода
POST /otp/generate
headers:
- Authorization string токен пользователя
body:
 - operationId string
 - channel string sms/telegram/file/email
 - destination string

curl
```
curl --location 'localhost:8080/otp/generate' \
--header 'Authorization: ••••••' \
--header 'Content-Type: application/json' \
--data '{
    "operationId": "operation-1",
    "channel": "telegram",
    "destination": "384811781" // chat_id для tg
}'
```

### Проверка кода
POST /otp/validate
headers:
- Authorization string токен пользователя
body:
 - operationId string
 - code string

curl
```
curl --location 'localhost:8080/otp/validate' \
--header 'Authorization: ••••••' \
--header 'Content-Type: application/json' \
--data '{
  "operationId": "operation-1",
  "code": "505791"
}'
```

## Админский блок

## Получение конфига
GET /admin/config
headers:
- Authorization string токен пользователя

curl
```
curl --location 'localhost:8080/admin/config' \
--header 'Authorization: ••••••'
```

## Обновление конфига
PUT /admin/config
headers:
- Authorization string токен пользователя
body:
 - codeLength int
 - ttlSeconds int

curl
```
curl --location --request PUT 'localhost:8080/admin/config' \
--header 'Authorization: ••••••' \
--header 'Content-Type: application/json' \
--data '{
    "codeLength": 6,
    "ttlSeconds": 180
}'
```

## Получение списка пользователей
GET /admin/config
headers:
- Authorization string токен пользователя

curl
```
curl --location 'localhost:8080/admin/users' \
--header 'Authorization: ••••••'
```

## Удаление пользователя
GET /admin/config
path-params:
- user_id int
headers:
- Authorization string токен пользователя

curl
```
curl --location --request DELETE 'localhost:8080/admin/users/1' \
--header 'Authorization: ••••••'
```