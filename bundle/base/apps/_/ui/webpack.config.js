
const path = require('path');

module.exports = {
    entry: [ './src/index.jsx', './src/styles/main.less' ],
    devtool: "source-map",
    output: {
        path: __dirname +'/../public/scripts',
        filename: 'main.js'
    },
    module: {
        rules: [
            {
                test: /\.jsx$/,
                include: [
                    path.resolve(__dirname, 'src')
                ],
                exclude: /node_modules/,
                use: [{
                    loader: 'babel-loader'
                }]
            },
            {
                test: /\.js$/,
                include: [
                    path.resolve(__dirname, 'src')
                ],
                exclude: /node_modules/,
                use: [{
                    loader: 'babel-loader'
                }]
            },
            {
                test: /\.less$/,
                include: [
                    path.resolve(__dirname, 'src')
                ],
                exclude: /node_modules/,
                use: [
                    {
                        loader: 'style-loader' // creates style nodes from JS strings
                    },
                    {
                        loader: 'css-loader', // translates CSS into CommonJ,
                        options: {
                            sourceMap: true/*,
                            modules: {
                                mode: 'local',
                                localIdentName: "[local]___[hash:base64:5]",
                                context: path.resolve(__dirname, 'src'),
                                hashPrefix: 'app',
                            }*/
                        }
                    },
                    {
                        loader: 'less-loader', // compiles Less to CSS
                        options: {
                            modifyVars: {
                                'primary-color': '#5b5ce1',
                                'link-color': '#5b5ce1',
                                'border-radius-base': '5px'
                            },
                            javascriptEnabled: true
                        }
                    }
                ]
            },
            {
                test: /\.css$/,
                include: [
                    path.resolve(__dirname, 'src')
                ],
                exclude: /node_modules/,
                use: [
                    'style-loader',
                    'css-loader'
                ]
            }
        ]
    }
};
