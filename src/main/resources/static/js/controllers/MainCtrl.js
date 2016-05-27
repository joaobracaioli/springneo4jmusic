(function() {

'use strict';

var module = angular.module('ionicspotify.controllers');

  module.controller('MainCtrl', function ($scope, apiREST) {

      $scope.qtdGroups = 0;

      apiREST.qtdByGroups().then(
        function(data) {

          $scope.qtdGroups =data;

        },
        function(errResponse){
            console.error('Error não consegui pegar o valor de qtd de group');
        });


  })
})();
