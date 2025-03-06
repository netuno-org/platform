const https = require('https');
const fs = require('fs');
const path = require('path');
const config = require(path.join(__dirname, 'publish-release.json'));

const publishMode = process.argv[2];

(async () => {
    console.log();
    console.log(`Publishing files in the ${publishMode} release.`);
    console.log();
    
    const releases = await request({
        path: '/releases'
    });
    const release = releases.find((r) => r.tag_name == publishMode);
    if (!release) {
        return console.error(`Release for the publish mode ${publishMode} not exists.`);
    }
    
    const assets = await request({
        path: `/releases/${release.id}/assets`
    });

    const bundleFiles = ['netuno.zip', 'netuno.json', 'netuno-setup.jar'];

    await (async ()=> {
        for (const asset of assets.filter((a) => bundleFiles.some((f) => a.name == f))) {
            await request({
                method: 'DELETE',
                path: `/releases/assets/${asset.id}`
            });
            console.log(`Asset ${asset.name} removed.`);
        }
    })();

    for (const bundleFile of bundleFiles) {
        console.log(`Publishing ${bundleFile}...`);
        await request({
            method: 'POST',
            bodyFile: path.join(__dirname, '/dist/', bundleFile),
            path: `/releases/${release.id}/assets?name=${bundleFile}`
        });
    }
    console.log();
    console.log(`The ${publishMode} release was successfully published.`);
    console.log();
})()

function request({method = 'GET', path, bodyFile}) {
    return new Promise(async (resolve, reject) => {
        const options = {
            method,
            host: 'api.github.com',
            port: 443,
            path: `/repos/netuno-org/platform${path}`,
            headers: {
                'User-Agent': 'Netuno-PublishRelease',
                'Accept': 'application/vnd.github+json',
                'Authorization': `Bearer ${config.apiToken}`,
                'X-GitHub-Api-Version': '2022-11-28',
            },
        };
        if (bodyFile) {
            options.host = 'uploads.github.com';
            options.headers['Content-Type'] = 'application/octet-stream';
            options.headers['Content-Length'] = fs.statSync(bodyFile).size;
        }
        const request = https.request(options, (res) => {
            if (res.statusCode !== 200 && res.statusCode !== 201 && res.statusCode !== 204) {
                console.error(`Did not get an OK for ${options.path}, the code is: ${res.statusCode}`);
                let errorData = '';
                res.on('data', (chunk) => {
                    errorData += chunk;
                });
                res.on('close', () => {
                    console.log(errorData);
                    reject(res);
                });
                return;
            }
        
            let data = '';
        
            res.on('data', (chunk) => {
                data += chunk;
            });
        
            res.on('close', () => {
                if (data != '') {
                    resolve(JSON.parse(data));
                } else {
                    resolve();
                }
            });
        });
        request.on('error', (err) => {
            console.error(`Encountered an error trying for ${options.path}: ${err.message}`);
            reject(err);
        });
        if (bodyFile) {
            const streamFile = fs.createReadStream(bodyFile);
            streamFile.on('finish', ()=> {
                request.end();
            });
            streamFile.pipe(request);
        } else {
            request.end();
        }
    });
}
