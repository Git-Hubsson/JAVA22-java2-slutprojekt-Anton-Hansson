# Production Regulator

## Overview

This Java application simulates a production and consumption process within a store using a multi-threaded environment. It showcases object-oriented programming principles, design patterns such as Singleton and Observer, and synchronization techniques for concurrent thread execution.

## Features
 Singleton Store: A unique central inventory system managing the production and consumption of products.

Multithreaded Workers: Workers that produce products at varying speeds and add them to the store inventory.

Consumer Thread: Simulated consumer that purchases products from the store, maintaining a balance in inventory levels.

Observer Pattern: Implemented to notify the GUI about changes in the store's state, such as inventory updates and average capacity calculations.

Synchronized Access: Ensures thread safety when multiple threads interact with the store's inventory.

Dynamic Worker Adjustment: Ability to increase or decrease the number of worker threads through the GUI.

Persistent State: Features for saving and loading the state of the application.

## GUI
The graphical user interface provides real-time updates on the store's inventory capacity, logs significant events, and allows the user to interact with the simulation by adding or removing workers and saving or loading the application state.

## Usage
To run the simulation, clone the repository and execute the main program. The GUI will display the store's status and provide controls for interacting with the simulation.
