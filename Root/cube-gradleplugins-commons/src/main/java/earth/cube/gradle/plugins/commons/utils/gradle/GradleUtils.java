package earth.cube.gradle.plugins.commons.utils.gradle;

import org.gradle.api.internal.ClosureBackedAction;

import groovy.lang.Closure;

public class GradleUtils {
	
	public static <T> T evaluate(@SuppressWarnings("rawtypes") Closure closure, Class<T> clazz) {
		try {
			T o = clazz.newInstance();
			ClosureBackedAction<T> a = new ClosureBackedAction<T>(closure);
			a.execute(o);
			return o;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
