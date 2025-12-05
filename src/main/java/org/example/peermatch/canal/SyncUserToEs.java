package org.example.peermatch.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author LinZeyuan
 * @description
 * @createDate 2025/12/5 16:18
 */
@Component
public class SyncUserToEs implements CommandLineRunner {
    private volatile boolean running = true;
    @Resource
    private CanalConnector connector;

    @Override
    public void run(String... args) {

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


}
