## Порядок работы с github

## Инициализация
1. Создайте [fork](https://help.github.com/articles/fork-a-repo) репозитория [fizteh-java-2015](https://github.com/KhurtinDN/fizteh-java-2015).
2. Создайте локальный клон своего репозитория
3. В директории [projects](projects) нужно создать maven модуль командой
  
  ```
  mvn -B archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart \
  -DgroupId=ru.mipt.diht.students -DartifactId=<Your github login>
  ```
4. Добавить свой модуль в родительский [projects/pom.xml](projects/pom.xml), если его там ещё не было. Нужно, чтобы можно было из корня собирать все модули разом.
  
  ```
  <modules>
      <module>dkhurtin</module>
      <!--Add you module here-->
  </modules>
  ```
5. Добавить в свой pom.xml (подобно, [pom.xml](projects/dkhurtin/pom.xml) ссылку на родительский модуль, если её там ещё не было. Нужно, чтобы унаследовать общие параметры сборки проекта.
  
  ```
  <parent>
      <groupId>ru.mipt.diht.students</groupId>
      <artifactId>parent</artifactId>
      <version>1.0-SNAPSHOT</version>
  </parent>
  ```
6. Смотрите в качестве примера модуль [dkhurtin](projects/dkhurtin). 

## Работа над заданием
1. Свои классы нужно добавлять в директории ```projects/<Your github login>/src/main/java/ru/mipt/diht/students/<Your github login>/<task>```.
2. Сборка модуля производится командой ```mvn package```.
3. Запускать приложение можно прямо из maven командой ```mvn exec:java -Dexec.mainClass=<Fully qualified main class name> -Dexec.args="<arguments>"```. Она сама добавит в classpath транзативно все необходимые библиотеки из pom.xml dependencies. 
  
  Также можно запускать с помощью команды java. Для этого нужно: 
  1. Выполнить единожды ```mvn dependency:copy-dependencies```. Эта команда скопирует все необходимые зависимости в target/dependency.
  2. Далее запускать командой ```java -cp "target/*:target/dependency/*" <Your main class name> <arguments>```
3. После того, как задание выполнено и протестировано в собственном репозитории, необходимо создать
[pull request](https://help.github.com/articles/using-pull-requests) в репозиторий [fizteh-java-2015](https://github.com/KhurtinDN/fizteh-java-2015). В заголовке
к pull request необходимо написать: ```Имя Фамилия, № группы, задание``` (например, ```Василий Иванов, 123, Shell```).
Также необходимо назначить pull request на своего семинариста.
В одном pull request должно быть решение только одной задачи. Если хочется сдавать параллельно несколько заданий,
необходимо создавать бранчи и делать pull request из бранчей.
4. Результаты сборки можно смотреть [тут](https://travis-ci.org/KhurtinDN/fizteh-java-2015)
5. Далее нужно провести полноценный ревью пулл реквестов как минимум 2х (двух) ваших одногруппников или ребят из параллельной группы, как вам удобно. Для этого вам нужно:
  1. Зафиксировать (commit) и отправить (push) свои последние изменения в свой удаленный репозиторий (чтобы не потерять их).
  2. Забрать последние изменения из базового репозитория (см. ниже "Синхронизация с базовым репозиторием").
  3. Выбрать pull-request из [множества pull-requests](https://github.com/KhurtinDN/fizteh-java-2015/pulls)
  3. Создать локальную ветку и вмержить в нее пулл реквест проверяемого студента. Например, для student123, который создал pull-request из своей вертки master, будет следующее:
  
    ```
    git checkout -b student123-master master
    git pull git@github.com:student123/fizteh-java-2015.git master
    ```
  4. Запустить приложение с разными параметрами и убедиться, что оно удовлетворяет спецификации (заданию) или указать на ошибки.
  5. Посмотреть код и попытаться выявить возможные нефункциональные ошибки и недочёты.
  6. После исправлений вашего коллеги - проверить ещё раз.
  7. В [журнале ревью](https://docs.google.com/spreadsheets/d/166_8McRUbSk8oknRXPlXsd97uLGI5gHHImTieuKHnVk/edit#gid=0) на пересечении себя для каждой задачи вписать кого вы проверяли (например,  Алексей Зотов хочет проверить у Ольги Сопильняк задание 2, тогда Алексей вписывает "Ольга Сопильняк" в ячейку на пересечении TwitterStream и своего имени "Алексей Зовтов"). Только смотрите, чтобы не было на один pull-review слишком много человек. Желательно, чтобы каждый pull-request ревьювил один-два студента (максимум три, не нужно всем накидываться на одно решение какого-нибудь студента :) ).

## Синхронизация с базовым репозиторием
Периодически синхронизируйтесь с базовым репозиторием, чтобы получать актуальные версии скриптов для сборки и примеров. 

1. Если в списке репозиториев, возвращаемых `git remote` у вас нет upstream, то добавьте себе удаленный репозиторий: 

  ```
  git remote add upstream https://github.com/KhurtinDN/fizteh-java-2015.git
  ```
2. Чтобы подхватить изменения из базового репозитория, в своём master делайте

  ```
  git pull upstream master
  ```
