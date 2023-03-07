package backend;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ActionQuery {
    public String startQuery(String query) throws IOException, InvocationTargetException, IllegalAccessException;
}
