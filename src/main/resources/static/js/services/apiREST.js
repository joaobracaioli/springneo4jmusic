(function() {

'use strict';

    var module = angular.module('ionicspotify.apiREST', [])

    module.factory('apiREST',  ['$http', '$q','$location', function($http, $q, $location) {


          //var baseUrl = 'http://192.168.100.10:8080/v1';
      if( $location.host() =="localhost"){
            var baseUrl = 'http://localhost:8100/v1';
          
          }else{
             var baseUrl = 'http://52.40.102.85:8080/v1';

          }
          /*
*/

        return {

            getGroups: function() {
                var ret = $q.defer();

                $http({
                  method: 'GET',
                  url: baseUrl + '/group',
                  headers: {
                    'Content-Type': 'application/json; charset=utf-8'
                  }
                 }).then(function successCallback(response) {
                      console.log("Api  " +response);
                        console.log(response);
                              ret.resolve(response.data);

                    },function errorCallback(response) {
                        console.log(response)
                        ret.reject(response);
                    });

                    return ret.promise;
            },
            exitGroup: function(id_spotify, id_group) {
                var ret = $q.defer();

                $http({
                             method: 'PUT',
                             url: baseUrl + '/group/removeUser/'+id_group+'/'+id_spotify,
                            data: "",
                            headers: {
                              'Content-Type': 'application/json'
                          }
                         }).then(function successCallback(response) {
                               console.log(response);
                               ret.resolve(response.data);
                         }, function errorCallback(response) {
                                console.log(response)
                                ret.reject(response);
                         });

                    return ret.promise;
            },
            qtdByGroups: function() {
                var ret = $q.defer();

                $http({
                  method: 'GET',
                  url: baseUrl + '/group/getSizeGroup',
                  headers: {
                    'Content-Type': 'application/json; charset=utf-8'
                  }
                 }).then(function successCallback(response) {
                              ret.resolve(response.data);
                    },function errorCallback(response) {
                        console.log(response)
                        ret.reject(response);
                    });

                    return ret.promise;
            },
            getGenre: function(id_group) {
                var ret = $q.defer();

                $http({
                  method: 'GET',
                  url: baseUrl + '/group/getGenreGroup/'+id_group,
                  headers: {
                    'Content-Type': 'application/json'
                  }
                 }).then(function successCallback(response) {
                        console.log(response);
                        ret.resolve(response.data);

                    },function errorCallback(response) {
                        console.log(response)
                        ret.reject(response);
                    });

                    return ret.promise;
            },
           getParticipants: function (id_group) {
             var ret = $q.defer();

                 $http({
                              method: 'GET',
                              url: baseUrl + '/group/getParticipants/'+id_group,
                              headers: {
                                'Content-Type': 'application/json'
                            }
                          }).then(function successCallback(response) {
                                console.log(response);
                                  ret.resolve(response.data);
                          }, function errorCallback(response) {
                                console.log(response)
                                ret.reject(response);
                          });
                   return ret.promise;

           },
           getbyEmail : function ( ){
                  $http.get(baseUrl + '/group', {
                 /*   headers: {
                        'Authorization': 'Bearer ' + Auth.getAccessToken()
                    }*/
                }).success(function(r) {
                    console.log('Groups', r);
                    ret.resolve(r);
                }).error(function(err) {
                    console.log('failed to get groups', err);
                    ret.reject(err);
                });
                return ret.promise;



            },
            addGroup: function(id_spotify, id_group) {
                            var ret = $q.defer();

                                $http({
                                             method: 'POST',
                                             url: baseUrl + '/group/add/'+id_group+'/'+id_spotify,
                                            data: "",
                                            headers: {
                                              'Content-Type': 'application/json'
                                          }
                                         }).then(function successCallback(response) {
                                               console.log(response);
                                               ret.resolve(response.data);
                                         }, function errorCallback(response) {
                                                console.log(response)
                                                ret.reject(response);
                                         });
                                  return ret.promise;

            },
            createGroup: function(id_spotify, data) {
                            var ret = $q.defer();

                                $http({
                                             method: 'POST',
                                             url: baseUrl + '/group/groupCreate/'+id_spotify,
                                             data: data,
                                             headers: {
                                               'Content-Type': 'application/json'
                                           }
                                         }).then(function successCallback(response) {
                                                 console.log(response.data);
                                                 ret.resolve(response.data);
                                         }, function errorCallback(response) {
                                                console.log(response)
                                                ret.reject(response);
                                         });
                                  return ret.promise;

            },
            createUser: function (data, token){

                            var ret = $q.defer();
                                $http({
                                      method: 'POST',
                                      url: baseUrl+'/teste2/'+token,
                                      data: data,
                                      headers: {
                                        'Content-Type': 'application/json; charset=utf-8'
                                      }
                                    }).then(function successCallback(response) {
                                          console.log(response);
                                          ret.resolve(response);
                                    }, function errorCallback(response) {
                                          console.log(response);
                                           ret.reject(response);
                                    });
                            return ret.promise;
        }
      }
    }])
})();
