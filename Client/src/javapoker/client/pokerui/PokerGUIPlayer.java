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

package javapoker.client.pokerui;

import javax.swing.*;

/**
 * Created by Derpie on 30.12.2016.
 */
public class PokerGUIPlayer {
    private JLabel[] lPlayerInfo;
    //0 = Name
    //1 = Money
    //2 = Round Bet
    //3 = Current Bet
    //4 = Cards //=> If fold go to "fold"

    PokerGUIPlayer(JLabel name, JLabel money, JLabel roundBet, JLabel currentBet, JLabel cards) {
        lPlayerInfo = new JLabel[5];
        lPlayerInfo[0] = name;
        lPlayerInfo[1] = money;
        lPlayerInfo[2] = roundBet;
        lPlayerInfo[3] = currentBet;
        lPlayerInfo[4] = cards;
    }

    public void SetName(String name) {
        lPlayerInfo[0].setText(name);
    }

    public void SetMoney(int money) {
        lPlayerInfo[1].setText(Integer.toString(money));
    }

    public void SetRoundBet(int roundBet) {
        lPlayerInfo[2].setText(Integer.toString(roundBet));
    }

    public void SetCurrentBet(int currentBet) {
        lPlayerInfo[3].setText(Integer.toString(currentBet));
    }

    public void SetCards(String cards) {
        lPlayerInfo[4].setText(cards);
    }

    public void AddCard(String card)
    {
        lPlayerInfo[4].setText(lPlayerInfo[4].getText() + " " + card);
    }

    public JLabel[] dumpLabels() {
        return lPlayerInfo;
    }

    public String GetName()
    {
        return lPlayerInfo[0].getText();
    }

    public void Disable()
    {
        for(int i = 0; i < 5; i++){
            lPlayerInfo[i].setVisible(false);
        }
    }
}
