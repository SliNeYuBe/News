# NewsApp

Новостное приложение с фильтрацией контента по ключевым словам пользователя.

## Скриншоты
<img width="360" height="752" alt="Снимок экрана 2026-02-24 190153" src="https://github.com/user-attachments/assets/1608362f-8676-4ac0-99c4-d27389f5b821" />
<img width="360" height="752" alt="Снимок экрана 2026-02-24 190215" src="https://github.com/user-attachments/assets/017837fd-090e-4f8c-ad85-e4a75f135553" />

## Функционал
- Реализовал UI на Jetpack Compose
- Настроил работу с сетью при помощи Retrofit
- Добавил DataStore для управления настройками
- Внедрил WorkManager для фонового обновления данных
- Реализовал уведомления при обновлении новостей

## Технологии
- Kotlin
- Jetpack Compose (UI)
- Jetpack Compose Navigation
- Retrofit
- DataStore
- MVVM + Clean Architecture
- Room (локальная БД)
- Hilt (DI)
- Coroutines + Flow
- WorkManager
- Intent / Pending Intent
- Notification

## Архитектура
Проект следует Clean Architecture с разделением на слои:
- Presentation (UI + ViewModel)
- Domain (Use Cases)
- Data (Repository + DataSource)

## Установка
`git clone https://github.com/SliNeYuBe/News.git`

Откройте проект в Android Studio Arctic Fox или новее.
