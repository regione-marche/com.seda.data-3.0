package com.seda.data.type;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HandlerRegistry {

	private static final Map<Class<?>, Class<?>> reversePrimitiveMap = new HashMap<Class<?>, Class<?>>() {
		private static final long serialVersionUID = 1L;

		{
			put(Byte.class, byte.class);
			put(Short.class, short.class);
			put(Integer.class, int.class);
			put(Long.class, long.class);
			put(Float.class, float.class);
			put(Double.class, double.class);
			put(Boolean.class, boolean.class);
		}
	};

	private final Map<JdbcType, TypeHandler> JDBC_TYPE_HANDLER_MAP = new HashMap<JdbcType, TypeHandler>();
	private final Map<Class<?>, Map<JdbcType, TypeHandler>> TYPE_HANDLER_MAP = new HashMap<Class<?>, Map<JdbcType, TypeHandler>>();
	private final TypeHandler UNKNOWN_TYPE_HANDLER = new UnknownTypeHandler(this);

	public HandlerRegistry() {
		register(Boolean.class, new BooleanHandler());
		register(boolean.class, new BooleanHandler());
		register(JdbcType.BOOLEAN, new BooleanHandler());
		register(JdbcType.BIT, new BooleanHandler());

		register(Byte.class, new ByteHandler());
		register(byte.class, new ByteHandler());
		register(JdbcType.TINYINT, new ByteHandler());

		register(Short.class, new ShortHandler());
		register(short.class, new ShortHandler());
		register(JdbcType.SMALLINT, new ShortHandler());

		register(Integer.class, new IntegerHandler());
		register(int.class, new IntegerHandler());
		register(JdbcType.INTEGER, new IntegerHandler());

		register(Long.class, new LongHandler());
		register(long.class, new LongHandler());

		register(Float.class, new FloatHandler());
		register(float.class, new FloatHandler());
		register(JdbcType.FLOAT, new FloatHandler());

		register(Double.class, new DoubleHandler());
		register(double.class, new DoubleHandler());
		register(JdbcType.DOUBLE, new DoubleHandler());

		register(String.class, new StringHandler());
		register(String.class, JdbcType.CHAR, new StringHandler());
		register(String.class, JdbcType.CLOB, new ClobHandler());
		register(String.class, JdbcType.VARCHAR, new StringHandler());
		register(String.class, JdbcType.LONGVARCHAR, new ClobHandler());
		register(String.class, JdbcType.NVARCHAR, new NStringHandler());
		register(String.class, JdbcType.NCHAR, new NStringHandler());
		register(String.class, JdbcType.NCLOB, new NClobHandler());
		register(JdbcType.CHAR, new StringHandler());
		register(JdbcType.VARCHAR, new StringHandler());
		register(JdbcType.CLOB, new ClobHandler());
		register(JdbcType.LONGVARCHAR, new ClobHandler());
		register(JdbcType.NVARCHAR, new NStringHandler());
		register(JdbcType.NCHAR, new NStringHandler());
		register(JdbcType.NCLOB, new NClobHandler());

		register(BigDecimal.class, new BigDecimalHandler());
		register(JdbcType.DECIMAL, new BigDecimalHandler());
		register(JdbcType.NUMERIC, new BigDecimalHandler());
		register(JdbcType.BIGINT, new BigDecimalHandler());
		register(JdbcType.REAL, new BigDecimalHandler());

		register(byte[].class, new ByteArrayHandler());
		register(byte[].class, JdbcType.BLOB, new BlobHandler());
		register(byte[].class, JdbcType.LONGVARBINARY, new BlobHandler());
		register(JdbcType.LONGVARBINARY, new BlobHandler());
		register(JdbcType.BLOB, new BlobHandler());

		register(Object.class, UNKNOWN_TYPE_HANDLER);
		register(Object.class, JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);
		register(JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);

		register(Date.class, new DateTimeHandler());
		register(Date.class, JdbcType.DATE, new DateHandler());
		register(Date.class, JdbcType.TIME, new TimeHandler());
		register(JdbcType.TIMESTAMP, new DateTimeHandler());
		register(JdbcType.DATE, new DateHandler());
		register(JdbcType.TIME, new TimeHandler());

		register(java.sql.Date.class, new SqlDateHandler());
		register(java.sql.Time.class, new SqlTimeHandler());
		register(java.sql.Timestamp.class, new SqlTimestampHandler());
	}

	public boolean hasTypeHandler(Class<?> javaType) {
		return hasTypeHandler(javaType, null);
	}

	public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
		return javaType != null && getTypeHandler(javaType, jdbcType) != null;
	}

	public TypeHandler getTypeHandler(Class<?> type) {
		return getTypeHandler(type, null);
	}

	public TypeHandler getTypeHandler(JdbcType jdbcType) {
		return JDBC_TYPE_HANDLER_MAP.get(jdbcType);
	}

	public TypeHandler getTypeHandler(Class<?> type, JdbcType jdbcType) {
		Map<JdbcType, TypeHandler> jdbcHandlerMap = TYPE_HANDLER_MAP.get(type);
		TypeHandler handler = null;
		if (jdbcHandlerMap != null) {
			handler = (TypeHandler) jdbcHandlerMap.get(jdbcType);
			if (handler == null) {
				handler = (TypeHandler) jdbcHandlerMap.get(null);
			}
		}
		if (handler == null && type != null && Enum.class.isAssignableFrom(type)) {
			handler = new EnumHandler(type);
		}
		return handler;
	}

	public TypeHandler getUnkownTypeHandler() {
		return UNKNOWN_TYPE_HANDLER;
	}

	public void register(JdbcType jdbcType, TypeHandler handler) {
		JDBC_TYPE_HANDLER_MAP.put(jdbcType, handler);
	}

	public void register(Class<?> type, TypeHandler handler) {
		register(type, null, handler);
	}

	public void register(Class<?> type, JdbcType jdbcType, TypeHandler handler) {
		Map<JdbcType, TypeHandler> map = TYPE_HANDLER_MAP.get(type);
		if (map == null) {
			map = new HashMap<JdbcType, TypeHandler>();
			TYPE_HANDLER_MAP.put(type, map);
		}
		map.put(jdbcType, handler);
		if (reversePrimitiveMap.containsKey(type)) {
			register(reversePrimitiveMap.get(type), jdbcType, handler);
		}
	}

}
