package model;

import java.io.Serializable;

public class Product implements Serializable {
	private long id = 0;
	private String name;
	private String[] products = {
		    "Laptop", "Smartphone", "Refrigerator", "Microwave", "Bicycle", 
		    "Television", "Bluetooth Speaker", "Coffee Maker", "Wristwatch", "Earrings", 
		    "Office Chair", "Desk Lamp", "Running Shoes", "Electric Kettle", "Sunglasses", 
		    "Digital Camera", "Air Conditioner", "Printer", "Oven", "Toaster", 
		    "Perfume", "Vacuum Cleaner", "Washing Machine", "Hairdryer", "Sofa", 
		    "Bed", "Dining Table", "Bookshelf", "Waffle Maker", "Electric Toothbrush", 
		    "Blender", "Wireless Mouse", "Keyboard", "External Hard Drive", "Water Filter", 
		    "Ice Cream Maker", "Electric Fan", "Umbrella", "Grill", "Hammock", 
		    "Wall Clock", "Handbag", "Sandals", "Picture Frame", "Wine Glasses", 
		    "Ceramic Vase", "Headphones", "Yoga Mat", "Suitcase", "Electric Razor", 
		    "Treadmill", "Tablecloth", "Juicer", "Notebook", "Frying Pan", 
		    "Scarf", "Plush Toy", "Leather Wallet", "Garden Hose", "Indoor Plant", 
		    "Candles", "Tool Set", "Bread Maker", "Alarm Clock", "Throw Pillow", 
		    "Dish Rack", "Trash Can", "Hairbrush", "Scented Soap", "Electric Mixer", 
		    "Rice Cooker", "Skillet", "Snow Boots", "Portable Charger", "Luggage Tag", 
		    "Fruit Bowl", "Patio Heater", "Electric Blanket", "Jigsaw Puzzle", "Rain Jacket", 
		    "Steak Knives", "Shower Curtain", "Teapot", "Decorative Lantern", "Ties", 
		    "Towel Set", "Bird Feeder", "Essential Oil Diffuser", "DVD Player", "Ice Bucket", 
		    "Wall Mirror", "Desk Organizer", "Exercise Ball", "Garden Gnome", "Table Tennis Paddle", 
		    "Popcorn Maker", "Chandelier", "Leather Gloves", "Inflatable Pool", "Board Game"
		};
	
	public Product() {
		this.id = id++;
		this.name = products[(int) (Math.random()*100)];
	}
	
	public String getName() {
		return name;
	}

}
