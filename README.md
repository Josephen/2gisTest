# Запуск

Убедитесь, что установлены Java (8+) и sbt.

В корне проекта выполните:

```bash
sbt run
```
Приложение по умолчанию стартует на порту 8080 (адрес: http://localhost:8080 ).

Роуты

GET /

Описание:
Возвращает строку "OK".

Назначение:
Для быстрой проверки работоспособности (health-check).

POST /crawl

Описание:
Принимает JSON с массивом URL для обработки.

Пример входных данных:
```bash
{
  "urls": ["https://example.com", "https://google.com"]
}
```
Ответ:
Возвращает массив объектов { "url": ..., "title": ... }.

Пример запроса:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"urls":["https://example.com"]}' \
  http://localhost:8080/crawl
```
Пример ответа:
```bash
[
  {
    "url": "https://example.com",
    "title": "Example Domain"
  }
]
```
