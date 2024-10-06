# Management_Service

Система управления задачами с использованием Java, Spring , Spring Boot, Spring Security, PostgreSQL, Redis, JWT, Swagger UI. Система обеспечивает создание, редактирование, удаление и просмотр задач. Каждая задача содержит заголовок, описание, статус (например, "в ожидании", "в процессе", "завершено") и приоритет (например, "высокий", "средний", "низкий"), а также автора задачи и исполнителя.

# Использованные технологии
* [Java](https://www.java.com/) – язык программирования
* [Spring Boot](https://spring.io/projects/spring-boot) – как основной фрэймворк
* [PostgreSQL](https://www.postgresql.org/) – как основная реляционная база данных
* [Mockito](https://site.mockito.org/) – для тестирования
* [Liquibase](https://www.liquibase.org/) – для ведения миграций схемы БД
* [Gradle](https://gradle.org/) – как система сборки приложения
* [Swagger](https://swagger.io/) – для документирования API
* [Spring Security](https://spring.io/projects/spring-security) – для аутентификации и авторизации
* [Docker](https://www.docker.com/) – для образования и управления контейнерами

# База данных

* База поднимается в отдельном сервисе [managment_services](../managment_services)
* Liquibase сам накатывает нужные миграции на голый PostgreSql при старте приложения
* В коде продемонстрирована работа с JPA (Hibernate)

# Как начать разработку?

1. Сначала нужно скачать и установить DockerDekstop
```shell
https://docker.qubitpi.org/desktop/install/windows-install/
```
2. Далее необходимо скачать Jdk amazoncorretto:19.0.2 для IntelligIdea
3. Далее необходимо перейти по ссылке 
```shell
https://github.com/INT1NGLENCE/Management_service
```
2. На открвышемся сайте нужно склоинровать репозиторий
```shell
#команда для терминала
git clone https://github.com/INT1NGLENCE/Management_service.git
```

```shell
#URL для Get from Vcs
https://github.com/INT1NGLENCE/Management_service.git
```
3. Ждём полного клонирования репозитория и установки всех необходимых зависимостей для проекта

4. Далее нам необходимо перейти в gradle, который находится в правой части IntelligIdea. 
* Открываем папку Tasks 
* После чего открываем папку build 
* И запускаем комманду build.

5. Ждём окончания комманды build.

6. Проект готов

# Поднимаем дев среду для проекта

1. В приложении IntellidIdea в дереве проекта слева необходимо выполнить следующие действия:

* Нажмите правой кнопкой мыши на папку проекта например, managment_service. Эта папка будет самой первой в дереве проекта.
* Выберите в появившемся меню команду Copy Path/References
* Нажмите левой кнопкой на Absolute Path

2. Запустите приложение Docker

3. Нажмите сочетание клавиш 
```shell
win+R
```
4. В появившемся окне напишите
```shell
cmd
```
и нажмите Ок
5. Далее в появившемся окне напишите
```shell
cd путь к проекта(Cntrl + V) 
```
* После cd и пробела необходимо нажать сочетание клавиш Cntrl + V, чтобы вставить путь к корневой папке проекта
* Нажмите Enter

6. Убедитесь в том , что Docker запущен и не выдаёт никакх проблем

7. Далее необходимо ввести комманду
```shell
   docker-compose up --build
```
8. Ждём окончания комманды
9. Контейнер успешно запущен
10. Можно запустить сам проект в IntelligIdea:
* Перейдите в класс Main
* Нажмите правой кнопкой мыши на класс Main
* Выберите Run.Main.main()
11. Проект полностью запущен и может функционировать
* Произведите тестовый запрос, чтобы убедиться в корректной работе проекта
* Ссылка для проверки работы проекта:
```shell
   http://localhost:8080/swagger-ui.html
```
# Код

RESTfullApi приложения - это система управление задачами 

* Обычная трёхслойная
  архитектура – [Controller](src/main/java/management.system/controller), [Service](src/main/java/management.system/service), [Repository](src/main/java/management.system/repository)
* Слой Repository реализован и на jdbcTemplate, и на JPA (Hibernate)
* Написан [GlobalExceptionHandler](src/main/java/management.system/exception/GlobalExceptionHandler.java)
который умеет обрабатьвывать все необходимые для приложения исключения и прокидывать их в [ExceptionHandler](src/main/java/management.system/exception)
* Настроена конфигурация [Spring Security](src/main/java/management.system/security)
которая отвечает за регистрация и аунтификацию пользователей для системы управления задачами
* Настроена конфигурация [Swagger](src/main/management.system/swagger) для документации системы управления задачами

# Тесты
Тесты покрывают все возможные сценарии работы приложения и написаны на все классы проекта
* MockMvc
* JUnit5
