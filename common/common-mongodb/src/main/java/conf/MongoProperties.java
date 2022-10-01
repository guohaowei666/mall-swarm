package conf;

import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoProperties {

    @Value("${mongo.rs}")
    private String rs;

    private List<ServerAddress> rsServers = new ArrayList<>();

    @Value("${mongo.dbname}")
    private String dbname;

    @Value("${mongo.username}")
    private String username;

    @Value("${mongo.password}")
    private String password;
    /**
     * 线程池允许的最大连接数,默认是100
     */
    @Value("${mongo.maxSize}")
    private int maxSize;
    /**
     * 线程池空闲时保持的最小连接数, 默认是0
     */
    @Value("${mongo.minSize}")
    private int minSize;
    /**
     * 线程等待连接变为可用的最长时间.默认为2分钟. 值为0意味着它不会等待. 负值意味着它将无限期地等待
     * 单位：MILLISECONDS
     */
    @Value("${mongo.maxWaitTime}")
    private int maxWaitTime;
    /**
     * 集群连接模式 0:SINGLE;1:MULTIPLE
     */
    @Value("${mongo.clusterConnectionMode}")
    private int clusterConnectionMode;
    /**
     * 0:STANDALONE;1:REPLICA_SET
     */
    @Value("${mongo.requiredClusterType}")
    private int requiredClusterType;
    /**
     * 0:普通Credential;1:SHA1;2:SHA256
     */
    @Value("${mongo.credentialType}")
    private int credentialType;


    public int getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(int credentialType) {
        this.credentialType = credentialType;
    }

    public int getRequiredClusterType() {
        return requiredClusterType;
    }

    public void setRequiredClusterType(int requiredClusterType) {
        this.requiredClusterType = requiredClusterType;
    }

    public int getClusterConnectionMode() {
        return clusterConnectionMode;
    }

    public void setClusterConnectionMode(int clusterConnectionMode) {
        this.clusterConnectionMode = clusterConnectionMode;
    }

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getDbname() {
        return dbname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public List<ServerAddress> getRsServers() {
        if (rs != null && rs.trim().length() > 0) {
            String[] hosts = rs.split(",");
            for (String host : hosts) {
                String[] ipAndPort = host.split(":");
                if (ipAndPort.length == 0) {
                    continue;
                }
                if (ipAndPort.length == 1) {
                    rsServers.add(new ServerAddress(ipAndPort[0], 27017));
                } else {
                    rsServers.add(new ServerAddress(ipAndPort[0], Integer
                            .parseInt(ipAndPort[1])));
                }
            }
        } else {
            rsServers.add(new ServerAddress("localhost", 27017));
            return rsServers;
        }
        return rsServers;
    }
}
