var app = new Vue({
    el:"#app",
    data:{
    games: [],
    },
});

    fetch("/api/games", {mode: "no-cors"})
    .then(function(response){
    return response.json()})
    .then(function(json){
    app.games = json
    })

