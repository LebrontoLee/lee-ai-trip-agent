//package com.lee.leeaitripagent.rag;
//
//import org.springframework.ai.chat.client.advisor.api.Advisor;
//import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
//import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
//import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.ai.vectorstore.filter.Filter;
//import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
//
///**
// * 创建自定义的 RAG 检索增强顾问的工厂
// */
//public class TripAppRagCustomAdvisorFactory {
//
//    public static Advisor createTripAppRagCustomAdvisor(VectorStore vectorStore, String status) {
//        // 过滤特定状态的文档
//        Filter.Expression expression = new FilterExpressionBuilder()
//                .eq("status", status)
//                .build();
//        DocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
//                .vectorStore(vectorStore)
//                .filterExpression(expression)
//                .similarityThreshold(0.6)
//                .topK(3)
//                .build();
//        return RetrievalAugmentationAdvisor.builder()
//                .documentRetriever(documentRetriever)
//                .queryAugmenter(TripAppContextualQueryAugmenterFactory.createTripAppContextualQueryAugmenter())
//                .build();
//    }
//}
