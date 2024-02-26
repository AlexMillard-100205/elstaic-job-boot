package cn.wolfcode.job;

import cn.wolfcode.domain.FileCustom;
import cn.wolfcode.mapper.FileCustomMapper;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class FileCustomElasticJob implements SimpleJob {

    @Autowired
    private FileCustomMapper fileCustomMapper;

    @Override
    public void execute(ShardingContext shardingContext) {
        Long threadId = Thread.currentThread().getId();
        log.info("线程ID:{},任务参数:{},分片个数:{}，分片索引:{}，分片参数:{}",
                threadId,
                shardingContext.getJobName(),
                shardingContext.getJobParameter(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter());
        doWrok(shardingContext.getShardingParameter());
    }

    private void doWrok(String type){
        List<FileCustom> fileCustoms = fileCustomMapper.selectByType(type);

        for(FileCustom custom:fileCustoms){
            backUp(custom);
        }
    }

    private void backUp(FileCustom custom) {
        System.out.println("备份的方法名字"+custom.getName()+" 备份的类型名字"+custom.getType());
        System.out.println("========================================");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        fileCustomMapper.changeState(custom.getId(),1);
    }
}
