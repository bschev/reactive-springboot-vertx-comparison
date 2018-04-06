## Comparison of Spring Boot WebFlux and Vert.x

Simple test application implemented in three ways:

* Spring Boot WebFlux 2.0.0, running in Netty 4.1.22
  * **(1)** [Annotated Controller](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-controller)
  * **(2)** [Functional Endpoint](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-fn)
* Vert.x 3.5.1 (vertx-core, vertx-web), running on Netty 4.1.19
  * **(3)** [Vert.x](https://vertx.io/docs) Callback API

### Application

Serves static HTTP Resources `/resources/data1.json`, `/resources/data2.json`  
(1)(2) [`WebConfig`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/WebConfig.java#L14-L15)  
(3) [`Server`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/vertx/src/main/java/com/bschev/reactive/Server.java#L32) 

The HTTP GET Endpoint `/data/{id}`  
(1) [`DataController`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/annotated/DataController.java#L25)  
(2) [`RoutingConfiguration`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/functional/RoutingConfiguration.java#L17)  
(3) [`Server`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/vertx/src/main/java/com/bschev/reactive/Server.java#L34) 

makes a HTTP request to retrieve the static JSON resource  
(1)(2) [`DataWebClient`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/DataWebClient.java#L23-L26)  
(3) [`DataWebClient`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/vertx/src/main/java/com/bschev/reactive/DataWebClient.java#L23) 

instantiates a Java object from the JSON  
(1)(2) [`DataWebClient`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/DataWebClient.java#L29)  
(3) [`DataWebClient`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/vertx/src/main/java/com/bschev/reactive/DataWebClient.java#L27)

encodes the POJO back to JSON and writes it to the HTTP response body.  
(1) [`DataController`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/annotated/DataController.java#L26) (handled by Spring)  
(2) [`DataHandler`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/springboot/src/main/java/com/bschev/reactive/functional/DataHandler.java#L33) (handled by Spring)  
(3) [`Server`](https://github.com/bschev/reactive-springboot-vertx-comparison/blob/39aa6bb3eb1af28367593030325bd3c09e8e65d5/vertx/src/main/java/com/bschev/reactive/Server.java#L57)
 
#### Run Applications

```
 # Spring WebFlux 
 $ mvn clean package -f springboot
 $ java -jar springboot/target/springboot-1.0-SNAPSHOT.jar
 ...
 # (1) Annotated Controller
 $ curl http://localhost:8083/annotated/data/2
 {"id":2,"value":"two","active":false,"props":["qwe","wer","ert"]}
 
 # (2) Functional Endpoint
 $ curl http://localhost:8083/functional/data/2
 {"id":2,"value":"two","active":false,"props":["qwe","wer","ert"]}
 
 # (3) Vert.x
 $ mvn clean package -f vertx
 $ java -jar vertx/target/vertx-1.0-SNAPSHOT-fat.jar
 ...
 $ curl http://localhost:8084/data/2
 {"id":2,"value":"two","active":false,"props":["qwe","wer","ert"]}
```
 
 
### Benchmark

Dirty benchmark, executed on a GCE 4 vCPUs, 3,6 GB instance.

#### Spring Annotated Controller

```
# warm up
$ ab -n 30000 -c 100 http://localhost:8083/annotated/data/2
...
# measure
$ ab -n 10000 -c 100 http://localhost:8083/annotated/data/2
This is ApacheBench, Version 2.3 <$Revision: 1706008 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8083

Document Path:          /annotated/data/2
Document Length:        76 bytes

Concurrency Level:      100
Time taken for tests:   3.810 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1880000 bytes
HTML transferred:       760000 bytes
Requests per second:    2624.96 [#/sec] (mean)
Time per request:       38.096 [ms] (mean)
Time per request:       0.381 [ms] (mean, across all concurrent requests)
Transfer rate:          481.93 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       3
Processing:     4   38   7.7     37      81
Waiting:        4   38   7.7     37      80
Total:          6   38   7.6     37      81

Percentage of the requests served within a certain time (ms)
  50%     37
  66%     39
  75%     40
  80%     40
  90%     43
  95%     48
  98%     67
  99%     69
 100%     81 (longest request)
```

#### Spring Functional Endpoint

```
# warm up
$ ab -n 30000 -c 100 http://localhost:8083/functional/data/2
...
# measure
$ ab -n 10000 -c 100 http://localhost:8083/functional/data/2
This is ApacheBench, Version 2.3 <$Revision: 1706008 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8083

Document Path:          /functional/data/2
Document Length:        76 bytes

Concurrency Level:      100
Time taken for tests:   3.711 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1880000 bytes
HTML transferred:       760000 bytes
Requests per second:    2695.02 [#/sec] (mean)
Time per request:       37.106 [ms] (mean)
Time per request:       0.371 [ms] (mean, across all concurrent requests)
Transfer rate:          494.79 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       4
Processing:     6   37   9.3     37      80
Waiting:        6   37   9.3     37      80
Total:          8   37   9.3     37      80

Percentage of the requests served within a certain time (ms)
  50%     37
  66%     41
  75%     43
  80%     43
  90%     46
  95%     49
  98%     64
  99%     69
 100%     80 (longest request)
```

#### Vert.x

```
# warm up
$ ab -n 30000 -c 100 http://localhost:8084/data/2
...
# measure
$ ab -n 10000 -c 100 http://localhost:8084/data/2
This is ApacheBench, Version 2.3 <$Revision: 1706008 $>
Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
Licensed to The Apache Software Foundation, http://www.apache.org/

Benchmarking localhost (be patient)
Completed 1000 requests
Completed 2000 requests
Completed 3000 requests
Completed 4000 requests
Completed 5000 requests
Completed 6000 requests
Completed 7000 requests
Completed 8000 requests
Completed 9000 requests
Completed 10000 requests
Finished 10000 requests


Server Software:        
Server Hostname:        localhost
Server Port:            8084

Document Path:          /data/2
Document Length:        65 bytes

Concurrency Level:      100
Time taken for tests:   1.503 seconds
Complete requests:      10000
Failed requests:        0
Total transferred:      1510000 bytes
HTML transferred:       650000 bytes
Requests per second:    6654.65 [#/sec] (mean)
Time per request:       15.027 [ms] (mean)
Time per request:       0.150 [ms] (mean, across all concurrent requests)
Transfer rate:          981.30 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.2      0       4
Processing:    10   15   1.7     14      32
Waiting:       10   15   1.7     14      31
Total:         12   15   1.8     15      33

Percentage of the requests served within a certain time (ms)
  50%     15
  66%     15
  75%     15
  80%     16
  90%     16
  95%     17
  98%     19
  99%     24
 100%     33 (longest request)
```
