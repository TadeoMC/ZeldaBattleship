package com.mindhubweb.salvo.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.util.*;

import static java.util.stream.Collectors.toSet;


@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPassword;

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade= CascadeType.ALL)
    Set<Score> scores = new HashSet<>();

    public Player(){}

    public Player(String firstName, String lastName, String mail, String password) {
        this.userFirstName = firstName;
        this.userLastName = lastName;
        this.userEmail = mail;
        this.userPassword = password;
    }

    public Map<String,Object> toDTO () {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("playerId", this.getId());
        playerMap.put("email", this.getUserEmail());
        playerMap.put("firstName", this.getUserFirstName());
        playerMap.put("lastName", this.getUserLastName());
        return playerMap;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String name) {
        this.userFirstName = name;
    }
    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String name) {
        this.userLastName = name;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String mail) {
        this.userEmail = mail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String password) {
        this.userPassword =password;
    }

    @JsonIgnore
    public Set<Game> getGame() {

        return gamePlayers.stream().map(GamePlayer::getGame).collect(toSet());
    }

    public long getId() {
        return id;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public Score getScore (Game game){
        return this.getScores().stream().filter(score -> score.getGame().getId() == game.getId()).findFirst().orElse(null);
    }

    public void addGamePlayer(GamePlayer gamePlayer){
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score){
        score.setPlayer(this);
        scores.add(score);
    }

}
