package com.mindhubweb.salvo;

import com.mindhubweb.salvo.models.*;
import com.mindhubweb.salvo.repositories.GamePlayerRepository;
import com.mindhubweb.salvo.repositories.GameRepository;
import com.mindhubweb.salvo.repositories.PlayerRepository;
import com.mindhubweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

	@Autowired
	PasswordEncoder passwordEncoder;

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ScoreRepository scoreRepository){
		return (args) -> {

			LocalDateTime time = LocalDateTime.now();

			Player p1 =new Player("Jack","Bauer","j.bauer@ctu.gov",passwordEncoder.encode("24"));
			Player p2 =new Player("Chloe","O'Brian","c.obrian@ctu.gov",passwordEncoder.encode("42"));
			Player p3 =new Player("Kim","Bauer","kim_bauer@gmail.com",passwordEncoder.encode("kb"));
			Player p4 =new Player("Tony","Almeida","t.almeida@ctu.gov",passwordEncoder.encode("mole"));

			Game g1 =new Game(time);
			Game g2 =new Game(time.plusHours(1));
			Game g3 =new Game(time.plusHours(2));
			Game g4 =new Game(time.plusHours(3));
			Game g5 =new Game(time.plusHours(4));
			Game g6 =new Game(time.plusHours(5));
			Game g7 =new Game(time.plusHours(6));
			Game g8 =new Game(time.plusHours(7));

			playerRepository.save(p1);
			playerRepository.save(p2);
			playerRepository.save(p3);
			playerRepository.save(p4);

			gameRepository.save(g1);
			gameRepository.save(g2);
			gameRepository.save(g3);
			gameRepository.save(g4);
			gameRepository.save(g5);
			gameRepository.save(g6);
			gameRepository.save(g7);
			gameRepository.save(g8);

			Ship g1p1D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("H2","H3", "H4")));
			Ship g1p1S = new Ship(Ship.ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("E1", "F1", "G1")));
			Ship g1p1P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("B4", "B5")));
			Ship g1p2D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g1p2P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("F1", "F2")));
			Ship g2p1D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g2p1P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("C6", "C7")));
			Ship g2p2S = new Ship(Ship.ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("A2", "A3", "A4")));
			Ship g2p2P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("G6", "H6")));
			Ship g3p2D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g3p2P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("C6", "C7")));
			Ship g3p4S = new Ship(Ship.ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("A2", "A3", "A4")));
			Ship g3p4P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("G6", "H6")));
			Ship g4p2D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g4p2P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("C6", "C7")));
			Ship g4p1S = new Ship(Ship.ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("A2", "A3", "A4")));
			Ship g4p1P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("G6", "H6")));
			Ship g5p4D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g5p4P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("C6", "C7")));
			Ship g5p1S = new Ship(Ship.ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("A2", "A3", "A4")));
			Ship g5p1P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("G6", "H6")));
			Ship g6p3D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g6p3P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("C6", "C7")));
			Ship g8p3D = new Ship(Ship.ShipType.DESTROYER, new ArrayList<>(Arrays.asList("B5", "C5", "D5")));
			Ship g8p3P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("C6", "C7")));
			Ship g8p4S = new Ship(Ship.ShipType.SUBMARINE, new ArrayList<>(Arrays.asList("A2", "A3", "A4")));
			Ship g8p4P = new Ship(Ship.ShipType.PATROLBOAT, new ArrayList<>(Arrays.asList("G6", "H6")));



			Salvo salvo_g1p1t1 = new Salvo(1, new ArrayList<>(Arrays.asList("B5", "C5","F1")));
			Salvo salvo_g1p2t1 = new Salvo(1, new ArrayList<>(Arrays.asList("B4", "B5","B6")));
			Salvo salvo_g1p1t2 = new Salvo(2, new ArrayList<>(Arrays.asList("F2", "D5")));
			Salvo salvo_g1p2t2 = new Salvo(2, new ArrayList<>(Arrays.asList("E1", "H3","A2")));
			Salvo salvo_g2p1t1 = new Salvo(1, new ArrayList<>(Arrays.asList("A2", "A4","G6")));
			Salvo salvo_g2p2t1 = new Salvo(1, new ArrayList<>(Arrays.asList("B5", "D5","C7")));
			Salvo salvo_g2p1t2 = new Salvo(2, new ArrayList<>(Arrays.asList("A3", "H6")));
			Salvo salvo_g2p2t2 = new Salvo(2, new ArrayList<>(Arrays.asList("C5","C6")));
			Salvo salvo_g3p2t1 = new Salvo(1, new ArrayList<>(Arrays.asList("G6", "H6","A4 ")));
			Salvo salvo_g3p4t1 = new Salvo(1, new ArrayList<>(Arrays.asList("H1", "H2","H3")));
			Salvo salvo_g3p2t2 = new Salvo(2, new ArrayList<>(Arrays.asList("A2", "A3","D8")));
			Salvo salvo_g3p4t2 = new Salvo(2, new ArrayList<>(Arrays.asList("E1", "F2","G3")));
			Salvo salvo_g4p2t1 = new Salvo(1, new ArrayList<>(Arrays.asList("A3", "A4","F7")));
			Salvo salvo_g4p1t1 = new Salvo(1, new ArrayList<>(Arrays.asList("B5", "C6","H1")));
			Salvo salvo_g4p2t2 = new Salvo(2, new ArrayList<>(Arrays.asList("A2", "G6","H6")));
			Salvo salvo_g4p1t2 = new Salvo(2, new ArrayList<>(Arrays.asList("C5", "C7","D5")));
			Salvo salvo_g5p4t1 = new Salvo(1, new ArrayList<>(Arrays.asList("A1", "A2","A3")));
			Salvo salvo_g5p1t1 = new Salvo(1, new ArrayList<>(Arrays.asList("B5", "B6","C7")));
			Salvo salvo_g5p4t2 = new Salvo(2, new ArrayList<>(Arrays.asList("G6", "G7","G8")));
			Salvo salvo_g5p1t2 = new Salvo(2, new ArrayList<>(Arrays.asList("C6", "D6","E6")));
			Salvo salvo_g5p1t3 = new Salvo(3, new ArrayList<>(Arrays.asList("H1", "H8")));


			GamePlayer p1g1 = new GamePlayer(p1,g1,time);
			p1g1.addShip(g1p1D);
			p1g1.addShip(g1p1S);
			p1g1.addShip(g1p1P);
			p1g1.addSalvo(salvo_g1p1t1);
			p1g1.addSalvo(salvo_g1p1t2);

			Score g1p1 = new Score(1, g1.getCreationDate().plusMinutes(30),g1,p1);
			scoreRepository.save(g1p1);

			GamePlayer p2g1 = new GamePlayer(p2,g1,time);
			p2g1.addShip(g1p2P);
			p2g1.addShip(g1p2D);
			p2g1.addSalvo(salvo_g1p2t1);
			p2g1.addSalvo(salvo_g1p2t2);

			Score g1p2 = new Score(0, g1.getCreationDate().plusMinutes(30),g1,p2);
			scoreRepository.save(g1p2);

			GamePlayer p1g2 = new GamePlayer(p1,g2,time);
			p1g2.addShip(g2p1P);
			p1g2.addShip(g2p1D);
			p1g2.addSalvo(salvo_g2p1t1);
			p1g2.addSalvo(salvo_g2p1t2);

			Score g2p1 = new Score(0.5, g2.getCreationDate().plusMinutes(30),g2,p1);
			scoreRepository.save(g2p1);

			GamePlayer p2g2 = new GamePlayer(p2,g2,time);
			p2g2.addShip(g2p2P);
			p2g2.addShip(g2p2S);
			p2g2.addSalvo(salvo_g2p2t1);
			p2g2.addSalvo(salvo_g2p2t2);

			Score g2p2 = new Score(0.5, g2.getCreationDate().plusMinutes(30),g2,p2);
			scoreRepository.save(g2p2);

			GamePlayer p2g3 = new GamePlayer(p2,g3,time);
			p2g3.addShip(g3p2P);
			p2g3.addShip(g3p2D);
			p2g3.addSalvo(salvo_g3p2t1);
			p2g3.addSalvo(salvo_g3p2t2);

			Score g3p2 = new Score(1, g3.getCreationDate().plusMinutes(30),g3,p2);
			scoreRepository.save(g3p2);

			GamePlayer p4g3 = new GamePlayer(p4,g3,time);
			p4g3.addShip(g3p4P);
			p4g3.addShip(g3p4S);
			p4g3.addSalvo(salvo_g3p4t1);
			p4g3.addSalvo(salvo_g3p4t2);

			Score g3p4 = new Score(0, g3.getCreationDate().plusMinutes(30),g3,p4);
			scoreRepository.save(g3p4);

			GamePlayer p2g4 = new GamePlayer(p2,g4,time);
			p2g4.addShip(g4p2P);
			p2g4.addShip(g4p2D);
			p2g4.addSalvo(salvo_g4p2t1);
			p2g4.addSalvo(salvo_g4p2t2);

			Score g4p2 = new Score(0.5, g4.getCreationDate().plusMinutes(30),g4, p2);
			scoreRepository.save(g4p2);

			GamePlayer p1g4 = new GamePlayer(p1,g4,time);
			p1g4.addShip(g4p1P);
			p1g4.addShip(g4p1S);
			p1g4.addSalvo(salvo_g4p1t1);
			p1g4.addSalvo(salvo_g4p1t2);

			Score g4p1 = new Score(0.5, g4.getCreationDate().plusMinutes(30),g4,p1);
			scoreRepository.save(g4p1);

			GamePlayer p1g5 = new GamePlayer(p1,g5,time);
			p1g5.addShip(g5p1P);
			p1g5.addShip(g5p1S);
			p1g5.addSalvo(salvo_g5p1t1);
			p1g5.addSalvo(salvo_g5p1t2);
			p1g5.addSalvo(salvo_g5p1t3);

			GamePlayer p4g5 = new GamePlayer(p4,g5,time);
			p4g5.addShip(g5p4P);
			p4g5.addShip(g5p4D);
			p4g5.addSalvo(salvo_g5p4t1);
			p4g5.addSalvo(salvo_g5p4t2);

			GamePlayer p3g6 = new GamePlayer(p3,g6,time);
			p3g6.addShip(g6p3P);
			p3g6.addShip(g6p3D);

			GamePlayer p4g7 = new GamePlayer(p4,g7,time);

			GamePlayer p3g8 = new GamePlayer(p3,g8,time);
			p3g8.addShip(g8p3P);
			p3g8.addShip(g8p3D);

			GamePlayer p4g8 = new GamePlayer(p4,g8,time);
			p4g8.addShip(g8p4P);
			p4g8.addShip(g8p4S);


			gamePlayerRepository.save(p1g1);
			gamePlayerRepository.save(p2g1);
			gamePlayerRepository.save(p1g2);
			gamePlayerRepository.save(p2g2);
			gamePlayerRepository.save(p2g3);
			gamePlayerRepository.save(p4g3);
			gamePlayerRepository.save(p1g4);
			gamePlayerRepository.save(p2g4);
			gamePlayerRepository.save(p1g5);
			gamePlayerRepository.save(p4g5);
			gamePlayerRepository.save(p3g6);
			gamePlayerRepository.save(p4g7);
			gamePlayerRepository.save(p3g8);
			gamePlayerRepository.save(p4g8);

		};
	}

}


