package model;

import java.io.Serializable;

public class Consumer implements Runnable, Serializable {
	Store store = null;
	private int productionSpeed = (int) (1000+(Math.random()*9000));
	
	public Consumer(Store store) {
		this.store = store;
	}

	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(productionSpeed);
				store.consume();
			} catch (InterruptedException e) {
                Thread.currentThread().interrupt();  
                break;
			}
		}
	}

	public void setStore(Store store) {
        this.store = store;
    }
	
}
