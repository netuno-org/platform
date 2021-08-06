
# Load Test

Simple web stress mecanisms using ApacheBench (Apache Benchmark).

Useful with the VisualVM to find leaks.

### GET

```
./get.sh http://localhost:9000/services/my-service
```

### POST

Parameters are loaded in `post.json`.

```
./post.sh http://localhost:9000/services/my-service
```
