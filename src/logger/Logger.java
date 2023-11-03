package logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Deque;

import controller.Controller;
import model.Observer;
import model.ObserverEnums;
import model.Store;
import model.Worker;

public class Logger implements Observer, Serializable {
	private static Logger instance;
	private Store store = Store.getInstance();
	private Controller controller = Controller.getInstance();
	Worker lastRemovedWorker;

	private Logger() {
		controller.registerObserver(this);
		store.registerObserver(this);
	}

	public static synchronized Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	@Override
	public synchronized void update(ObserverEnums enumState) {
		Deque<Worker> workers = controller.getWorkers();
		int currentAmountOfWorkers = workers.size();
		int capacity = store.getCurrentCapacity();

		try (FileOutputStream fos = new FileOutputStream("src/files/log.dat");
				ObjectOutputStream oos = new ObjectOutputStream(fos)) {
			switch (enumState) {

			case INVENTORY_UPDATE:
				oos.writeInt(capacity);
				break;

			case AVERAGE_CAPACITY:
				oos.writeInt(store.getAverageCapacity());
				System.out.println("The average amount of products in the store is " + store.getAverageCapacity());
				break;

			case WORKER_EMPLOYED:
				Worker worker = workers.peekLast();
				oos.writeObject(worker);
				oos.writeInt(currentAmountOfWorkers);
				System.out.println("Worker added");
				System.out.println("Current number of workers: " + currentAmountOfWorkers);
				break;

			case WORKER_FIRED:
				if (lastRemovedWorker == controller.getLastRemovedWorker()) {
					System.out.println("All workers are fired");
					return;
				}
				oos.writeObject(controller.getLastRemovedWorker());
				oos.writeInt(currentAmountOfWorkers);
				lastRemovedWorker = controller.getLastRemovedWorker();
				System.out.println("Worker with id " + controller.getLastRemovedWorker().getId() + " got fired");
				break;

			default:
				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveState() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("src/files/save.dat"))) {
			oos.writeObject(store.getInventory());
			oos.writeObject(new ArrayList<>(controller.getWorkers()));
			oos.writeObject(new ArrayList<>(controller.getConsumers()));
			oos.writeInt(store.getAverageCapacity());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
