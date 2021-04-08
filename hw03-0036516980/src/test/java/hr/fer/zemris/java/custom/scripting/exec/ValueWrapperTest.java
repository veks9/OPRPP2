package hr.fer.zemris.java.custom.scripting.exec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.Socket;

import org.junit.jupiter.api.Test;

class ValueWrapperTest {

	@Test
	void test0() {
		ObjectMultistack multistack = new ObjectMultistack();
		ValueWrapper year = new ValueWrapper(Integer.valueOf(2000));
		multistack.push("year", year);

		ValueWrapper price = new ValueWrapper(200.51);
		multistack.push("price", price);

		assertEquals(2000, multistack.peek("year").getValue());
		assertEquals(200.51, multistack.peek("price").getValue());

		multistack.push("year", new ValueWrapper(Integer.valueOf(1900)));

		assertEquals(1900, multistack.peek("year").getValue());

		multistack.peek("year").setValue(((Integer) multistack.peek("year").getValue()).intValue() + 50);

		assertEquals(1950, multistack.peek("year").getValue());

		multistack.pop("year");

		assertEquals(2000, multistack.peek("year").getValue());

		multistack.peek("year").add("5");

		assertEquals(2005, multistack.peek("year").getValue());

		multistack.peek("year").add(5);

		assertEquals(2010, multistack.peek("year").getValue());

		multistack.peek("year").add(5.0);

		assertEquals(2015.0, multistack.peek("year").getValue());
	}
	
	@Test
	void test1() {
		ValueWrapper v1 = new ValueWrapper(null);
		ValueWrapper v2 = new ValueWrapper(null);
		v1.add(v2.getValue()); 
		
		assertEquals(0, v1.getValue());
	}
	
	@Test
	void test2() {
		ValueWrapper v1 = new ValueWrapper("1.2E1");
		ValueWrapper v2 = new ValueWrapper(Integer.valueOf(1));
		v1.add(v2.getValue()); 
		
		assertEquals(13.0, v1.getValue());
	}
	
	@Test
	void test3() {
		ValueWrapper v1 = new ValueWrapper("Ankica");
		ValueWrapper v2 = new ValueWrapper(Integer.valueOf(1));
		
		assertThrows(RuntimeException.class, () -> v1.add(v2.getValue()));
	}
	
	@Test
	void test4() {
		ValueWrapper v1 = new ValueWrapper(Double.valueOf(2));
		ValueWrapper v2 = new ValueWrapper(new Socket());
		
		assertThrows(RuntimeException.class, () -> v1.add(v2.getValue()));
	}
	
	@Test
	void test5() {
		ValueWrapper v1 = new ValueWrapper(Integer.valueOf(1));
		ValueWrapper v2 = new ValueWrapper(6.0);
		
		assertEquals(-5, v1.numCompare(v2.getValue()));
	}

}