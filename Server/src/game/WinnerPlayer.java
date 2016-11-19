/*
 * JavaPoker - Online Poker Game Copyright (C) 2016 Tim Büchner, Matthias Döpmann
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */

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
