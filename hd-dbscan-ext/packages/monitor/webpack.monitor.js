const path = require('path');

module.exports = {
    mode: 'development',
    entry: './src/monitor/monitor.ts',
    output: {
        path: path.resolve(__dirname, '../../dist'),
        filename: 'monitor/monitor.js'
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
                        configFile: 'tsconfig.monitor.json'
                    }
                },
                exclude: /node_modules/,
            },
        ],
    },
    devtool: false
};
