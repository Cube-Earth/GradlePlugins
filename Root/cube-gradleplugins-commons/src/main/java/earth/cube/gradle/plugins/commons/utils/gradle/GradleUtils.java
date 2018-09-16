package earth.cube.gradle.plugins.commons.utils.gradle;

import java.io.File;

import org.gradle.api.Project;
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
	
	@SuppressWarnings("unchecked")
	public static <T> T findProperty(Project project, String sName) {
		if(project == null)
			throw new IllegalArgumentException(sName);
		if(project.hasProperty(sName))
			return (T) project.getProperties().get(sName);
		return findProperty(project.getParent(), sName);
	}
	
	public static String getBuildDir(Project project, String sDir) {
		return new File(project.getBuildDir(), sDir).getAbsolutePath();
	}
}
