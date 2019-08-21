package com.mindhubweb.salvo.models;

public enum GameState {

    PLACE_SHIPS,
    WAITING_FOR_OPPONENT,
    WAITING_FOR_OPPONENT_SHIPS,
    FIREING_SALVOES,
    WAITING_FOR_OPPONENT_SALVOES,
    WON,
    LOST,
    TIE
}
