package edu.mit.ll.bluetoothmanagement;

/* Purpose of this interface is to generalize the data parsing that takes place
 * whenever a new Bluetooth message is received by
 * BluetoothPollingStateMachine.java */
public interface DataParser {
    // Generalized from a HidalgoDataParser, which is what this is typically.

    void processBuffer(byte[] buffer, int size);

}
