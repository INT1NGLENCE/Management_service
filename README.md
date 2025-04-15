### Основные требования
1. **Единый интерфейс**: Нужен главный интерфейс `ReportService` с методами для работы с тестами (запуск, успех, провал, пропуск, шаги, прикрепление данных, завершение отчёта).
2. **Взаимозаменяемые имплементации**: Поддержка TestIt, Allure и других систем через конкретные реализации.
3. **Конфигурация**: Удобная передача настроек (например, проперти для TestIt) в модуль.
4. **Кастомная аннотация**: Одна аннотация для тестов с обязательным `testId` и опциональными параметрами (теги, описание).
5. **Интеграция**: Лёгкое встраивание в `RestModule` с автоматической генерацией отчётов.
6. **Удобство**: Минимум boilerplate-кода для пользователей.

---

### Предлагаемая архитектура

#### 1. Главный интерфейс `ReportService`
Создадим интерфейс, который будет содержать основные методы для работы с репортами. Это позволит унифицировать взаимодействие с разными системами репортинга.

```java
public interface ReportService {
    void init(ReportConfig config); // Инициализация с конфигурацией
    void startTest(String testId, Map<String, Object> metadata); // Начало теста
    void passTest(String testId, Map<String, Object> metadata); // Успешное завершение
    void failTest(String testId, Throwable throwable, Map<String, Object> metadata); // Провал теста
    void skipTest(String testId, String reason, Map<String, Object> metadata); // Пропуск теста
    void addStep(String stepName, Map<String, Object> metadata); // Добавление шага
    void attachData(String name, byte[] data, String type); // Прикрепление данных
    void completeReport(); // Завершение отчёта
}
```

- **`init`**: Принимает конфигурацию для настройки сервиса.
- **`metadata`**: Гибкий способ передачи дополнительных данных (теги, описание и т.д.).
- Методы покрывают основные сценарии тестирования.

#### 2. Класс конфигурации `ReportConfig`
Для передачи проперти в модуль создадим класс `ReportConfig`:

```java
public class ReportConfig {
    private final Map<String, Object> properties;

    public ReportConfig(Map<String, Object> properties) {
        this.properties = properties;
    }

    public <T> T getProperty(String key, Class<T> type) {
        return type.cast(properties.get(key));
    }
}
```

- Проперти передаются как `Map`, что делает конфигурацию гибкой (например, `{"testit.api.key": "abc123"}`).
- Метод `getProperty` упрощает доступ к настройкам с приведением типов.

#### 3. Кастомная аннотация `Reportable`
Создадим аннотацию для标记 тестов:

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Reportable {
    String testId(); // Обязательный ID теста
    String[] tags() default {}; // Опциональные теги
    String description() default ""; // Опциональное описание
}
```

- **`testId`**: Уникальный идентификатор теста (обязательный).
- **`tags`**: Массив тегов для категоризации (опционально).
- **`description`**: Описание теста (опционально).

#### 4. Абстрактный класс `AbstractReportService`
Чтобы упростить создание имплементаций, добавим абстрактный класс:

```java
public abstract class AbstractReportService implements ReportService {
    protected ReportConfig config;

    @Override
    public void init(ReportConfig config) {
        this.config = config;
        initialize();
    }

    protected abstract void initialize();
}
```

- Хранит конфигурацию и предоставляет точку для кастомной инициализации.

#### 5. Имплементация для TestIt
Пример реализации для TestIt:

```java
public class TestItReportService extends AbstractReportService {
    @Override
    protected void initialize() {
        // Используем config для настройки TestIt, например, API-ключи
        String apiKey = config.getProperty("testit.api.key", String.class);
        // Логика инициализации TestIt
    }

    @Override
    public void startTest(String testId, Map<String, Object> metadata) {
        // Логика начала теста в TestIt
    }

    @Override
    public void passTest(String testId, Map<String, Object> metadata) {
        // Логика успешного завершения
    }

    @Override
    public void failTest(String testId, Throwable throwable, Map<String, Object> metadata) {
        // Логика провала теста
    }

