package app.cap.ajm.API;

import java.util.List;

import app.cap.ajm.Model.Item;

public interface OnFinishSearchListener {
	public void onSuccess(List<Item> itemList);
	public void onFail();
}
