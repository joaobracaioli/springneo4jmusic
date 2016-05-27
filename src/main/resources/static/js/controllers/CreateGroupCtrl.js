(function() {

var module = angular.module('ionicspotify.controllers');

    module.controller('CreateGroupCtrl',  function($scope, apiREST,$rootScope, $state, $ionicHistory, $ionicPopup, Api, $ionicLoading ) {
      //var self = this;
        console.log("Entrei no GroupController");

        var contexto=[];
        $scope.name = "";
        $scope.selection = [];
        $scope.selectionList = [{id:1, name: 'Academia'}, {id:4, name: 'Bar/Restaurante'},
                                              {id:2, name: 'Festa'},  {id:3, name: 'Trabalho'},
                                               {id:5, name: 'Viagem'}];

        $scope.setSelected = function (idx, item) {
            var pos = $scope.selection.indexOf(idx);
            if (pos == -1) {
                $scope.selection.push(idx);
                contexto.push(item.name);
            } else {
                $scope.selection.splice(pos, 1);
                contexto.splice(pos, 1);
            }

            console.log(contexto);
         };

         $scope.createGroup = function (nameGroup) {
           $ionicLoading.show({
             template: 'Loading...'
           });
                //usuario
                  console.log("Entrei para criar +"+Api.getId_spotify());
                  var id_spotify = Api.getId_spotify();


                  if (id_spotify == null) {
                    console.log("erro, sem usuario em localStorage");
                        var alertPopup = $ionicPopup.alert({
                               title: 'Erro - Usuário',
                               template: 'Verifique se você está logado.'
                             });
                             alertPopup.then(function(res) {
                               console.log('Deu ruim, tem que problema no localStorage');
                             });

                    return ;
                  }


                  var dataObj = {
                    name : nameGroup,
                    caracteristicas : contexto,

                  };


                  /* comentado para realizar teste        */
                  apiREST.createGroup(id_spotify, dataObj).then(
                    
                                            function(dataGroup) {
                                              $ionicHistory.nextViewOptions({
                                                disableBack: true
                                              });
                                                 $rootScope.id_group = dataGroup.id;
                                                $ionicLoading.hide();
                                                console.log(dataGroup);
                                                $state.go('app.group');

                                            },
                                            function(errResponse){
                                                console.error('Erro ao criar grupo'+errResponse);
                                            });

           };


});


})();