    // Другие методы аналогично
}
```

- Аналогичные классы можно создать для Allure и AllureTestOps.

#### 6. Фабрика `ReportServiceFactory`
Для создания нужной имплементации используем фабрику:

```java
public class ReportServiceFactory {
    public static ReportService createReportService(String type, ReportConfig config) {
        switch (type.toLowerCase()) {
            case "testit":
                TestItReportService service = new TestItReportService();
                service.init(config);
                return service;
            case "allure":
                return new AllureReportService(); // Аналогично с инициализацией
            case "alluretestops":
                return new AllureTestOpsReportService();
            default:
                throw new IllegalArgumentException("Unknown report service type: " + type);
        }
    }
}
```

- Фабрика упрощает выбор имплементации по строковому типу.

#### 7. Интеграция с `RestModule` через AspectJ
Для автоматической генерации отчётов используем аспект (AspectJ):

```java
public aspect ReportAspect {
    private ReportService reportService;

    pointcut reportableTest(): execution(@Reportable * *(..));

    before(): reportableTest() {
        Reportable annotation = thisJoinPoint.getTarget()
            .getClass()
            .getMethod(thisJoinPoint.getSignature().getName())
            .getAnnotation(Reportable.class);
        
        if (annotation != null) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("tags", annotation.tags());
            metadata.put("description", annotation.description());
            reportService.startTest(annotation.testId(), metadata);
        }
    }

    after() returning: reportableTest() {
        Reportable annotation = thisJoinPoint.getTarget()
            .getClass()
            .getMethod(thisJoinPoint.getSignature().getName())
            .getAnnotation(Reportable.class);
        
        if (annotation != null) {
            reportService.passTest(annotation.testId(), Collections.emptyMap());
        }
    }

    after() throwing(Throwable t): reportableTest() {
        Reportable annotation = thisJoinPoint.getTarget()
            .getClass()
            .getMethod(thisJoinPoint.getSignature().getName())
            .getAnnotation(Reportable.class);
        
        if (annotation != null) {
            reportService.failTest(annotation.testId(), t, Collections.emptyMap());
        }
    }

    public void setReportService(ReportService service) {
        this.reportService = service;
    }
}
```

- Аспект перехватывает методы с аннотацией `@Reportable` и вызывает методы `ReportService` автоматически.

#### 8. Пример использования в `RestModule`
Интеграция в модуль:

```java
public class RestModule {
    private ReportService reportService;

    public RestModule(String reportType, ReportConfig config) {
        this.reportService = ReportServiceFactory.createReportService(reportType, config);
        ReportAspect.aspectOf().setReportService(this.reportService); // Установка сервиса в аспект
    }

    @Reportable(testId = "REST-001", tags = {"smoke", "rest"}, description = "Test REST endpoint")
    public void testRestEndpoint() {
        reportService.addStep("Send request", Collections.emptyMap());
        // Логика теста
    }
}
```

- При создании `RestModule` передаём тип репортинга и конфигурацию.
- Тесты с `@Reportable` автоматически генерируют отчёты.

---

### Преимущества архитектуры
1. **Гибкость**: Легко добавить новую систему репортинга, создав новую имплементацию.
2. **Удобство**: Одна аннотация заменяет кучу нативных аннотаций TestIt/Allure.
3. **Автоматизация**: Аспект убирает необходимость ручного вызова методов `ReportService`.
4. **Конфигурация**: Проперти передаются через `ReportConfig` единообразно.
5. **Интеграция**: Модуль легко встраивается в `RestModule`, `KafkaModule` и т.д.

---

### Альтернативный подход
Если AspectJ кажется сложным, можно использовать **паттерн Listener**:
- Создать `ReportListener`, который будет вызываться в тестах вручную.
- Это потребует больше кода в тестах, но уберёт зависимость от AspectJ.

Пример:
```java
public class RestModule {
    private ReportService reportService;

