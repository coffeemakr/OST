package ch.unstable.ost.api.search;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;


public class SearchAPITest {
    @Test
    public void getStationsByQuery() throws Exception {
        SearchAPI searchAPI = new SearchAPI();
        System.out.println(Arrays.toString(searchAPI.getStationsByQuery("luz")));
    }
}