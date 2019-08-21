package com.mindhubweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    public enum ShipType { CARRIER, BATTLESHIP, SUBMARINE, DESTROYER, PATROLBOAT }
    private ShipType shipType;

    @ManyToOne (fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    private List<String> shipLocations = new ArrayList<>();

    public Ship (){}

    public Ship (ShipType shipType,List<String> shipLocations){
        this.shipType = shipType;
        this.shipLocations = shipLocations;
    }

    public Map<String, Object> toDTO (){
        Map<String, Object> shipsMap = new HashMap<>();
        shipsMap.put("type", this.getShipType());
        shipsMap.put ("locations",this.getShipLocations());
        return shipsMap;
    }


    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setShipLocations(List<String> shipLocations) {
        this.shipLocations = shipLocations;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

}
