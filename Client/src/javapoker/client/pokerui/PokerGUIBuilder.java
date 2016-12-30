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

import javapoker.client.game.Player;
import javapoker.client.game.Table;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Derpie on 30.12.2016.
 */
public class PokerGUIBuilder {
    private int playerCount;
    private int builtPlayers;
    private PokerGUIPlayer[] aPlayers;
    private JLabel lTableCards;
    private JLabel lPlayerPerformingAction;
    private JLabel lTablePot;
    private Table table;

    private JLabel lSmallBlind;
    private JLabel lBigBlind;
    private JLabel lDealer;

    private JTextField iAmount;
    private JTextArea tLog;
    public JButton bCheck, bBet, bFold;
    private AbsoluteLayoutFrame f;
    private String action;
    private int betAmount;

    public PokerGUIBuilder(int playerCount, Table table) {
        this.table = table;
        this.builtPlayers = 0;
        this.playerCount = playerCount;
        int windowWidth = 800;
        int infoHeight = 20;
        f = new AbsoluteLayoutFrame(windowWidth, infoHeight * (playerCount + 1) + 350);
        f.setTitle("Poker GUI - Client");
        f.add(new JLabel("Player Name"), 0, 0, windowWidth / 5, infoHeight);
        f.add(new JLabel("Player Money"), windowWidth / 5, 0, windowWidth / 5, infoHeight);
        f.add(new JLabel("Player Round Bet"), windowWidth / 5 * 2, 0, windowWidth / 5, infoHeight);
        f.add(new JLabel("Player Current Bet"), windowWidth / 5 * 3, 0, windowWidth / 5, infoHeight);
        f.add(new JLabel("Player Cards"), windowWidth / 5 * 4, 0, windowWidth / 5, infoHeight);

        aPlayers = new PokerGUIPlayer[playerCount];
        for (int i = 0; i < playerCount; i++) {
            aPlayers[i] = new PokerGUIPlayer(
                    new JLabel("Player " + (i + 1)),
                    new JLabel("0"),
                    new JLabel("0"),
                    new JLabel("0"),
                    new JLabel("-")
            );
            for (int i2 = 0; i2 < 5; i2++) {
                JLabel labelDump[] = aPlayers[i].dumpLabels();
                f.add(labelDump[i2], i2 * windowWidth / 5, (i + 1) * infoHeight, windowWidth / 5, infoHeight);
            }
        }


        //TableCards & Player Info
        int relativeTop = (playerCount + 1) * infoHeight + 30;
        lTableCards = new JLabel("Table Cards: ");
        lTablePot = new JLabel("Table Pot: ");
        lPlayerPerformingAction = new JLabel("Watiting for Table");

        f.add(lTableCards, 0, relativeTop, windowWidth / 3, infoHeight);
        f.add(lTablePot, windowWidth / 3, relativeTop, windowWidth / 3, infoHeight);
        f.add(lPlayerPerformingAction, windowWidth / 3 * 2, relativeTop, windowWidth / 3, infoHeight);

        //Small, Big, Dealer
        relativeTop += infoHeight + 10;
        lSmallBlind = new JLabel("SB: ");
        lBigBlind = new JLabel("BB: ");
        lDealer = new JLabel("D: ");

        f.add(lSmallBlind, 0, relativeTop, windowWidth / 3, infoHeight);
        f.add(lBigBlind, windowWidth / 3, relativeTop, windowWidth / 3, infoHeight);
        f.add(lDealer, windowWidth / 3 * 2, relativeTop, windowWidth / 3, infoHeight);

        //Player Actions
        relativeTop += infoHeight + 10;

        iAmount = new JTextField("0");
        bCheck = new JButton("CHECK");
        bCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.SendPlayerActionPerformed(
                        "CHECK",
                        0)
                ;
            }
        });
        bBet = new JButton("BET");
        bBet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.SendPlayerActionPerformed(
                        "BET",
                        Integer.parseInt(iAmount.getText())
                );
            }
        });
        bFold = new JButton("FOLD");
        bFold.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                table.SendPlayerActionPerformed(
                        "FOLD",
                        0
                );
            }
        });

        f.add(bCheck, 0, relativeTop, windowWidth / 4, infoHeight);
        f.add(bBet, windowWidth / 4, relativeTop, windowWidth / 4, infoHeight);
        f.add(iAmount, windowWidth / 4 * 2, relativeTop, windowWidth / 4, infoHeight);
        f.add(bFold, windowWidth / 4 * 3, relativeTop, windowWidth / 4, infoHeight);

        //Log...
        relativeTop += infoHeight + 10;
        tLog = new JTextArea("Log:\n");

        f.add(tLog, 0, relativeTop, windowWidth, 200);

        //Done?
        SetPlayerChoice(false);
        f.setVisible(true);
    }

    public void ChangeBettingOperations(boolean b) {
        //TODO
    }

    public void SetPlayerChoice(boolean b) {
        if (b) {
            action = null;
        }
        bCheck.setEnabled(b);
        bBet.setEnabled(b);
        iAmount.setEnabled(b);
        bFold.setEnabled(b);
    }

    public void SetSmallBlind(String name) {
        lSmallBlind.setText("SB: " + name);
    }

    public void SetBigBlind(String name) {
        lBigBlind.setText("BB: " + name);
    }

    public void SetDealer(String name)
    {
        lDealer.setText("D: " + name);
    }

    public PokerGUIPlayer GetPlayerByName(String name)
    {
        for(int i = 0; i < playerCount; i++) {
            if (aPlayers[i].GetName().equals(name)) {
                return aPlayers[i];
            }
        }
        return null;
    }

    public void WriteLog(String message)
    {
        //System.out.println(message);
        tLog.append("\n" + message);
    }

    public void BuildPlayer(Player p)
    {
        aPlayers[builtPlayers].SetName(p.id);
        builtPlayers += 1;
    }

    public void AddBoardCard(String card)
    {
        lTableCards.setText(lTableCards.getText() + card + " ");
    }

    public void ResetBoardCards()
    {
        lTableCards.setText("Table Cards: ");
    }

    public void SetTablePot(int pot)
    {
        lTableCards.setText("Table Pot: " + Integer.toString(pot));
    }

    public void SetPerformingPlayer(String player)
    {
        lPlayerPerformingAction.setText(player + " is makes action...");
    }

    public void ResetPlayerCards()
    {
        for (int i = 0; i < playerCount; i++) {
            aPlayers[i].SetCards("");
        }
    }

    public void AddDummyCards(String id)
    {
        for (int i = 0; i < playerCount; i++)
        {
            if (aPlayers[i] != GetPlayerByName(id)) {
                aPlayers[i].AddCard(" | ");
            }
        }
    }
}
