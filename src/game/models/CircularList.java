package game.models;

import java.util.ArrayList;

/**
 * Created by Tim on 06.11.2016.
 */
public class CircularList<E> extends ArrayList<E> {
	@Override
	public E get(int index)
	{
		return super.get(index % size());
	}
}
