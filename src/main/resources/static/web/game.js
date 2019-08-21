var searchParams = getSearchParams();
var playerBoard = {};
var gamePlayerId = searchParams.gp;
var firstPlayerId = searchParams.gp;
var gameState;
var numOfSalvoes = 0;
var newSalvoLocations = [];
var firstLoad = true;
var timerId;

$(function () {
    var gsBoardOptions = {
        width: 10,
        height: 10,
        verticalMargin: 0,
        cellHeight: 45,
        disableResize: true,
		float: true,
        disableOneColumnMode: true,
        staticGrid: false,
        animate: true,
        acceptWidgets: '.grid-stack-item'
    }

    $('#gs-board').gridstack(gsBoardOptions);
    board = $('#gs-board').data('gridstack');
    createGrid(11);
    fetchJson();


});


function cargarBarcosMobiles(){

    var board = $('#gs-board').data('gridstack');
    console.log(board);

    board.addWidget($('<div id="submarine"><div id="submarineHandler" class="grid-stack-item-content submarineVer"><button class="rotateButton" onclick=rotate(\'submarine\')><img src="dist/rotate.png" class="rotateIcon"></img></button></div><div/>'),
    0, 0, 1, 3);
    board.addWidget($('<div id="patrolboat"><div id="patrolboatHandler" class="grid-stack-item-content patrolboatVer"><button class="rotateButton" onclick=rotate(\'patrolboat\')><img src="dist/rotate.png" class="rotateIcon"></img></button></div><div/>'),
    1, 0, 1, 2);
    board.addWidget($('<div id="destroyer"><div id="destroyerHandler" class="grid-stack-item-content destroyerVer"><button class="rotateButton" onclick=rotate(\'destroyer\')><img src="dist/rotate.png" class="rotateIcon"></img></button></div><div/>'),
    2, 0, 1, 3);
    board.addWidget($('<div id="carrier"><div id="carrierHandler" class="grid-stack-item-content carrierVer"><button class="rotateButton" onclick=rotate(\'carrier\')><img src="dist/rotate.png" class="rotateIcon"></img></button></div><div/>'),
    3, 0, 1, 5);
    board.addWidget($('<div id="battleship"><div id="battleshipHandler" class="grid-stack-item-content battleshipVer"><button class="rotateButton" onclick=rotate(\'battleship\')><img src="dist/rotate.png" class="rotateIcon"></img></button></div><div/>'),
    4, 0, 1, 4);

}

function cargarBarcosEstaticos(ships) {

    var board = $('#gs-board').data('gridstack');
    console.log(board);

    ships.forEach(function(ship){
        var shipId = (ship.type).toLowerCase();
        var shipLocations = ship.locations.sort(function(a, b){
                                                return parseInt(a.slice(1)) - parseInt(b.slice(1))
                                             });
        var x = parseInt(shipLocations[0].slice(1)) - 1;
        shipLocations = shipLocations.sort();
        var y = shipLocations[0][0].charCodeAt() - 65;
        var width = 0;
        var height = 0;

        if (isHorizontal(shipLocations)){
            width = shipLocations.length;
            height = 1;
            board.addWidget($('<div id="' + shipId +'"><div id="'+shipId+'Handler" class="grid-stack-item-content '+ shipId +'Hor"></div><div/>'),
            x, y, width, height);
        }
        else {
            height = shipLocations.length;
            width = 1;
            board.addWidget($('<div id="' + shipId +'"><div id="'+shipId+'Handler" class="grid-stack-item-content '+ shipId +'Ver"></div><div/>'),
            x, y, width, height);
        }
    });

    function isHorizontal(shipLocations){
        var firstCell = shipLocations[0];
        return shipLocations.every(function(cell){
            return(cell[0] == firstCell[0])
    });
    }
};


