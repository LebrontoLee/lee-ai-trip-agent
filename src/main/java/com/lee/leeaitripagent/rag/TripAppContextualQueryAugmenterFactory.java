//package com.lee.leeaitripagent.rag;
//
//import org.springframework.ai.chat.prompt.PromptTemplate;
//import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
//
///**
// * 创建上下文查询增强器的工厂
// */
//public class TripAppContextualQueryAugmenterFactory {
//    public static ContextualQueryAugmenter createTripAppContextualQueryAugmenter(){
//        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
//                你应该输出下面的内容：
//                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
//                有问题可以联系客服 https://baidu.com
//                """
//        );
//        return ContextualQueryAugmenter.builder()
//                .allowEmptyContext(false)
//                .emptyContextPromptTemplate(emptyContextPromptTemplate)
//                .build();
//    }
//}
