const urlParams = new URLSearchParams(window.location.search);
const myParam = urlParams.get('gp');

var game_view_data=[];
fetch("/api/game_view/" + myParam, {mode: "no-cors"})
    .then(function(response){
    return response.json()})
    .then(function(json){
    game_view_data = json;
    loadGrid();});


    var shipTypeArr=[];

function getPosition(){
 var shipAxX;
 var shipAxY;
 var shipWidth;
 var endLoc;
 var letterArr = ["A","B","C","D","E","F","G","H","I","J"];
    for(var i=0; i<game_view_data.ships.length; i++){
        shipAxY = letterArr.indexOf(game_view_data.ships[i].locations[0].slice(0,1));
        shipAxX = parseInt(game_view_data.ships[i].locations[0].slice(1,2));
        shipWidth = game_view_data.ships[i].locations.length;
        endLoc = letterArr.indexOf(game_view_data.ships[i].locations[game_view_data.ships[i].locations.length -1].slice(0,1));
        if (endLoc = shipAxY){
            grid.addWidget($('<div id="'+game_view_data.ships[i].shipType+'"><div class="grid-stack-item-content carrierHorizontal"></div><div/>'),
                    shipAxX, shipAxY, shipWidth, 1);
        }
        else{
            grid.addWidget($('<div id="'+game_view_data.ships[i].shipType+'"><div class="grid-stack-item-content carrierHorizontalRed"></div><div/>'),
                                shipAxX, shipAxY, 1, shipWidth);
        }

    }
}

function loadGrid () {
    var options = {
        //grilla de 10 x 10
        width: 10,
        height: 10,
        //separacion entre elementos (les llaman widgets)
        verticalMargin: 0,
        //altura de las celdas
        cellHeight: 45,
        //deshabilitando el resize de los widgets
        disableResize: true,
        //widgets flotantes
		float: true,
        //removeTimeout: 100,
        //permite que el widget ocupe mas de una columna
        disableOneColumnMode: true,
        //false permite mover, true impide
        staticGrid: false,
        //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
        animate: true
    }
    //se inicializa el grid con las opciones
    $('.grid-stack').gridstack(options);

    grid = $('#grid').data('gridstack');
getPosition();
    /*//agregando un elmento(widget) desde el javascript
    grid.addWidget($('<div id="carrier2"><div class="grid-stack-item-content carrierHorizontal"></div><div/>'),
        1, 5, 3, 1);

    grid.addWidget($('<div id="patroal2"><div class="grid-stack-item-content patroalHorizontal"></div><div/>'),
        1, 8, 2, 1);*/

    //verificando si un area se encuentra libre
    //no está libre, false
    console.log(grid.isAreaEmpty(1, 8, 3, 1));
    //está libre, true
    console.log(grid.isAreaEmpty(1, 7, 3, 1));

    $("#carrier,#carrier2").click(function(){
        if($(this).children().hasClass("carrierHorizontal")){
            grid.resize($(this),1,3);
            $(this).children().removeClass("carrierHorizontal");
            $(this).children().addClass("carrierHorizontalRed");
        }else{
            grid.resize($(this),3,1);
            $(this).children().addClass("carrierHorizontal");
            $(this).children().removeClass("carrierHorizontalRed");
        }
    });

    $("#patroal,#patroal2").click(function(){
        if($(this).children().hasClass("patroalHorizontal")){
            grid.resize($(this),1,2);
            $(this).children().removeClass("patroalHorizontal");
            $(this).children().addClass("patroalHorizontalRed");
        }else{
            grid.resize($(this),2,1);
            $(this).children().addClass("patroalHorizontal");
            $(this).children().removeClass("patroalHorizontalRed");
        }
    });

createGrid(11, $(".grid-ships"))
    //todas las funciones se encuentran en la documentación
    //https://github.com/gridstack/gridstack.js/tree/develop/doc
};

const createGrid = function(size, element){

let wrapper = document.createElement('DIV')
wrapper.classList.add('grid-wrapper')

    for(let i = 0; i < size; i++){
        let row = document.createElement('DIV')
        row.classList.add('grid-row')
        row.id =`grid-row${i}`
        wrapper.appendChild(row)

        for(let j = 0; j < size; j++){
            let cell = document.createElement('DIV')
            cell.classList.add('grid-cell')
            if(i > 0 && j > 0)
            cell.id = `grid-cell${String.fromCharCode(i+64) + j}`

            if(j===0 && i > 0){
                let textNode = document.createElement('SPAN')
                textNode.innerText = String.fromCharCode(i+64)
                cell.appendChild(textNode)
            }
            if(i === 0 && j > 0){
                let textNode = document.createElement('SPAN')
                textNode.innerText = j
                cell.appendChild(textNode)
            }
            row.appendChild(cell)
        }
    }

    element.append(wrapper)
}
