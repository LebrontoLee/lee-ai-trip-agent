//package com.lee.leeaitripagent.rag;
//
//import jakarta.annotation.Resource;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.embedding.EmbeddingModel;
//import org.springframework.ai.vectorstore.SimpleVectorStore;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
///**
// * AI Trip 向量数据库配置（初始化基于内存的向量数据库 Bean）
// */
//@Configuration
//public class TripVectorStoreConfig {
//    @Resource
//    private TripAppDocumentLoader tripAppDocumentLoader;
//
//    @Resource
//    private MyTokenTextSplitter myTokenTextSplitter;
//
//    @Resource
//    private MyKeyWordEnricher myKeyWordEnricher;
//
//    @Bean
//    VectorStore TripAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
//        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
//        // 加载文档
//        List<Document> documentList = tripAppDocumentLoader.loadMarkdowns();
//        // 自主切分文档
//        List<Document> splitDocumentList = myTokenTextSplitter.splitDocuments(documentList);
//        // 自动补充关键词元信息
//        List<Document> enrichedDocumentList = myKeyWordEnricher.enrichDocuments(splitDocumentList);
//        simpleVectorStore.add(enrichedDocumentList);
//        return simpleVectorStore;
//    }
//}
