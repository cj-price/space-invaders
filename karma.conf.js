module.exports = function (config) {
    config.set({
        browsers: ['Firefox'],
        basePath: 'out',
        files: ['test.js'],
        frameworks: ['cljs-test'],
        plugins: ['karma-cljs-test', 'karma-firefox-launcher'],
        colors: true,
        logLevel: config.LOG_INFO,
        client: {
            args: ["shadow.test.karma.init"],
            singleRun: true
        }
    })
};
