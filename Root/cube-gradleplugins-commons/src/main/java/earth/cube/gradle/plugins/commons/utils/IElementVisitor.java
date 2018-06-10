package earth.cube.gradle.plugins.commons.utils;

import java.io.IOException;

import org.w3c.dom.Element;

public interface IElementVisitor {

	boolean visit(Element elem) throws IOException;

}