    public void testRestEndpoint() {
        reportService.startTest("REST-001", Map.of("tags", new String[]{"smoke"}));
        try {
            reportService.addStep("Send request", Collections.emptyMap());
            // Логика теста
            reportService.passTest("REST-001", Collections.emptyMap());
        } catch (Exception e) {
            reportService.failTest("REST-001", e, Collections.emptyMap());
        }
    }
}
```

---

### Итог
Предложенная архитектура с `ReportService`, `ReportConfig`, `@Reportable` и AspectJ полностью отвечает вашим требованиям. Она удобна, масштабируема и легко встраивается в существующие модули. Если вы хотите избежать AspectJ, можно использовать более явный подход с ручным вызовом методов, но это увеличит объём кода в тестах. Выбор зависит от ваших предпочтений и инфраструктуры.

Давайте разберём, для чего нужны методы интерфейса `ReportService` (`startTest`, `passTest`, `failTest`, `skipTest`, `addStep`, `attachData`), как они используются и какую роль играют в контексте вашей задачи создания `ReportModule` для автотестирования. Я объясню их назначение, сценарии использования и как пользователи (разработчики тестов) будут с ними взаимодействовать.

---

### Контекст
`ReportService` — это главный интерфейс вашего `ReportModule`, который унифицирует работу с разными системами репортинга (TestIt, Allure, AllureTestOps). Методы интерфейса предоставляют стандартный API для записи событий тестирования (начало теста, его результат, шаги и т.д.). Они нужны, чтобы:
1. Абстрагировать логику репортинга от конкретной системы (TestIt или Allure).
2. Обеспечить удобство и единообразие для пользователей.
3. Поддерживать автоматическую генерацию отчётов (например, через аннотации и аспекты).

Ваша цель — минимизировать ручную работу с репортами, поэтому методы в основном будут вызываться автоматически (через аспект или аннотацию `@Reportable`), но их также можно использовать вручную для большей гибкости.

---

### Методы `ReportService` и их назначение

#### 1. `void startTest(String testId, Map<String, Object> metadata)`
- **Для чего нужен**: Отмечает начало выполнения теста в системе репортинга.
- **Что делает**:
  - Создаёт запись о новом тесте с уникальным идентификатором (`testId`).
  - Передаёт дополнительные метаданные (`metadata`), такие как теги, описание, имя теста и т.д.
  - Например, в TestIt это может инициировать тест-кейс, в Allure — создать новую запись в отчёте.
- **Как используется**:
  - **Автоматически**: Когда тест помечен аннотацией `@Reportable`, аспект вызывает `startTest` перед выполнением метода, извлекая `testId` и метаданные (теги, описание) из аннотации.
  - **Вручную**: Если пользователь хочет явно управлять репортингом, он может вызвать метод в коде теста, например:
    ```java
    reportService.startTest("REST-001", Map.of("tags", new String[]{"smoke"}, "description", "Test endpoint"));
    ```
- **Пример сценария**: Тест начинается, и система репортинга фиксирует его старт с указанием ID и контекста (например, "Тест REST-001, smoke-тест").

#### 2. `void passTest(String testId, Map<String, Object> metadata)`
- **Для чего нужен**: Отмечает успешное завершение теста.
- **Что делает**:
  - Фиксирует, что тест с указанным `testId` прошёл успешно.
  - Может добавить дополнительные метаданные (например, время выполнения, комментарии).
  - В TestIt это обновляет статус тест-кейса на "Passed", в Allure — помечает тест как успешный.
- **Как используется**:
  - **Автоматически**: Аспект вызывает `passTest`, если тест завершился без исключений.
  - **Вручную**: Пользователь может вызвать метод, если хочет явно отметить успешное выполнение:
    ```java
    reportService.passTest("REST-001", Map.of("comment", "All good"));
    ```
- **Пример сценария**: Тест `REST-001` выполнился без ошибок, и отчёт фиксирует его как успешный.

#### 3. `void failTest(String testId, Throwable throwable, Map<String, Object> metadata)`
- **Для чего нужен**: Отмечает провал теста с указанием причины.
- **Что делает**:
  - Фиксирует, что тест с `testId` провалился.
  - Передаёт исключение (`throwable`), чтобы сохранить стек-трейс и детали ошибки.
  - Добавляет метаданные (например, комментарий или контекст).
  - В TestIt это обновляет статус на "Failed" с описанием ошибки, в Allure — добавляет стек-трейс в отчёт.
- **Как используется**:
  - **Автоматически**: Аспект вызывает `failTest`, если в тесте выброшено исключение.
  - **Вручную**: Пользователь может вызвать метод для явного провала теста:
    ```java
    try {
        // Логика теста
    } catch (Exception e) {
        reportService.failTest("REST-001", e, Map.of("reason", "Invalid response"));
    }
    ```
- **Пример сценария**: Тест `REST-001` получил неверный ответ от API, и отчёт фиксирует провал с описанием ошибки.

#### 4. `void skipTest(String testId, String reason, Map<String, Object> metadata)`
- **Для чего нужен**: Отмечает, что тест был пропущен.
- **Что делает**:
  - Фиксирует, что тест с `testId` не выполнялся.
  - Указывает причину пропуска (`reason`) и дополнительные метаданные.
  - В TestIt это помечает тест как "Skipped", в Allure — добавляет соответствующую метку.
- **Как используется**:
  - **Автоматически**: Может вызываться, если тест помечен как пропущенный (например, через `@Ignore` или условие в тесте).
  - **Вручную**: Пользователь вызывает метод, если решает пропустить тест:
    ```java
    reportService.skipTest("REST-001", "Not implemented yet", Collections.emptyMap());
    ```
- **Пример сценария**: Тест `REST-001` ещё не реализован, и отчёт фиксирует его как пропущенный с указанием причины.

#### 5. `void addStep(String stepName, Map<String, Object> metadata)`
- **Для чего нужен**: Добавляет промежуточный шаг в тесте для детализации отчёта.
- **Что делает**:
  - Фиксирует шаг выполнения теста (например, "Отправка запроса", "Проверка ответа").
  - Передаёт метаданные для шага (например, параметры запроса).
  - В TestIt это добавляет шаг в тест-кейс, в Allure — создаёт вложенный шаг в отчёте.
- **Как используется**:
  - **Автоматически**: Может вызываться в `RestModule` для стандартных операций (например, отправка HTTP-запроса).
  - **Вручную**: Пользователь вызывает метод для явного добавления шага:
    ```java
    reportService.addStep("Send POST request", Map.of("url", "/api/endpoint"));
    ```
- **Пример сценария**: В тесте `REST-001` фиксируется шаг "Отправка POST-запроса" с указанием URL.

#### 6. `void attachData(String name, byte[] data, String type)`
- **Для чего нужен**: Прикрепляет дополнительные данные к тесту или шагу (скриншоты, логи, JSON и т.д.).
- **Что делает**:
  - Добавляет файл или данные (например, скриншот в виде `byte[]`) с именем и типом.
  - В TestIt это прикрепляет вложение к тест-кейсу, в Allure — добавляет вложение в отчёт.
- **Как используется**:
  - **Автоматически**: Может вызываться в `RestModule` для сохранения ответа API или логов.
  - **Вручную**: Пользователь прикрепляет данные, например, скриншот:
    ```java
    reportService.attachData("response.json", response.getBytes(), "application/json");
    ```
- **Пример сценария**: Тест `REST-001` прикрепляет JSON-ответ API к отчёту для анализа.

#### 7. `void completeReport()`
- **Для чего нужен**: Финализирует отчёт, завершая все операции.
- **Что делает**:
  - Закрывает открытые тесты и сохраняет отчёт.
  - В TestIt это может отправить данные на сервер, в Allure — сгенерировать итоговый HTML-отчёт.
- **Как используется**:
  - **Автоматически**: Вызывается в конце тестового прогона (например, в `tearDown`).
  - **Вручную**: Пользователь может вызвать метод явно:
    ```java
    reportService.completeReport();
    ```
- **Пример сценария**: Все тесты завершены, и отчёт сохраняется в TestIt или Allure.

---

### Как люди будут пользоваться методами?

#### Основной сценарий: Автоматическое использование через `@Reportable`
Благодаря аннотации `@Reportable` и аспекту (как в предложенной архитектуре), пользователи в большинстве случаев **не будут вызывать методы вручную**. Аспект перехватывает выполнение тестов и вызывает методы автоматически:

```java
public class RestModule {
    @Reportable(testId = "REST-001", tags = {"smoke", "rest"}, description = "Test REST endpoint")
    public void testRestEndpoint() {
        // Логика теста
        // Например, отправка HTTP-запроса
    }
}
```

- **Что происходит под капотом**:
  - Аспект вызывает `startTest("REST-001", metadata)` перед выполнением метода.
  - Если тест проходит, вызывается `passTest("REST-001", metadata)`.
  - Если тест падает, вызывается `failTest("REST-001", throwable, metadata)`.
  - Пользователь может добавить шаги или вложения через явные вызовы, если нужно:
    ```java
    reportService.addStep("Check response", Map.of("status", 200));
    ```

#### Вторичный сценарий: Ручное использование
Если пользователь хочет больше контроля или работает без аспекта, он может вызывать методы явно:

```java
public class RestModule {
    private ReportService reportService;

