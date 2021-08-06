#!/bin/bash

ab -p post.json -T application/json -H 'Accept: application/json' -n 1000 -c 10 $1

