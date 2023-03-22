package backend;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ActionQuery {
    String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException;
}
