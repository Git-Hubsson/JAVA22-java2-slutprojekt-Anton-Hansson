package model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.Timer;


public class Store implements Observable, Serializable {

    private static Store instance;
    private int maxCapacity = 100;
    private Queue<Product> inventory = new LinkedList<Product>();
    private List<Observer> observers = new ArrayList<>();
	private int averageCapacity;
	private int totalCapacitySum = 0;
	private int updateCount = 0;

    private Store() {
    	averageTimer.start();
    	logAverageTimer.start();
    }

    public synchronized static Store getInstance() {
        if (instance == null) {
            instance = new Store();
        }
        return instance;
    }
    
    Timer averageTimer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			totalCapacitySum += inventory.size();
		    updateCount++;
		    averageCapacity = totalCapacitySum / updateCount;
		}
	});
    
    Timer logAverageTimer = new Timer(10000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			notifyObservers(ObserverEnums.AVERAGE_CAPACITY);
		}
	});

    public synchronized void addToInventory(Product product) {
        while (inventory.size() >= maxCapacity) {
            try {
                wait();
            } catch (InterruptedException e) {
            	Thread.currentThread().interrupt();
            	return;
            }
        }
        inventory.add(product);
        System.out.println(product.getName() + " added to inventory.");
        notify();

        notifyObservers(ObserverEnums.INVENTORY_UPDATE);
    }

    public synchronized void consume() {
        while (inventory.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
            	Thread.currentThread().interrupt();
            	return;
            }
        }
        Product product = inventory.remove();
        System.out.println("**!! Consumer bought " + product.getName());
        notify();

        notifyObservers(ObserverEnums.INVENTORY_UPDATE);
    }

    public synchronized int getCurrentCapacity() {
        return inventory.size();
    }

    public synchronized int getMaxCapacity() {
        return maxCapacity;
    }

    @Override
    public synchronized void registerObserver(Observer observer) {
        observers.add(observer);
    }

	@Override
	public synchronized void notifyObservers(ObserverEnums enumState) {
		for (Observer observer : observers) {
            observer.update(enumState);
        }
	}

	public Object getInventory() {
		return inventory;
	}

	public void setInventory(Queue<Product> inventory) {
		this.inventory = inventory;
	}

	public synchronized int getAverageCapacity() {
		return averageCapacity;
	}

	public void setAverageCapacity(int averageCapacity) {
		this.averageCapacity = averageCapacity;
	}

	@Override
	public void removeObserver(Observer observer) {
	}

}
