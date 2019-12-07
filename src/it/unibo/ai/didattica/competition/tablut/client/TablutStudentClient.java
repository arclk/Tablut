package it.unibo.ai.didattica.competition.tablut.client;

import java.io.IOException;
import java.net.UnknownHostException;

import it.unibo.ai.didattica.competition.tablut.domain.Game;
import it.unibo.ai.didattica.competition.tablut.domain.GameAshtonTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;
import it.unibo.ai.didattica.competition.tablut.domain.*;
import it.unibo.ai.didattica.competition.tablut.AI.*;

/**
 * 
 * @author Arcangelo Alberico
 * 
 * 
 */
public class TablutStudentClient extends TablutClient {
	
	public TablutStudentClient(String player, String name) throws UnknownHostException, IOException {
		super(player, name);
	}
	
	public TablutStudentClient(String player, String name, int timeout, String ipAddress) throws UnknownHostException, IOException {
		super(player, name, timeout, ipAddress);
	}
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		String role = "";
		String name = "Arcangelo";
		String ipAddress = "localhost";
		int timeout = 60;
		
		if (args.length < 1) {
			System.out.println("You must specify which player you are (WHITE or BLACK)");
			System.exit(-1);
		} else {
			System.out.println(args[0]);
			role = (args[0]);
		}
		if (args.length == 2) {
			System.out.println(args[1]);
			timeout = Integer.parseInt(args[1]);
		}
		if (args.length == 3) {
			timeout = Integer.parseInt(args[1]);
			ipAddress = args[2];
		}
		System.out.println("Selected client: " + args[0]);

		TablutStudentClient client = new TablutStudentClient(role, name, timeout, ipAddress);
		client.run();
	}


	@Override
	public void run() 
	{
		try {
			this.declareName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		State state;		
		Game rules = null;
		state = new StateTablut();
		state.setTurn(State.Turn.WHITE);
		rules = new GameAshtonTablut(99, 0, "garbage", "fake", "fake");
		System.out.println("Ashton Tablut game");
		AlphaBetaSearch algorithm = new AlphaBetaSearch(rules, super.getTimeout()-3, true, 3);
		
		System.out.println("You are player " + this.getPlayer().toString() + "!");
		
		while(true)
		{
			try {
				this.read();
			} catch (ClassNotFoundException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
			System.out.println("Current state:");
			
			state = this.getCurrentState();
			state.updateCoords();

			System.out.println(state.toString());			

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			
			// verifica quale giocatore sei
			if (this.getPlayer().equals(Turn.WHITE)) 
			{
				// giocatore bianco 
				// verifica di chi è il turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.WHITE)) {
					// turno del giocatore bianco
					// chiama l'algoritmo per la scelta della mossa
					Action action = algorithm.makeDecision(state);
					System.out.println("Mossa scelta: " + action.toString());
					
					// invia la mossa al server
					try {
						this.write(action);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				}
				// turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
					System.out.println("Waiting for your opponent move... ");
				}
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				}
				// ho perso
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				}
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}
				
			}
			else
			{
				// giocatore nero
				// verifica di chi è il turno
				if (this.getCurrentState().getTurn().equals(StateTablut.Turn.BLACK)) {
					// turno del giocatore nero
					// chiama l'algoritmo per la scelta della mossa
					Action action = algorithm.makeDecision(state);
					System.out.println("Mossa scelta: " + action.toString());
					
					// invia la mossa al server
					try {
						this.write(action);
					} catch (ClassNotFoundException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				// turno dell'avversario
				else if (state.getTurn().equals(StateTablut.Turn.WHITE)) {
					System.out.println("Waiting for your opponent move... ");
				} 
				// ho perso				
				else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
					System.out.println("YOU LOSE!");
					System.exit(0);
				} 
				// ho vinto
				else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
					System.out.println("YOU WIN!");
					System.exit(0);
				} 
				// pareggio
				else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
					System.out.println("DRAW!");
					System.exit(0);
				}		
			}			
		}
	}
}
