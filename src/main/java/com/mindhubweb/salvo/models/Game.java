package com.mindhubweb.salvo.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Entity
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime creationDate;

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="game", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    Set<Score> scores = new HashSet<>();

    public Game(){}

    public Game (LocalDateTime creationDate)
    {
        this.creationDate =creationDate;
    }

    public Map<String, Object> toDTO (){
        Map<String, Object> gameMap = new HashMap<>();
        gameMap.put("gameId", this.getId());
        gameMap.put("creationDate", this.getCreationDate());
        if (this.getScores().isEmpty()){
            gameMap.put("state", "inProgress");
        }
        else{
            gameMap.put("state", "finished");
        }
        gameMap.put("gamePlayers", this.getGamePlayers().stream().map(GamePlayer::toScoreDTO).collect(toList()));
        return gameMap;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
    public long getId() {return id;}

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @JsonIgnore
    public Set<Player> getPlayers() {
        return gamePlayers.stream().map(GamePlayer::getPlayer).collect(toSet());
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setGame(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score){
        score.setGame(this);
        scores.add(score);
    }
}