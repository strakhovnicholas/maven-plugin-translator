# Translator Maven Plugin

[![Maven Central](https://img.shields.io/maven-central/v/ru.strakhov.devs/translator-maven-plugin.svg?label=Maven%20Central)](https://search.maven.org/artifact/ru.strakhov.devs/translator-maven-plugin)  
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)  
[![Java](https://img.shields.io/badge/Java-17-blue?logo=java)](https://www.oracle.com/java/)  
[![Docker](https://img.shields.io/badge/Docker-Active-blue?logo=docker)](https://www.docker.com/)  
[![API](https://img.shields.io/badge/API-LibreTranslate-lightgrey?logo=apigee)](https://libretranslate.com/)

> 📝 Автоматический перевод комментариев и строк в Java-коде с помощью [LibreTranslate](https://libretranslate.com/)  
> Поддерживает перевод Javadoc, блоковых и построчных комментариев.

---

## 📑 Оглавление

- [📌 Описание](#-описание)  
- [🚀 Установка](#-установка)  
- [⚙️ Конфигурация](#️-конфигурация)  
- [▶️ Запуск](#️-запуск)  
- [🔧 Работа с LibreTranslate](#-работа-с-libretranslate)  
- [📂 Пример](#-пример)  
- [🛠️ Архитектура](#️-архитектура)  
- [📊 Логирование](#-логирование)  
- [⚖️ Лицензия](#️-лицензия)  
- [💡 Идеи для развития](#-идеи-для-развития)  
- [✍ Автор](#-автор)

---

## 📌 Описание

Этот Maven-плагин позволяет автоматически переводить текст внутри исходного кода Java.  
Поддерживается перевод:
- ✅ Javadoc  
- ✅ Блочных комментариев (`/* ... */`)  
- ✅ Построчных комментариев (`// ...`)  
- ✅ Строковых литералов (`"..."`)  

Перевод выполняется через **LibreTranslate API** (по умолчанию `http://localhost:5000/translate`).  
Это удобно для интернационализации проектов, адаптации кода под другие команды или подготовки документации.

---

## 🚀 Установка

Добавьте плагин в `pom.xml` вашего проекта:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>ru.strakhov.devs</groupId>
      <artifactId>translator-maven-plugin</artifactId>
      <version>1.0.0</version>
      <configuration>
        <source.language>en</source.language>
        <target.language>ru</target.language>
        <translateJavadoc>true</translateJavadoc>
        <translateBlockComments>true</translateBlockComments>
        <translateLineComments>true</translateLineComments>
        <translateStrings>false</translateStrings>
      </configuration>
    </plugin>
  </plugins>
</build>
```
---

## ⚙️ Конфигурация

| Параметр                | Тип      | Значение по умолчанию | Описание |
|--------------------------|---------|----------------------|----------|
| `source.language`        | String  | —                    | Язык исходного текста (например, `en`, `ru`, `de`). |
| `target.language`        | String  | —                    | Язык для перевода. |
| `translateJavadoc`       | boolean | true                 | Переводить ли Javadoc-комментарии. |
| `translateBlockComments` | boolean | true                 | Переводить ли блочные комментарии. |
| `translateLineComments`  | boolean | true                 | Переводить ли однострочные комментарии. |
| `translateStrings`       | boolean | false                | Переводить ли строковые литералы в коде. |
| `includes`               | List    | `**/*.java`          | Маски файлов для включения. |
| `excludes`               | List    | —                    | Маски файлов для исключения. |

---

## ▶️ Запуск

Выполните команду Maven:

```bash
mvn ru.strakhov.devs:translator-maven-plugin:translate
```

---

## 🔧 Работа с LibreTranslate

Плагин использует **LibreTranslate**.  
Для локальной работы нужно поднять сервер перевода:

```bash
docker run -it -p 5000:5000 libretranslate/libretranslate
```

## 🛠️ Архитектура

Структура проекта:

- **Translator** — абстрактный класс для реализации различных переводчиков.  
- **LibreTranslateTranslator** — реализация с использованием REST API LibreTranslate.  
- **TranslatorMOJO** — основной класс Maven-плагина, отвечает за:
  - поиск файлов,  
  - парсинг AST через JavaParser,  
  - замену комментариев и строк,  
  - сохранение обновленного исходного кода.  

---

## 📊 Логирование

Плагин использует **SLF4J** для логирования.  

Примеры логов:

```bash
-INFO Starting translation from en to ru
-INFO Found 12 Java files to process
-INFO Processing file src/main/java/... (5 comments)
-INFO Replaced attached line comment in Hello.java
-INFO File saved: src/main/java/.../Hello.java
```
---



