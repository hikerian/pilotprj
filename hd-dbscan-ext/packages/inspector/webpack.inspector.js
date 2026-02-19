const path = require('path');
const CopyPlugin = require('copy-webpack-plugin');

module.exports = {
    mode: 'development',
    entry: "./src/inspector/inspector.ts",
    output: {
        path: path.resolve(__dirname, '../../dist'),
        filename: 'inspector/inspector.js'
    },
    resolve: {
        extensions: ['.ts', '.js'],
        alias: {
            '@': path.resolve(__dirname, 'src'),
        },
    },
    module: {
        rules: [
            {
                test: /\.ts$/,
                use: {
                    loader: 'ts-loader',
                    options: {
                        configFile: 'tsconfig.inspector.json'
                    }
                },
                exclude: /node_modules/,
            }
        ],
    },
    plugins: [
        new CopyPlugin({
            patterns: [
                {
                    from: 'src/inspector/inspector.html',
                    to: 'inspector/inspector.html'
                },
                {
                    from: 'src/inspector/icon.png',
                    to: 'inspector/icon.png'
                },
                {
                    from: '../../node_modules/jquery/dist/jquery.js',
                    to: 'inspector/jquery.js'
                }
            ]
        })
    ],
    devtool: false
};
