package net.josephharding.routequest.transactions;

import java.util.List;

/**
 * Interface stub defining the common responsibilities of a response.
 * @param <T>   Generic datamodel type
 */

public interface IResponse<T> {

    public void parseResponse(String response);

    public List<T> getResults();

    public boolean success();

}
