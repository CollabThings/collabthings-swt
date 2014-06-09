package org.libraryofthings.swt.controls;

import org.libraryofthings.math.LVector;

public class TableTestData {
	private String value1 = "value1";
	private String value2 = "value2";
	private int intvalue = 5;
	private LVector v = new LVector(1, 2, 3);

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
}
