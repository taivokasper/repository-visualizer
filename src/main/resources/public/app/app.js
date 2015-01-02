var app = angular.module('app', ['ui.router', 'ngResource', 'ngVideo']);

app.config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.otherwise('/');

    $stateProvider
        .state('index', {
            url: '/',
            templateUrl: 'views/index.html',
            controller: 'IndexController'
        })
        .state('index.video', {
            url: '/video/:repoName',
            templateUrl: 'views/video.html',
            controller: 'VideoController'
        });
});