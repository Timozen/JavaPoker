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

	public int GetIndex(E element)
	{
		for(int i = 0; i < this.size() - 1; i++) {
			if (get(i) == element) { return i; }
		}
		return -1;
	}
}
