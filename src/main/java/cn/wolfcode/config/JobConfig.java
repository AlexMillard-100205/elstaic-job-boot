package cn.wolfcode.config;

import cn.wolfcode.job.FileCustomElasticJob;
import cn.wolfcode.job.MyElasticJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JobConfig {
    @Bean
    public CoordinatorRegistryCenter registryCenter(@Value("${elasticjob.zookeeper-url}")String url, @Value("${elasticjob.group-name}")String groupName){
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(url,groupName);
        //设置节点超时时间
        zookeeperConfiguration.setSessionTimeoutMilliseconds(100);
        //主持中心zookeeper
        CoordinatorRegistryCenter registryCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        registryCenter.init();
        return registryCenter;

    }

    private LiteJobConfiguration creatJobConfiguration(Class clazz, String cron, int shardingCount, String shardingParam, boolean isDataFlowJob){
        //定义作业的核心配置newBuidilder（“名称”，“cron表达式”，“分片数量”）
        JobCoreConfiguration.Builder coreConfigBuilder = JobCoreConfiguration.newBuilder(clazz.getSimpleName(),cron,shardingCount);

        if (!shardingParam.isEmpty()){
            coreConfigBuilder.shardingItemParameters(shardingParam);
        }
        //定义simple类型配置 cn.wolfcode.MyElasticJob
        JobCoreConfiguration coreConfig = coreConfigBuilder.build();

        JobTypeConfiguration jobTypeConfiguration;
        if (isDataFlowJob){
            jobTypeConfiguration = new DataflowJobConfiguration(coreConfig,clazz.getName(),true);
        }else {
            jobTypeConfiguration = new SimpleJobConfiguration(coreConfig,clazz.getName());
        }


        //定义Lite作业根配置
        LiteJobConfiguration simpleRootConfig = LiteJobConfiguration.newBuilder(jobTypeConfiguration).overwrite(true).build();

        return simpleRootConfig;
    }

    @Autowired
    private DataSource dataSource;

/*
    @Bean(initMethod = "init")
    public SpringJobScheduler testScheduler(MyElasticJob job,CoordinatorRegistryCenter registryCenter){

        LiteJobConfiguration jobConfiguration = creatJobConfiguration(job.getClass(),"0/3 * * * * ?",1);
        return new SpringJobScheduler(job,registryCenter,jobConfiguration);
    }

    @Bean(initMethod = "init")
    public SpringJobScheduler fileScheduler(FileCustomElasticJob job, CoordinatorRegistryCenter registryCenter){

        LiteJobConfiguration jobConfiguration = creatJobConfiguration(job.getClass(),"0/3 * * * * ?",4,"0=text,1=image,2=radio,3=vedio",true);
        return new SpringJobScheduler(job,registryCenter,jobConfiguration);
    }
*/
    @Bean(initMethod = "init")
    public SpringJobScheduler testScheduler(MyElasticJob job,CoordinatorRegistryCenter registryCenter){
        //增加任务事件追踪配置
        JobEventConfiguration jobEventConfiguration = new JobEventRdbConfiguration(dataSource);

        LiteJobConfiguration jobConfiguration = creatJobConfiguration(job.getClass(),"0/3 * * * * ?",1,"",false);
        return new SpringJobScheduler(job,registryCenter,jobConfiguration,jobEventConfiguration);
    }
}
