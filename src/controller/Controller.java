package controller;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Consumer;
import model.Observable;
import model.Observer;
import model.ObserverEnums;
import model.Product;
import model.Store;
import model.Worker;
import view.GUI;

public class Controller implements Observable, Serializable {
	private static Controller instance;
	private Store store = Store.getInstance();
	private transient Deque<Thread> consumersThread = new LinkedList<>();
	private transient Deque<Thread> workersThread = new LinkedList<>();
	private Deque<Consumer> consumers = new LinkedList<>();
	private Deque<Worker> workers = new LinkedList<>();
	private List<Observer> observers = new ArrayList<>();
	private Worker lastRemovedWorker;


	private Controller() {
		createConsumers();
	}

	public static synchronized Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}
	
	public Deque<Consumer> getConsumers() {
		return consumers;
	}

	public void increaseWorkers() {
		Worker newWorker = new Worker(store);
		Thread workerThread = new Thread(newWorker);
		workers.add(newWorker);
		workersThread.add(workerThread);
		workerThread.start();

		notifyObservers(ObserverEnums.WORKER_EMPLOYED);
	}

	public void reduceWorkers() {
		if (!workers.isEmpty() && !workersThread.isEmpty()) {
			lastRemovedWorker = workers.poll();
			Thread lastThread = workersThread.poll();
			lastThread.interrupt();
		}
		notifyObservers(ObserverEnums.WORKER_FIRED);
	}

	public void createConsumers() {
		for (int i = 0; i <= 3 + (int) Math.random() * 12; i++) {
			Consumer consumer = new Consumer(store);
			Thread consumerThread = new Thread(consumer);
			consumers.add(consumer);
			consumersThread.add(consumerThread);
			consumerThread.start();
		}
	}
	
	public void loadState() {
	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("src/files/save.dat"))) {
	        stopAndClearAllCurrentThreads(); 
	        
	        GUI.getInstance().clearLogArea();
	        
	        Queue<Product> loadedInventory = (Queue<Product>) ois.readObject();
	        store.setInventory(loadedInventory);
	        
	        notifyObservers(ObserverEnums.INVENTORY_UPDATE);

	        List<Worker> loadedWorkers = (List<Worker>) ois.readObject();
	        workers.clear();
	        
	        for (Worker worker : loadedWorkers) {
	        	worker.setStore(store);
	        	workers.add(worker);
	            Thread workerThread = new Thread(worker);
	            workersThread.add(workerThread);
	            workerThread.start();
	            notifyObservers(ObserverEnums.WORKER_EMPLOYED);
	        }

	        List<Consumer> loadedConsumers = (List<Consumer>) ois.readObject();
	        consumers.clear();
	        
	        for (Consumer consumer : loadedConsumers) {
	        	consumer.setStore(store); 
	            consumers.add(consumer);
	            Thread consumerThread = new Thread(consumer);
	            consumersThread.add(consumerThread);
	            consumerThread.start();
	        }
	        
	        int averageCapacity = ois.readInt();
	        store.setAverageCapacity(averageCapacity);
	        

	    } catch (IOException | ClassNotFoundException e) {
	        e.printStackTrace();
	    }
	}

	public void stopAndClearAllCurrentThreads() {
	    interruptAllThreads(workersThread);
	    interruptAllThreads(consumersThread);

	    joinAllThreads(workersThread);
	    joinAllThreads(consumersThread);

	    workersThread.clear();
	    consumersThread.clear();
	}

	private void interruptAllThreads(Deque<Thread> threads) {
	    for (Thread thread : threads) {
	        thread.interrupt();
	    }
	}

	private void joinAllThreads(Deque<Thread> threads) {
	    for (Thread thread : threads) {
	        try {
	            thread.join();
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }
	}

	@Override
	public void registerObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public synchronized void notifyObservers(ObserverEnums enumState) {
		for (Observer observer : observers) {
			observer.update(enumState);
		}
	}
	
	public Worker getLastRemovedWorker() {
		return lastRemovedWorker;
	}

	public Deque<Worker> getWorkers() {
		return new LinkedList<>(workers);
	}

	@Override
	public void removeObserver(Observer observer) {
	}
}