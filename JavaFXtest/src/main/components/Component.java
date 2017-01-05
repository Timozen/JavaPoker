package main.components;

import javafx.scene.layout.Pane;


public abstract class Component extends Pane {
	
	protected boolean isTurn;
	
	public abstract void SetNameLabel(String name);
	public abstract void SetMoneyLabel(String money);
	public abstract void SetBetAmountLabel(String betAmount);
	public abstract void SetStatusLabel(String betAmount);
	public abstract void SetSmallBlind();
	public abstract void SetBigBlind();
	public abstract void SetDealer();
	public abstract void TurnChange(boolean isTurn);
	public abstract void SetCard1(String card);
	public abstract void SetCard2(String card);
	
	public boolean IsTurn()
	{
		return isTurn;
	}
	
	public void SetTurn(boolean turn)
	{
		isTurn = turn;
		TurnChange(isTurn);
	}
}
