
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;

public class Pair implements Serializable {
    private final BigInteger[] range;
    private final String port;
    private final String ip;

    public Pair(BigInteger[] range, String ip,String port){
        this.range = range;
        this.port = port;
        this.ip = ip;
    }

    public BigInteger[] getRange() {
        return range;
    }

    public String getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }

    public BigInteger getBound1(){
        return range[0];
    }

    public BigInteger getBound2(){
        return range[1];
    }
}
