package org.collabthings.swt.controls;

import java.util.HashSet;
import java.util.Set;

import org.collabthings.math.LOrientation;
import org.collabthings.math.LVector;

public class TableTestData {
	private String value1 = "value1";
	private String value2 = "value2";
	private int intvalue = 5;
	private LVector v = new LVector(1, 2, 3);
	private LOrientation o = new LOrientation();
	private Set<String> somestrings = new HashSet<String>();

	public TableTestData() {
		while (somestrings.size() < 3) {
			somestrings.add("String" + somestrings.size());
		}
	}

	@Override
	public String toString() {
		return "TableTestData";
	}

	public int getIntvalue() {
		return intvalue;
	}

	public void setIntvalue(int intvalue) {
		this.intvalue = intvalue;
	}

	public LVector getV() {
		return v;
	}

	public void setV(LVector v) {
		this.v = v;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public LOrientation getO() {
		return o;
	}

	public void setO(LOrientation o) {
		this.o = o;
	}

	public Set<String> getSomestrings() {
		return somestrings;
	}

	public void setSomestrings(Set<String> somestrings) {
		this.somestrings = somestrings;
	}
}
