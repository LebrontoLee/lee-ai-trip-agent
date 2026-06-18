package com.lee.leeaitripagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBasedChatMemory implements ChatMemory {
    // 所有会话文件保存的根目录
    private final String BASE_DIR;

    private static final Kryo kryo = new Kryo();

    // 滑动窗口大小：每个会话最多保留最近 10 条消息
    private final int lastN = 10;

    static {
        // 不需要提前注册所有要序列化的类（方便开发）
        kryo.setRegistrationRequired(false);
        // 设置实例化策略，支持没有无参构造函数的类实例化
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    // 构造对象时，指定文件保存目录
    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File BaseDir = new File(dir);
        if (!BaseDir.exists()) {
            BaseDir.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, Message message) {
        // 读取该会话已有的历史消息（不存在则返回空列表）
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        // 添加新消息到列表末尾
        conversationMessages.add(message);
        // 把更新后的列表重新写入文件
        saveConversation(conversationId, conversationMessages);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        conversationMessages.addAll(messages);
        saveConversation(conversationId, conversationMessages);
    }

    @Override
    public List<Message> get(String conversationId) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        // 如果消息总数 ≤ 10，直接返回全部
        if (conversationMessages.size() <= lastN) {
            return conversationMessages;
        }
        // 否则只返回最后 10 条（滑动窗口）
        return conversationMessages.stream()
                .skip(Math.max(0, conversationMessages.size() - lastN))
                .toList();
    }

    @Override
    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        // 如果会话文件存在，直接删除
        if (file.exists()) {
            file.delete();
        }
    }
    /**
     * 获取或创建会话消息的列表
     *
     * @param conversationId 会话 Id
     * @return 消息列表
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File file = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        if (file.exists()) {
            // 用 Kryo 反序列化文件内容为消息列表
            try(Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        File file = getConversationFile(conversationId);
        // 用 Kryo 序列化消息列表并写入文件
        try(Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, messages);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每个会话文件单独保存
     * @param conversationId 会话 Id
     * @return File
     */
    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }
}
