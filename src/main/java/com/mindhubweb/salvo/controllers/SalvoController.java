package com.mindhubweb.salvo.controllers;

import com.mindhubweb.salvo.models.*;
import com.mindhubweb.salvo.repositories.GamePlayerRepository;
import com.mindhubweb.salvo.repositories.GameRepository;
import com.mindhubweb.salvo.repositories.PlayerRepository;
import com.mindhubweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

        @Autowired
        private GameRepository gameRepository;

        @Autowired
        private PlayerRepository playerRepository;

        @Autowired
        private GamePlayerRepository gamePlayerRepository;

        @Autowired
        private ScoreRepository scoreRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @GetMapping("/games")
        public Map<String,Object> getGames(Authentication authentication) {
            Map<String,Object> gamesMap = new HashMap<>();
            if (isGuest(authentication)){
                gamesMap.put("currentPlayer", "GUEST");
            }
            else{
                Player currentPlayer = playerRepository.findByUserEmail(authentication.getName());
                gamesMap.put("currentPlayer", currentPlayer.toDTO());
            }
            gamesMap.put("games",gameRepository.findAll().stream().map(Game::toDTO).collect(Collectors.toList()));
            return gamesMap;
        }

        @GetMapping("/game_view/{gamePlayerId}")
        public ResponseEntity<Map<String,Object>> getGamePlayerId(@PathVariable Long gamePlayerId, Authentication authentication) {

            Optional<GamePlayer> gamePlayer = gamePlayerRepository.findById(gamePlayerId);
            ResponseEntity<Map<String,Object>> response;

            if(gamePlayer.isPresent()) {
                GamePlayer gPlayer = gamePlayer.get();
                if (gPlayer.getPlayer().getUserEmail() == authentication.getName())
                    response = new ResponseEntity<>(gPlayer.toDTO(), HttpStatus.OK);
                else
                    response = new ResponseEntity<>(makeMap("ERROR","Not authorized"), HttpStatus.UNAUTHORIZED);
            }
            else {
                response =  new ResponseEntity<>(makeMap("ERROR","Game Player not Found"), HttpStatus.FORBIDDEN);
            }
            return response;
        }

    @PostMapping(path = "/players")
        public ResponseEntity<Object> register(
                @RequestParam String firstName, @RequestParam  String lastName,
                @RequestParam String email, @RequestParam String password) {

        ResponseEntity<Object> response;

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                response = new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
            }

            else if (playerRepository.findByUserEmail(email) !=  null) {
                response = new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
            }
            else {
                playerRepository.save(new Player(firstName, lastName, email, passwordEncoder.encode(password)));
                response = new ResponseEntity<>(HttpStatus.CREATED);
            }

            return  response;
        }

    @PostMapping(path = "/games")
    public ResponseEntity<Object> createNewGame(Authentication authentication) {

        LocalDateTime time = LocalDateTime.now();
        ResponseEntity<Object> response;

        if (isGuest(authentication)) {
            response = new ResponseEntity<>("Player not registered", HttpStatus.FORBIDDEN);
        }
        else {

            Player player = playerRepository.findByUserEmail(authentication.getName());
            Game newGame = new Game(time);
            GamePlayer newGamePlayer = new GamePlayer(player, newGame, time);

            gameRepository.save(newGame);
            gamePlayerRepository.save(newGamePlayer);
            response = new ResponseEntity<>(newGamePlayer.getId(), HttpStatus.CREATED);
        }
        return response;
    }

    @PostMapping(path = "/game/{gameId}/players")
    public ResponseEntity<Map<String,Object>> joinGame(@PathVariable Long gameId, Authentication authentication) {

        LocalDateTime time = LocalDateTime.now();
        ResponseEntity<Map<String,Object>> responseEntity;

        Optional<Game> gameOptional = gameRepository.findById(gameId);

        if (isGuest(authentication)) {
            responseEntity = new ResponseEntity<>(makeMap("ERROR","Player not registered"), HttpStatus.UNAUTHORIZED);
        }

        else if (!gameOptional.isPresent()){
            responseEntity = new ResponseEntity<>(makeMap("ERROR","No such game"), HttpStatus.FORBIDDEN);
        }else {

            Game game = gameOptional.get();
            if (game.getGamePlayers().size() > 1) {
                responseEntity = new ResponseEntity<>(makeMap("ERROR", "Game is full"), HttpStatus.FORBIDDEN);
            }

            else if (game.getPlayers().contains(playerRepository.findByUserEmail(authentication.getName()))) {
                responseEntity = new ResponseEntity<>(makeMap("ERROR", "you are already in that game"), HttpStatus.FORBIDDEN);
            }
            else {

                Player player = playerRepository.findByUserEmail(authentication.getName());
                GamePlayer newGamePlayer = new GamePlayer(player, game, time);

                gamePlayerRepository.save(newGamePlayer);

                responseEntity = new ResponseEntity<>(makeMap("gamePlayerId", newGamePlayer.getId()), HttpStatus.CREATED);
            }

        }
        return responseEntity;
    }

    @PostMapping(path = "games/players/{gamePlayerId}/ships")
    public ResponseEntity<Object> saveShips (@PathVariable Long gamePlayerId, @RequestBody Set<Ship> ships, Authentication authentication) {

        ResponseEntity<Object> responseEntity;

        Optional<GamePlayer> gamePlayerOptional = gamePlayerRepository.findById(gamePlayerId);

        if (isGuest(authentication)) {
            responseEntity = new ResponseEntity<>(makeMap("ERROR","there is no current user logged in"), HttpStatus.UNAUTHORIZED);
        }

        else if (!gamePlayerOptional.isPresent()){
            responseEntity = new ResponseEntity<>(makeMap("ERROR","there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
        }

        else{
            GamePlayer gamePlayer = gamePlayerOptional.get();
            Player player = playerRepository.findByUserEmail(authentication.getName());

            if (gamePlayer.getPlayer().getId() != player.getId()){
                responseEntity = new ResponseEntity<>(makeMap("ERROR","the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
            }

            else if (!(gamePlayer.getShips().isEmpty())){
                responseEntity = new ResponseEntity<>(makeMap("ERROR","ships already deployed"), HttpStatus.FORBIDDEN);
            }
            else{
                ships.stream().forEach(ship -> gamePlayer.addShip(ship));
                gamePlayerRepository.save(gamePlayer);
                responseEntity = new ResponseEntity<>(makeMap("SUCCESS","Ships deployed!!"), HttpStatus.CREATED);
            }
        }

        return  responseEntity;
    }

    @PostMapping(path = "games/players/{gamePlayerId}/salvoes")
    public ResponseEntity<Object> saveSalvoes (@PathVariable Long gamePlayerId, @RequestBody  List<String> salvoLocations, Authentication authentication) {

        ResponseEntity<Object> responseEntity;

        Optional<GamePlayer> gamePlayerOptional = gamePlayerRepository.findById(gamePlayerId);

        if (isGuest(authentication)) {
            responseEntity = new ResponseEntity<>(makeMap("ERROR","there is no current user logged in"), HttpStatus.UNAUTHORIZED);
        }

        else if (!gamePlayerOptional.isPresent()){
            responseEntity = new ResponseEntity<>(makeMap("ERROR","there is no game player with the given ID"), HttpStatus.UNAUTHORIZED);
        }

        else{
            GamePlayer gamePlayer = gamePlayerOptional.get();
//            Salvo myLastSalvo = gamePlayer.myLastSalvo();
            Player player = playerRepository.findByUserEmail(authentication.getName());
            Optional<GamePlayer> enemyPlayerOptional = gamePlayer.getEnemyPlayer();

            if (gamePlayer.getPlayer().getId() != player.getId()){
                responseEntity = new ResponseEntity<>(makeMap("ERROR","the current user is not the game player the ID references"), HttpStatus.UNAUTHORIZED);
            }

            else if(enemyPlayerOptional.isPresent()) {
                GamePlayer enemyGamePlayer = enemyPlayerOptional.get();
                int compare = enemyGamePlayer.getSalvoes().size() - gamePlayer.getSalvoes().size();

                GameState gameState = gamePlayer.gameState();

                if(gameState == GameState.TIE || gameState == GameState.LOST || gameState == GameState.WON){
                    responseEntity = new ResponseEntity<>(makeMap("ERROR","Game Ended"), HttpStatus.FORBIDDEN);
                }

                else if (compare < 0){
                    responseEntity = new ResponseEntity<>(makeMap("ERROR","Waiting for Enemy Player"), HttpStatus.FORBIDDEN);
                }

                else if (salvoLocations.size() <= gamePlayer.myShipsLeft()){
                    Salvo salvo= new Salvo(gamePlayer.getSalvoes().size() + 1,salvoLocations);
                    gamePlayer.addSalvo(salvo);
                    gamePlayerRepository.save(gamePlayer);
                    responseEntity = new ResponseEntity<>(makeMap("SUCCESS","Salvoes FIRED!!"), HttpStatus.CREATED);

                    gameState = gamePlayer.gameState();
                    if(gameState == GameState.TIE || gameState == GameState.LOST || gameState == GameState.WON){

                        LocalDateTime endTime = LocalDateTime.now();
                        Game thisGame = gamePlayer.getGame();
                        Player enemyPlayer = enemyGamePlayer.getPlayer();

                        if(gameState == GameState.TIE){

                            Score myScore = new Score(0.5, endTime,thisGame,player);
                            scoreRepository.save(myScore);

                            Score enemyScore = new Score(0.5, endTime,thisGame,enemyPlayer);
                            scoreRepository.save(enemyScore);
                        }
                        else if(gameState == GameState.WON){

                            Score myScore = new Score(1, endTime,thisGame,player);
                            scoreRepository.save(myScore);

                            Score enemyScore = new Score(0, endTime,thisGame,enemyPlayer);
                            scoreRepository.save(enemyScore);
                        }

                        else{

                            Score myScore = new Score(0, endTime,thisGame,player);
                            scoreRepository.save(myScore);

                            Score enemyScore = new Score(1, endTime,thisGame,enemyPlayer);
                            scoreRepository.save(enemyScore);
                        }



                    }
                }
                else {
                    responseEntity = new ResponseEntity<>(makeMap("ERROR","Can't fire more salvoes than ships left"), HttpStatus.FORBIDDEN);
                }
            }
            else{
                responseEntity = new ResponseEntity<>(makeMap("ERROR","Waiting for Enemy Player"), HttpStatus.FORBIDDEN);
            }

        }

        return  responseEntity;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
