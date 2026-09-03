
# Load Test

Simple web stress mechanisms using ApacheBench (Apache Benchmark).

Useful for using with VisualVM to find leaks.

### GET

```
./get.sh http://localhost:9000/services/my-service
```

### POST

`post.sh` sends the request body from a `post.json` file (`ab -p post.json -T application/json`). That file is not included, so create it in this directory first, for example:

```json
{}
```

```
./post.sh http://localhost:9000/services/my-service
```
