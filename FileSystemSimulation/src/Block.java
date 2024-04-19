import java.util.*;

public class Block {
  public static final int BLOCK_SIZE = 1024; // Block size in bytes

  private byte[] data;

  public Block() {
    this.data = new byte[BLOCK_SIZE];
  }

  // Gets the data from this block
  public byte[] getData() {
    return data.clone(); // Returns a copy to avoid external modifications
  }

  // Sets the data for this block. If data exceeds BLOCK_SIZE, it will be
  // truncated.
  public void setData(byte[] data) {
    int length = Math.min(data.length, BLOCK_SIZE);
    System.arraycopy(data, 0, this.data, 0, length);
    if (data.length < BLOCK_SIZE) {
      // If provided data is smaller than BLOCK_SIZE, fill the rest with zeros
      Arrays.fill(this.data, data.length, BLOCK_SIZE, (byte) 0);
    }
  }

  public int geBlockSize() {
    return BLOCK_SIZE;
  }

  // Utility method to clear the data in this block
  public void clearData() {
    Arrays.fill(this.data, (byte) 0);
  }
}
