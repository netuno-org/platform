
# Load Test

Simple web stress mechanisms using ApacheBench (Apache Benchmark).

Useful for using with VisualVM to find leaks.

### GET

```
./get.sh http://localhost:9000/services/my-service
```

### POST

Parameters are loaded in `post.json`.

```
./post.sh http://localhost:9000/services/my-service
```
