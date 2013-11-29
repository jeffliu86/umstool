package com.telenav.user.commons;

import java.util.HashMap;
import java.util.Map;

public class DependencyInjector extends UserObject {

	private final Map<Class<?>, Class<?>> bindingMap;

	private final static DependencyInjector instance = new DependencyInjector();

	private DependencyInjector() {
		this.bindingMap = new HashMap<Class<?>, Class<?>>();
	}

	public static <T> void bindInterface(final Class<T> interphace, final Class<? extends T> implementation) {
		instance.bindingMap.put(interphace, implementation);
	}

	@SuppressWarnings("unchecked")
	private <T> Class<? extends T> getImplementationBindingFor(final Class<T> interphace) {
		return (Class<? extends T>) this.bindingMap.get(interphace);
	}

	public static <T> T getInstance(final Class<T> interphace) {

		T returnValue = null;
		final Class<? extends T> impl = instance.getImplementationBindingFor(interphace);
		try {
			returnValue = impl.newInstance();
		}
		catch (final InstantiationException e) {
			throw new UserException(ResponseCode.GENERAL_INTERNAL_ERROR, e);
		}
		catch (final IllegalAccessException e) {
			throw new UserException(ResponseCode.GENERAL_INTERNAL_ERROR, e);
		}
		return returnValue;
	}
}