function rotate(ship){

    shipId = "#" + ship;
    widthNow = Number($(shipId).attr('data-gs-width'));
    heightNow = Number($(shipId).attr('data-gs-height'));
    xNow = Number($(shipId).attr('data-gs-x'));
    yNow = Number($(shipId).attr('data-gs-y'));
    console.log("x: " + xNow + ", y: "+ yNow + ", width: " + widthNow + ", height: " + heightNow);
    if($(shipId+"Handler").hasClass(ship + "Hor")&&((yNow + widthNow - 1)<10)&& board.isAreaEmpty(xNow, yNow + 1, heightNow, widthNow - 1)){
        console.log("x: " + xNow + ", y: "+ (yNow + 1) + ", width: " + heightNow + ", height: " + (widthNow - 1));
        board.resize($(shipId),heightNow,widthNow);
        $(shipId+"Handler").removeClass(ship + "Hor");
        $(shipId+"Handler").addClass(ship + "Ver");
    }
    else if ($(shipId+"Handler").hasClass(ship + "Ver")&&((xNow + heightNow - 1)<10)&&board.isAreaEmpty(xNow + 1, yNow, widthNow - 1, heightNow)){
        board.resize($(shipId),heightNow,widthNow);
        $(shipId+"Handler").removeClass(ship + "Ver");
        $(shipId+"Handler").addClass(ship + "Hor");
    }
    else{
        console.log("error");
    }
};

function getPositions(){

    var ships = []
        $("#gs-board").children().each(function(){

        var ship = {
        shipType: "",
        shipLocations: [],
        };
        var x = + $(this).attr("data-gs-x") + 1;
        var y = + $(this).attr("data-gs-y");
        var width = $(this).attr("data-gs-width");
        var height = $(this).attr("data-gs-height");
        var position;

        ship.shipType = $(this).attr("id").toUpperCase();

        if (width > height){
            for ( i = 0; i < width; i++){
              position = String.fromCharCode(65 + y) + (x + i);
              ship.shipLocations.push(position)
            }
        }else{
            for ( i = 0; i < height; i++){
              position = String.fromCharCode(65 + y + i) + x;
              ship.shipLocations.push(position)
            }
        }

        return ships.push(ship)
    });
    return ships
}


/* inicializa la página creando la grilla, luego trae el JSON perteneciente al jugador particular
y coloca los barcos y luego los salvos en la grilla.*/

function fetchJson() {

    $.getJSON("/api/game_view/"+firstPlayerId).done(
            function(data){

                playerBoard = data;
                numOfSalvoes = playerBoard.salvoesAvailable;
                newSalvoLocations = [];
                gameState = playerBoard.gameState;
                gameInfo(playerBoard);
                mySalvoes(playerBoard.mySalvoes);

                 if(gameState == 'PLACE_SHIPS' && firstLoad)
                     {
                         cargarBarcosMobiles();
                         placingSalvoes();
                         firstLoad = false;
                     }
                 else if (firstLoad)
                     {
                         cargarBarcosEstaticos(playerBoard.ships);
                         $("#gs-board").data('gridstack').setStatic(true);
                         $("#enemyBoard").css('display', 'block');
                         $("#saveBoats").css('display', 'none');
                         numOfSalvoes = playerBoard.salvoesAvailable;
                         mySalvoes(playerBoard.mySalvoes);
                         placingSalvoes();
                         firstLoad = false;
                     }

                 if (gameState != 'PLACE_SHIPS' && gameState != 'WAITING_FOR_OPPONENT')
                 {
                     enemySalvoes(playerBoard.enemySalvoes);
                 }

                 if ( gameState == 'FIREING_SALVOES')
                 {
                    stopFetchingLoop();
                 }
            }
        )
};

function startFetchingLoop(seconds) {
  timerId = setInterval(function() { fetchJson(); console.log('fetchJson') },seconds * 1000);
}

function stopFetchingLoop() {
  clearInterval(timerId);
}

/* crea la grilla vacía*/

function createGrid(x) {
    var letter;
    for (var rows = 0; rows < x ; rows++) {
        $(".grid").append("<div class='rows row_"+rows+"'></div>");
        for (var columns = 0; columns < x; columns++) {
            if((rows == 0) && (columns != 0))
            $(".row_"+rows).append("<div class='box'><div class='inner columns'>"+ columns +"</div></div>");
            else if ((rows != 0) && (columns == 0)){
            letter = rows + 64;
            $(".row_"+rows).append("<div class='box'><div class='inner rows'>"+ String.fromCharCode(letter) +"</div></div>");
            }
            else{
            if (rows == 0)
            $(".row_"+rows).append("<div class='box'><div class='inner columns' ></div></div>");
            else
            $(".row_"+rows).append("<div class='box'><div class='inner "+String.fromCharCode(letter)+columns+" empty' data-cell='"+String.fromCharCode(letter)+columns+"'></div></div>")
            }
        };
    };
};

