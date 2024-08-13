const path = require('path');

module.exports = (env, argv) => {
    const isProduction = argv.mode === 'production';

    return {
        entry: './src/index.ts',
        module: {
            rules: [
                {
                    test: /\.tsx?$/,
                    use: 'ts-loader',
                    exclude: /node_modules/,
                },
            ],
        },
        resolve: {
            extensions: ['.tsx', '.ts', '.js'],
        },
        output: {
            filename: isProduction ? 'sdk.min.js' : 'sdk.js',
            path: path.resolve(__dirname, 'dist'),
            library: {
                name: 'LogBat',
                type: 'umd',
                export: 'default'
            },
            globalObject: 'this',
            umdNamedDefine: true
        },
        devtool: isProduction ? 'source-map' : 'eval-source-map',
        mode: isProduction ? 'production' : 'development',
        optimization: {
            minimize: isProduction
        }
    };
};