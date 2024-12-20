//package com.azure.cosmos.examples.search.async;
//
//import com.azure.ai.openai.OpenAIAsyncClient;
//import com.azure.ai.openai.OpenAIClientBuilder;
//import com.azure.cosmos.ConsistencyLevel;
//import com.azure.cosmos.CosmosAsyncClient;
//import com.azure.cosmos.CosmosAsyncContainer;
//import com.azure.cosmos.CosmosAsyncDatabase;
//import com.azure.cosmos.CosmosClientBuilder;
//import com.azure.cosmos.examples.common.AccountSettings;
//import com.azure.cosmos.examples.queries.async.QueriesQuickstartAsync;
//import com.azure.cosmos.models.CosmosContainerProperties;
//import com.azure.cosmos.models.CosmosContainerResponse;
//import com.azure.cosmos.models.CosmosDatabaseRequestOptions;
//import com.azure.cosmos.models.CosmosDatabaseResponse;
//import com.azure.cosmos.models.CosmosFullTextIndex;
//import com.azure.cosmos.models.CosmosFullTextPath;
//import com.azure.cosmos.models.CosmosFullTextPolicy;
//import com.azure.cosmos.models.CosmosVectorDataType;
//import com.azure.cosmos.models.CosmosVectorDistanceFunction;
//import com.azure.cosmos.models.CosmosVectorEmbedding;
//import com.azure.cosmos.models.CosmosVectorEmbeddingPolicy;
//import com.azure.cosmos.models.CosmosVectorIndexSpec;
//import com.azure.cosmos.models.CosmosVectorIndexType;
//import com.azure.cosmos.models.IncludedPath;
//import com.azure.cosmos.models.IndexingMode;
//import com.azure.cosmos.models.IndexingPolicy;
//import com.azure.cosmos.models.ThroughputProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.Collections;
//
//public class SampleSearchQueriesAsync {
//    private CosmosAsyncClient client;
//
//    private final String databaseName = "AzureSampleProductDB";
//    private final String containerName = "ProductContainer";
//
//    private CosmosAsyncDatabase database;
//    private CosmosAsyncContainer container;
//
//    private static final Logger logger = LoggerFactory.getLogger(QueriesQuickstartAsync.class);
//
//    public void close() {
//        client.close();
//    }
//
//    public static void main(String[] args) {
//        SampleSearchQueriesAsync p = new SampleSearchQueriesAsync();
//        try {
//            logger.info("Starting ASYNC main");
//            p.searchQueriesDemo();
//            logger.info("Demo complete, please hold while resources are released");
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(String.format("Cosmos getStarted failed with %s", e));
//        } finally {
//            logger.info("Closing the client");
//            p.shutdown();
//        }
//    }
//
//    private void searchQueriesDemo() throws Exception {
//        //  Create async client
//        client = new CosmosClientBuilder()
//                .endpoint(AccountSettings.HOST)
//                .key(AccountSettings.MASTER_KEY)
//                .consistencyLevel(ConsistencyLevel.EVENTUAL)
//                .contentResponseOnWriteEnabled(true)
//                .buildAsyncClient();
//
//
//        createDatabaseIfNotExists();
//        createContainerIfNotExists();
//    }
//
//    // Database Create
//    private void createDatabaseIfNotExists() throws Exception {
//        logger.info("Create database {} if not exists...", databaseName);
//
//        //  Create database if not exists
//        CosmosDatabaseResponse databaseResponse = client.createDatabaseIfNotExists(databaseName).block();
//        database = client.getDatabase(databaseResponse.getProperties().getId());
//
//        logger.info("createDatabaseIfNotExists done synchronously.");
//    }
//
//    // Container Create
//    private void createContainerIfNotExists() throws Exception {
//        logger.info("Create container {} if not exists...", containerName);
//
//        //  Create container if not exists
//        CosmosContainerProperties containerProperties =
//                new CosmosContainerProperties(containerName, "/categoryId");
//        containerProperties.setIndexingPolicy(populateIndexingPolicy());
//        containerProperties.setVectorEmbeddingPolicy(populateVectorEmbeddingPolicy());
//        containerProperties.setFullTextPolicy(populateFullTextPolicy());
//
//        // Provision throughput
//        ThroughputProperties throughputProperties = ThroughputProperties.createManualThroughput(400);
//
//        //  Create container with 200 RU/s
//        CosmosContainerResponse containerResponse = database.createContainerIfNotExists(containerProperties, throughputProperties).block();
//        container = database.getContainer(containerResponse.getProperties().getId());
//
//        logger.info("createContainerIfNotExists done synchronously.");
//    }
//
//    // Create documents
//    private void createDocuments() {
//
//    }
//
//    private void generateEmbeddings() {
//        OpenAIAsyncClient openAIClient = OpenAIClientBuilder()
//    }
//
//    // Indexing Policy
//    private IndexingPolicy populateIndexingPolicy() {
//        IndexingPolicy indexingPolicy = new IndexingPolicy();
//        indexingPolicy.setIndexingMode(IndexingMode.CONSISTENT);
//        IncludedPath includedPath1 = new IncludedPath("/*");
//        indexingPolicy.setIncludedPaths(Collections.singletonList(includedPath1));
//
//        CosmosVectorIndexSpec cosmosVectorIndexSpec = new CosmosVectorIndexSpec();
//        cosmosVectorIndexSpec.setPath("/embedding");
//        cosmosVectorIndexSpec.setType(CosmosVectorIndexType.DISK_ANN.toString());
//        cosmosVectorIndexSpec.setQuantizationSizeInBytes(2);
//        cosmosVectorIndexSpec.setIndexingSearchListSize(30);
//        indexingPolicy.setVectorIndexes(Collections.singletonList(cosmosVectorIndexSpec));
//
//        CosmosFullTextIndex fullTextIndex = new CosmosFullTextIndex();
//        fullTextIndex.setPath("/text");
//        indexingPolicy.setCosmosFullTextIndexes(Collections.singletonList(fullTextIndex));
//        return indexingPolicy;
//    }
//
//    // Vector Embedding Policy
//    private CosmosVectorEmbeddingPolicy populateVectorEmbeddingPolicy() {
//        CosmosVectorEmbeddingPolicy vectorEmbeddingPolicy = new CosmosVectorEmbeddingPolicy();
//        CosmosVectorEmbedding embedding = new CosmosVectorEmbedding();
//        embedding.setPath("/embedding");
//        embedding.setDataType(CosmosVectorDataType.FLOAT32);
//        embedding.setEmbeddingDimensions(1536);
//        embedding.setDistanceFunction(CosmosVectorDistanceFunction.COSINE);
//        vectorEmbeddingPolicy.setCosmosVectorEmbeddings(Collections.singletonList(embedding));
//        return vectorEmbeddingPolicy;
//    }
//
//    // Full Text Policy
//    private CosmosFullTextPolicy populateFullTextPolicy() {
//        CosmosFullTextPolicy fullTextPolicy = new CosmosFullTextPolicy();
//        CosmosFullTextPath fullTextPath = new CosmosFullTextPath();
//        fullTextPath.setPath("/text");
//        fullTextPath.setLanguage("en-US");
//        fullTextPolicy.setDefaultLanguage("en-US");
//        fullTextPolicy.setPaths(Collections.singletonList(fullTextPath));
//        return fullTextPolicy;
//    }
//
//    // Database delete
//    private void deleteADatabase() throws Exception {
//        logger.info("Last step: delete database {} by ID.", databaseName);
//
//        // Delete database
//        CosmosDatabaseResponse dbResp = client.getDatabase(databaseName).delete(new CosmosDatabaseRequestOptions()).block();
//        logger.info("Status code for database delete: {}",dbResp.getStatusCode());
//
//        logger.info("Done.");
//    }
//
//    // Cleanup before close
//    private void shutdown() {
//        try {
//            //Clean shutdown
//            deleteADatabase();
//        } catch (Exception err) {
//            logger.error("Deleting Cosmos DB resources failed, will still attempt to close the client. See stack trace below.");
//            err.printStackTrace();
//        }
//        client.close();
//        logger.info("Done with sample.");
//    }
//
//
//}
