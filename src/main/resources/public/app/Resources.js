angular.module('rv').factory('RepoListResource', function ($resource) {
    'use strict';

    return $resource('rest/repo/list', {}, {
        getRepos: {method: 'GET', isArray: true}
    });
});

angular.module('rv').factory('RepoResource', function ($resource) {
    'use strict';

    return $resource('rest/repo/:repoName', {repoName: '@repoName'});
});

angular.module('rv').factory('GenerationInfoResource', function ($resource) {
    'use strict';

    return $resource('rest/generation/generationInfo/:repoName', {}, {
        getForRepo: {method: 'GET', params: {repoName: '@repoName'}},
        get: {method: 'GET'}
    });
});

angular.module('rv').factory('GenerateVideoResource', function ($resource) {
    'use strict';

    return $resource('rest/generation/generate/:repoName', {repoName: '@repoName'}, {
        generate: {method: 'POST'}
    });
});