/* coloca mis salvos en la grilla del enemigo*/

function mySalvoes (salvoes){

    for (var i = 0 ; i < salvoes.length ; i++)
    {
        var location = salvoes[i].salvoLocations;
        var hits = salvoes[i].hitLocations;

        for(var cell = 0 ;cell < location.length ; cell ++)
        {
            $(".enemyFleet ."+ location[cell] ).removeClass("empty");
            if (hits.indexOf(location[cell]) != -1)
            {
                $(".enemyFleet ."+ location[cell] ).addClass("hit");
                $(".enemyFleet ."+ location[cell] ).removeClass("hover");
                $(".enemyFleet ."+ location[cell] ).removeClass("shot");
            }
            else
            $(".enemyFleet ."+ location[cell] ).addClass("shot");
            $(".enemyFleet ."+ location[cell] ).html(salvoes[i].turn);
         }
    }
}

function placingSalvoes(){

    $("#enemyBoard .enemyFleet .box .inner.empty").hover(function(){

        if(playerBoard.gameState == 'FIREING_SALVOES' && numOfSalvoes > 0 && $(this).hasClass("empty")){
        $( this )
                .toggleClass( "hover" )
                .next()
                  .stop( true, true )
                  .slideToggle();
        }
      })
      $("#enemyBoard .enemyFleet .box .inner.empty").click( function() {

        if(playerBoard.gameState == 'FIREING_SALVOES' && numOfSalvoes > 0 && $(this).hasClass("empty")){
            $(this).addClass("shot")
            $(this).removeClass("empty")
            console.log("fireSalvo")
            newSalvoLocations.push($(this).data("cell"))
            numOfSalvoes --;
            $(".fireSalvoesBtn").css("display", "block")
        }
      })
}

function enemySalvoes (salvoes){

    for (var i = 0 ; i < salvoes.length ; i++)
    {
        var location = salvoes[i].hitLocations;
        for(var cell = 0 ;cell < location.length ; cell ++)
        {
            var y = (location[cell][0].charCodeAt() - 65)*45 + 45;
            var x = (parseInt(location[cell].slice(1)) - 1)*45 + 45;
            $(".grid-ships").append("<div class='salvo_turn_"+ i + cell +" salvo'></div>")
            $(".grid-ships > .salvo_turn_"+ i + cell)
                .css({height: '45px',width: '45px',top: y + 'px', left: x +'px',position: 'absolute'})
                .addClass("hit")
         }
    }
}

function gameInfo(gamePlayers){
    var thisPlayer = gamePlayers.thisPlayer;
    var enemyPlayer = gamePlayers.enemyPlayer || "Waiting for 2nd player";
    $("#gameInfo .card-title.title").html("SALVO!: LET'S FIGHT!!");
    $("#gameInfo .card-text ").html(thisPlayer.email + " (YOU) VS. " + (enemyPlayer.email || enemyPlayer));
    $("#gameInfo .card-title.state").html(gameState);
};


/* obtiene los parametros de busqueda de la URL*/
function getSearchParams(){
    var params = {};
    document.location.search.substr(1).split('&').forEach(pair => {
    [key, value] = pair.split('=');
    params [key]= decodeURI(value);
  })
  return params;
};

function logout() {
  $.post("/api/logout").done(function () {window.location = "http://localhost:8080/web/games.html"})
}

function saveBoats() {
   var ships = getPositions();

       $.post({
         url: "/api/games/players/"+ gamePlayerId +"/ships",
         data: JSON.stringify(ships),
         dataType: "text",
         contentType: "application/json"
       }).done(function(){
             $("#enemyBoard").css('display', 'block');
             $("#saveBoats").css('display', 'none');
             $("#gs-board").data('gridstack').setStatic(true);
             $(".rotateButton").css('display', 'none');
             fetchJson();
             startFetchingLoop(4);
       })
 }

 function fireSalvoes() {
        $.post({
          url: "/api/games/players/"+ gamePlayerId +"/salvoes",
          data: JSON.stringify(newSalvoLocations),
          dataType: "text",
          contentType: "application/json"
        })
        .done(function(data, status){
                $(".fireSalvoesBtn").css("display", "none");
                fetchJson();
                startFetchingLoop(4);
        })
  }


