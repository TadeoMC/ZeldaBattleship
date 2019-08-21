package com.mindhubweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime finalizingDate;
    double score;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    public Score (){}

    public Score (double score, LocalDateTime finalizingDate,Game game, Player player){
        this.score = score;
        this.finalizingDate = finalizingDate;
        this.game = game;
        this.player = player;
    }

    public Map<String, Object> toDTO (){
        Map<String, Object> scoreMap = new HashMap<>();
        scoreMap.put("score", this.getScore());
        scoreMap.put("finalizingDate",this.getFinalizingDate());
        return scoreMap;
    }

    public LocalDateTime getFinalizingDate() {
        return finalizingDate;
    }

    public void setFinalizingDate(LocalDateTime finalizingDate) {
        this.finalizingDate = finalizingDate;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @JsonIgnore
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

}
