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

package game.models;

import java.util.ArrayList;

/**
 * Created by Tim on 06.11.2016.
 */
public class CircularList<E> extends ArrayList<E> {
	@Override
	public E get(int index)
	{
		if (size() > 0) {
			return super.get(index % size());
		}
		return null;
	}

	public void Swap(int index1, int index2) {
		E indexOneElement = this.get(index1);
		this.set(index1, this.get(index2));
		this.set(index2, indexOneElement);
	}
}
