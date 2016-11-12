package game;

import game.models.CircularList;
import game.models.PlayerState;
import handChecker.HandChecker;

/**
 * Created by Derpie on 29.10.2016.
 */
public class WinnerHandler {
	private CircularList<Player> playingAndPayingPlayers;
	private CircularList<WinnerPlayer> winningPlayers;
	
	private Table table;
	private HandChecker checker;

	public WinnerHandler(Table table)
	{
		this.table = table;
		checker = new HandChecker();
		CircularList<Player> playersInRound = (CircularList<Player>)table.GetPlayersOnTable().clone();
		playingAndPayingPlayers = new CircularList<>();
		System.out.println("Players who can win:");
		for (int i = 0; i < playersInRound.size(); i++){
			Player p = playersInRound.get(i);
			if (p.GetPlayerState() == PlayerState.ALLIN || p.GetPlayerState() == PlayerState.PLAYING) {
				p.SetCardsWithTable(table);
				p.SetWinnerNumber(i);
				playingAndPayingPlayers.add(p);
				System.out.println(p.GetNickname() + " with roundBet " + p.GetRoundBetAll() + " Number " + p.GetWinnerNumber());
			}
		}

		//Bubblesort, easy and with max amount of 23 players
		//Still doing well (529 iterations)
		//Highest winner is on top
		System.out.println("\nStarting Winner Comparison");
		for (int i = 0; i < playingAndPayingPlayers.size() - 1; i++){
			for (int j = 0; j < playingAndPayingPlayers.size() - (1 + i); j++) {
				//indexPlayerOne = j
				//indexPlayerTwo = j + 1
				Player p1 = playingAndPayingPlayers.get(j);
				Player p2 = playingAndPayingPlayers.get(j + 1);
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
						p1.SetWinnerNumber(p2.GetWinnerNumber());
						System.out.println(p1.GetNickname() + " equal to " + p2.GetNickname());
						break;
					case 1:
						System.out.println(p1.GetNickname() + " worse than " + p2.GetNickname());
						break;
					case -1:
						System.out.println(p1.GetNickname() + " better than " + p2.GetNickname());
						p2.DecreaseWinnerNumber(1);
						p1.IncreaseWinnerNumber(1);
						playingAndPayingPlayers.Swap(j, j + 1);
						break;
				}
			}
		}
	}

	public CircularList<WinnerPlayer> CalculateWinnerPlayerList()
	{
		System.out.println("\n#### Starting WinnerList Calculation");
		System.out.println("Current pot value: " + table.GetPotValue());
		winningPlayers = new CircularList<>();
		boolean isPlayerAllIn = false;

		if (playingAndPayingPlayers.size() > 0) {
			System.out.println(playingAndPayingPlayers.get(playingAndPayingPlayers.size() - 1).GetNickname() + " added to actual winners.");
			winningPlayers.add(new WinnerPlayer(playingAndPayingPlayers.get(playingAndPayingPlayers.size() - 1)));
			if (playingAndPayingPlayers.get(playingAndPayingPlayers.size() - 1).GetPlayerState() == PlayerState.ALLIN) {
				isPlayerAllIn = true;
			}
		}


		for (int i = playingAndPayingPlayers.size() - 2; i >= 0; i--) {
			Player p1 = playingAndPayingPlayers.get(i + 1);	//already contained
			Player p2 = playingAndPayingPlayers.get(i + 2);	//not contained
			if (checker.check(p1.GetCardsWithTable()).compareTo(
					checker.check(p2.GetCardsWithTable())) == 0) {
				System.out.println(playingAndPayingPlayers.get(playingAndPayingPlayers.size() - 1).GetNickname() + " added to actual winners.");
				winningPlayers.add(new WinnerPlayer(p2));
				if (p2.GetPlayerState() == PlayerState.ALLIN) {
					isPlayerAllIn = true;
				}
			} else {
				System.out.println(p2.GetNickname() + " is worse than " + p1.GetNickname());
				i = -1;
			}
		}

		if (winningPlayers.size() == 1) {
			System.out.println(winningPlayers.get(0).GetPlayerHandle().GetNickname() + " is onliest winner.");
			if (!isPlayerAllIn) {
				System.out.println("The player is not allin.");
				winningPlayers.get(0).SetWinAmount(table.GetPotValue());
				winningPlayers.get(0).SetFullyPaid(true);
				System.out.println("The player will win " + winningPlayers.get(0).GetWinAmount());
			} else {
				System.out.println("The player is allin.");
				winningPlayers.get(0).SetWinAmount(
						table.GetHighestAmountToSubstractFromPot(
								winningPlayers.get(0).GetPlayerHandle().GetRoundBetAll()));
				winningPlayers.get(0).SetFullyPaid(true);
				//SetFullyPaid = Cam be removed from playingAndPayingPlayers
				this.playingAndPayingPlayers.remove(winningPlayers.get(0).GetPlayerHandle());
				System.out.println("The player will win " + winningPlayers.get(0).GetWinAmount());
			}
		} else {
			//SPLITPOT
			if (isPlayerAllIn) {
				//Splitpot with All-In State

			} else {
				//SplitPot without AllIn => Every player gets part of table value
				System.out.println("Splitpot and no Player is allin.");
				int winAmountPerPlayer = table.GetPotValue() / winningPlayers.size();
				for (WinnerPlayer wp : winningPlayers) {
					System.out.println(wp.GetPlayerHandle().GetNickname() + " will win " + winAmountPerPlayer);
					wp.SetWinAmount(winAmountPerPlayer);
					wp.SetFullyPaid(true);
					playingAndPayingPlayers.remove(wp.GetPlayerHandle());
				}
			}
		}

		System.out.println("#### Ending WinnerList Calculation\n");
		return winningPlayers;
	}

	public void dumpWinnerListAsc()
	{
		System.out.println("\n#### Winning Players Output Asc Start");
		for (int i = 0; i < playingAndPayingPlayers.size(); i++) {
			System.out.println(playingAndPayingPlayers.get(i).GetNickname()
					+ " with RoundBet " + playingAndPayingPlayers.get(i).GetRoundBetAll()
					+ " Number " + playingAndPayingPlayers.get(i).GetWinnerNumber());
			System.out.println("Cards: " + playingAndPayingPlayers.get(i).GetCardsWithTable());
		}
		for (int i = 0; i < playingAndPayingPlayers.size(); i++){
			System.out.print(playingAndPayingPlayers.get(i).GetWinnerNumber() + " ");
		}
		System.out.println("\n#### Winning Players Output Asc End\n");
	}

	public void dumpWinnerListDesc()
	{
		System.out.println("\n#### Winning Players Output Desc Start");
		for (int i = playingAndPayingPlayers.size() - 1; i >= 0; i--) {
			System.out.println(playingAndPayingPlayers.get(i).GetNickname()
					+ " with RoundBet " + playingAndPayingPlayers.get(i).GetRoundBetAll()
					+ " Number " + playingAndPayingPlayers.get(i).GetWinnerNumber());
			System.out.println("Cards: " + playingAndPayingPlayers.get(i).GetCardsWithTable());
		}
		for (int i = playingAndPayingPlayers.size() - 1; i >= 0; i--){
			System.out.print(playingAndPayingPlayers.get(i).GetWinnerNumber() + " ");
		}
		System.out.println("\n#### Winning Players Output Desc End\n");
	}
}