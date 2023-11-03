package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import model.Observer;
import model.ObserverEnums;
import model.Store;
import model.Worker;
import controller.Controller;
import logger.Logger;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class GUI implements Observer, Serializable {
	private Store store = Store.getInstance();
	private Logger logger = Logger.getInstance();
	private Controller controller = Controller.getInstance();
	private boolean hasLoggedLow = true;
	private boolean hasLoggedHigh = false;
	private static GUI instance;
	private JTextArea logArea;
	private JProgressBar progressBar;
	private Worker lastRemovedWorker;
	int previousAmountOfWorkers;


	public static GUI getInstance() {
		if (instance == null) {
			instance = new GUI();
		}
		return instance;
	}

	private GUI() {

		store.registerObserver(this);
		controller.registerObserver(this);

		JFrame f = new JFrame("Production Regulator");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(600, 700);
		f.setLayout(new BorderLayout());

		JPanel leftPanel = createLeftPanel(controller);
		JPanel logPanel = createLogAreaPanel();

		f.add(leftPanel, BorderLayout.WEST);
		f.add(logPanel, BorderLayout.CENTER);

		f.setVisible(true);
	}

	private JPanel createLeftPanel(Controller controller) {
	    JPanel panel = new JPanel();
	    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

	    progressBar = new JProgressBar(0, store.getMaxCapacity());
	    progressBar.setValue(store.getCurrentCapacity());
	    progressBar.setStringPainted(true);
	    progressBar.setBorder(new TitledBorder("Capacity"));

	    panel.add(progressBar);
	    
	    JButton increaseWorkers = new JButton("Increase Workers");
	    increaseWorkers.addActionListener(e -> controller.increaseWorkers());
	    JButton reduceWorkers = new JButton("Reduce Workers");
	    reduceWorkers.addActionListener(e -> controller.reduceWorkers());
	    JButton saveStateButton = new JButton("Save State");
	    saveStateButton.addActionListener(e -> logger.saveState());
	    JButton loadStateButton = new JButton("Load State");
	    loadStateButton.addActionListener(e -> controller.loadState());

	    panel.add(increaseWorkers);
	    panel.add(Box.createVerticalStrut(5)); 
	    panel.add(reduceWorkers);
	    panel.add(Box.createVerticalStrut(5));
	    panel.add(saveStateButton);
	    panel.add(Box.createVerticalStrut(5));
	    panel.add(loadStateButton);
	    panel.add(Box.createVerticalGlue());

	    return panel;
	}

	private JPanel createLogAreaPanel() {
		JPanel logPanel = new JPanel(new BorderLayout());
		logPanel.setBorder(new TitledBorder("Log"));

		logArea = new JTextArea(20, 30);
		JScrollPane scrollPane = new JScrollPane(logArea);

		logPanel.add(scrollPane);

		return logPanel;
	}

	public void update(ObserverEnums observerEnum) {
		int currentCapacity = store.getCurrentCapacity();
		progressBar.setValue(currentCapacity);

		if (currentCapacity > 10 && currentCapacity < 90) {
			progressBar.setForeground(Color.lightGray);
		}

		try (FileInputStream fis = new FileInputStream("src/files/log.dat");
				ObjectInputStream ois = new ObjectInputStream(fis)) {

			switch (observerEnum) {

			case INVENTORY_UPDATE:
			    int capacity = ois.readInt();
			    if (capacity >= 90) {
			        progressBar.setForeground(Color.green);
			        if(!hasLoggedHigh) {
			            addLogMessage("The amount of products are over 90%");
			            hasLoggedHigh = true;
			        }
			    } else if (capacity <= 10) {
			        progressBar.setForeground(Color.red);
			        if(!hasLoggedLow) {
			            addLogMessage("The amount of products are under 10%");
			            hasLoggedLow = true;
			        }
			    } else {
			        hasLoggedHigh = false;
			        hasLoggedLow = false;
			    }
			    break;

			case AVERAGE_CAPACITY:
				int averageAmountOfProducts = ois.readInt();
				addLogMessage("The average amount of products in the store is " + averageAmountOfProducts);
				break;

			case WORKER_EMPLOYED:
				Worker worker = (Worker) ois.readObject();
				addLogMessage("Worker added with ID: " + worker.getId() + " and production speed "
						+ worker.getProductionSpeed());
				int currentAmountOfWorkers = ois.readInt();
				addLogMessage("Current number of workers: " + currentAmountOfWorkers);
				break;

			case WORKER_FIRED:
				if (lastRemovedWorker == controller.getLastRemovedWorker()) {
					addLogMessage("All workers are fired");
					return;
				}
				Worker worker1 = (Worker) ois.readObject();
				addLogMessage("Worker with ID: " + worker1.getId() + " got fired.");
				int currentAmountOfWorkers1 = ois.readInt();

				addLogMessage("Current number of workers: " + currentAmountOfWorkers1);
				lastRemovedWorker = controller.getLastRemovedWorker();

			default:
				break;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setLogArea(JTextArea logArea) {
		this.logArea = logArea;
	}

	public void clearLogArea() {
		logArea.setText("");
	}

	public void addLogMessage(String message) {
		logArea.append(message + "\n");
	}
}
