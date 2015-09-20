## Порядок работы с github

## Инициализация
1. Создайте [fork](https://help.github.com/articles/fork-a-repo) репозитория [fizteh-java-2015](https://github.com/KhurtinDN/fizteh-java-2015).
2. Создайте локальный клон своего репозитория
3. В директории [projects](projects) нужно создать maven модуль командой
  
  ```
  mvn -B archetype:generate -DarchetypeGroupId=org.apache.maven.archetypes -DarchetypeArtifactId=maven-archetype-quickstart \
  -DgroupId=ru.mipt.diht.students -DartifactId=<Your github login>
  ```
4. Добавить свой модуль в родительский [projects/pom.xml](projects/pom.xml). Нужно, чтобы можно было из корня собирать все модули разом.
  
  ```
  <modules>
      <module>dkhurtin</module>
      <!--Add you module here-->
  </modules>
  ```
5. Добавить в свой pom.xml (подобно, [pom.xml](projects/dkhurtin/pom.xml) ссылку на родительский модуль. Нужно, чтобы унаследовать общие параметры сборки проекта.
  
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
3. Запускать приложение можно прямо из maven командой ```mvn exec:java -Dexec.mainClass=<Your main class name> -Dexec.arguments=<arguments>```. Она сама добавит в classpath транзативно все необходимые библиотеки из pom.xml dependencies. 
  
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
