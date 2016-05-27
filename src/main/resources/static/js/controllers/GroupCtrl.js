(function() {

var module = angular.module('ionicspotify.controllers');

  module.controller('GroupCtrl',  function($scope, apiREST, $stateParams,$rootScope, $state, Spotify, $timeout,
                                            $ionicPlatform, $ionicPopup,$ionicHistory,Api) {

    $scope.listname = $stateParams.listname;

    $scope.audio = new Audio();
    $scope.genres = [];
    $scope.participants = [];
    $scope.tracks = [];

    var promise;
    var contador = 10;
    var aberto = false;
    var id_group = $rootScope.id_group;
    var string_push = "";
    var qdo_old = 0;



    var deregisterFirst = $ionicPlatform.registerBackButtonAction(function(event) {
           if (true) { // your check here

             $ionicPopup.confirm({
               title: 'Sair ',
               template: 'Deseja sair do grupo?'
             }).then(function(res) {
               if (res) {
                  sair();
               }
             })
          }

         }, 102);

   $scope.$on('$destroy', deregisterFirst);

    function entrar() {
       aberto = true;
       getParticipantes();
       ativarRefresh();
       geraRecommendacao();

    }

    function ativarRefresh() {
             contador--;

             if (contador === 0) {


               getParticipantes();
               //verificar se existe numero mínimo
                var qtdParticipantes = $scope.participants.length;

                if(qtdParticipantes >1){
                  if(qdo_old != qtdParticipantes){
                    console.log("atualizar");
                    atualizar();
                    qdo_old =  $scope.participants.length;

                  }else{
                        console.log("qtdParticipantes"+qtdParticipantes);
                        console.log("qdo_old"+qdo_old);
                        console.log("manteve");
                  }

                }else{
                  console.log("só é considerado um grupo 3 ou mais participantes");
                }

              contador = 10;
             }
             promise = $timeout(ativarRefresh, 1000);
    }
    $scope.sairGroup = function sair(){
      console.log("sair");

             apiREST.exitGroup(Api.getId_spotify(), id_group).then(
                                function sucesso(data) {
                                  console.log(data);
                                  $ionicHistory.nextViewOptions({
                                    disableBack: true
                                  });

                                  $state.go('app.main');
                                },
                                function error(errResponse){
                                    console.error('Error ao pegar os participants');
                                });

            $timeout.cancel(promise);
            aberto = false;
    }
    function atualizar() {
         getGenre();

       //getParticipantes();
   }

   function getParticipantes(){

        apiREST.getParticipants(id_group).then(
                                  function sucesso(data) {
                                        $scope.participants = data;
                                  },
                                  function error(errResponse){
                                      console.error('Error ao pegar os participants');
                                  });
   };

   function getGenre(){
            apiREST.getGenre(id_group).then(
                                      function sucesso(data) {
                                        console.log("pegou o genero")
                                        console.log(data);
                                        $scope.genres = data;

                                                g = [];
                                                for (var i = 0; i < data.length, i<3; i++) {
                                                  g.push(data[i].name);
                                                }

                                                string_push = {
                                                  seed_genres: g,
                                                  min_energy: 0.7,
                                                  min_popularity: 70
                                                };


                                             geraRecommendacao(string_push);
                                                  

                                      },
                                      function error(errResponse){
                                          console.error('Error ao recuperar os generos');
                                      });


    }
    function geraRecommendacao(string_push){

      Spotify.getRecommendations( {
                                                  seed_genres: "rock",
                                                  min_energy: 0.7,
                                                  min_popularity: 70
                                                }).then(function (data) {
                                                                             console.log("entrei  recommendations");
                                                                             console.log(data);
                                                                             $scope.tracks = data.tracks;

      });
                                             
    };

    $scope.getPlaylist = function () {
      /*
      Spotify.getPlaylist($stateParams.userId, $stateParams.playlistId).then(function (data) {
        $scope.tracks = data.tracks.items;
      });
      */
    };

    $scope.playTrack = function (trackInfo) {

      $scope.audio.src = trackInfo.preview_url;
      $scope.audio.play();
    };
  /*
    $scope.openSpotify = function (link) {
      window.open(link, '_blank', 'location=yes');
    };
  */
    $scope.stop = function () {
      if ($scope.audio.src) {
        $scope.audio.pause();
      }
    };

    $scope.play = function () {
      if ($scope.audio.src) {
        $scope.audio.play();
      }
    };



    ionic.Platform.ready(function () {
          //$scope.getPlaylist();
          entrar();
    });

  });


})();
