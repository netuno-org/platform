
# Load Test

Simple web stress mechanisms using ApacheBench (Apache Benchmark).

Useful for using with VisualVM to find leaks.

### GET

```
./get.sh http://localhost:9000/services/my-service
```

### POST

The request body is loaded from `post.json`. Create that file in this directory before running the script; for example:

```json
{}
```

```
./post.sh http://localhost:9000/services/my-service
```
