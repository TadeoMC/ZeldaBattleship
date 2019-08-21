var games = new Vue  ({
    el:"#games",
    data:{
    register: false,
    jason:{},
    scores:[],
    },
    methods:{
    joinGame : function (gameId) {
                   $.post("/api/game/" + gameId +"/players",
                    function(data){
                       window.location = "/web/game.html?gp=" + data.gamePlayerId
                       }
                       ).fail(function(){alert("ERROR")});
               }
    }
})

$.getJSON("/api/games").done(function(data){

 games.jason = data;

 for (game = 0 ; game < data.games.length ; game++)
 {
    var gamePlayers = data.games[game].gamePlayers;

    for (i = 0 ; i < gamePlayers.length ; i++)
    {
        player = gamePlayers[i].player;
        if (gamePlayers[i].score !== undefined) score = gamePlayers[i].score.score;

        if(games.scores.find(x => x.id == player.playerId) !== undefined ){
            p = games.scores.find(x => x.id == player.playerId)
            p.score += score;
            if (score == 1) p.won ++;
            else if(score == 0) p.lost ++;
            else p.tied ++;
        }
        else {

            var p = {"id": player.playerId , "firstName": player.firstName,"lastName": player.lastName, "score": score,"won": 0,"lost": 0,"tied": 0};
            if (score == 1) p.won ++;
            else if(score == 0) p.lost ++;
            else p.tied ++;
            games.scores.push( p )
        }
    }
 }
    games.scores = games.scores.sort(function (a,b){return b.score - a.score});

    if(games.jason.currentPlayer != 'GEST')
    {
         playerId = games.jason.currentPlayer.playerId;
         games.jason.games.sort(function (a,b){
               a_value = 0;
               b_value = 0;
               a.gamePlayers.forEach(function(gp){
                    if(gp.player.playerId == playerId)
                    return a_value = 1;
               })
               b.gamePlayers.forEach(function(gp){
                    if(gp.player.playerId == playerId)
                    return b_value = 1;
               })
               return b_value - a_value;
         })
    }
    games.jason.games.sort(function (a,b){
            if(a.state == "finished" && b.state == "inProgress")return 1;
            else if(b.state == "finished" && a.state == "inProgress")return -1;
            else return 0;
        })
})

function login(evt) {
  evt.preventDefault();
  var form = evt.target;
  $.post("/api/login",
         { email: form["email"].value,
           password: form["password"].value })
           .done(function(){location.reload()})
           .done(function(){
                if (games.register)
                regist();
           })
           .fail(function(){alert("fail to login")});
}

function logout(evt) {
  evt.preventDefault();
  $.post("/api/logout").done(function () {location.reload()})
}

function register(evt) {
  evt.preventDefault();
  var form = evt.target;
  $.post("/api/players",
          { firstName: form["firstName"].value,
            lastName: form["lastName"].value,
            email: form["email"].value,
            password: form["password"].value })
            .done(function() {login(evt)})
            .fail(function(){alert("fail to register")});
}

function regist(){
    games.register = !games.register;
}

function newGame() {

  $.post("/api/games",
         function (data){
                window.location = "/web/game.html?gp=" + data;
         }
         ).fail(function(){alert("fail to start new game")});

}