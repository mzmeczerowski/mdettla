/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.content.onto;

//#J2ME_EXCLUDE_FILE
//#APIDOC_EXCLUDE_FILE

import java.lang.reflect.Method;

class SlotAccessData {
	Class type;
	Method getter;
	Method setter;
	boolean aggregate;
	boolean mandatory;
	Class aggregateClass;
	int cardMin;
	int cardMax;

	SlotAccessData(Class type, Method getter, Method setter, boolean mandatory, Class aggregateClass, int cardMin, int cardMax) {
		this.type = type;
		this.getter = getter;
		this.setter = setter;
		aggregate = isAggregate(type);
		this.mandatory = mandatory;
		this.aggregateClass = aggregateClass;
		this.cardMin = cardMin;
		this.cardMax = cardMax;
	}

	static boolean isAggregate(Class clazz) {
		return clazz.isArray() || java.util.Collection.class.isAssignableFrom(clazz) || jade.util.leap.Collection.class.isAssignableFrom(clazz);
	}

	static boolean isSequence(Class clazz) {
		return clazz.isArray() || java.util.List.class.isAssignableFrom(clazz) || jade.util.leap.List.class.isAssignableFrom(clazz);
	}

	static boolean isSet(Class clazz) {
		return java.util.Set.class.isAssignableFrom(clazz) || jade.util.leap.Set.class.isAssignableFrom(clazz);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SlotAccessData {");
		sb.append("type=");
		sb.append(type.getName());
		sb.append(" getter=");
		sb.append(getter.getName());
		sb.append(" setter=");
		sb.append(setter.getName());
		sb.append(" aggregate=");
		sb.append(aggregate);
		sb.append(" aggregateClass=");
		sb.append(aggregateClass != null ? aggregateClass.getName() : null);
		sb.append(" mandatory=");
		sb.append(mandatory);
		sb.append(" cardMin=");
		sb.append(cardMin);
		sb.append(" cardMax=");
		sb.append(cardMax);
		sb.append('}');
		return sb.toString();
	}
}