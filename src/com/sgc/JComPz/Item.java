package com.sgc.JComPz;

public class Item {
	public int id;
	public String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String toString() {
		return getName();
	}
	public Item(String name) {
		super();
		this.name = name;
	}
	public Item() {
		super();
	}
	
}
