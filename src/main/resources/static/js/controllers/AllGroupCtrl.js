(function() {

'use strict';

var module = angular.module('ionicspotify.controllers');

  module.controller('AllGroupCtrl', function ($scope, apiREST, $rootScope, $ionicHistory, $state,$timeout, Api) {

      console.log("Entre no AllGroupCtrl ")
        $scope.groups = [];

        var promise;
        var contador = 0;
        var aberto = false;
        var qtd_antes = 0;
        /*
        $scope.carregar = function () {
           aberto = true;
           ativarRefresh()
         }
        function ativarRefresh() {
           contador--;
           if (contador === 0) {
             atualizar();
             contador = 8;
           }
           promise = $timeout(ativarRefresh, 1000);
         }
         */
        $scope.atualizar = function () {
            console.log("atualizar");
            $scope.groups =  apiREST.getGroups().then(
                                  function successCallback(data) {
                                      console.log(data);

                                         $scope.groups = data;

                                  },
                                  function errorCallback(errResponse){
                                      console.error('Error while fetching Currencies');
                                  }
            ).finally(function() {
                // Stop the ion-refresher from spinning
              $scope.$broadcast('scroll.refreshComplete');
            });
      }

        $scope.addGroup = function (idGroup){

          console.log(Api.getId_spotify())
            apiREST.addGroup(Api.getId_spotify(), idGroup).then(

                        function successCallback(data) {


                            console.log("sucesso ..."+ data);

                            $ionicHistory.nextViewOptions({
                              disableBack: true
                            });
                               $rootScope.id_group = idGroup;
                               //$timeout.cancel(promise);
                               //aberto = false;
                              $state.go('app.group');

                        },
                        function errorCallback(errResponse){
                            console.error('Error ao entrar no grupo');
                        }
            );


        }



  })
})();
