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

import javapoker.client.connection.SocketConnection;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Derpie on 30.12.2016.
 */
public class PokerLoginGUIBuilder {
    private SocketConnection connection;
    public AbsoluteLayoutFrame f;
    public JLabel lResult;

    public PokerLoginGUIBuilder(SocketConnection connection)
    {
        this.connection = connection;
        int windowWidth = 300;
        f = new AbsoluteLayoutFrame(300, 160);
        f.setTitle("Login to Server");
        f.add(new JLabel("User Information"), 0, 0, windowWidth, 20);
        f.add(new JLabel("Name"), 0, 25, windowWidth / 4, 20);
        f.add(new JLabel("Pass"), 0, 50, windowWidth / 4, 20);
        JTextField iName = new JTextField("test");
        JTextField iPass = new JTextField("pass");
        f.add(iName, windowWidth / 4, 25, windowWidth * 3/4 - 25, 20);
        f.add(iPass, windowWidth / 4, 50, windowWidth * 3/4 - 25, 20);

        //Login Possibilities & Result
        JButton bLogin = new JButton("Login");
        bLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    PerformLoginRegisterRequest(iName.getText(), iPass.getText(), "LOGIN_REQUEST_ANSWER");
                }
        });
        JButton bRegister = new JButton("Register");
        bRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PerformLoginRegisterRequest(iName.getText(), iPass.getText(), "REGISTER_REQUEST");
            }
        });

        f.add(bLogin, 0, 75, windowWidth / 2, 20);
        f.add(bRegister, windowWidth / 2, 75, windowWidth / 2, 20);
        lResult = new JLabel("-------");
        f.add(lResult, 0, 100, windowWidth, 20);

        f.setVisible(true);
    }

    private void PerformLoginRegisterRequest(String userName, String password, String type)
    {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(password.getBytes());
            byte[] digest = messageDigest.digest();
            StringBuilder stringBuffer = new StringBuilder();

            for (byte b : digest) {
                stringBuffer.append(String.format("%02x", b & 0xef));
            }

            password = stringBuffer.toString();

            connection.SendMessage(new JSONObject().put("op", 1)
                    .put("type", type)
                    .put("data", new JSONObject().put("username", userName)
                            .put("password", password)
                    )
            );

        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
    }
}
