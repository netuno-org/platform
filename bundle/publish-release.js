const https = require('https');
const fs = require('fs');
const path = require('path');
const config = require(path.join(__dirname, 'publish-release.json'));

const publishMode = process.argv[2];

const publishModeRelease = async () => {
    console.log();
    console.log(`Publishing files in the ${publishMode} release.`);
    console.log();
    
    let created = false;
    const releases = await request({
        path: '/releases'
    });
    let release = releases.find((r) => r.tag_name == publishMode);

    if (release && publishMode == 'stable') {
      await request({
        method: 'DELETE',
        path: `/releases/${release.id}`
      });
      await request({
        method: 'DELETE',
        path: `/git/refs/tags/${publishMode}`
      });
      release = null;
    }
  
    if (!release) {
      release = await request({
        method: 'POST',
        path: '/releases',
        data: {
          owner: 'netuno-org',
          repo: 'platform',
          tag_name: publishMode,
          target_commitish: 'main',
          name: publishMode.substring(0, 1).toUpperCase() + publishMode.substring(1),
          body: releaseDescription({url: publishMode, install: publishMode}),
          draft: false,
          prerelease: publishMode == 'testing',
          generate_release_notes: false,
        }
      });
    }
    
    const assets = await request({
        path: `/releases/${release.id}/assets`
    });

    const bundleFiles = ['netuno.zip', 'netuno.json', 'netuno-setup.jar'];

  if (created === false) {
    await (async ()=> {
      for (const asset of assets.filter((a) => bundleFiles.some((f) => a.name == f))) {
        await request({
          method: 'DELETE',
          path: `/releases/assets/${asset.id}`
        });
        console.log(`Asset ${asset.name} removed.`);
      }
    })();
  }

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
};

const publishVersionRelease = async () => {
  const getVersion = (separator = '.') => {
    const date = new Date();
    return date.getFullYear() + separator + (date.getMonth() + 1 < 10 ? '0'+ (date.getMonth() + 1) : date.getMonth() + 1);
  };

  console.log();
  console.log(`Publishing files in the ${getVersion()} release.`);
  console.log();

  const release = await request({
    method: 'POST',
    path: '/releases',
    data: {
      owner: 'netuno-org',
      repo: 'platform',
      tag_name: getVersion('_'),
      target_commitish: 'main',
      name: getVersion(),
      body: releaseDescription({url: getVersion('_'), install: getVersion()}),
      draft: false,
      prerelease: false,
      generate_release_notes: false,
    }
  });
    
    const assets = await request({
        path: `/releases/${release.id}/assets`
    });

    const bundleFiles = [`netuno-${getVersion('_')}.zip`, 'netuno-setup.jar'];

    for (const bundleFile of bundleFiles) {
        console.log(`Publishing ${bundleFile}...`);
        await request({
            method: 'POST',
            bodyFile: path.join(__dirname, '/dist/', bundleFile),
            path: `/releases/${release.id}/assets?name=${bundleFile}`
        });
    }

    console.log();
    console.log(`The ${getVersion()} release was successfully published.`);
    console.log();
};

function releaseDescription(version) {
  const template = [
    "Netuno platform is open-source, polyglot, and low-code, for fast development of reliable web solutions.",
    "Easy database and back-office modeling with the supported databases: PostgreSQL, MariaDB/MySQL, and MSSQL.",
    "Real-time API development with the supported languages: JavaScript, Python, Ruby, Groovy, Kotlin, and Java.",
    "You can use it to run over Linux, Windows, and MacOS.",
    "# Installation",
    "[Here is more about the installation process, in the official documentation.](https://doc.netuno.org/docs/get-started/installation/)",
    "## Linux or Mac OS X",
    "Create the Netuno directory, and execute in the **terminal**:",
    "```plaintext\nmkdir -p netuno && cd netuno\n```",
    "Choose one of the following methods of **download**:",
    "* Download with *cURL*:",
    "```plaintext\ncurl -L https://github.com/netuno-org/platform/releases/download/%URL-VERSION%/netuno-setup.jar -o netuno-setup.jar\n```",
    "* Download with *Wget*:",
    "```plaintext\nwget https://github.com/netuno-org/platform/releases/download/%URL-VERSION%/netuno-setup.jar -O netuno-setup.jar\n```",
    "After downloading one of the previous methods, execute the **Netuno's installation**:",
    "* Installation of this version:",
    "```plaintext\njava -jar netuno-setup.jar install version=%INSTALL-VERSION%\n```",
    "> On MAC OS X, you need to disable the quarantine, thus inside the `netuno` home folder, please run:\n> `sudo xattr -r -d com.apple.quarantine .`",
    "## Windows",
    "In Windows open the terminal of **PowerShell** go to the start menu and type: `powershell`",
    "Now copy and paste the command below and follow the **Installation** steps:",
    "```plaintext\nmd netuno; cd netuno\nInvoke-WebRequest -Uri \"https://github.com/netuno-org/platform/releases/download/%URL-VERSION%/netuno-setup.jar\" -OutFile \"netuno-setup.jar\"\n```",
    "After completing the previous download, execute the **Netuno's installation**:",
    "* Installation of this version:",
    "```plaintext\njava -jar netuno-setup.jar install version=%INSTALL-VERSION%\n```"
  ];
  return template.join('\n\n')
    .replaceAll('%URL-VERSION%', version.url)
    .replaceAll('%INSTALL-VERSION%', version.install)
};

function request({method = 'GET', path, data = null, bodyFile}) {
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
        if (data) {
            options.headers['Content-Type'] = 'application/json';
            options.headers['Content-Length'] = Buffer.byteLength(JSON.stringify(data))
        } else if (bodyFile) {
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
        
            let resData = '';
        
            res.on('data', (chunk) => {
                resData += chunk;
            });
        
            res.on('close', () => {
                if (data != '' && res.statusCode !== 204) {
                    resolve(JSON.parse(resData));
                } else {
                    resolve();
                }
            });
        });
        request.on('error', (err) => {
            console.error(`Encountered an error trying for ${options.path}: ${err.message}`);
            reject(err);
        });
        if (data) {
          request.write(JSON.stringify(data));
        } else if (bodyFile) {
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

(async () => {
  if (publishMode == 'testing') {
    await publishModeRelease();
  } else if (publishMode == 'stable') {
    await publishModeRelease();
    await publishVersionRelease();
  } else {
    console.error('Invalid publish mode:', publishMode);
  }
})();