    public void testRestEndpoint() {
        Map<String, Object> metadata = Map.of("tags", new String[]{"smoke"}, "description", "Test endpoint");
        reportService.startTest("REST-001", metadata);
        try {
            reportService.addStep("Send request", Map.of("url", "/api/endpoint"));
            // Логика теста
            reportService.attachData("response.json", response.getBytes(), "application/json");
            reportService.passTest("REST-001", metadata);
        } catch (Exception e) {
            reportService.failTest("REST-001", e, metadata);
        }
    }
}
```

- Это полезно для сложных тестов, где требуется детальный контроль над шагами и вложениями.

#### Интеграция с `RestModule`
В `RestModule` методы могут вызываться автоматически для стандартных операций:
- Например, при отправке HTTP-запроса можно добавить шаг:
  ```java
  reportService.addStep("Send " + request.getMethod() + " request", Map.of("url", request.getUrl()));
  ```
- При получении ответа — прикрепить данные:
  ```java
  reportService.attachData("response.json", response.getBody(), "application/json");
  ```

---

### Зачем это нужно пользователям?
1. **Упрощение репортинга**:
   - Вместо изучения API TestIt или Allure пользователи работают с одной аннотацией `@Reportable` и единым интерфейсом.
   - Методы унифицируют взаимодействие, скрывая различия между системами.

2. **Автоматизация**:
   - Аспект и аннотация минимизируют ручной код, автоматически фиксируя начало, результат и провалы тестов.
   - Пользователи сосредотачиваются на логике теста, а не на репортинге.

3. **Гибкость**:
   - Методы `addStep` и `attachData` позволяют добавлять детали (шаги, вложения) по необходимости.
   - Пользователь может выбрать, использовать методы вручную или полагаться на автоматизацию.

4. **Детализация отчётов**:
   - Методы обеспечивают структурированные отчёты с шагами, вложениями и метаданными, что упрощает анализ результатов.
   - Например, в TestIt можно увидеть шаги теста, в Allure — красивый отчёт с вложениями.

---

### Итог
Методы `ReportService` (`startTest`, `passTest`, `failTest`, `skipTest`, `addStep`, `attachData`, `completeReport`) покрывают полный жизненный цикл теста в системе репортинга:
- **Начало и завершение теста**: `startTest`, `passTest`, `failTest`, `skipTest`.
- **Детализация процесса**: `addStep`, `attachData`.
- **Финализация**: `completeReport`.

Пользователи в основном будут полагаться на автоматический вызов методов через `@Reportable` и аспект, что упрощает интеграцию с `RestModule`. При необходимости они могут использовать методы вручную для добавления шагов, вложений или явного управления репортингом. Это делает `ReportModule` удобным, гибким и подходящим для разных сценариев автотестирования.
