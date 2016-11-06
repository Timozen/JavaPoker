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

    public WinnerObject(CircularList<Player> playerList, Table table){
        actualWinningPlayers = new ArrayList<Player>();

        winningPlayers = (CircularList<Player>)playerList.clone();
        checker = new HandChecker();
        for (Player p : playerList) {
            if (p.GetPlayerState() == PlayerState.FOLD) {
                winningPlayers.remove(p);
            } else {
                p.SetCardsWithTable(table);
            }
        }
        this.table = table;

        //Calculate List of winners
        //-------------
        //First Step: Bubblesort
        //Second Step: Giving numeric representations
        //Third Step: Getting Winners
        for (int i = 0; i < winningPlayers.size() - (i + 1); i++) {
            for (int j = 0; j < winningPlayers.size() - (i + 1); j++){
                if (checker.check(winningPlayers.get(j).GetCardsWithTable())
                        .compareTo(checker.check(winningPlayers.get(j + 1).GetCardsWithTable()))
                        == -1){
                    //
                    Player p = winningPlayers.get(j);
                    winningPlayers.set(j, winningPlayers.get(j + 1));
                    winningPlayers.set(j + 1, p);
                }
            }
        }
        //Setting numeric values
        int winnerNumber = 0;
        winningPlayers.get(0).SetWinnerNumber(winnerNumber);
        for (int i = 0; i< winningPlayers.size() - 1; i++) {
            if (checker.check(winningPlayers.get(i).GetCardsWithTable())
                    .compareTo(checker.check(winningPlayers.get(i + 1).GetCardsWithTable()))
                    != -1) {
                winnerNumber += 1;
                winningPlayers.get(i + 1).SetWinnerNumber(winnerNumber);
            }
        }
    }

    public void CalculateActualWinnerList(){
        int highestNumeric = winningPlayers.get(winningPlayers.size() - 1).GetWinnerNumber();
        for (int i = winningPlayers.size() - 1;
            winningPlayers.get(i).GetWinnerNumber() == highestNumeric && winningPlayers.size() > 0;
            i--) {
            actualWinningPlayers.add(winningPlayers.get(i));
            winningPlayers.remove(i);
        }
        //actualWinnerList is done
        //need calculation for highest Bets (if they are not equal, anyway dont care if)
        for (int i = 0; i < actualWinningPlayers.size() - (i + 1); i++) {
            for (int j = 0; j < actualWinningPlayers.size() - (i + 1); j++){
                if(actualWinningPlayers.get(j).GetRoundBet() < actualWinningPlayers.get(j + 1).GetRoundBet()) {
                    Player p = actualWinningPlayers.get(j);
                    actualWinningPlayers.set(j, actualWinningPlayers.get(j + 1));
                    actualWinningPlayers.set(j + 1, p);
                }
            }
        }
        //lowest RoudnBet is on top
        //.size() - 1 Player is Player with smallest RoundBet
    }

    public List<Player> GetActualWinnerList(){
        return actualWinningPlayers;
    }

    public Player PopActualWinner() {
        Player p = actualWinningPlayers.get(actualWinningPlayers.size() - 1);
        actualWinningPlayers.remove(p);
        return p;
    }

    public int GetWinnerAmount() { return this.winningPlayers.size(); }

    public int GetActualWinnerAmount() { return  this.actualWinningPlayers.size(); }

    public boolean IsSomeActualWinnerAllIn() {
        for (Player p : actualWinningPlayers) {
            if (p.GetPlayerState() == PlayerState.ALLIN) { return true; }
        }
        return false;
    }
}
