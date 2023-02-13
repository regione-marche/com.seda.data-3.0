/**
 * 
 */
package com.seda.data.procedure;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import com.seda.data.procedure.xml.XMLSystemParser;

/**
 * @author f.ricci
 *
 */
public class RuntimeSystemFactory {

	public SessionFactory getSessionFactory(Reader reader) {
		return getSessionFactory(reader, null, null);
	}

	public SessionFactory getSessionFactory(Reader reader, String plan) {
		return getSessionFactory(reader, plan, null);
	}

	public SessionFactory getSessionFactory(Reader reader, Properties properties) {
		return getSessionFactory(reader, null, properties);
	}

	public SessionFactory getSessionFactory(Reader reader, String plan, Properties props) {
		try {
			XMLSystemParser parser = new XMLSystemParser(reader, plan, props);
			SubSystem system = parser.parse();
			return getSessionFactory(system);
		} catch (Exception e) {
			throw new RuntimeSystemException("Error building Session Factory.", e);
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException e) {}
		}
	}

	public SessionFactory getSessionFactory(SubSystem context) {
		return new SessionFactoryImpl(context);
	}	

}
