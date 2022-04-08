package crypto.project.Model.functional;

public class PermutationFunction {
    public static byte[] permutation(byte[] pattern, byte[] block, int length) {
        byte[] blockDestination = new byte[length];
        for (int i = 0; i < length; i++) {
            blockDestination[i] = block[pattern[i]-1];
        }
        return blockDestination;
    }
}
