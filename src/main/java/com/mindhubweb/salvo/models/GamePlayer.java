package com.mindhubweb.salvo.models;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import static java.util.stream.Collectors.*;

@Entity
public class GamePlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joiningDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy="gamePlayer", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
    Set<Salvo> salvoes = new HashSet<>();

    public GamePlayer(){}

    public GamePlayer (Player player,Game game,LocalDateTime joiningDate){
        this.player = player;
        this.game = game;
        this.joiningDate =joiningDate;
    }

    public LocalDateTime getCreationDate() {

        return joiningDate;
    }

    public Map<String, Object> toDTO (){

        Map<String, Object> gamePlayerMap = new HashMap<>();

        gamePlayerMap.put("gameId", this.getGame().getId());
        gamePlayerMap.put("gamePlayerId", this.getId());
        gamePlayerMap.put("thisPlayer", this.getPlayer().toDTO());
        gamePlayerMap.put("ships", this.getShips().stream().map(Ship::toDTO).collect(toList()));
        gamePlayerMap.put("myTurn", this.getSalvoes().size());
        gamePlayerMap.put("enemyTurn", this.getSalvoes().size());
        gamePlayerMap.put("mySalvoes", this.getSalvoes().stream().map(Salvo::toDTO));
        gamePlayerMap.put("salvoesAvailable", this.myShipsLeft());

        Optional<GamePlayer> optionalEnemyPlayer = this.getEnemyPlayer();
        if(optionalEnemyPlayer.isPresent()) {
            GamePlayer enemyPlayer = optionalEnemyPlayer.get();
            gamePlayerMap.put("enemyPlayer", enemyPlayer.getPlayer().toDTO());
            gamePlayerMap.put("enemySalvoes", enemyPlayer.getSalvoes().stream().map(Salvo::toDTO));
        }
        gamePlayerMap.put("gameState", this.gameState());
        return gamePlayerMap;
    }

    public Map<String, Object> toScoreDTO (){
        Map<String, Object> gamePlayerMap = new HashMap<>();
        gamePlayerMap.put("gamePlayerId", this.getId());
        gamePlayerMap.put("player", this.getPlayer().toDTO());
        if (this.getScore()!= null)
            gamePlayerMap.put("score", this.getScore().toDTO());
        return gamePlayerMap;
    }

    public GameState gameState () {

        GameState gameState;
        if (this.getShips().isEmpty()){
            gameState = GameState.PLACE_SHIPS;
        }
        else if(!this.getEnemyPlayer().isPresent()){
            gameState = GameState.WAITING_FOR_OPPONENT;
        }
        else{

            GamePlayer enemyPlayer = this.getEnemyPlayer().get();

            Salvo myLastSalvo = myLastSalvo();
            Salvo enemyLastSalvo =enemyPlayer.myLastSalvo();

            if(enemyPlayer.getShips().isEmpty()){
                gameState = GameState.WAITING_FOR_OPPONENT_SHIPS;
            }
            else if (myLastSalvo == null){
                gameState = GameState.FIREING_SALVOES;
            }
            else if (enemyLastSalvo == null){
                gameState = GameState.WAITING_FOR_OPPONENT_SALVOES;
            }
            else{
                //int myShipsLeft = myLastSalvo.myShipsLeft(this.getShips(), enemyPlayer.getSalvoes());
                int myShipsLeft =this.myShipsLeft();
                //int enemyShipsLeft = enemyLastSalvo.myShipsLeft(enemyPlayer.getShips(),this.getSalvoes());
                int enemyShipsLeft = enemyPlayer.myShipsLeft();

                if(myShipsLeft != 0 && enemyShipsLeft != 0)
                {
                    if( myLastSalvo.getTurn() <= enemyLastSalvo.getTurn()){
                        gameState = GameState.FIREING_SALVOES;
                    }
                    else{
                        gameState = GameState.WAITING_FOR_OPPONENT_SALVOES;
                    }
                }

                else /*end of game*/
                {
                    if( myLastSalvo.getTurn() < enemyLastSalvo.getTurn())
                    {
                        gameState = GameState.FIREING_SALVOES;
                    }
                    else if ( myLastSalvo.getTurn() > enemyLastSalvo.getTurn())
                    {
                        gameState = GameState.WAITING_FOR_OPPONENT_SALVOES;
                    }
                    else{
                        if(myShipsLeft > enemyShipsLeft){
                            gameState = GameState.WON;
                        }
                        else if (myShipsLeft < enemyShipsLeft)
                        {
                            gameState = GameState.LOST;
                        }
                        else{
                            gameState = GameState.TIE;
                        }
                    }

                }
            }
        }
        return gameState;
    }

    public Salvo myLastSalvo () {
        return this.getSalvoes().stream()
                .sorted(Comparator.comparing(Salvo::getTurn).reversed())
                .findFirst()
                .orElse(null);
    }

    public int myShipsLeft () {
        if(this.getEnemyPlayer().isPresent())
        {
            if(this.myLastSalvo() == null) return this.getShips().size();
            else return this.myLastSalvo().myShipsLeft(this.getShips(), this.getEnemyPlayer().get().getSalvoes());
        }
        else return this.getShips().size();
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<Ship> getShips() {
        return ships;
    }

    public Score getScore (){
        return this.getPlayer().getScore(this.getGame());
    }

    public Set<Salvo> getSalvoes() {
        return salvoes;
    }

    public void addShip (Ship ship){
        ship.setGamePlayer(this);
        ships.add(ship);
    }

    public void addSalvo (Salvo salvo){
        salvo.setGamePlayer(this);
        salvoes.add(salvo);
    }

    public Optional<GamePlayer> getEnemyPlayer () {
       return this.getGame().getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getId()!= this.getId()).findFirst();
    }

}