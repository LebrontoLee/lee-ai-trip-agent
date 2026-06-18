//package com.lee.leeaitripagent.runner;
//
//import jakarta.annotation.Resource;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 项目启动时，自动扫描加载本地 Markdown 文档，向 PostgreSQL 向量库（pgVector）写入文本向量；
// * 如果库里已经存在数据则直接跳过，避免重复导入
// */
//@Component
//public class DocLoadRunner  implements ApplicationRunner {
//    @Resource
//    private VectorStore pgVectorVectorStore;
//
//    @Resource
//    TripAppDocumentLoader tripAppDocumentLoader;
//
//    public void run(ApplicationArguments args) {
//        // ✅ 先查询有没有数据
//        List<Document> existing = pgVectorVectorStore.similaritySearch("check");
//        if (!existing.isEmpty()) {
//            System.out.println("✅ 表已有数据，跳过加载");
//            return;
//        }
//        List<Document> documents = tripAppDocumentLoader.loadMarkdowns();
//        // 👇 关键！手动切成每批 10 条，阿里云只允许这个大小
//        int batchSize = 10;
//        for (int i = 0; i < documents.size(); i += batchSize) {
//            int end = Math.min(i + batchSize, documents.size());
//            List<Document> batch = new ArrayList<>(documents.subList(i, end));
//            pgVectorVectorStore.add(batch);
//            System.out.println("✅ 已导入：" + end + "/" + documents.size());
//        }
//        // pgVectorVectorStore.add(documents);
//        System.out.println("✅ 文档向量入库完成");
//    }
//}
