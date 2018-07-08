# incubator project
#### spawning new cool projects
<br/>

# prism
#### brute force estimation of time complexity of any algorithm

## run it
1. clone the project
2. compile using ```mvn clean install``` <sup>1</sup> 
3. run from the command line using ```java -jar target/incubator-0.0.1.jar -Dspring.profiles.active=absolute```

<sup>1</sup> installing [maven](https://maven.apache.org/install.html)

## *prism* modes
*prism* computes algorithm´s cpu times either in absolute execution time, via [jmx](http://www.oracle.com/technetwork/articles/java/javamanagement-140525.html) or using the [sigar](https://github.com/hyperic/sigar) library.<br/>
To specify the desired method 3 spring profiles were created: ```absolute```, ```jmx``` and ```sigar```, respectively.<br/>
Here are the commads to run *prism* in each of the modes:<br/>
* ```java -jar target\incubator-0.0.1.jar -Dspring.profiles.active=absolute```
* ```java -jar target\incubator-0.0.1.jar -Dspring.profiles.active=jmx```
* ```java -jar target\incubator-0.0.1.jar -Dspring.profiles.active=sigar -Djava.library.path=./src/main/resources/sigar-native-libs```<sup>1</sup><br/>

<sup>1</sup> notice the sigar native library added to the libraries path
