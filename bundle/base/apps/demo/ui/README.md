
### Install Packages

`npm install`

### Watch changes and auto recompile

`npm run watch`

### Installation requirements

On Windows with an Administrator terminal, run:

```
npm install --global --production windows-build-tools
npm install --global node-gyp
```

With others Python version, then configure the Python 2.7 path:

```
node-gyp --python /path/to/python2.7
npm config set python /path/to/executable/python2.7
```
