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

angular.module('rv').factory('GenerateVideoResource', function ($resource) {
	'use strict';

	return $resource('rest/repo/:repoName', {repoName: '@repoName'}, {
		generate: {method: 'POST'}
	});
});