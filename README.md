# 🏦 BankCards Management System

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-✓-blue?logo=docker)
![Liquibase](https://img.shields.io/badge/Liquibase-✓-blue?logo=liquibase)
![JWT](https://img.shields.io/badge/JWT-Auth-yellow?logo=jsonwebtokens)

**Backend система для управления банковскими картами с полной аутентификацией и авторизацией**
</div>

## 🌟 Возможности

### 👤 Для пользователей:
- 🔐 Безопасная аутентификация через JWT
- 💳 Просмотр своих банковских карт с маскированными номерами
- 📱 Пагинация и фильтрация списка карт
- 🔄 Переводы между своими картами
- ⚠️ Запрос на блокировку карты
- 📊 Просмотр баланса

### 👑 Для администраторов:
- 👥 Полное управление пользователями (CRUD)
- 🃏 Создание, блокировка, активация и удаление карт
- 👁️ Просмотр всех карт и переводов в системе
- 🔍 Детальная информация по любому пользователю
- ⚙️ Управление статусами карт

### 🛡️ Безопасность: 
- 🔒 Шифрование номеров карт (AES-256)
- 🎭 Маскирование данных при отображении
- 🛂 Ролевой доступ (ADMIN/USER)
- ⏱️ JWT токены с TTL
- 🌐 Настраиваемый CORS

## 🏗️ Архитектура

### Технологический стек:

| Компонент | Технология | Назначение |
|-----------|------------|------------|
| **Backend Framework** | Spring Boot 3.5.7 | Основной фреймворк |
| **Language** | Java 17 | Язык программирования |
| **Database** | PostgreSQL 17 | Основная СУБД |
| **Authentication** | JWT + Spring Security | Аутентификация и авторизация |
| **API Documentation** | OpenAPI 3.1 + Swagger | Документация API |
| **Migrations** | Liquibase | Управление миграциями БД |
| **Containerization** | Docker + Docker Compose | Контейнеризация |
| **Testing** | JUnit 5 + Mockito | Модульное тестирование |
| **Code Generation** | Lombok | Сокращение шаблонного кода |
| **Validation** | Spring Validation | Валидация данных |

### Диаграмма зависимостей:
```
┌─────────────────────────────────────────┐
│           BankCards System              │
├─────────────────────────────────────────┤
│  Spring Boot 3.5.7                      │
│  ├── Spring Web MVC                     │
│  ├── Spring Security + JWT              │
│  ├── Spring Data JPA                    │
│  ├── Spring Validation                  │
│  └── SpringDoc OpenAPI                  │
│                                         │
│  PostgreSQL 17 (Docker)                 │
│  Liquibase (миграции)                   │
│  Docker Compose (оркестрация)           │
└─────────────────────────────────────────┘
```

## 🚀 Быстрый старт

### Предварительные требования

- **Java 17** или выше
- **Maven 3.6** или выше
- **Docker** и **Docker Compose**
- **Git** (для клонирования репозитория)

### 1. Настройка окружения

Скопируйте файл с примером переменных окружения:

```bash
cp .env.example .env
```

Отредактируйте файл `.env`, установив свои значения:

```env
# Database Configuration
POSTGRES_DB=bankcards
POSTGRES_USER=your_username_here          # Измените на свой
POSTGRES_PASSWORD=your_password_here      # Измените на свой

# JWT Configuration
JWT_SECRET=your_base64_jwt_secret_here    # Base64 ключ для HS256 (32 байта)
JWT_EXPIRATION=86400000                   # В миллисекундах (24 часа)

# Encryption
SECRET_KEY=your_base64_aes_key_here       # Base64 ключ для AES-256 (32 байта)
```

**Генерация секретных ключей:**

```bash
# Генерация JWT секрета (минимум 32 байта)
openssl rand -base64 32

# Генерация AES-256 ключа (32 байта)
openssl rand -base64 32
```

### 2. Сборка проекта

```bash
# Очистка и сборка проекта (без тестов)
mvn clean package -DskipTests

# Или сборка с тестами
mvn clean package
```

Артефакт будет создан в: `target/bankcards-1.0.0-SNAPSHOT.jar`

### 3. Запуск через Docker

```bash
# Сборка и запуск всех сервисов
docker-compose up --build

# Запуск в фоновом режиме
docker-compose up -d --build

# Просмотр логов
docker-compose logs -f app

# Остановка всех сервисов
docker-compose down

# Остановка с удалением томов данных
docker-compose down -v
```

### 4. Запуск из IDE

Для запуска из среды разработки (IntelliJ IDEA, Eclipse, VS Code):

1. **Добавьте файл `.env` в конфигурацию запуска**
2. **Или установите переменные окружения вручную (настройка `SPRING_DATASOURCE` требуется обязательно, даже с `.env` файлом):**

```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/bankcards
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
JWT_SECRET=your_base64_jwt_secret
SECRET_KEY=your_base64_aes_key
```

## 🗄️ Структура базы данных

### Таблица `users`
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Таблица `cards`
```sql
CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    card_number_hash VARCHAR(255) UNIQUE NOT NULL,
    last_four_digits VARCHAR(4) NOT NULL,
    expiry_date DATE NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    card_status VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

### Таблица `transfer`
```sql
CREATE TABLE transfer (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    from_card_id BIGINT NOT NULL REFERENCES cards(id),
    to_card_id BIGINT NOT NULL REFERENCES cards(id),
    amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

## 🔐 Безопасность

### Ролевая модель доступа

| Роль | Доступные операции |
|------|-------------------|
| **USER** | Просмотр своих карт, переводы между своими картами, блокировка своих карт |
| **ADMIN** | Все операции USER + управление пользователями, полный доступ ко всем картам и переводам |

### Шифрование данных

- **Номера карт**: Шифруются AES-256 перед сохранением в БД
- **Отображение**: Маскированный формат `**** **** **** 1234`
- **Пароли**: Хэшируются с использованием BCrypt
- **Токены**: JWT с HS256 алгоритмом и сроком действия

### CORS настройки

Конфигурация позволяет безопасные кросс-доменные запросы:

```yaml
cors:
  origins:           # Разрешенные источники
  methods:           # Разрешенные HTTP методы
    - POST
    - GET
    - PUT
    - DELETE
  headers: "*"       # Разрешенные заголовки
  allowedCredentials: true
  cache:
    maxAge: 3600     # Время кэширования предварительных запросов
```

## 📡 API Endpoints

### Аутентификация

#### `POST /api/auth/login`
Аутентификация пользователя и получение JWT токена.

**Запрос:**
```json
{
  "username": "testuser",
  "password": "123456"
}
```

**Ответ:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "generationTime": "2024-01-15T10:30:00Z",
  "expirationTime": "2024-01-16T10:30:00Z"
}
```

### Пользователь (USER)

#### `GET /api/cards`
Получение списка карт пользователя с пагинацией.

**Параметры запроса:**

| Параметр | Тип | Описание | Пример |
|----------|-----|----------|--------|
| `page` | Integer | Номер страницы (начинается с 0) | `0` |
| `size` | Integer | Количество элементов на странице | `10` |
| `sort` | String | Поле для сортировки | `id,asc` или `createdAt,desc` |

**Пример запроса:**
```
GET /api/cards?page=0&size=10&sort=id,asc
```

**Пример ответа:**
```json
{
  "content": [
    {
      "id": 1,
      "hiddenNumber": "**** **** **** 1234",
      "status": "ACTIVE",
      "expiryDate": "2025-12-31",
      "balance": 1500.75,
      "createdAt": "2024-01-15T10:30:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "page": {
    "size": 10,
    "number": 0,
    "totalElements": 25,
    "totalPages": 3
  }
}
```

#### `GET /api/cards/{cardId}`
Получение информации о конкретной карте.

```
GET /api/cards/1
```

#### `POST /api/cards/block/{cardId}`
Запрос на блокировку карты.

```
POST /api/cards/block/1
```

#### `POST /api/transfers`
Создание перевода между своими картами.

**Запрос:**
```json
{
  "fromCard": 1,
  "toCard": 2,
  "amount": 100.50
}
```

#### `GET /api/users`
Получение информации о текущем пользователе.

```
GET /api/users
```

### Администратор (ADMIN)

#### Управление пользователями:
- `GET /api/admin/users` - Все пользователи (с пагинацией)
- `POST /api/admin/users` - Создание пользователя
- `GET /api/admin/users/{userId}` - Информация о пользователе
- `DELETE /api/admin/users/{userId}` - Удаление пользователя

#### Управление картами:
- `GET /api/admin/cards` - Все карты в системе (с пагинацией)
- `POST /api/admin/cards` - Создание новой карты
- `POST /api/admin/cards/block/{cardId}` - Принудительная блокировка
- `POST /api/admin/cards/activate/{cardId}` - Активация карты
- `DELETE /api/admin/cards/{cardId}` - Удаление карты

#### Управление переводами:
- `GET /api/admin/transfers` - Все переводы (с пагинацией)
- `GET /api/admin/transfers/{transferId}` - Информация о переводе

## 🧪 Тестирование

Проект включает модульные тесты для ключевой бизнес-логики:

```bash
# Запуск всех тестов
mvn test

# Запуск тестов с отчетом
mvn test surefire-report:report
```
Учтите, что запуск тестов требует ручного включения `.env` файла и настройки переменных окружения `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` 

### Используемые библиотеки тестирования:
- **JUnit 5** - основной тестовый фреймворк
- **Mockito** - мокирование зависимостей
- **Spring Security Test** - тестирование безопасности
- **Spring Boot Test** - интеграционное тестирование

## 🐳 Docker контейнеры

### Сервисы:

| Сервис | Образ | Порт | Назначение |
|--------|-------|------|------------|
| **postgres** | `postgres:17` | 5432:5432 | PostgreSQL база данных |
| **app** | Сборка из Dockerfile | 8080:8080 | Spring Boot приложение |

### Docker Compose конфигурация:

```yaml
services:
  postgres:
    image: postgres:17
    container_name: bankcards-postgres
    environment:
      POSTGRES_DB: ${POSTGRES_DB}
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  app:
    build: .
    container_name: bankcards-app
    env_file:
      - .env
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/${POSTGRES_DB}
      SPRING_DATASOURCE_USERNAME: ${POSTGRES_USER}
      SPRING_DATASOURCE_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "8080:8080"
    depends_on:
      - postgres
```

## ⚙️ Конфигурация

### Основной конфигурационный файл `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: bankcards
  
  datasource:
    url: ${SPRING_DATASOURCE_URL}
    username: ${SPRING_DATASOURCE_USERNAME}
    password: ${SPRING_DATASOURCE_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: validate    # Валидация схемы без изменений
    show-sql: false
  
  liquibase:
    enabled: true
    change-log: classpath:db/migration/db.changelog-master.yaml

springdoc:
  api-docs:
    enabled: true
    path: /v3/api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html

logging:
  level:    # Здесь настраиваются уровни логирования
    '[com.example.bankcards]': DEBUG
    '[org.springframework.security]': INFO

application:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION}
  
  encryption:
    secret-key: ${SECRET_KEY}
```

### Переменные окружения:

| Переменная | Описание | Пример |
|------------|----------|--------|
| `SPRING_DATASOURCE_URL` | URL подключения к БД | `jdbc:postgresql://localhost:5432/bankcards` |
| `SPRING_DATASOURCE_USERNAME` | Имя пользователя БД | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Пароль БД | `admin` |
| `JWT_SECRET` | Секрет для подписи JWT | `base64_encoded_string_32_bytes` |
| `SECRET_KEY` | Ключ для шифрования AES-256 | `base64_encoded_string_32_bytes` |

## 🚨 Устранение неполадок

### 1. Ошибка подключения к базе данных
**Решение:**
```bash
# Проверьте статус контейнера PostgreSQL
docker-compose ps

# Перезапустите сервисы
docker-compose restart postgres

# Проверьте логи PostgreSQL
docker-compose logs postgres
```

### 2. Ошибка аутентификации JWT

**Решение:**
- Убедитесь, что `JWT_SECRET` в `.env` файле совпадает с использованным при генерации
- Перезапустите приложение после изменения секрета

### 3. Ошибка шифрования AES

**Решение:**
- Убедитесь, что `SECRET_KEY` имеет длину 32 байта в Base64 формате
- Сгенерируйте новый ключ: `openssl rand -base64 32`
