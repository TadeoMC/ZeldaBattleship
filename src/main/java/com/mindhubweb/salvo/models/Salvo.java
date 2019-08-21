package com.mindhubweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ElementCollection
    private List<String> salvoLocations = new ArrayList<>();

    @ManyToOne (fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo(){}

    public Salvo (int turn, List<String> salvoLocations) {
        this.turn = turn;
        this.salvoLocations = salvoLocations;
    }

    public Map<String, Object> toDTO (){

        Map<String, Object> salvoMap = new HashMap<>();
        salvoMap.put("turn",this.getTurn());
        salvoMap.put("salvoLocations",this.getSalvoLocations());

        Optional<GamePlayer> optionalEnemyPlayer = this.getGamePlayer().getEnemyPlayer();
        if(optionalEnemyPlayer.isPresent()) {

            Set<Ship> enemyShips = optionalEnemyPlayer.get().getShips();
            List<String> salvoLocationListUpToThisTurn = salvoLocationListUpToThisTurn(this.getGamePlayer().getSalvoes());

            salvoMap.put("shipStatus", hitsOrSunk(enemyShips,salvoLocationListUpToThisTurn));
            salvoMap.put("hitLocations", hitLocationsInThisTurn(enemyShips));
        }
        return salvoMap;
    }

    private List<String> salvoLocationListUpToThisTurn (Set<Salvo> mySalvoes){

        return mySalvoes.stream()
                .filter(salvo -> salvo.getTurn() <= this.getTurn())
                .map(Salvo::getSalvoLocations)
                .flatMap(locations -> locations.stream()).collect(Collectors.toList());

    }

    private Map <String, Object> hitsOrSunk (Set<Ship> enemyShips, List<String> listOfSalvoesUpToThisTurn) {

        Map <String, Object> hitsOrSunk = new HashMap<>();

        enemyShips.forEach( ship ->{
            Long shipSize = new Long (ship.getShipLocations().size());
            Long hits = 0L;

            hits =  ship.getShipLocations().stream()
                    .filter(location -> listOfSalvoesUpToThisTurn.indexOf(location)!= -1)
                    .count();

            if(shipSize.equals(hits)){
                hitsOrSunk.put(ship.getShipType().toString(),"SUNK");
            }
            else{
                hitsOrSunk.put(ship.getShipType().toString(),hits);
        }});

        return hitsOrSunk;
    }

    private List<String> hitLocationsInThisTurn (Set<Ship> enemyShips) {

        return enemyShips.stream()
                .map(Ship::getShipLocations)
                .flatMap(locations -> locations.stream())
                .filter(location -> this.getSalvoLocations().indexOf(location) != -1)
        .collect(Collectors.toList());
    }

    public int myShipsLeft(Set<Ship> myShips, Set<Salvo> enemySalvoes) {

        List<String> listOfSalvoesUpToThisTurn = salvoLocationListUpToThisTurn(enemySalvoes);
        AtomicInteger count = new AtomicInteger(0);
        myShips.stream()
                .forEach (ship ->{
                    if(listOfSalvoesUpToThisTurn.containsAll(ship.getShipLocations()))
                    {
                        count.getAndIncrement();
                    };
                });
        return myShips.size() - count.get();
    }


    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

}
