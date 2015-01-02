var app = angular.module('app');

app.factory('RepoListResource', function ($resource) {
    return $resource('rest/repo/list', {}, {
        getRepos: {method: 'GET', isArray: true}
    });
});

app.factory('RepoResource', function ($resource) {
    return $resource('rest/repo/:repoName', {repoName: '@repoName'});
});

app.factory('GenerateVideoResource', function ($resource) {
    return $resource('rest/repo/:repoName', {repoName: '@repoName'}, {
        generate: {method: 'POST'}
    });
});