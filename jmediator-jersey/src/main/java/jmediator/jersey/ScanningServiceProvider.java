package jmediator.jersey;

public class ScanningServiceProvider {

    private JerseyServiceScanner scanner;
    private String packagesToScan;

    public ScanningServiceProvider(String packagesToScan) {
        this.packagesToScan = packagesToScan;
        this.scanner = new JerseyServiceScanner();
    }

    public void scan() {

    }
}
