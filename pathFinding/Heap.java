package com.TowerDefense.game.pathFinding;



public class Heap <T extends IHeapItem<T>>{
	private Array<T> items;
	private int currentItemSize;
	
	public Heap(int maxHeapSize) {
		items = new Array<T>(maxHeapSize);
	}
	
	public void add(T newItem) {
		newItem.heapIndex = currentItemSize;
		items.set(currentItemSize,newItem);
		sortUp(newItem);
		currentItemSize++;
	}
	
	public T removeFirst() {
		T firstItem = items.get(0);
		currentItemSize--;
		items.set(0, items.get(currentItemSize));
		items.get(0).heapIndex = 0;
		sortDown(items.get(0));
		return firstItem;
	}
	
	private void sortUp(T newItem) {
		int parentIndex = (newItem.heapIndex-1)/2;
		
		while (true) {
			T parentItem = items.get(parentIndex);
			if (newItem.compareTo(parentItem) > 0)
				swap(newItem, parentItem);
			else 
				break;
			parentIndex = (newItem.heapIndex-1)/2;
		}
	}
	
	private void sortDown(T item) {
		while (true) {
			int childIndexLeft = item.heapIndex*2+1;
			int childIndexRight = item.heapIndex*2+2;
			int swapIndex = 0;
			
			if (childIndexLeft < currentItemSize) {
				swapIndex = childIndexLeft;
				if (childIndexRight < currentItemSize) {
					if (items.get(childIndexLeft).compareTo(items.get(childIndexRight)) < 0)
						swapIndex = childIndexRight;
				}
				
				if (item.compareTo(items.get(swapIndex)) < 0)
					swap(item,items.get(swapIndex));
				else 
					return;
			}
			else
				return;
		}
	}
	
	public boolean contains(T item) {
		return items.get(item.heapIndex) == item;
	}
	
	public void updateItem(T item) {
		sortUp(item);
	}
	
	public int size() {
		return currentItemSize;
	}
	
	private void swap(T itemA, T itemB) {
		items.set(itemA.heapIndex,itemB);
		items.set(itemB.heapIndex,itemA);
		
		int tempIndex = itemA.heapIndex;
		itemA.heapIndex = itemB.heapIndex;
		itemB.heapIndex = tempIndex;
	}
	
}
class Array<T>{
	private final Object[] arr;
	public final int length;
	
	public Array(int length) {
		this.length = length;
		arr = new Object[length];
	}
	
	T get(int i) {
		final T t = (T) arr[i];
		return t;
	}
	
	void set(int i, T t) {
		arr[i] = t;
	}
}
abstract class IHeapItem<T> implements Comparable<T> { 
	public int heapIndex;
}