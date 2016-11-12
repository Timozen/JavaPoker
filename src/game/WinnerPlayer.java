package game;

/**
 * Created by Derpie on 12.11.2016.
 */
public class WinnerPlayer
{
    private Player p;
    private int winAmount;
    private boolean isFullyPaid;

    WinnerPlayer(Player p)
    {
        isFullyPaid = false;
        this.p = p;
    }

    WinnerPlayer(Player p, int winAmount)
    {
        this(p);
        this.winAmount = winAmount;
    }

    public int GetWinAmount()
    {
        return this.winAmount;
    }

    public void SetWinAmount(int winAmount)
    {
        this.winAmount = winAmount;
    }

    public Player GetPlayerHandle()
    {
        return this.p;
    }

    public void SetFullyPaid(boolean isFullyPaid)
    {
        this.isFullyPaid = isFullyPaid;
    }
}
