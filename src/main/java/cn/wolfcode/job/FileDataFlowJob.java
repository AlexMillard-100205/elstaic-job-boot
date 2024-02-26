package cn.wolfcode.job;

import cn.wolfcode.domain.FileCustom;
import cn.wolfcode.mapper.FileCustomMapper;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FileDataFlowJob implements DataflowJob<FileCustom> {

    @Autowired
    private FileCustomMapper fileCustomMapper;

    @Override
    public List<FileCustom> fetchData(ShardingContext shardingContext) {
        List<FileCustom> fileCustoms = fileCustomMapper.selectLimit(2);
        return fileCustoms;
    }

    @Override
    public void processData(ShardingContext shardingContext, List<FileCustom> data) {
        System.out.println("抓取数据============================");
            for (FileCustom custom:data){
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
