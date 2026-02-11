const path = require('path');

module.exports = {
    mode: 'development',
    entry: "./src/collector/collector.ts",
    output: {
        path: path.resolve(__dirname, '../../dist'),
        filename: 'collector/collector.js',
        clean: true
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
                        configFile: 'tsconfig.collector.json'
                    }
                },
                exclude: /node_modules/,
            },
        ],
    },
    devtool: false
};
