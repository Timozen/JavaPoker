package game;

import game.models.CircularList;
import game.models.PlayerState;
import handChecker.HandChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Derpie on 29.10.2016.
 */
public class WinnerObject {
	private CircularList<Player> winningPlayers;
	private List<Player> actualWinningPlayers;
	
	private Table table;
	private HandChecker checker;

	public WinnerObject(CircularList<Player> playerList, Table table)
	{
		this.table = table;
		checker = new HandChecker();
		CircularList<Player> playersInRound = (CircularList<Player>)table.GetPlayersOnTable().clone();
		winningPlayers = new CircularList<>();
		System.out.println("Players who can win:");
		for (int i = 0; i < playersInRound.size(); i++){
			Player p = playersInRound.get(i);
			if (p.GetPlayerState() == PlayerState.ALLIN || p.GetPlayerState() == PlayerState.PLAYING) {
				p.SetCardsWithTable(table);
				p.SetWinnerNumber(i);
				winningPlayers.add(p);
				System.out.println(p.GetNickname() + " with roundBet " + p.GetRoundBetAll() + " Number " + p.GetWinnerNumber());
			}
		}

		//Bubblesort, easy and with max amount of 23 players
		//Still doing well (529 iterations)
		//Highest winner is on top
		System.out.println("\nStarting Winner Comparison");
		for (int i = 0; i < winningPlayers.size() - 1; i++){
			for (int j = 0; j < winningPlayers.size() - (1 + i); j++) {
				//indexPlayerOne = j
				//indexPlayerTwo = j + 1
				Player p1 = winningPlayers.get(j);
				Player p2 = winningPlayers.get(j + 1);
				System.out.println("\nComparing "
						+ p1.GetNickname()
						+ " to "
						+ p2.GetNickname());
				System.out.println(p1.GetNickname() + " Cards: "
						+ p1.GetCardsWithTable());
				System.out.println(p2.GetNickname() + " Cards: "
						+ p2.GetCardsWithTable());
				int result = checker.check(p1.GetCardsWithTable()).compareTo(checker.check(p2.GetCardsWithTable()));
				//p1 == p2	=>	0
				//p1 < p2	=> -1
				//p1 > p2	=>	1 => swap
				switch (result) {
					case 0:
						System.out.println(p1.GetNickname() + " equal to " + p2.GetNickname());
						break;
					case 1:
						System.out.println(p1.GetNickname() + " worse than " + p2.GetNickname());
						break;
					case -1:
						System.out.println(p1.GetNickname() + " better than " + p2.GetNickname());
						p2.DecreaseWinnerNumber(1);
						p1.IncreaseWinnerNumber(1);
						winningPlayers.Swap(j, j + 1);
						break;
				}
			}
		}
	}

	public void dumpWinnerListAsc() {
		System.out.println("\n#### Winning Players Output Desc Start");
		for (int i = 0; i < winningPlayers.size(); i++) {
			System.out.println(winningPlayers.get(i).GetNickname()
					+ " with RoundBet " + winningPlayers.get(i).GetRoundBetAll()
					+ " Number " + winningPlayers.get(i).GetWinnerNumber());
			System.out.println("Cards: " + winningPlayers.get(i).GetCardsWithTable());
		}
		for (int i = 0; i < winningPlayers.size(); i++){
			System.out.print(winningPlayers.get(i).GetWinnerNumber() + " ");
		}
		System.out.println("\n#### Winning Players Output Desc End\n");
	}

	public void dumpWinnerListDesc() {
		System.out.println("\n#### Winning Players Output Asc Start");
		for (int i = winningPlayers.size() - 1; i >= 0; i--) {
			System.out.println(winningPlayers.get(i).GetNickname()
					+ " with RoundBet " + winningPlayers.get(i).GetRoundBetAll()
					+ " Number " + winningPlayers.get(i).GetWinnerNumber());
			System.out.println("Cards: " + winningPlayers.get(i).GetCardsWithTable());
		}
		for (int i = winningPlayers.size() - 1;  i >= 0; i--){
			System.out.print(winningPlayers.get(i).GetWinnerNumber() + " ");
		}
		System.out.println("\n#### Winning Players Output Asc End\n");
	}
}
