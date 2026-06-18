//package com.lee.leeaitripagent.rag;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.ai.rag.Query;
//import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
//import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
//import org.springframework.stereotype.Component;
//
///**
// * 查询重写器
// */
//@Component
//public class QueryRewritten {
//
//    private QueryTransformer queryTransformer;
//
//    private QueryRewritten(ChatModel dashscopeChatModel) {
//        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
//        // 创建查询重写转换器
//        queryTransformer = RewriteQueryTransformer.builder()
//                .chatClientBuilder(builder)
//                .build();
//    }
//
//    public String doQueryRewrite(String prompt)
//    {
//        Query query = new Query(prompt);
//        // 执行查询重写
//        Query transformedQuery = queryTransformer.transform(query);
//        return transformedQuery.text();
//    }
//}
