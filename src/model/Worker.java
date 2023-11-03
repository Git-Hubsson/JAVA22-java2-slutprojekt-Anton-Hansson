package model;

import java.io.Serializable;

public class Worker implements Runnable, Serializable {
    private transient Store store;
    private static int idCounter = 0;
    private int id;
    private int productionSpeed = (int) (1000+(Math.random()*9000));
    
    public Worker(Store store) {
        this.store = store;
        this.id = idCounter++;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(productionSpeed);
                Product randomProduct = new Product();
                store.addToInventory(randomProduct);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  
                break;
            }
        }
    }

    public int getProductionSpeed() {
        return productionSpeed;
    }

    public int getId() {
        return id;
    }

    public void setStore(Store store) {
        this.store = store;
    }

}

