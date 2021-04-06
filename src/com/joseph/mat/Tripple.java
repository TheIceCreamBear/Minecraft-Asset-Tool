package com.joseph.mat;

/**
 * An immutable class to hold three different types of related objects. Useful for 
 * when you need to return 3 objects from one method, and those three objects are 
 * all related to each other.
 * 
 * @author Joseph
 *
 * @param <A> - the type of the first object, accessed with getA()
 * @param <B> - the type of the second object, accessed with getB()
 * @param <C> - the type of the third object, accessed with getC()
 */
public class Tripple<A, B, C> {
	private A a;
	private B b;
	private C c;
	
	/**
	 * Constructs a new triple, with the first value being of type <code>A</code>,
	 * the second being of type <code>B</code>, and the third being of type
	 * <code>C</code>. The values can be access through the getters of their respective
	 * type and paramater order.
	 * @param a - the first object to store, of type <code>A</code>
	 * @param b - the second object to store, of type <code>B</code>
	 * @param c - the third object to store, of type <code>C</code>
	 */
	public Tripple(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public A getA() {
		return this.a;
	}
	
	public B getB() {
		return this.b;
	}
	
	public C getC() {
		return this.c;
	}
}
