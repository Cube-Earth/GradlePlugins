package earth.cube.gradle.plugins.commons.utils;

public interface IConverter<T, U> {

	U convert(T sValue);

}
