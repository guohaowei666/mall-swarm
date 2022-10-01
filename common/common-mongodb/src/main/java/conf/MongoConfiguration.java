package conf;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.connection.ClusterConnectionMode;
import com.mongodb.connection.ClusterType;
import com.mongodb.connection.ConnectionPoolSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {
    @Autowired
    private MongoProperties mongoProperties;

    @Bean
    @Override
    public  MongoClient mongoClient(){
    	char[] password = mongoProperties.getPassword().toCharArray();
    	String userName = mongoProperties.getUsername();
    	String database = mongoProperties.getDbname();
        MongoCredential credential = null;
        if(0 == mongoProperties.getCredentialType()) {
        	credential = MongoCredential.createCredential(userName, database, password);
        }
        if(1 == mongoProperties.getCredentialType()) {
        	credential = MongoCredential.createScramSha1Credential(userName, database, password);
        }
        if(2 == mongoProperties.getCredentialType()) {
        	credential = MongoCredential.createScramSha256Credential(userName, database, password);
        }
        		//
//      ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        ConnectionPoolSettings connectionPoolSettings = ConnectionPoolSettings.builder()
                .minSize(mongoProperties.getMinSize())
                .maxSize(mongoProperties.getMaxSize())
                //.maxWaitQueueSize(mongoProperties.getThreadsAllowedToBlockForConnectionMultiplier()
                //                  * mongoProperties.getConnectionsPerHost())
                .maxWaitTime(mongoProperties.getMaxWaitTime(),TimeUnit.MILLISECONDS)
                //.maxConnectionIdleTime(mongoProperties.getMaxConnectionIdleTime(), MILLISECONDS)
                //.maxConnectionLifeTime(mongoProperties.getMaxConnectionLifeTime(), MILLISECONDS)
                .build();
        int mode = mongoProperties.getClusterConnectionMode();
        Map<Integer,ClusterConnectionMode> modeMap = new HashMap<>();
        modeMap.put(0, ClusterConnectionMode.SINGLE);
        modeMap.put(1, ClusterConnectionMode.MULTIPLE);
        
        int requiredClusterType = mongoProperties.getRequiredClusterType();
        Map<Integer,ClusterType> clusterTypeMap = new HashMap<>();
        clusterTypeMap.put(0, ClusterType.STANDALONE);
        clusterTypeMap.put(1, ClusterType.REPLICA_SET);
        
        MongoClientSettings setting=MongoClientSettings.builder()
                .credential(credential)
                .applyToConnectionPoolSettings(block->block.applySettings(connectionPoolSettings))
//                .applyConnectionString(connectionString)
                .applyToClusterSettings(builder ->
                        builder.hosts(mongoProperties.getRsServers())
                                .mode(modeMap.get(mode))
                                .requiredClusterType(clusterTypeMap.get(requiredClusterType))
                ).build();
        return MongoClients.create(setting);
    }

    @Bean
    @Override
    public MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), mongoProperties.getDbname());
    }
    @Bean
    public MongoTemplate mongoTemplate() {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
        return mongoTemplate;
    }
    @Bean
    public GridFSBucket getGridFSBuckets() {
        MongoDatabase db = mongoDbFactory().getMongoDatabase();
        return GridFSBuckets.create(db);
    }
    @Override
    protected String getDatabaseName() {
        return mongoProperties.getDbname();
    }
//	@Override
//	public Mongo mongo() throws Exception {
//		return mongoClient();
//	}
}
