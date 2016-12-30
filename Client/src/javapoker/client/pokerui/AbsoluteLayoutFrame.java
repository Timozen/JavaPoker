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
import java.awt.*;

/**
 * Created by Derpie on 30.12.2016.
 */
class AbsoluteLayoutFrame extends JFrame {

    public AbsoluteLayoutFrame(int width, int height)
    {
        this.setLayout(null);
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public Component add(Component component, int x, int y, int width, int height)
    {
        component.setBounds(x, y, width, height);
        super.add(component);
        return component;
    }

    public Component add(Component component, int x, int y)
    {
        component.setBounds(x, y, component.getPreferredSize().width, component.getPreferredSize().height);
        super.add(component);
        return component;
    }

    @Override
    public Component add(Component component)
    {
        component.setBounds(0, 0, component.getPreferredSize().width, component.getPreferredSize().height);
        super.add(component);
        return component;
    }
}
