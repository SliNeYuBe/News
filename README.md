# ExpenseTracker

Новостное приложение с фильтрацией контента по ключевым словам пользователя.

## Скриншоты
<img width="272" height="573" alt="image" src="https://github.com/user-attachments/assets/4735830b-d84a-4e9d-b659-43fc518de032" />
<img width="266" height="573" alt="image" src="https://github.com/user-attachments/assets/e9e41bb4-93a4-4698-bf34-5db1b28f860f" />

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
- JUnit + Mockk (тесты)
- WorkManager
- Intent / Pending Intent
- Notification

## Архитектура
Проект следует Clean Architecture с разделением на слои:
- Presentation (UI + ViewModel)
- Domain (Use Cases)
- Data (Repository + DataSource)

## Установка
`git clone https://github.com/SliNeYuBe/Notes.git`

Откройте проект в Android Studio Arctic Fox или новее.
