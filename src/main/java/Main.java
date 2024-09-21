import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.security.SecureRandom;

public class Main {
    final static int PORT = 5000;
    final static int TAILLE = 65535;
    static byte[] buffer = new byte[TAILLE];
    static BigInteger p, g, k, a, b, s;
    static SecureRandom secureRandom = new SecureRandom();
    static int bitLength = 128;
    static int publicCount = 0;
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            System.out.println("Lancement du serveur");
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, TAILLE);
                socket.receive(packet);
                if (publicCount != 2) {
                    publicCount++;
                    String chaine = new String(packet.getData(), 0, packet.getLength());
                    if (publicCount == 1) {
                        p = new BigInteger(chaine);
                        System.out.println("La cle publique " + publicCount + ": " + p);
                    } else {
                        g = new BigInteger(chaine);
                        System.out.println("La cle publique " + publicCount + ": " + g);
                    }
                } else {
                    do {
                        k = BigInteger.probablePrime(bitLength, secureRandom);
                    } while (k.compareTo(p) > 0);

                    String chaine = new String(packet.getData(), 0, packet.getLength());
                    a = new BigInteger(chaine);

                    b = g.modPow(k, p);
                    packet.setData(new String(String.valueOf(b)).getBytes());
                    socket.send(packet);

                    s = a.modPow(k, p);


                    System.out.println("La cle secrete de Bob: " + k);
                    System.out.println("Resulting public key (A): " + a);
                    System.out.println("Resulting public key (B): " + b);
                    System.out.println("La cle secrete: " + s);
                    System.out.println("\n");
                    publicCount = 0;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}