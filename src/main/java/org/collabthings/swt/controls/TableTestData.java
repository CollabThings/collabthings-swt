/*******************************************************************************
 * Copyright (c) 2014 Juuso Vilmunen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Juuso Vilmunen
 ******************************************************************************/
package org.collabthings.swt.controls;

import java.util.HashSet;
import java.util.Set;

import org.collabthings.datamodel.WObject;
import org.collabthings.datamodel.WObjectID;
import org.collabthings.datamodel.WStringID;
import org.collabthings.math.LOrientation;
import org.collabthings.model.CTObject;

import com.jme3.math.Vector3f;

public class TableTestData implements CTObject {
	private String value1 = "value1";
	private String value2 = "value2";
	private int intvalue = 5;
	private Vector3f v = new Vector3f(1, 2, 3);
	private LOrientation o = new LOrientation();
	private Set<String> somestrings = new HashSet<String>();
	private String name = "testdata";

	public TableTestData() {
		while (somestrings.size() < 3) {
			somestrings.add("String" + somestrings.size());
		}
	}

	@Override
	public boolean load(WStringID id) {
		return true;
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

	public Vector3f getV() {
		return v;
	}

	public void setV(Vector3f v) {
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

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void publish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public WObjectID getID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WObject getObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean parse(WObject o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String n) {
		this.name = n;
	}
}
