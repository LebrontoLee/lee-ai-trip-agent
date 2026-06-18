//package com.lee.leeaitripagent.rag;
//
//import org.springframework.ai.document.Document;
//import org.springframework.ai.transformer.splitter.TokenTextSplitter;
//
//import java.util.List;
//
///**
// * 自定义基于 Token 的切词器
// */
//public class MyTokenTextSplitter {
//
//    public List<Document> splitDocuments(List<Document> documents) {
//        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
//        return tokenTextSplitter.apply(documents);
//    }
//
//    public List<Document> splitCustomized(List<Document> documents) {
//        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
//                .withChunkSize(1000)
//                .withMinChunkSizeChars(400)
//                .withMinChunkLengthToEmbed(10)
//                .withMaxNumChunks(5000)
//                .withKeepSeparator(true)
//                .build();
//        return tokenTextSplitter.apply(documents);
//    }
//}
