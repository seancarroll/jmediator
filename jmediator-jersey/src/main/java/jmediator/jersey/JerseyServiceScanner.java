package jmediator.jersey;

import jmediator.RequestHandler;

import java.util.List;

import io.github.classgraph.ClassGraph;

public class JerseyServiceScanner {

    public List<String> serviceNames(String... packages) {
        return new ClassGraph()
        	.whitelistPackages(packages)
            .scan()
            .getClassesImplementing(RequestHandler.class.getName()).getNames();
    }

}
