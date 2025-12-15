package org.example.peermatch.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import org.example.peermatch.model.domain.User;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/12/5 16:18
 */
//@Component
public class SyncUserToEs implements CommandLineRunner {
    private volatile boolean running = true;
    @Resource
    private CanalConnector connector;
    @Resource
    private ExecutorService executorService;

    @Override
    public void run(String... args) {
        executorService.submit(this::process);
    }

    @PreDestroy
    public void stop(){
        running = false;
        try{
            connector.disconnect();
        }catch (Exception e){

        }
        executorService.shutdown();
        try{
            if(executorService.awaitTermination(10, TimeUnit.SECONDS)){
                executorService.shutdownNow();
            }
        }catch (Exception e){
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void process() {
        try {
            connector.connect();
            connector.subscribe("peer-match.user");
            connector.rollback();
            final int batchSize = 500;
            while (running) {
                Message message = connector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    Thread.sleep(1000);
                } else {
                    handleEntries(message.getEntries());
                }
                connector.ack(batchId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connector.disconnect();
        }
    }
    private void handleEntries(List<CanalEntry.Entry> entries) throws Exception{
        for(CanalEntry.Entry entry: entries){
            if(entry.getEntryType()==CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType()==CanalEntry.EntryType.TRANSACTIONEND){
                continue;
            }
            CanalEntry.RowChange rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            CanalEntry.EventType eventType = rowChange.getEventType();
            for (CanalEntry.RowData rowData :rowChange.getRowDatasList()){
                switch(eventType){
                    case INSERT:
                        handleInsert(rowData.getAfterColumnsList());
                        break;
                    case UPDATE:
                        handleUpdate(rowData.getAfterColumnsList());
                        break;
                    case DELETE:
                        handleDelete(rowData.getBeforeColumnsList());
                        break;
                }
            }
        }
    }

    private void handleInsert(List<CanalEntry.Column> columns) {
        System.out.println("新增"+parseUser(columns));
        //TODO
    }

    private void handleUpdate(List<CanalEntry.Column> columns) {
        System.out.println("新增"+parseUser(columns));
        //TODO
    }
    private void handleDelete(List<CanalEntry.Column> columns) {
        System.out.println("新增"+parseUser(columns));
        //TODO
    }

    private User parseUser(List<CanalEntry.Column> columns){
        User user=new User();
        for(CanalEntry.Column c:columns){
            switch(c.getName()) {
                case "id":
                    user.setId(Long.valueOf(c.getValue()));
                    break;
                case "user_name":
                    user.setUserName(c.getValue());
                    break;
                case "tags":
                    user.setTags(c.getValue());
                    break;
                case "update_time":
                    user.setUpdateTime(Date.from(Instant.parse(c.getValue())));
                    break;
            }
        }
        return user;
    }
}
